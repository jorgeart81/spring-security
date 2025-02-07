package com.jorgereyesdev.spring_security.infrastructure.services

import com.jorgereyesdev.spring_security.config.EnvironmentVariables
import com.jorgereyesdev.spring_security.domain.models.User
import com.jorgereyesdev.spring_security.domain.services.JWTService
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.stereotype.Service
import java.util.*
import javax.crypto.SecretKey

@Service
class JWTServiceImpl : JWTService {
    private val log: Logger = LoggerFactory.getLogger(JWTService::class.java)

    override fun generateToken(user: User): String {
        return buildToken(
            user = user,
            expiration = EnvironmentVariables.Jwt.EXPIRATION,
            extraClaims = mapOf(USERNAME to user.username, SECURITY_STAMP to user.securityStamp)
        )
    }

    override fun generateRefreshToken(user: User): String {
        return buildToken(
            user = user,
            expiration = EnvironmentVariables.Jwt.REFRESH_EXPIRATION,
            extraClaims = mapOf(GRANT_TYPE to REFRESH_TOKEN, SECURITY_STAMP to user.securityStamp)
        )
    }

    override fun isTokenValid(token: String, username: String, securityStamp: String?): Boolean {
        val validationParameters = TokenValidationParameters(
            validateUsername = true,
            username = username,
            validateSecurityStamp = true,
            securityStamp = securityStamp,
            validateLifetime = true,
            validateIssuerSigningKey = true,
            issuerSigningKey = getSignInKey()
        )

        val claims = JwtSecurityTokenHandler.validateToken(
            parameters = validationParameters,
            token = token,
        )

        return claims != null

    }

    override fun getUsernameFromToken(token: String): String? {
        val validationParameters = TokenValidationParameters(
            validateLifetime = true,
            validateIssuerSigningKey = true,
            issuerSigningKey = getSignInKey()
        )

        return JwtSecurityTokenHandler.validateToken(
            parameters = validationParameters,
            token = token,
        )?.subject
    }

    private fun buildToken(user: User, expiration: Int, extraClaims: Map<String, Any> = mapOf()): String {
        val currentDate = Date(System.currentTimeMillis())
        val expirationDate = Date(System.currentTimeMillis() + expiration)
        return Jwts.builder()
            .id(user.id.toString())
            .claims(extraClaims)
            .subject(user.username)
            .issuedAt(currentDate) // Timestamp when the JWT was created.
            .expiration(expirationDate) // After this timestamp should not be used.
            .signWith(getSignInKey(), Jwts.SIG.HS256)
            .compact()
    }

    private fun getSignInKey(): SecretKey {
        val keyBytes: ByteArray = Decoders.BASE64.decode(EnvironmentVariables.Jwt.SECRET_KEY)
        return Keys.hmacShaKeyFor(keyBytes)
    }

    companion object {
        const val USERNAME = "username"
        const val GRANT_TYPE = "grantType"
        const val SECURITY_STAMP = "securityStamp"
        const val ACCESS_TOKEN = "access"
        const val REFRESH_TOKEN = "refresh"
    }
}

private class TokenValidationParameters(
    val validateUsername: Boolean = false,
    val username: String? = null,
    val validateSecurityStamp: Boolean = false,
    val securityStamp: String? = null,
    val validateLifetime: Boolean = true,
    val validateIssuerSigningKey: Boolean = true,
    val issuerSigningKey: SecretKey,
)

private object JwtSecurityTokenHandler {
    const val SECURITY_STAMP = "securityStamp"

    private val log: Logger = LoggerFactory.getLogger(JWTService::class.java)
    private var status: ValidationStatus = ValidationStatus.ClaimsValidation

    fun validateToken(
        parameters: TokenValidationParameters,
        token: String,
    ): Claims? {
        status = ValidationStatus.ClaimsValidation
        val claims = getAllClaims(token, parameters)
        claimsValidation(claims, parameters)

        val result = when (status) {
            is ValidationStatus.Expired -> null
            is ValidationStatus.InvalidUsername -> null
            is ValidationStatus.InvalidSecurityStamp -> null
            else -> claims
        }

        return result
    }

    private fun claimsValidation(claims: Claims, parameters: TokenValidationParameters) {
        var isExpired = false
        var isValidSecurityStamp = true
        var isValidUsername = true

        if (status !is ValidationStatus.ClaimsValidation) return
        if (parameters.validateLifetime) isExpired = claims.expiration.before(Date(System.currentTimeMillis()))
        if (parameters.validateSecurityStamp) isValidSecurityStamp = claims[SECURITY_STAMP] == parameters.securityStamp
        if (parameters.validateUsername) isValidUsername = claims.subject == parameters.username

        when {
            isExpired -> status = ValidationStatus.Expired
            !isValidSecurityStamp -> status = ValidationStatus.InvalidSecurityStamp
            !isValidUsername -> status = ValidationStatus.InvalidUsername
        }
    }

    private fun getAllClaims(token: String, parameters: TokenValidationParameters): Claims {
        val parserBuilder = Jwts.parser().apply {
            if (parameters.validateIssuerSigningKey) {
                verifyWith(parameters.issuerSigningKey)
            }
        }.build()

        return parserBuilder.parseSignedClaims(token).payload
    }

    private fun <T> getClaims(claims: Claims, claimsResolver: (Claims) -> T): T {
        try {
            return claimsResolver(claims)
        } catch (exception: Exception) {
            log.error(exception.message)
            throw AuthenticationCredentialsNotFoundException("Invalid jwt")
        }
    }

    private sealed class ValidationStatus {
        data object ClaimsValidation : ValidationStatus()
        data object Expired : ValidationStatus()
        data object InvalidUsername : ValidationStatus()
        data object InvalidSecurityStamp : ValidationStatus()
    }
}
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

    override fun isTokenExpired(token: String): Boolean {
        return getExpiration(token).before(Date(System.currentTimeMillis()))
    }

    override fun isTokenValid(token: String, username: String, securityStamp: String?): Boolean {
        val (subject, stamp) = getClaims(token) { claims ->
            Pair(claims.subject, claims[SECURITY_STAMP].toString())
        }
        val isValidUsername = username == subject
        val isValidSecurityStamp = securityStamp.equals(stamp)
        return isValidUsername && isValidSecurityStamp && !isTokenExpired(token)

    }

    override fun isRefreshTokenValid(token: String, username: String): Boolean {
        val (subject, grantType) = getClaims(token) { claims ->
            claims[GRANT_TYPE] as? String
            Pair(claims.subject, claims[GRANT_TYPE] as? String)
        }
        return subject.equals(username) && grantType.equals(REFRESH_TOKEN) && !isTokenExpired(token)
    }

    override fun getUsernameFromToken(token: String): String? {
        return getClaims(token, Claims::getSubject)
    }

    private fun buildToken(user: User, expiration: Int, extraClaims: Map<String, Any> = mapOf()): String {
        return Jwts.builder()
            .id(user.id.toString())
            .claims(extraClaims)
            .subject(user.username)
            .issuedAt(Date(System.currentTimeMillis()))
            .expiration(Date(System.currentTimeMillis() + expiration))
            .signWith(getSignInKey(), Jwts.SIG.HS256)
            .compact()
    }

    private fun getSignInKey(): SecretKey? {
        val keyBytes: ByteArray = Decoders.BASE64.decode(EnvironmentVariables.Jwt.SECRET_KEY)
        return Keys.hmacShaKeyFor(keyBytes)
    }

    private fun getAllClaims(token: String): Claims {
        return Jwts
            .parser()
            .verifyWith(getSignInKey())
            .build()
            .parseSignedClaims(token)
            .payload
    }

    private fun <T> getClaims(token: String, claimsResolver: (Claims) -> T): T {
        try {
            val claims = getAllClaims(token)
            val x = Claims::getSubject
            return claimsResolver(claims)
        } catch (exception: Exception) {
            log.error(exception.message)
            throw AuthenticationCredentialsNotFoundException("Invalid jwt")
        }
    }


    private fun getExpiration(token: String): Date {
        return getClaims(token, Claims::getExpiration)
    }

    companion object {
        const val USERNAME = "username"
        const val GRANT_TYPE = "grantType"
        const val SECURITY_STAMP = "securityStamp"
        const val ACCESS_TOKEN = "access"
        const val REFRESH_TOKEN = "refresh"
    }
}
package com.jorgereyesdev.spring_security.infrastructure.services

import com.jorgereyesdev.spring_security.config.Constants.Jwt
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
        val extraClaims = mapOf("username" to user.username)
        return buildToken(
            user,
            EnvironmentVariables.Jwt.EXPIRATION,
            extraClaims
        )
    }

    override fun generateRefreshToken(user: User): String {
        val extraClaims = mapOf(Jwt.GRANT_TYPE to Jwt.REFRESH_TOKEN)
        return buildToken(user, EnvironmentVariables.Jwt.REFRESH_EXPIRATION, extraClaims)
    }

    override fun isTokenValid(token: String, username: String): Boolean {
        val tokenUsername = getUsernameFromToken(token)
        return tokenUsername.equals(username) && !isTokenExpired(token)

    }

    override fun isRefreshTokenValid(token: String, username: String): Boolean {
        val (subject, grantType) = getClaims(token) { claims ->
            claims[Jwt.GRANT_TYPE] as? String
            Pair(claims.subject, claims[Jwt.GRANT_TYPE] as? String)
        }
        return subject.equals(username) && grantType.equals(Jwt.REFRESH_TOKEN) && !isTokenExpired(token)
    }

    override fun getUsernameFromToken(token: String): String? {
        return getClaims(token, Claims::getSubject)
    }

    fun <T> getClaims(token: String, claimsResolver: (Claims) -> T): T {
        try {
            val claims = getAllClaims(token)
            val x = Claims::getSubject
            return claimsResolver(claims)
        } catch (exception: Exception) {
            log.error(exception.message)
            throw AuthenticationCredentialsNotFoundException("Invalid jwt")
        }
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

    private fun getExpiration(token: String): Date {
        return getClaims(token, Claims::getExpiration)
    }

    private fun isTokenExpired(token: String): Boolean {
        return getExpiration(token).before(Date(System.currentTimeMillis()))
    }
}
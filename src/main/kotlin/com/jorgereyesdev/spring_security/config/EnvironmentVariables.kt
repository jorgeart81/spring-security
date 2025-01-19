package com.jorgereyesdev.spring_security.config

import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class EnvironmentVariables {
    @Value("\${spring.security.jwt.secret-key}")
    private lateinit var jwtSecretKey: String

    @Value("\${spring.security.jwt.expiration}")
    private var jwtExpiration: Int = 86400000 // a day

    @Value("\${spring.security.jwt.refresh-token.expiration}")
    private var jwtRefreshExpiration: Int = 604800000 // 7 days


    @PostConstruct
    private fun validateEnvironment() {
        if (jwtSecretKey.isBlank()) {
            throw IllegalArgumentException("JWT_SECRET_KEY must not be blank")
        }

        if (jwtExpiration < 0 || jwtRefreshExpiration < 0) {
            throw IllegalArgumentException("JWT_EXPIRATION and JWT_REFRESH_EXPIRATION must be greater than 0")
        }

        Jwt.initialize(secretKey = jwtSecretKey, expiration = jwtExpiration, refreshExpiration = jwtRefreshExpiration)
    }

    object Jwt {
        lateinit var SECRET_KEY: String
            private set
        var EXPIRATION: Int = 0
            private set
        var REFRESH_EXPIRATION: Int = 0
            private set

        internal fun initialize(secretKey: String, expiration: Int, refreshExpiration: Int) {
            this.SECRET_KEY = secretKey
            this.EXPIRATION = expiration
            this.REFRESH_EXPIRATION = refreshExpiration
        }
    }
}
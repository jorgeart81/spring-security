package com.jorgereyesdev.spring_security.config

import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class EnvironmentVariables {
    @Value("\${spring.security.jwt.secret-key}")
    private lateinit var _jwtSecretKey: String

    @Value("\${spring.security.jwt.expiration:86400000}")
    private var _jwtExpiration: Int = 86400000 // a day

    @Value("\${spring.security.jwt.refresh-token.expiration:604800000}")
    private var _jwtRefreshExpiration: Int = 604800000 // 7 days

    @Value("\${spring.cookie.domain}")
    private lateinit var _domain: String

    @PostConstruct
    private fun validateEnvironment() {
        if (_jwtSecretKey.isBlank()) {
            throw IllegalArgumentException("JWT_SECRET_KEY must not be blank")
        }

        if (_jwtExpiration < 0 || _jwtRefreshExpiration < 0) {
            throw IllegalArgumentException("JWT_EXPIRATION and JWT_REFRESH_EXPIRATION must be greater than 0")
        }

        Jwt.initialize(
            secretKey = _jwtSecretKey,
            expiration = _jwtExpiration,
            refreshExpiration = _jwtRefreshExpiration
        )

        Api.initialize(domain = _domain)
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

    object Api {
        lateinit var DOMAIN: String

        internal fun initialize(domain: String) {
            this.DOMAIN = domain

        }
    }
}
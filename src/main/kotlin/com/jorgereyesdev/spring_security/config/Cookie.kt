package com.jorgereyesdev.spring_security.config

import com.jorgereyesdev.spring_security.config.EnvironmentVariables.*
import jakarta.servlet.http.Cookie

object RefreshCookie {
    private const val NAME = Api.COOKIE_NAME
    private const val PATH = "/"
    private const val IS_HTTP_ONLY = true
    private const val IS_SECURE = true
    private var _domain = Api.DOMAIN
    private var _maxAge = Jwt.REFRESH_EXPIRATION / 1000

    fun build(value: String): Cookie {
        return Cookie(NAME, value).apply {
            path = PATH
            maxAge = _maxAge
            secure = IS_SECURE
            domain = _domain
            isHttpOnly = IS_HTTP_ONLY
        }
    }

    fun expired(): Cookie {
        return Cookie(NAME, null).apply {
            path = PATH
            maxAge = 0
            domain = _domain
            secure = IS_SECURE
            isHttpOnly = IS_HTTP_ONLY
        }
    }
}
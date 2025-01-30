package com.jorgereyesdev.spring_security.config.security

import jakarta.servlet.http.Cookie

object RefreshCookie {
    private lateinit var _name: String
    private lateinit var _path: String
    private lateinit var _domain: String
    private var _maxAge: Int = 0
    private val _isHttpOnly = true
    private val _isSecure = true

    fun build(value: String): Cookie {
        return Cookie(_name, value).apply {
            path = _path
            maxAge = _maxAge
            secure = _isSecure
            domain = _domain
            isHttpOnly = _isHttpOnly
        }
    }

    fun expired(): Cookie {
        return Cookie(_name, null).apply {
            path = _path
            maxAge = -1
            domain = _domain
            secure = _isSecure
            isHttpOnly = _isHttpOnly
        }
    }
}
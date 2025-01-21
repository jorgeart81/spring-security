package com.jorgereyesdev.spring_security.domain.services

import com.jorgereyesdev.spring_security.domain.models.User

interface JWTService {
    fun generateToken(user: User): String

    fun generateRefreshToken(user: User): String

    fun isTokenExpired(token: String): Boolean

    fun isTokenValid(token: String, username: String): Boolean

    fun isRefreshTokenValid(token: String, username: String): Boolean

    fun getUsernameFromToken(token: String): String?
}
package com.jorgereyesdev.spring_security.domain.services

import com.jorgereyesdev.spring_security.domain.models.User

interface JWTService {
    fun generateToken(user: User): String

    fun generateRefreshToken(user: User): String

    fun isTokenValid(token: String, username: String, securityStamp: String?): Boolean

    fun getUsernameFromToken(token: String): String?
}
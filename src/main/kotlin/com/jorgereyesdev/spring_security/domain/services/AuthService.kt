package com.jorgereyesdev.spring_security.domain.services

import com.jorgereyesdev.spring_security.domain.models.Token
import com.jorgereyesdev.spring_security.domain.models.User

interface AuthService {
//    suspend fun register(user: User): Result<User>
    fun register(user: User): User
    fun login(username: String, password: String): User
    fun refreshToken(authHeader: String): User
}
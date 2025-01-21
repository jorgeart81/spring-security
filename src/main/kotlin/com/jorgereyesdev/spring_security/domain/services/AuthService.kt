package com.jorgereyesdev.spring_security.domain.services

import com.jorgereyesdev.spring_security.domain.models.User

interface AuthService {
    fun register(user: User): User
    suspend fun register2(user: User): Result<User>
    fun login(username: String, password: String): User
    fun validateToken(authHeader: String?): Pair<User?, String>
}
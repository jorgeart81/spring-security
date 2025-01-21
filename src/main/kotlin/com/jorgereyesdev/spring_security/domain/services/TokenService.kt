package com.jorgereyesdev.spring_security.domain.services

import com.jorgereyesdev.spring_security.domain.models.Token
import com.jorgereyesdev.spring_security.domain.models.User

interface TokenService {
    fun saveToken(token: Token): Token
    suspend fun saveToken2(token: Token): Result<Token>
    fun revokeAllUserTokens(user: User): Unit
}

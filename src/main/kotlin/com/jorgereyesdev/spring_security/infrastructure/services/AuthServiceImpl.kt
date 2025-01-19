package com.jorgereyesdev.spring_security.infrastructure.services

import com.jorgereyesdev.spring_security.domain.models.Token
import com.jorgereyesdev.spring_security.domain.models.User
import com.jorgereyesdev.spring_security.domain.services.AuthService
import com.jorgereyesdev.spring_security.infrastructure.extensions.toDomain
import com.jorgereyesdev.spring_security.infrastructure.extensions.toEntity
import com.jorgereyesdev.spring_security.infrastructure.repositories.TokenRepository
import com.jorgereyesdev.spring_security.infrastructure.repositories.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthServiceImpl(
    val userRepository: UserRepository,
    val tokenRepository: TokenRepository,
    val passwordEncoder: PasswordEncoder
) : AuthService {
    override fun register(user: User): User {
        val userEntity = user.toEntity()
        userEntity.password = passwordEncoder.encode(user.password)

        return userRepository.save(userEntity).toDomain()
    }

    override fun login(username: String, password: String): User {
        TODO("Not yet implemented")
    }

    override fun refreshToken(authHeader: String): User {
        TODO("Not yet implemented")
    }

    override fun saveToken(token: Token): Int? {
        val tokenEntity = tokenRepository.save(token.toEntity())
        return tokenEntity.id
    }
}
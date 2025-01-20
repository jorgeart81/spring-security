package com.jorgereyesdev.spring_security.infrastructure.services

import com.jorgereyesdev.spring_security.domain.models.User
import com.jorgereyesdev.spring_security.domain.services.AuthService
import com.jorgereyesdev.spring_security.domain.services.TokenService
import com.jorgereyesdev.spring_security.infrastructure.extensions.toDomain
import com.jorgereyesdev.spring_security.infrastructure.extensions.toEntity
import com.jorgereyesdev.spring_security.infrastructure.repositories.UserRepository
import jakarta.transaction.Transactional
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthServiceImpl(
    val userRepository: UserRepository,
//    val tokenService: TokenService,
    val passwordEncoder: PasswordEncoder,
    val authenticationManager: AuthenticationManager,
) : AuthService {
    private val log: Logger = LoggerFactory.getLogger(AuthServiceImpl::class.java)

//    @Transactional
//    override suspend fun register(user: User): Result<User> {
//        return runCatching {
//            val userEntity = user.toEntity()
//            userEntity
//                .setSecurePassword(user.password, passwordEncoder)
//                .enable()
//
//            userRepository.save(userEntity).toDomain()
//        }.onFailure {
//            log.error(it.message)
//        }
//    }

    @Transactional
    override fun register(user: User): User {
        val userEntity = user.toEntity()
        userEntity
            .setSecurePassword(user.password, passwordEncoder)
            .enable()

        return userRepository.save(userEntity).toDomain()
    }

    @Transactional
    override fun login(username: String, password: String): User {
        authenticationManager.authenticate(UsernamePasswordAuthenticationToken(username, password))
        val user = userRepository.findByUsername(username) ?: throw NoSuchElementException("User not found")
        return user.toDomain()
    }

    @Transactional
    override fun refreshToken(authHeader: String): User {
        TODO("Not yet implemented")
    }

}
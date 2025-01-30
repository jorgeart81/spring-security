package com.jorgereyesdev.spring_security.infrastructure.services

import com.jorgereyesdev.spring_security.config.Constants
import com.jorgereyesdev.spring_security.domain.models.RoleName
import com.jorgereyesdev.spring_security.domain.models.User
import com.jorgereyesdev.spring_security.domain.services.AuthService
import com.jorgereyesdev.spring_security.domain.services.JWTService
import com.jorgereyesdev.spring_security.domain.services.RoleService
import com.jorgereyesdev.spring_security.infrastructure.extensions.toDomain
import com.jorgereyesdev.spring_security.infrastructure.extensions.toEntity
import com.jorgereyesdev.spring_security.infrastructure.repositories.UserRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthServiceImpl(
    val userRepository: UserRepository,
    val roleService: RoleService,
    val jwtService: JWTService,
    val passwordEncoder: PasswordEncoder,
    val authenticationManager: AuthenticationManager,
) : AuthService {
    private val log: Logger = LoggerFactory.getLogger(AuthServiceImpl::class.java)

    @Transactional
    override suspend fun register2(user: User): Result<User> {
        return runCatching {
            val userEntity = user.toEntity()

            roleService.findRoleByRoleName(RoleName.USER) {
                userEntity
                    .setSecurePassword(user.password, passwordEncoder)
                    .generateSecurityStamp()
                    .addRole(roleEntity = it)
                    .enable()
            }

            userRepository.save(userEntity).toDomain()
        }.onFailure {
            log.error(it.message)
        }
    }

    @Transactional
    override fun register(user: User): User {
        val userEntity = user.toEntity()

        roleService.findRoleByRoleName(RoleName.USER) {
            userEntity
                .setSecurePassword(user.password, passwordEncoder)
                .generateSecurityStamp()
                .addRole(roleEntity = it)
                .enable()
        }

        return userRepository.save(userEntity).toDomain()
    }

    @Transactional
    override fun login(username: String, password: String): User {
        authenticate(username, password)

        val user = userRepository.findByUsername(username) ?: throw NoSuchElementException("User not found")
        return user.toDomain()
    }

    @Transactional
    override fun validateToken(authHeader: String?): Pair<User?, String> {
        if (authHeader.isNullOrEmpty() || !authHeader.startsWith(Constants.BEARER)) {
            throw IllegalArgumentException("Invalid token")
        }

        val refreshToken = authHeader.substring(7)
        val username = jwtService.getUsernameFromToken(refreshToken) ?: throw IllegalArgumentException("Invalid token")

        require(jwtService.isRefreshTokenValid(refreshToken, username)) {
            "Invalid token"
        }

        val userEntity = userRepository.findByUsernameWithValidTokens(username)
        val isTokenAllowed = userEntity?.tokens?.any { it.token == refreshToken } ?: false

        if (!isTokenAllowed) throw IllegalArgumentException("Invalid token")

        val user = username.let { userRepository.findByUsername(it)?.toDomain() }

        return Pair(user, refreshToken)
    }

    private fun authenticate(username: String, password: String) {
        val authentication = authenticationManager.authenticate(UsernamePasswordAuthenticationToken(username, password))
        SecurityContextHolder.getContext().authentication = authentication
    }
}
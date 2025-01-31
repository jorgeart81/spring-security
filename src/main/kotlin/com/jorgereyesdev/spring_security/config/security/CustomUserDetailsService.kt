package com.jorgereyesdev.spring_security.config.security

import com.jorgereyesdev.spring_security.infrastructure.entities.UserEntity
import com.jorgereyesdev.spring_security.infrastructure.repositories.UserRepository
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CustomUserDetailsService(val userRepository: UserRepository) : UserDetailsService {
    override fun loadUserByUsername(username: String?): UserDetails {
        val user = getUser(username)

        val authorities: MutableSet<GrantedAuthority> = mutableSetOf()

        user.roles
            .asSequence()
            .map {
                authorities.add(SimpleGrantedAuthority("ROLE_${it.name}"))
                it
            }
            .flatMap { it.permissions.asSequence() }
            .forEach {  authorities.add(SimpleGrantedAuthority("${it?.name}")) }

        return CustomUserDetails.builder()
            .username(user.username)
            .password(user.password)
            .disabled(!user.enabled)
            .securityStamp(user.securityStamp)
            .authorities(authorities.toList())
            .build()
    }

    @Transactional
    private fun getUser(username: String?): UserEntity {
        return username?.let { userRepository.findByUsername(username) }
            ?: throw UsernameNotFoundException("User not found")
    }
}
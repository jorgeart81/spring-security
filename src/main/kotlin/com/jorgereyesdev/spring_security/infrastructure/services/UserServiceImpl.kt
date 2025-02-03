package com.jorgereyesdev.spring_security.infrastructure.services

import com.jorgereyesdev.spring_security.domain.models.User
import com.jorgereyesdev.spring_security.domain.services.UserService
import com.jorgereyesdev.spring_security.infrastructure.extensions.toDomain
import com.jorgereyesdev.spring_security.infrastructure.repositories.UserRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserServiceImpl(val userRepository: UserRepository) : UserService {

    @Transactional
    override fun findById(id: Long): User =
        userRepository.findByIdOrNull(id)?.toDomain() ?: throw Exception("User with id '${id}' not found")
}
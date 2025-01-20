package com.jorgereyesdev.spring_security.infrastructure.repositories

import com.jorgereyesdev.spring_security.infrastructure.entities.UserEntity
import org.springframework.data.repository.CrudRepository

interface UserRepository : CrudRepository<UserEntity, Long> {
    fun findByUsername(username: String): UserEntity?
    fun existsByUsername(username: String): Boolean
}
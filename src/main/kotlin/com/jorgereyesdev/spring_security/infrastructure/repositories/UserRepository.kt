package com.jorgereyesdev.spring_security.infrastructure.repositories

import com.jorgereyesdev.spring_security.infrastructure.entities.UserEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

interface UserRepository : CrudRepository<UserEntity, Long> {
    fun findByUsername(username: String): UserEntity?
    fun existsByUsername(username: String): Boolean

    @Query("SELECT u FROM UserEntity u JOIN FETCH u.tokens t WHERE u.username = :username AND t.expired = false AND t.revoked = false")
    fun findByUsernameWithValidTokens(@Param("username") username: String): UserEntity?
}
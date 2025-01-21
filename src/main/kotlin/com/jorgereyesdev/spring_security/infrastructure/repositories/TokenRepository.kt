package com.jorgereyesdev.spring_security.infrastructure.repositories

import com.jorgereyesdev.spring_security.infrastructure.entities.TokenEntity
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param

interface TokenRepository : CrudRepository<TokenEntity, Long> {
    fun findByToken(token: String): TokenEntity?

    @Query("SELECT t FROM TokenEntity t WHERE t.user.id = :userId AND t.expired = false AND t.revoked = false")
    fun findAllValidByUserId(@Param("userId") userId: Long): List<TokenEntity>

}
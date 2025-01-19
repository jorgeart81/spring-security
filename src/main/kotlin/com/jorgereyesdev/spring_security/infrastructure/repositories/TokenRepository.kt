package com.jorgereyesdev.spring_security.infrastructure.repositories

import com.jorgereyesdev.spring_security.infrastructure.entities.TokenEntity
import org.springframework.data.repository.CrudRepository

interface TokenRepository : CrudRepository<TokenEntity, Int> {
}
package com.jorgereyesdev.spring_security.infrastructure.repositories

import com.jorgereyesdev.spring_security.infrastructure.entities.UserClaimsEntity
import org.springframework.data.repository.CrudRepository

interface UserClaimsRepository:CrudRepository<UserClaimsEntity, Long> {
}
package com.jorgereyesdev.spring_security.infrastructure.repositories

import com.jorgereyesdev.spring_security.domain.models.RoleName
import com.jorgereyesdev.spring_security.infrastructure.entities.RoleEntity
import org.springframework.data.repository.CrudRepository

interface RoleRepository : CrudRepository<RoleEntity, Long> {
    fun findByName(name: RoleName): RoleEntity?
}

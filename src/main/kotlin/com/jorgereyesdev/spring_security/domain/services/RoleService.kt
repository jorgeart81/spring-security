package com.jorgereyesdev.spring_security.domain.services

import com.jorgereyesdev.spring_security.domain.models.RoleName
import com.jorgereyesdev.spring_security.infrastructure.entities.RoleEntity

interface RoleService {
    fun findRoleByRoleName(role: RoleName): RoleEntity
    fun findRoleByRoleName(role: RoleName, entity: (entity: RoleEntity) -> Unit)
}
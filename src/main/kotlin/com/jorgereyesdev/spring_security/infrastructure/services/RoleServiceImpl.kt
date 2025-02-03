package com.jorgereyesdev.spring_security.infrastructure.services

import com.jorgereyesdev.spring_security.domain.models.RoleName
import com.jorgereyesdev.spring_security.domain.services.RoleService
import com.jorgereyesdev.spring_security.infrastructure.entities.RoleEntity
import com.jorgereyesdev.spring_security.infrastructure.repositories.RoleRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RoleServiceImpl(val roleRepository: RoleRepository) : RoleService {

    @Transactional(readOnly = true)
    override fun findRoleByRoleName(role: RoleName): RoleEntity =
        roleRepository.findByName(role) ?: throw Exception("Role not found")

    @Transactional(readOnly = true)
    override fun findRoleByRoleName(role: RoleName, entity: (RoleEntity) -> Unit) {
        entity(findRoleByRoleName(role))
    }
}

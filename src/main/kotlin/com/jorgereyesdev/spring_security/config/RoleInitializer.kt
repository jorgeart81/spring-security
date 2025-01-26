package com.jorgereyesdev.spring_security.config

import com.jorgereyesdev.spring_security.domain.models.RoleName
import com.jorgereyesdev.spring_security.infrastructure.entities.RoleEntity
import com.jorgereyesdev.spring_security.infrastructure.repositories.RoleRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class RoleInitializer(val roleRepository: RoleRepository) : CommandLineRunner {
    private val defaultRoles = listOf(RoleName.ADMIN, RoleName.USER)

    override fun run(vararg args: String?) {
        val roleEntities = roleRepository.findAll()
        val roles: MutableList<RoleEntity> = mutableListOf()
        val addRole = { roleName: RoleName ->
            roles.add(
                RoleEntity(
                    name = roleName,
                )
            )
        }

        if (roleEntities.toList().isNotEmpty()) for (roleName in defaultRoles) {
            for (entity in roleEntities) {
                if (entity.name == roleName) continue
                addRole(roleName)
            }
        } else {
            for (roleName in defaultRoles) {
                addRole(roleName)
            }
        }

        if (roles.size > 0) roleRepository.saveAll(roles)
    }

}
package com.jorgereyesdev.spring_security.config

import com.jorgereyesdev.spring_security.domain.models.PermissionName
import com.jorgereyesdev.spring_security.domain.models.RoleName
import com.jorgereyesdev.spring_security.domain.models.isAllowedTo
import com.jorgereyesdev.spring_security.infrastructure.entities.PermissionEntity
import com.jorgereyesdev.spring_security.infrastructure.entities.RoleEntity
import com.jorgereyesdev.spring_security.infrastructure.repositories.PermissionRepository
import com.jorgereyesdev.spring_security.infrastructure.repositories.RoleRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class RoleInitializer(val roleRepository: RoleRepository, val permissionRepository: PermissionRepository) :
    CommandLineRunner {

    private val defaultRoles = listOf(RoleName.ADMIN, RoleName.USER)
    private val defaultPermissions =
        listOf(PermissionName.CREATE, PermissionName.UPDATE, PermissionName.READ, PermissionName.DELETE)

    @Transactional
    override fun run(vararg args: String?) {
        var permissionDB = permissionRepository.findAll()
        val rolesDB = roleRepository.findAll()

        val permissionsByRoleName = { roleName: RoleName ->
            roleName.isAllowedTo().map { permissionName ->
                permissionDB.find { it.name == permissionName }
            }.toMutableList()
        }

        if (permissionDB.toList().isNotEmpty() || rolesDB.toList().isNotEmpty()) return

        val permissions = defaultPermissions.map { PermissionEntity(name = it) }
        permissionDB = permissionRepository.saveAll(permissions)

        val roles = defaultRoles.map { roleName ->
            RoleEntity(
                name = roleName,
                permissions = permissionsByRoleName(roleName)
            )
        }
        roleRepository.saveAll(roles)


    }
}
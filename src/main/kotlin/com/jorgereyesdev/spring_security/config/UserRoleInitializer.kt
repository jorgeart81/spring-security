package com.jorgereyesdev.spring_security.config

import com.jorgereyesdev.spring_security.domain.models.PermissionName
import com.jorgereyesdev.spring_security.domain.models.RoleName
import com.jorgereyesdev.spring_security.domain.models.isAllowedTo
import com.jorgereyesdev.spring_security.infrastructure.entities.PermissionEntity
import com.jorgereyesdev.spring_security.infrastructure.entities.RoleEntity
import com.jorgereyesdev.spring_security.infrastructure.entities.UserEntity
import com.jorgereyesdev.spring_security.infrastructure.repositories.PermissionRepository
import com.jorgereyesdev.spring_security.infrastructure.repositories.RoleRepository
import com.jorgereyesdev.spring_security.infrastructure.repositories.UserRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class UserRoleInitializer(
    val roleRepository: RoleRepository,
    val permissionRepository: PermissionRepository,
    val userRepository: UserRepository,
    val passwordEncoder: PasswordEncoder,
) : CommandLineRunner {

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
                name = roleName, permissions = permissionsByRoleName(roleName)
            )
        }

        val rolesEntities = roleRepository.saveAll(roles)
        rolesEntities.asSequence().forEach {
            if (it.name == RoleName.ADMIN) {
                createFirstAdmin(it)
            }
        }
    }

    private fun createFirstAdmin(role: RoleEntity) {
        val user = UserEntity(
            username = "admin",
            password = "",
            securityStamp = "",
            accountNonExpired = true,
            accountNonLocked = true,
            credentialsNonExpired = true,
            enabled = true,
            roles = mutableListOf(role)
        )

        user.setSecurePassword("Admin1234", passwordEncoder)
            .generateSecurityStamp()

        userRepository.save(user)
    }
}
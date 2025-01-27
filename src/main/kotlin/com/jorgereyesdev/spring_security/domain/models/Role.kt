package com.jorgereyesdev.spring_security.domain.models

data class Role(
    var id: Long? = null,
    var name: RoleName,
    var users: MutableList<User> = mutableListOf(),
    var permissions: MutableSet<Permission> = hashSetOf(),
)

enum class RoleName {
    ADMIN,
    USER,
}

fun RoleName.isAllowedTo(permissions: (HashSet<PermissionName>) -> Unit = {}): HashSet<PermissionName> {
    val allowedPermissions = when (this) {
        RoleName.ADMIN ->
            hashSetOf(
                PermissionName.CREATE,
                PermissionName.UPDATE,
                PermissionName.READ,
                PermissionName.DELETE
            )

        RoleName.USER ->
            hashSetOf(
                PermissionName.CREATE,
                PermissionName.READ,
            )
    }

    permissions(allowedPermissions)
    return allowedPermissions
}

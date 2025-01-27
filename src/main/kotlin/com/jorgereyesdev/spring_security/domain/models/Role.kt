package com.jorgereyesdev.spring_security.domain.models

data class Role(
    var id: Long? = null,
    var name: RoleName,
    var users: MutableList<User> = mutableListOf(),
    var permissions: Set<Permission> = hashSetOf(),
)

enum class RoleName {
    ADMIN,
    USER,
}

fun RoleName.isAllowedTo(permissions: (HashSet<PermissionName>) -> Unit) {
    when (this) {
        RoleName.ADMIN -> permissions(
            hashSetOf(
                PermissionName.CREATE,
                PermissionName.UPDATE,
                PermissionName.READ,
                PermissionName.DELETE
            )
        )

        RoleName.USER -> permissions(
            hashSetOf(
                PermissionName.CREATE,
                PermissionName.READ,
            )
        )
    }
}

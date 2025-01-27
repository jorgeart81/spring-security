package com.jorgereyesdev.spring_security.domain.models

data class Permission(
    val id: Long? = null,
    val name: PermissionName,
)

enum class PermissionName {
    CREATE,
    UPDATE,
    READ,
    DELETE,
}
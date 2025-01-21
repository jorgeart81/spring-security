package com.jorgereyesdev.spring_security.domain.models

data class Role(
    val id: Long? = null,
    val name: RoleName,
)

enum class RoleName {
    ADMIN,
    USER,
}

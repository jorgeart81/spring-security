package com.jorgereyesdev.spring_security.domain.models

data class Role(
    val id: Long? = null,
    val name: RoleName,
    val users: MutableList<User> = mutableListOf()
)

enum class RoleName {
    ADMIN,
    USER,
}

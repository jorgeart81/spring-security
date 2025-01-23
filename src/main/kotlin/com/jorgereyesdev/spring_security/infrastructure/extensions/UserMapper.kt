package com.jorgereyesdev.spring_security.infrastructure.extensions

import com.jorgereyesdev.spring_security.domain.models.User
import com.jorgereyesdev.spring_security.infrastructure.entities.UserEntity

fun User.toEntity(): UserEntity {
    val userEntity = UserEntity(
        username = this.username,
        password = this.password,
        enabled = this.enable,
        tokens = this.tokens?.map { it.toEntity() }?.toMutableList(),
        roles = this.roles?.map { it.toEntity() }?.toMutableList()
    )

    if (this.id != null) userEntity.id = this.id

    return userEntity
}

fun UserEntity.toDomain() = User(
    id = this.id,
    username = this.username,
    password = this.password,
    enable = this.enabled,
    tokens = mutableListOf(),
    roles = this.roles?.map { it.toDomain() }?.toMutableList()
)
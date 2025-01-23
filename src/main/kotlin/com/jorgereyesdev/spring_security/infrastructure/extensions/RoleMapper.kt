package com.jorgereyesdev.spring_security.infrastructure.extensions

import com.jorgereyesdev.spring_security.domain.models.Role
import com.jorgereyesdev.spring_security.infrastructure.entities.RoleEntity

fun Role.toEntity(): RoleEntity {
    val roleEntity = RoleEntity(
        name = this.name
    )

    if (this.id != null) roleEntity.id = this.id

    return roleEntity
}

fun RoleEntity.toDomain() = Role(
    id = this.id,
    name = this.name
)
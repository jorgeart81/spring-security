package com.jorgereyesdev.spring_security.infrastructure.extensions

import com.jorgereyesdev.spring_security.domain.models.Role
import com.jorgereyesdev.spring_security.domain.models.User
import com.jorgereyesdev.spring_security.infrastructure.entities.RoleEntity
import com.jorgereyesdev.spring_security.infrastructure.entities.UserEntity

fun Role.toEntity(): RoleEntity {
    val roleEntity = RoleEntity(
        name = this.name,
        users = this.users.map {
            UserEntity(
                id = it.id,
                username = it.username,
                password = it.password,
                securityStamp = it.securityStamp,
                accountNonExpired = it.accountNonExpired,
                accountNonLocked = it.accountNonLocked,
                credentialsNonExpired = it.credentialsNonExpired,
                enabled = it.enabled,
            )
        }.toMutableList()
    )

    if (this.id != null) roleEntity.id = this.id

    return roleEntity
}

fun RoleEntity.toDomain() = Role(
    id = this.id,
    name = this.name,
    users = this.users.map {
        User(
            id = it.id,
            username = it.username,
            password = it.password,
            securityStamp = it.securityStamp,
            accountNonExpired = it.accountNonExpired,
            accountNonLocked = it.accountNonLocked,
            credentialsNonExpired = it.credentialsNonExpired,
            enabled = it.enabled,
        )
    }.toMutableList()
)
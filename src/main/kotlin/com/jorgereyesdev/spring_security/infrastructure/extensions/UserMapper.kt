package com.jorgereyesdev.spring_security.infrastructure.extensions

import com.jorgereyesdev.spring_security.domain.models.Role
import com.jorgereyesdev.spring_security.domain.models.Token
import com.jorgereyesdev.spring_security.domain.models.User
import com.jorgereyesdev.spring_security.infrastructure.entities.RoleEntity
import com.jorgereyesdev.spring_security.infrastructure.entities.TokenEntity
import com.jorgereyesdev.spring_security.infrastructure.entities.UserEntity

fun User.toEntity(): UserEntity {
    val userEntity = UserEntity(
        username = this.username,
        password = this.password,
        accountNonExpired = this.accountNonExpired,
        accountNonLocked = this.accountNonLocked,
        credentialsNonExpired = this.credentialsNonExpired,
        enabled = this.enabled,
        tokens = this.tokens.map {
            TokenEntity(
                id = it.id,
                token = it.token,
                tokenType = it.tokenType,
                grantType = it.grantType,
                revoked = it.revoked,
                expired = it.expired,
            )
        }.toMutableList(),
        roles = this.roles.map {
            RoleEntity(
                id = it.id,
                name = it.name,
            )
        }.toMutableList()
    )

    if (this.id != null) userEntity.id = this.id

    return userEntity
}

fun UserEntity.toDomain() = User(
    id = this.id,
    username = this.username,
    password = this.password,
    accountNonExpired = this.accountNonExpired,
    accountNonLocked = this.accountNonLocked,
    credentialsNonExpired = this.credentialsNonExpired,
    enabled = this.enabled,
    tokens = this.tokens.map {
        Token(
            id = it.id,
            token = it.token,
            tokenType = it.tokenType,
            grantType = it.grantType,
            revoked = it.revoked,
            expired = it.expired,
        )
    }.toMutableList(),
    roles = this.roles.map {
        Role(
            id = it.id,
            name = it.name,
        )
    }.toMutableList()
)

fun UserEntity.toDomainWithoutTokens() = User(
    id = this.id,
    username = this.username,
    password = this.password,
    accountNonExpired = this.accountNonExpired,
    accountNonLocked = this.accountNonLocked,
    credentialsNonExpired = this.credentialsNonExpired,
    enabled = this.enabled,
    roles = this.roles.map {
        Role(
            id = it.id,
            name = it.name,
        )
    }.toMutableList()
)
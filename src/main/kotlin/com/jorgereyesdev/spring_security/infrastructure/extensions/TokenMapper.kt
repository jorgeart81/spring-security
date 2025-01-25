package com.jorgereyesdev.spring_security.infrastructure.extensions

import com.jorgereyesdev.spring_security.domain.models.Token
import com.jorgereyesdev.spring_security.domain.models.User
import com.jorgereyesdev.spring_security.infrastructure.entities.TokenEntity
import com.jorgereyesdev.spring_security.infrastructure.entities.UserEntity

fun Token.toEntity(): TokenEntity {
    val tokenEntity = TokenEntity(
        token = this.token,
        grantType = this.grantType,
        revoked = this.revoked,
        expired = this.expired,
        tokenType = this.tokenType,
        user = this.user?.toEntity()
    )

    if (this.id != null) tokenEntity.id = this.id

    return tokenEntity
}

fun TokenEntity.toTDomain() = Token(
    id = this.id,
    token = this.token,
    tokenType = this.tokenType,
    grantType = this.grantType,
    revoked = this.revoked,
    expired = this.expired,
    user = this.user?.toDomain(),
)

fun TokenEntity.toTDomain(userMapper: (UserEntity) -> User) = Token(
    id = this.id,
    token = this.token,
    tokenType = this.tokenType,
    grantType = this.grantType,
    revoked = this.revoked,
    expired = this.expired,
    user = this.user?.let { userMapper(it) }
)
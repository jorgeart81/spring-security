package com.jorgereyesdev.spring_security.infrastructure.extensions

import com.jorgereyesdev.spring_security.domain.models.Token
import com.jorgereyesdev.spring_security.infrastructure.entities.TokenEntity

fun Token.toEntity(): TokenEntity {
    val tokenEntity = TokenEntity(
        token = this.token,
        grantType = this.grantType,
        revoked = this.revoked,
        expired = this.expired,
        user = this.user.toEntity(),
    )

    if (this.id != null) tokenEntity.id = this.id
    if (this.tokenType != null) tokenEntity.tokenType = this.tokenType

    return tokenEntity
}

fun TokenEntity.toTDomain() = Token(
    id = this.id,
    token = this.token,
    tokenType = this.tokenType,
    grantType = this.grantType,
    revoked = this.revoked ?: false,
    expired = this.expired ?: false,
    user = this.user.toDomain(),
)
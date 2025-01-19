package com.jorgereyesdev.spring_security.domain.models

data class Token(
    var id: Int? = null,
    var token: String,
    var tokenType: TokenType? = null,
    var revoked: Boolean? = null,
    var expired: Boolean? = null,
    var user: User,
)

enum class TokenType {
    BEARER
}
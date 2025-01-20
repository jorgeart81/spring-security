package com.jorgereyesdev.spring_security.domain.models

data class Token(
    var id: Long? = null,
    var token: String,
    var tokenType: TokenType? = null,
    var revoked: Boolean,
    var expired: Boolean,
    var user: User,
)

enum class TokenType {
    BEARER
}
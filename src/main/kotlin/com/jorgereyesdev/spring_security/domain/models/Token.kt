package com.jorgereyesdev.spring_security.domain.models

data class Token(
    var id: Long? = null,
    var token: String,
    var tokenType: TokenType? = null,
    var grantType: GrantType,
    var revoked: Boolean,
    var expired: Boolean,
    var user: User? = null,
)

enum class TokenType {
    BEARER
}

enum class GrantType {
    ACCESS,
    REFRESH
}
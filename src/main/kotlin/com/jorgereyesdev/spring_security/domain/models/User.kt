package com.jorgereyesdev.spring_security.domain.models

data class User(
    var id: Long? = null,
    var username: String,
    var password: String,
    var securityStamp: String,
    var accountNonExpired: Boolean,
    var accountNonLocked: Boolean,
    var credentialsNonExpired: Boolean,
    var enabled: Boolean,
    var tokens: MutableList<Token> = mutableListOf(),
    var roles: MutableSet<Role> = mutableSetOf(),
)

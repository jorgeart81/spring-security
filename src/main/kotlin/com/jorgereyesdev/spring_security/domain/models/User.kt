package com.jorgereyesdev.spring_security.domain.models

import com.jorgereyesdev.spring_security.infrastructure.entities.TokenEntity

data class User(
    var id: Int? = null,
    var username: String,
    var password: String,
    var enable: Boolean,
    var tokens: MutableList<Token>? = null,
)

package com.jorgereyesdev.spring_security.presentantion.request

import com.jorgereyesdev.spring_security.domain.models.User
import com.jorgereyesdev.spring_security.infrastructure.extensions.toEntity
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class RegisterRequest(
    @NotBlank
    @Size(min = 3, max = 25)
    var username: String,

    @NotBlank
    @Size(min = 6, max = 50)
    var password: String,
) {
    fun clean() {
        this.username = ""
        this.password = ""
    }
}

fun RegisterRequest.toDomain() = User(
    username = this.username,
    password = this.password,
    securityStamp = "",
    accountNonExpired = true,
    accountNonLocked = true,
    credentialsNonExpired = true,
    enabled = true,
)
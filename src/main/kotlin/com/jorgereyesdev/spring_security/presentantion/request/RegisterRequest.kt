package com.jorgereyesdev.spring_security.presentantion.request

import com.jorgereyesdev.spring_security.domain.models.User
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class RegisterRequest(
    @NotBlank
    @Size(min = 3, max = 25)
    val username: String,

    @NotBlank
    @Size(min = 6, max = 50)
    val password: String,
)

fun RegisterRequest.toDomain() = User(
    username = this.username,
    password = this.password,
    enabled = true,
)
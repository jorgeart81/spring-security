package com.jorgereyesdev.spring_security.presentantion.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class LoginRequest(
    @NotBlank
    @Size(min = 3, max = 25)
    val username: String,

    @NotBlank
    @Size(min = 6, max = 50)
    val password: String,
)
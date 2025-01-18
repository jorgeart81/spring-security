package com.jorgereyesdev.spring_security.presentantion.dtos

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class UserResponseDTO(
    val id: Long?,
    val username: String,
    val enabled: Boolean = false,
)
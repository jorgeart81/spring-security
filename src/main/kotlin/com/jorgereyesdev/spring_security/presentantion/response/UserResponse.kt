package com.jorgereyesdev.spring_security.presentantion.response

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class UserResponse(
    val id: Long?,
    val username: String,
    val enabled: Boolean,
)
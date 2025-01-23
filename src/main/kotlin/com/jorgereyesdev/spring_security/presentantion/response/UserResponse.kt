package com.jorgereyesdev.spring_security.presentantion.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.jorgereyesdev.spring_security.domain.models.Role

@JsonInclude(JsonInclude.Include.NON_NULL)
data class UserResponse(
    val id: Long?,
    val username: String,
    val enabled: Boolean,
    val roles: MutableList<Role>?
)
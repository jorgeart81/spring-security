package com.jorgereyesdev.spring_security.presentantion.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.jorgereyesdev.spring_security.domain.models.Permission
import com.jorgereyesdev.spring_security.domain.models.Role
import com.jorgereyesdev.spring_security.domain.models.RoleName
import com.jorgereyesdev.spring_security.domain.models.User

@JsonInclude(JsonInclude.Include.NON_NULL)
data class UserResponse(
    val id: Long?,
    val username: String,
    val enabled: Boolean,
    val roles: MutableList<RoleResponse>?
)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class RoleResponse(
    val id: Long?,
    val name: RoleName,
)
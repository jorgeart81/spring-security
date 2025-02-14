package com.jorgereyesdev.spring_security.presentantion.extensions

import com.jorgereyesdev.spring_security.domain.models.User
import com.jorgereyesdev.spring_security.presentantion.response.RoleResponse
import com.jorgereyesdev.spring_security.presentantion.response.UserResponse

fun User.toUserResponse() = UserResponse(
    id = this.id,
    username = this.username,
    enabled = this.enabled,
    roles = this.roles.map {
        RoleResponse(
            id = it.id,
            name = it.name,
        )
    }.toMutableList(),
)
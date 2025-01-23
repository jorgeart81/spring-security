package com.jorgereyesdev.spring_security.presentantion.controllers

import com.jorgereyesdev.spring_security.config.Constants.Routes
import com.jorgereyesdev.spring_security.domain.models.Role
import com.jorgereyesdev.spring_security.domain.models.RoleName
import com.jorgereyesdev.spring_security.presentantion.response.UserResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(Routes.USERS)
class UserController {

    @GetMapping
    fun getUsers(): List<UserResponse> {
        return listOf(
            UserResponse(id = 1, "Fernando", true, mutableListOf(Role(id = 1, name = RoleName.USER))),
            UserResponse(id = 1, "Armando", true, mutableListOf(Role(id = 1, name = RoleName.USER))),
            UserResponse(id = 1, "Miguel Angel", true, mutableListOf(Role(id = 1, name = RoleName.USER)))
        )
    }
}
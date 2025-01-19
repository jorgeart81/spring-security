package com.jorgereyesdev.spring_security.presentantion.controllers

import com.jorgereyesdev.spring_security.config.Constants
import com.jorgereyesdev.spring_security.presentantion.response.UserResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("${Constants.Companion.API}/users")
class UserController {

    @GetMapping
    fun getUsers(): List<UserResponse> {
        return listOf(
            UserResponse(id = 1, "Fernando", true),
            UserResponse(id = 1, "Armando", true),
            UserResponse(id = 1, "Miguel Angel", true)
        )
    }
}
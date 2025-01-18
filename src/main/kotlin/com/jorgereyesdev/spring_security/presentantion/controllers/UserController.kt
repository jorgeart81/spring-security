package com.jorgereyesdev.spring_security.presentantion.controllers

import com.jorgereyesdev.spring_security.config.Constants
import com.jorgereyesdev.spring_security.presentantion.dtos.UserResponseDTO
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("${Constants.Companion.API}/users")
class UserController {

    @GetMapping
    fun getUsers(): List<UserResponseDTO> {
        return listOf(
            UserResponseDTO(id = 1, "Fernando"),
            UserResponseDTO(id = 1, "Armando"),
            UserResponseDTO(id = 1, "Miguel Angel")
        )
    }
}
package com.jorgereyesdev.spring_security.presentantion.controllers

import com.jorgereyesdev.spring_security.config.Constants.Routes
import com.jorgereyesdev.spring_security.domain.models.RoleName
import com.jorgereyesdev.spring_security.domain.services.UserService
import com.jorgereyesdev.spring_security.presentantion.extensions.toUserResponse
import com.jorgereyesdev.spring_security.presentantion.response.ApiResponse
import com.jorgereyesdev.spring_security.presentantion.response.RoleResponse
import com.jorgereyesdev.spring_security.presentantion.response.UserResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(Routes.USERS)
class UserController(val userService: UserService) {

    @GetMapping
    fun getUsers(): List<UserResponse> {
        return listOf(
            UserResponse(id = 1, "Fernando", true, mutableListOf(RoleResponse(id = 1, name = RoleName.USER))),
            UserResponse(id = 1, "Armando", true, mutableListOf(RoleResponse(id = 1, name = RoleName.USER))),
            UserResponse(id = 1, "Miguel Angel", true, mutableListOf(RoleResponse(id = 1, name = RoleName.USER)))
        )
    }

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long): ResponseEntity<ApiResponse<UserResponse>> {
        val user = userService.findById(id)

        return ResponseEntity.ok(ApiResponse.Success(data = user.toUserResponse()))
    }
}
package com.jorgereyesdev.spring_security.presentantion.controllers

import com.jorgereyesdev.spring_security.config.Constants
import com.jorgereyesdev.spring_security.domain.models.Token
import com.jorgereyesdev.spring_security.domain.services.AuthService
import com.jorgereyesdev.spring_security.domain.services.JWTService
import com.jorgereyesdev.spring_security.presentantion.extensions.toUserResponse
import com.jorgereyesdev.spring_security.presentantion.request.LoginRequest
import com.jorgereyesdev.spring_security.presentantion.request.RegisterRequest
import com.jorgereyesdev.spring_security.presentantion.request.toDomain
import com.jorgereyesdev.spring_security.presentantion.response.ApiResponse
import com.jorgereyesdev.spring_security.presentantion.response.UserResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

@RestController
@RequestMapping("${Constants.Companion.API}/auth")
class AuthController(val authService: AuthService, val jwtService: JWTService) {

    @PostMapping("/register")
    fun register(@RequestBody registerRequest: RegisterRequest): ResponseEntity<ApiResponse<Nothing>> {
        val newUser = authService.register(registerRequest.toDomain())
        val location =
            ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(newUser.id)
                .toUri()
        val accessToken = jwtService.generateToken(newUser)
        val refreshToken = jwtService.generateRefreshToken(newUser)

        val tokenId = authService.saveToken(
            Token(
                token = accessToken,
                user = newUser
            )
        )

        return ResponseEntity.created(location)
            .body(
                ApiResponse.Success(
                    accessToken = accessToken,
                    refreshToken = refreshToken
                )
            )
    }

    @PostMapping("/login")
    fun login(@RequestBody loginRequest: LoginRequest): ResponseEntity<ApiResponse<UserResponse>> {
        val user = authService.login(username = loginRequest.username, password = loginRequest.password)
        val accessToken = jwtService.generateToken(user)
        val refreshToken = jwtService.generateRefreshToken(user)

        return ResponseEntity.ok(
            ApiResponse.Success(
                data = user.toUserResponse(),
                accessToken = accessToken,
                refreshToken = refreshToken
            )
        )
    }

    @PostMapping("/refresh")
    fun refresh(@RequestHeader(HttpHeaders.AUTHORIZATION) authHeader: String): ResponseEntity<ApiResponse<Nothing>> {
        val user = authService.refreshToken(authHeader)
        val accessToken = jwtService.generateToken(user)
        val refreshToken = jwtService.generateRefreshToken(user)

        return ResponseEntity.ok(
            ApiResponse.Success(
                accessToken = accessToken,
                refreshToken = refreshToken
            )
        )
    }

}
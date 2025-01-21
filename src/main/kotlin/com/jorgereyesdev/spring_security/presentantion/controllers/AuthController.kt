package com.jorgereyesdev.spring_security.presentantion.controllers

import com.jorgereyesdev.spring_security.config.Constants.Routes
import com.jorgereyesdev.spring_security.domain.models.Token
import com.jorgereyesdev.spring_security.domain.services.AuthService
import com.jorgereyesdev.spring_security.domain.services.JWTService
import com.jorgereyesdev.spring_security.domain.services.TokenService
import com.jorgereyesdev.spring_security.presentantion.extensions.toUserResponse
import com.jorgereyesdev.spring_security.presentantion.request.LoginRequest
import com.jorgereyesdev.spring_security.presentantion.request.RegisterRequest
import com.jorgereyesdev.spring_security.presentantion.request.toDomain
import com.jorgereyesdev.spring_security.presentantion.response.ApiResponse
import com.jorgereyesdev.spring_security.presentantion.response.UserResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

@RestController
@RequestMapping(Routes.AUTH)
class AuthController(val authService: AuthService, val tokenService: TokenService, val jwtService: JWTService) {

    @PostMapping("/register")
    fun register(@RequestBody registerRequest: RegisterRequest): ResponseEntity<ApiResponse<Nothing>> {
        val newUser = authService.register(registerRequest.toDomain())
        val location =
            ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(newUser.id)
                .toUri()
        val accessToken = jwtService.generateToken(newUser)
        val refreshToken = jwtService.generateRefreshToken(newUser)

        val tokenId = tokenService.saveToken(
            Token(
                token = accessToken,
                user = newUser,
                revoked = false,
                expired = false
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

        tokenService.revokeAllUserTokens(user)
        tokenService.saveToken(
            Token(
                token = accessToken,
                user = user,
                revoked = false,
                expired = false
            )
        )

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
        val (user, refreshToken) = authService.validateToken(authHeader)
        if (user == null) return ResponseEntity.badRequest().build()
        val accessToken = jwtService.generateToken(user)

        tokenService.revokeAllUserTokens(user)
        tokenService.saveToken(
            Token(
                token = accessToken,
                user = user,
                revoked = false,
                expired = false
            )
        )

        return ResponseEntity.ok(
            ApiResponse.Success(
                accessToken = accessToken,
                refreshToken = refreshToken
            )
        )
    }

    private fun <T> errorResponse(exception: Throwable): ResponseEntity<ApiResponse<T>> {
        return when (exception) {
            is IllegalArgumentException -> ResponseEntity.status(HttpStatus.CONFLICT)
                .body(
                    ApiResponse.Error(
                        HttpStatus.CONFLICT,
                        exception.message ?: "Unexpected error occurred"
                    )
                )

            is RuntimeException -> ResponseEntity.notFound().build()
            else -> ResponseEntity.internalServerError().build()
        }
    }

    @PostMapping("/register2")
    suspend fun register2(@RequestBody registerRequest: RegisterRequest): ResponseEntity<ApiResponse<UserResponse>> {
        authService.register2(registerRequest.toDomain()).mapCatching { createdUser ->
            val accessToken = jwtService.generateToken(createdUser)
            val refreshToken = jwtService.generateRefreshToken(createdUser)

            val token = tokenService.saveToken(
                Token(
                    token = accessToken,
                    user = createdUser,
                    revoked = false,
                    expired = false
                )
            )

            Pair(createdUser, accessToken to refreshToken)
        }.fold(
            onSuccess = { (createdUser, tokens) ->
                val location =
                    ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(createdUser.id)
                        .toUri()
                val (accessToken, refreshToken) = tokens

                return ResponseEntity.created(location).body(
                    ApiResponse.Success(
                        accessToken = accessToken,
                        refreshToken = refreshToken
                    )
                )
            },
            onFailure = { exception ->
                return errorResponse(exception)
            }
        )
    }

}
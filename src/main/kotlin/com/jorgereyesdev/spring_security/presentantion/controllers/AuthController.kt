package com.jorgereyesdev.spring_security.presentantion.controllers

import com.jorgereyesdev.spring_security.config.Constants
import com.jorgereyesdev.spring_security.config.Constants.Routes
import com.jorgereyesdev.spring_security.config.EnvironmentVariables.Api
import com.jorgereyesdev.spring_security.config.RefreshCookie
import com.jorgereyesdev.spring_security.domain.models.GrantType
import com.jorgereyesdev.spring_security.domain.models.Token
import com.jorgereyesdev.spring_security.domain.models.TokenType
import com.jorgereyesdev.spring_security.domain.models.User
import com.jorgereyesdev.spring_security.domain.services.AuthService
import com.jorgereyesdev.spring_security.domain.services.JWTService
import com.jorgereyesdev.spring_security.domain.services.TokenService
import com.jorgereyesdev.spring_security.presentantion.extensions.toUserResponse
import com.jorgereyesdev.spring_security.presentantion.request.LoginRequest
import com.jorgereyesdev.spring_security.presentantion.request.RegisterRequest
import com.jorgereyesdev.spring_security.presentantion.request.toDomain
import com.jorgereyesdev.spring_security.presentantion.response.ApiResponse
import com.jorgereyesdev.spring_security.presentantion.response.UserResponse
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder

@RestController
@RequestMapping(Routes.AUTH)
class AuthController(val authService: AuthService, val tokenService: TokenService, val jwtService: JWTService) {

    @PostMapping("/register")
    fun register(
        @RequestBody registerRequest: RegisterRequest,
        response: HttpServletResponse
    ): ResponseEntity<ApiResponse<Nothing>> {
        val newUser = authService.register(registerRequest.toDomain())
        val location =
            ServletUriComponentsBuilder.fromCurrentContextPath().path("/${Routes.USERS}/{id}").buildAndExpand(newUser.id)
                .toUri()

        val (accessToken, refreshToken) = saveAccessAndRefreshToken(newUser)

        registerRequest.clean()
        addHeaderToken(response, accessToken)
        response.addCookie(RefreshCookie.build(refreshToken))
        return ResponseEntity.created(location)
            .body(
                ApiResponse.Success(
                    accessToken = accessToken,
                )
            )
    }

    @PostMapping("/login")
    fun login(
        @RequestBody loginRequest: LoginRequest,
        response: HttpServletResponse
    ): ResponseEntity<ApiResponse<UserResponse>> {
        val user = authService.login(username = loginRequest.username, password = loginRequest.password)

        tokenService.revokeAllUserValidTokens(user)

        val (accessToken, refreshToken) = saveAccessAndRefreshToken(user)

        loginRequest.clean()
        addHeaderToken(response, accessToken)
        response.addCookie(RefreshCookie.build(refreshToken))
        return ResponseEntity.ok(
            ApiResponse.Success(
                data = user.toUserResponse(),
                accessToken = accessToken,
            )
        )
    }

    @PostMapping("/refresh")
    fun refresh(
        @CookieValue(Api.COOKIE_NAME) refreshTokenCookie: String?,
        @RequestHeader(HttpHeaders.AUTHORIZATION) authHeader: String?,
        response: HttpServletResponse
    ): ResponseEntity<ApiResponse<Nothing>> {
        var refreshToken = refreshTokenCookie

        if (refreshToken.isNullOrBlank()) {
            if (authHeader.isNullOrEmpty() || !authHeader.startsWith(Constants.BEARER)) {
                return ResponseEntity.badRequest().build()
            }
            refreshToken = authHeader.substring(7)
        }

        val user = authService.validateToken(refreshToken) ?: return ResponseEntity.badRequest().build()

        tokenService.revokeUserTokensByGrantType(user, GrantType.ACCESS)

        val accessToken = tokenService.saveToken(
            Token(
                token = jwtService.generateToken(user),
                user = user,
                grantType = GrantType.ACCESS,
                revoked = false,
                expired = false
            )
        )

        addHeaderToken(response, accessToken.token)
        return ResponseEntity.ok(
            ApiResponse.Success(
                accessToken = accessToken.token,
            )
        )
    }

    @PostMapping("/register2")
    suspend fun register2(
        @RequestBody registerRequest: RegisterRequest,
        response: HttpServletResponse
    ): ResponseEntity<ApiResponse<UserResponse>> {
        authService.register2(registerRequest.toDomain()).mapCatching { createdUser ->
            val accessToken = jwtService.generateToken(createdUser)
            val refreshToken = jwtService.generateRefreshToken(createdUser)

            val token = tokenService.saveToken(
                Token(
                    token = accessToken,
                    user = createdUser,
                    grantType = GrantType.ACCESS,
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

                addHeaderToken(response, accessToken)
                response.addCookie(RefreshCookie.build(refreshToken))
                return ResponseEntity.created(location).body(
                    ApiResponse.Success(
                        accessToken = accessToken,
                    )
                )
            },
            onFailure = { exception ->
                return errorResponse(exception)
            }
        )
    }

    private fun saveAccessAndRefreshToken(user: User): Pair<String, String> {
        val accessToken = tokenService.saveToken(
            Token(
                token = jwtService.generateToken(user),
                user = user,
                tokenType = TokenType.BEARER,
                grantType = GrantType.ACCESS,
                revoked = false,
                expired = false
            )
        )

        val refreshToken = tokenService.saveToken(
            Token(
                token = jwtService.generateRefreshToken(user),
                user = user,
                tokenType = TokenType.BEARER,
                grantType = GrantType.REFRESH,
                revoked = false,
                expired = false
            )
        )

        return Pair(accessToken.token, refreshToken.token)
    }

    private fun addHeaderToken(response: HttpServletResponse, token: String) {
        response.addHeader(Constants.AUTHORIZATION, "${Constants.BEARER} $token")
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


}
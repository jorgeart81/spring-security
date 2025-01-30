package com.jorgereyesdev.spring_security.config.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.jorgereyesdev.spring_security.presentantion.response.ApiResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component

@Component
class JWTAuthenticationEntryPoint : AuthenticationEntryPoint {
    override fun commence(
        request: HttpServletRequest?,
        response: HttpServletResponse?,
        authException: AuthenticationException?
    ) {
        val objectMapper = ObjectMapper()
        val responseError = ApiResponse.Error(
            HttpStatus.UNAUTHORIZED,
            authException?.message.toString()
        )

        response?.setHeader("Set-Cookie", "JSESSIONID=; Path=/; HttpOnly; Secure; Max-Age=0; SameSite=Strict")
        response?.contentType = "application/json"
        response?.characterEncoding = "UTF-8"
        response?.status = HttpServletResponse.SC_UNAUTHORIZED
        response?.writer?.write(objectMapper.writeValueAsString(responseError))
    }
}
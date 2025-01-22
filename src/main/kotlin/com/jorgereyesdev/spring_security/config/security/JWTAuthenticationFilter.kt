package com.jorgereyesdev.spring_security.config.security

import com.jorgereyesdev.spring_security.config.Constants.*
import com.jorgereyesdev.spring_security.domain.services.JWTService
import com.jorgereyesdev.spring_security.infrastructure.repositories.UserRepository
import com.jorgereyesdev.spring_security.infrastructure.services.AuthServiceImpl
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    val userRepository: UserRepository,
    val jwtService: JWTService,
    val userDetailsService: UserDetailsService
) :
    OncePerRequestFilter() {
    private val log: Logger = LoggerFactory.getLogger(AuthServiceImpl::class.java)

    override fun doFilterInternal(
        request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain
    ) {
        if (request.servletPath.contains("/auth")) {
            filterChain.doFilter(request, response)
            return
        }

        val token = getTokenFromRequest(request)
        if (token.isNullOrEmpty()) {
            filterChain.doFilter(request, response)
            return
        }

        val userDetails =
            runCatching {
                val username = jwtService.getUsernameFromToken(token)
                val isTokenExpired = jwtService.isTokenExpired(token)

                if (username.isNullOrEmpty() || isTokenExpired) {
                    throw AuthenticationCredentialsNotFoundException("Invalid jwt")
                }

                val userDetails = userDetailsService.loadUserByUsername(username)
                val userEntity = userRepository.findByUsernameWithValidTokens(userDetails.username)
                val isTokenAllowed = userEntity?.tokens?.any { it.token == token } ?: false

                if (userEntity == null || !isTokenAllowed) {
                    throw AuthenticationCredentialsNotFoundException("Invalid jwt")
                }

                val isTokenValid = jwtService.isTokenValid(token, userEntity.username)

                if (!isTokenValid) throw AuthenticationCredentialsNotFoundException("Invalid jwt")

                userDetails
            }.onFailure {
                filterChain.doFilter(request, response)
                log.warn(it.message)
            }.getOrThrow()

        configureAuthentication(userDetails, request)

        filterChain.doFilter(request, response)
    }

    private fun getTokenFromRequest(request: HttpServletRequest): String? {
        val authHeader = request.getHeader(HttpHeaders.AUTHORIZATION)

        return if (StringUtils.hasText(authHeader) && authHeader.startsWith(Authorization.BEARER)) {
            authHeader.substring(7)
        } else {
            null
        }
    }

    private fun configureAuthentication(userDetails: UserDetails, request: HttpServletRequest) {
        val authenticationToken =
            UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities).apply {
                details = WebAuthenticationDetailsSource().buildDetails(request)
            }
        SecurityContextHolder.getContext().authentication = authenticationToken
    }
}

//val username =
//    runCatching {
//        val username = jwtService.getUsernameFromToken(token)
//        val isTokenExpired = jwtService.isTokenExpired(token)
//
//        if (username.isNullOrEmpty() || isTokenExpired) throw AuthenticationCredentialsNotFoundException("Invalid jwt")
//
//        username
//    }.onFailure { filterChain.doFilter(request, response) }.getOrThrow()
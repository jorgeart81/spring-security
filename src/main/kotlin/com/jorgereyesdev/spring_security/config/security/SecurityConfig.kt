package com.jorgereyesdev.spring_security.config.security

import com.jorgereyesdev.spring_security.config.Constants
import com.jorgereyesdev.spring_security.config.Constants.ErrorMessages
import com.jorgereyesdev.spring_security.config.Constants.Routes
import com.jorgereyesdev.spring_security.config.RefreshCookie
import com.jorgereyesdev.spring_security.domain.models.RoleName
import com.jorgereyesdev.spring_security.domain.services.JWTService
import com.jorgereyesdev.spring_security.domain.services.TokenService
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.logout.LogoutHandler
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler
import org.springframework.transaction.annotation.Transactional


@Configuration
@EnableWebSecurity
class SecurityConfig(
    val userDetailsService: UserDetailsService,
    val tokenService: TokenService,
    val jwtService: JWTService,
    val jwtAuthenticationFilter: JwtAuthenticationFilter,
    val jwtAuthenticationEntryPoint: JWTAuthenticationEntryPoint,
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager {
        return config.authenticationManager
    }

    @Bean
    fun authenticationProvider(): AuthenticationProvider {
        val authProvider = DaoAuthenticationProvider()
        authProvider.setUserDetailsService(userDetailsService)
        authProvider.setPasswordEncoder(passwordEncoder())

        return authProvider
    }

    @Bean
    fun filterChainDsl(httpSecurity: HttpSecurity): SecurityFilterChain {
        httpSecurity.invoke {
            csrf { disable() }
            exceptionHandling { authenticationEntryPoint = jwtAuthenticationEntryPoint }
            authorizeHttpRequests {
                authorize("${Routes.AUTH}/**", permitAll)

                authorize("${Routes.USERS}/{id}", hasAnyRole(RoleName.ADMIN.name, RoleName.USER.name))
                authorize("${Routes.USERS}/**", hasRole(RoleName.ADMIN.name))

                authorize("${Routes.PRODUCTS}/**", hasAnyRole(RoleName.ADMIN.name, RoleName.USER.name))

                authorize(anyRequest, authenticated)
            }
            sessionManagement { SessionCreationPolicy.STATELESS }
            addFilterBefore<UsernamePasswordAuthenticationFilter>(jwtAuthenticationFilter)
//            anonymous { disable() }
        }

        httpSecurity.authenticationProvider(authenticationProvider()).logout {
            it.logoutUrl("${Routes.AUTH}/logout")
            it.addLogoutHandler(LogoutHandler { request, response, _ ->
                val authHeader = request.getHeader(HttpHeaders.AUTHORIZATION)
                logout(authHeader, response)
            })
            it.logoutSuccessHandler(LogoutSuccessHandler { _, _, _ ->
                SecurityContextHolder.clearContext()
            })
        }

        return httpSecurity.build()
    }

    @Transactional
    private fun logout(token: String, response: HttpServletResponse) {
        require(token.startsWith(Constants.BEARER)) { ErrorMessages.INVALID_TOKEN }

        val jwtToken = token.substring(7)
        val user = run {
            val foundToken = tokenService.findByToken(jwtToken)
                ?: throw IllegalArgumentException(ErrorMessages.INVALID_TOKEN)

            if (foundToken.expired || foundToken.revoked) throw IllegalArgumentException(ErrorMessages.INVALID_TOKEN)

            foundToken.user ?: throw IllegalArgumentException(ErrorMessages.INVALID_TOKEN)
        }

        val isTokenValid = jwtService.isTokenValid(jwtToken, user.username, user.securityStamp)

        if (!isTokenValid) throw AuthenticationCredentialsNotFoundException(ErrorMessages.INVALID_TOKEN)

        tokenService.revokeAllUserValidTokens(user)
        response.addCookie(RefreshCookie.expired())
    }
}



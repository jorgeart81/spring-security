package com.jorgereyesdev.spring_security.config.security

import com.jorgereyesdev.spring_security.config.Constants.*
import com.jorgereyesdev.spring_security.domain.services.JWTService
import com.jorgereyesdev.spring_security.domain.services.TokenService
import com.jorgereyesdev.spring_security.infrastructure.extensions.toDomain
import com.jorgereyesdev.spring_security.infrastructure.repositories.UserRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
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
    val jwtService: JWTService,
    val tokenService: TokenService,
    val jwtAuthenticationFilter: JwtAuthenticationFilter,
    val jwtAuthenticationEntryPoint: JWTAuthenticationEntryPoint,
    val userRepository: UserRepository,
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
                authorize("${Routes.USERS}/**", permitAll)
                authorize(anyRequest, authenticated)
            }
            sessionManagement { SessionCreationPolicy.STATELESS }
            addFilterBefore<UsernamePasswordAuthenticationFilter>(jwtAuthenticationFilter)
        }

        httpSecurity.authenticationProvider(authenticationProvider()).logout {
            it.logoutUrl("${Routes.AUTH}/logout")
            it.addLogoutHandler(LogoutHandler { request, _, _ ->
                val authHeader = request.getHeader(HttpHeaders.AUTHORIZATION)
                logout(authHeader)
            })
            it.logoutSuccessHandler(LogoutSuccessHandler { _, _, _ ->
                SecurityContextHolder.clearContext()
            })
        }

        return httpSecurity.build()
    }

    @Transactional
    private fun logout(token: String) {
        require(token.startsWith(Authorization.BEARER)) { ErrorMessages.INVALID_TOKEN }

        val jwtToken = token.substring(7)
        val user = run {
            val foundToken = tokenService.findByToken(jwtToken)
                ?: throw IllegalArgumentException(ErrorMessages.INVALID_TOKEN)

            if (foundToken.expired || foundToken.revoked) throw IllegalArgumentException(ErrorMessages.INVALID_TOKEN)

            val username = jwtService.getUsernameFromToken(foundToken.token)
                ?: throw IllegalArgumentException(ErrorMessages.INVALID_TOKEN)
            val userEntity = userRepository.findByUsername(username)
                ?: throw IllegalArgumentException(ErrorMessages.INVALID_TOKEN)

            userEntity.toDomain()
        }

        tokenService.revokeAllUserValidTokens(user)
    }
}



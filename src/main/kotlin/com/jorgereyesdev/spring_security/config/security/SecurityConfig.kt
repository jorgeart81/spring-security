package com.jorgereyesdev.spring_security.config.security

import com.jorgereyesdev.spring_security.config.Constants
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain


@Configuration
@EnableWebSecurity
class SecurityConfig {

//    @Bean
//    fun filterChain(httpSecurity: HttpSecurity): SecurityFilterChain {
//        httpSecurity.authorizeHttpRequests {
//            it.requestMatchers("${Constants.API}/users/**").permitAll()
//            it.anyRequest().authenticated()
//
//        }.httpBasic {
//            Customizer.withDefaults<Any>()
//        }.csrf { it.disable() }
//
//        return httpSecurity.build()
//    }

    @Bean
    fun filterChainDsl(httpSecurity: HttpSecurity): SecurityFilterChain {
        httpSecurity.invoke {
            authorizeHttpRequests {
                authorize("/api/users/**", permitAll)
                authorize(anyRequest, authenticated)
            }
            httpBasic { Customizer.withDefaults<Any>() }
            csrf { disable() }
        }
        return httpSecurity.build()
    }

    @Bean
    fun testUsers(passwordEncoder: PasswordEncoder): UserDetailsService {
        val userBuilder = User.builder()
        val user1 = userBuilder.username("Tony")
            .password(passwordEncoder.encode("Advenger1"))
            .roles()
            .build()
        return InMemoryUserDetailsManager(user1)
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

}
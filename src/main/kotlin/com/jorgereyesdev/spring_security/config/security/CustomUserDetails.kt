package com.jorgereyesdev.spring_security.config.security

import org.springframework.security.core.CredentialsContainer
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CustomUserDetails private constructor(
    @Transient private var password: String,
    @Transient private val username: String,
    @Transient private val authorities: List<GrantedAuthority>,
    val accountNonExpired: Boolean = true,
    val accountNonLocked: Boolean = true,
    val credentialsNonExpired: Boolean = true,
    val enabled: Boolean = false,
    val securityStamp: String? = null,
) : UserDetails, CredentialsContainer {

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = this.authorities.toMutableList()

    override fun getPassword(): String = this.password

    override fun getUsername(): String = this.username

    override fun eraseCredentials() {
        this.password = ""
    }

    object UserBuilder {
        private var _password: String = ""
        private var _username: String = ""
        private var _authorities: MutableList<GrantedAuthority> = mutableListOf()
        private var _accountExpired: Boolean = false
        private var _accountLocked: Boolean = false
        private var _credentialsExpired: Boolean = false
        private var _disabled: Boolean = false
        private var _passwordEncoder: (String) -> String = { it }

        // Custom properties
        private var _securityStamp: String = ""

        fun username(username: String) = apply { this._username = username }
        fun password(password: String) = apply { this._password = _passwordEncoder(password) }
        fun authorities(authorities: List<GrantedAuthority>) = apply { this._authorities = authorities.toMutableList() }
        fun accountExpired(accountExpired: Boolean) = apply { this._accountExpired = accountExpired }
        fun accountLocked(accountLocked: Boolean) = apply { this._accountLocked = accountLocked }
        fun credentialsExpired(credentialsExpired: Boolean) = apply { this._credentialsExpired = credentialsExpired }
        fun disabled(disabled: Boolean) = apply { this._disabled = disabled }
        fun passwordEncoder(passwordEncoder: (String) -> String) = apply { this._passwordEncoder = passwordEncoder }

        // Custom methods
        fun securityStamp(securityStamp: String) = apply { this._securityStamp = securityStamp }

        fun build(): CustomUserDetails {
            return CustomUserDetails(
                password = _password,
                username = _username,
                authorities = _authorities,
                accountNonExpired = _accountExpired,
                accountNonLocked = _accountLocked,
                credentialsNonExpired = _credentialsExpired,
                enabled = _disabled,
                securityStamp = _securityStamp
            )
        }
    }

    companion object {
        fun builder(): UserBuilder {
            return UserBuilder
        }
    }
}



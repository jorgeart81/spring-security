package com.jorgereyesdev.spring_security.infrastructure.entities

import com.jorgereyesdev.spring_security.domain.models.TokenType
import jakarta.persistence.*

@Entity
@Table(name = "tokens")
data class TokenEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null,

    @Column(unique = true, nullable = false)
    val token: String,

    @Column(name = "token_type")
    val tokenType: TokenType? = TokenType.BEARER,

    val revoked: Boolean?,

    val expired: Boolean?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user: UserEntity,
)

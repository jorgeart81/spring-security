package com.jorgereyesdev.spring_security.infrastructure.entities

import jakarta.persistence.*

@Entity
@Table(name = "users")
data class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int? = null,

    @Column(name = "username", length = 100, unique = true, nullable = false)
    val username: String,

    @Column(nullable = false)
    var password: String,

    @Column(columnDefinition = "bit(1) default 1", nullable = false)
    val enabled: Boolean = true,

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    var tokens: MutableList<TokenEntity>? = mutableListOf(),
)

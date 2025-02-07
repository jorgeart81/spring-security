package com.jorgereyesdev.spring_security.infrastructure.entities

import jakarta.persistence.*

@Entity()
@Table(name = "user_claims")
data class UserClaimsEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "claim_type", nullable = false)
    var claimType:String,

    @Column(name = "claim_value", nullable = false)
    var claimValue:String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    var user: UserEntity? = null,
)

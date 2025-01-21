package com.jorgereyesdev.spring_security.infrastructure.entities

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.jorgereyesdev.spring_security.domain.models.RoleName
import jakarta.persistence.*

@Entity
@Table(name = "roles")
data class RoleEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(length = 25, nullable = false, unique = true)
    val name: RoleName,

    @JsonIgnoreProperties(value = ["roles"])
    @ManyToMany(mappedBy = "roles")
    val users: MutableList<UserEntity> = mutableListOf()
) {
    override fun toString(): String {
        return "RoleEntity(id=$id)"
    }
}

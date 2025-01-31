package com.jorgereyesdev.spring_security.infrastructure.entities

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.jorgereyesdev.spring_security.domain.models.PermissionName
import jakarta.persistence.*

@Entity
@Table(name = "permissions")
data class PermissionEntity(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(length = 25, nullable = false, unique = true, updatable = false)
    val name: PermissionName,

    @JsonIgnoreProperties(value = ["permissions"])
    @ManyToMany(mappedBy = "permissions")
    val roles: MutableList<RoleEntity> = mutableListOf(),
) {
    override fun toString(): String {
        return "PermissionEntity(id=$id)"
    }
}


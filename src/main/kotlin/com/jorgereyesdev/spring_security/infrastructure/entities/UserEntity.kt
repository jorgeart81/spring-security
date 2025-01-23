package com.jorgereyesdev.spring_security.infrastructure.entities

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.jorgereyesdev.spring_security.domain.models.Role
import jakarta.persistence.*
import org.springframework.security.crypto.password.PasswordEncoder

@Entity
@Table(name = "users")
data class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "username", length = 100, unique = true, nullable = false)
    val username: String,

    @Column(nullable = false)
    var password: String,

    @Column(columnDefinition = "bit(1) default 1", nullable = false)
    var enabled: Boolean,

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    var tokens: MutableList<TokenEntity>? = mutableListOf(),

    @JsonIgnoreProperties(value = ["users"])
    @ManyToMany
    @JoinTable(
        name = "user_role",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "role_id")],
        uniqueConstraints = [UniqueConstraint(columnNames = ["user_id", "role_id"])]
    )
    var roles: MutableList<RoleEntity> = mutableListOf(),
) {
    override fun toString(): String {
        return "UserEntity(id=$id, username=$username)"
    }

    fun setSecurePassword(password: String, passwordEncoder: PasswordEncoder): UserEntity {
        this.password = passwordEncoder.encode(password)
        return this
    }

    fun enable() {
        this.enabled = true
    }

    fun disable() {
        this.enabled = false
    }

    fun addRole(roleEntity: RoleEntity): UserEntity {
        val roleList = HashSet(this.roles)
        roleList.add(roleEntity)
        this.roles = roleList.toMutableList()
        return this
    }
}

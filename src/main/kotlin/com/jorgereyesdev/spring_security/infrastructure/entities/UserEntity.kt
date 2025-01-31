package com.jorgereyesdev.spring_security.infrastructure.entities

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.*
import org.springframework.security.crypto.password.PasswordEncoder
import java.security.MessageDigest
import java.util.UUID

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

    @Column(name = "security_stamp", nullable = false)
    var securityStamp: String,

    @Column(name = "account_non_expired", columnDefinition = "bit(1) default 1", nullable = false)
    var accountNonExpired: Boolean,

    @Column(name = "account_non_locked", columnDefinition = "bit(1) default 1", nullable = false)
    var accountNonLocked: Boolean,

    @Column(name = "credentials_non_expired", columnDefinition = "bit(1) default 1", nullable = false)
    var credentialsNonExpired: Boolean,

    @Column(columnDefinition = "bit(1) default 1", nullable = false)
    var enabled: Boolean,

    @JsonIgnoreProperties(value = ["user"])
    @OneToMany(mappedBy = "user")
    var tokens: MutableList<TokenEntity> = mutableListOf(),

    @JsonIgnoreProperties(value = ["users"])
    @ManyToMany(fetch = FetchType.EAGER)
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

    fun generateSecurityStamp(): UserEntity {
        val uuid = UUID.randomUUID().toString()
        val hash = MessageDigest.getInstance("SHA-256").digest(uuid.toByteArray())

        securityStamp = hash
            .joinToString("") { "%02x".format(it) }
            .take(32)
            .uppercase()
        return this
    }
}

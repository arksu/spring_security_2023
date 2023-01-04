package com.vavito.auth.entity

import jakarta.persistence.*
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.*

@Entity
@Table(name = "users")
class User(
    @Id
    val id: UUID = UUID.randomUUID(),

    @Column(nullable = false, unique = true)
    val login: String,

    @Column(nullable = false)
    val passwordHash: String,

    @ManyToMany(fetch = FetchType.EAGER)
    val roles: MutableSet<Role> = LinkedHashSet(),
) : UserDetails {
    override fun getAuthorities(): List<SimpleGrantedAuthority> {
        return roles.map {
            SimpleGrantedAuthority(it.name)
        }
    }

    override fun getPassword(): String {
        return passwordHash
    }

    override fun getUsername(): String {
        return login
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }
}
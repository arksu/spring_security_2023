package com.vavito.auth.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id

@Entity
class Role(
    @Id
    @GeneratedValue
    val id: Int,

    @Column(nullable = false)
    val name: String
)
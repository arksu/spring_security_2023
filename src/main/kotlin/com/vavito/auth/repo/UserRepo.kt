package com.vavito.auth.repo

import com.vavito.auth.entity.User
import org.springframework.data.repository.CrudRepository
import java.util.*

interface UserRepo : CrudRepository<User, UUID> {
    fun findByLogin(login: String): User?
}
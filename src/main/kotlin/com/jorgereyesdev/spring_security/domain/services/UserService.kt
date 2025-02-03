package com.jorgereyesdev.spring_security.domain.services

import com.jorgereyesdev.spring_security.domain.models.User

interface UserService {
    fun findById(id: Long): User
}
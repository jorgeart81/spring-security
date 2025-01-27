package com.jorgereyesdev.spring_security.infrastructure.repositories

import com.jorgereyesdev.spring_security.infrastructure.entities.PermissionEntity
import org.springframework.data.repository.CrudRepository

interface PermissionRepository : CrudRepository<PermissionEntity, Long> {
}
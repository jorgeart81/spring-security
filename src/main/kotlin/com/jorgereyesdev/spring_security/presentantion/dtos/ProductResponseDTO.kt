package com.jorgereyesdev.spring_security.presentantion.dtos

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ProductResponseDTO(
    val id: Long,
    val name: String,
    val price: Int,
    val description: String? = null
)

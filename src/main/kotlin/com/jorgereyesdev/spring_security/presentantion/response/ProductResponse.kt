package com.jorgereyesdev.spring_security.presentantion.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonPropertyOrder

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("id", "name", "price", "description")
data class ProductResponse(
    val id: Long?,
    val name: String,
    val price: Int,
    val description: String? = null
)

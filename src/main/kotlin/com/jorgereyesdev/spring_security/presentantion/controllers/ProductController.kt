package com.jorgereyesdev.spring_security.presentantion.controllers

import com.jorgereyesdev.spring_security.config.Constants
import com.jorgereyesdev.spring_security.presentantion.dtos.ProductResponseDTO
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("${Constants.Companion.API}/products")
class ProductController {

    @GetMapping
    fun getProducts(): List<ProductResponseDTO> {
        return listOf(
            ProductResponseDTO(1, "Macbook Pro M4", 2500),
            ProductResponseDTO(2, "Fender Stratocaster Signature", 3000),
            ProductResponseDTO(3, "Piano Digital Kawai KDP120 RW", 2300),
        )
    }
}
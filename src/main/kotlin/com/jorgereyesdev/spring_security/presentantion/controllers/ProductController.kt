package com.jorgereyesdev.spring_security.presentantion.controllers

import com.jorgereyesdev.spring_security.config.Constants
import com.jorgereyesdev.spring_security.presentantion.response.ProductResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("${Constants.API}/products")
class ProductController {

    @GetMapping
    fun getProducts(): List<ProductResponse> {
        return listOf(
            ProductResponse(1, "Macbook Pro M4", 2500),
            ProductResponse(2, "Fender Stratocaster Signature", 3000),
            ProductResponse(3, "Piano Digital Kawai KDP120 RW", 2300),
        )
    }
}
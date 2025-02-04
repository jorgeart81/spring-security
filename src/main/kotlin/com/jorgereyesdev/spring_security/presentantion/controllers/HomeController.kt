package com.jorgereyesdev.spring_security.presentantion.controllers

import io.swagger.v3.oas.annotations.Hidden
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.view.RedirectView

@RestController
class HomeController {

    @Hidden
    @GetMapping("/swagger-ui")
    fun redirectToSwagger(): RedirectView {
        return RedirectView("/swagger-ui/index.html")
    }
}
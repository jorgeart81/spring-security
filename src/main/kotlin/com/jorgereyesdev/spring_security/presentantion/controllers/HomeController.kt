package com.jorgereyesdev.spring_security.presentantion.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.view.RedirectView

@RestController
class HomeController {

    @GetMapping("/swagger-ui")
    fun redirectToSwagger(): RedirectView {
        return RedirectView("/swagger-ui/index.html")
    }
}
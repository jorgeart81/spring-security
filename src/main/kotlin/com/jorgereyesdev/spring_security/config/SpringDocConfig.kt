package com.jorgereyesdev.spring_security.config

import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType
import io.swagger.v3.oas.annotations.info.Info
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.security.SecurityScheme
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders

@OpenAPIDefinition(
    info = Info(title = "Security API", version = "1.0.0"),
    security = [SecurityRequirement(name = "Authorization")]
)
@SecurityScheme(
    name = "Authorization",
    description = "Access Token",
    type = SecuritySchemeType.HTTP,
    paramName = HttpHeaders.AUTHORIZATION,
    `in` = SecuritySchemeIn.HEADER,
    scheme = "bearer",
    bearerFormat = "JWT",
)
@Configuration
class SpringDocConfig {
}
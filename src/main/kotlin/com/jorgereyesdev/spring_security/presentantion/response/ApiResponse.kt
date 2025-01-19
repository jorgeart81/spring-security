package com.jorgereyesdev.spring_security.presentantion.response

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.http.HttpStatus

@JsonInclude(JsonInclude.Include.NON_NULL)
sealed interface ApiResponse<out T> {
    data class Success<T>(
        val data: T? = null,
        val accessToken: String? = null,
        val refreshToken: String? = null
    ) : ApiResponse<T>

    data class Error(
        val status: Int,
        val error: String,
        val message: String,
        val validationErrors: Map<String, String>? = null
    ) : ApiResponse<Nothing> {
        constructor(httpStatus: HttpStatus, message: String, validationErrors: Map<String, String>? = null) : this(
            status = httpStatus.value(),
            error = httpStatus.reasonPhrase,
            message = message,
            validationErrors = validationErrors
        )
    }
}
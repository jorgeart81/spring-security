package com.jorgereyesdev.spring_security.config

class Constants {
    companion object {
        const val API = "/api"
    }

    object Authorization {
        const val BEARER = "Bearer"
    }

    object Routes {
        const val AUTH = "${API}/auth"
        const val PRODUCTS = "${API}/products"
        const val USERS = "${API}/users"
    }

    object ErrorMessages {
        const val INVALID_TOKEN = "Invalid Token"
    }
}
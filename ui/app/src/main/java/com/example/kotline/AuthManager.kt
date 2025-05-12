package com.example.kotline

object AuthManager {
    var token: String? = null
    var firstName: String? = null
    var userId: String? = null

    fun clear() {
        token = null
        firstName = null
        userId = null
    }
} 
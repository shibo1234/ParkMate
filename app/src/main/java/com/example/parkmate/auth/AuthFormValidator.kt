package com.example.parkmate.auth

object AuthFormValidator {
    fun validateLogin(email: String, password: String): String? {
        return when {
            email.isBlank() -> "Email is required."
            !email.contains("@") -> "Enter a valid email address."
            password.length < 6 -> "Password must be at least 6 characters."
            else -> null
        }
    }

    fun validateSignUp(displayName: String, email: String, password: String): String? {
        return when {
            displayName.isBlank() -> "Display name is required."
            else -> validateLogin(email = email, password = password)
        }
    }
}

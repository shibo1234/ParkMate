package com.example.parkmate.auth

/**
 * What: Validates the login and sign-up forms (email format, password length, display name).
 * Who:  Called by AuthViewModel before each auth call; covered by AuthFormValidatorTest.
 * When: On Log In and Create Account.
 */
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

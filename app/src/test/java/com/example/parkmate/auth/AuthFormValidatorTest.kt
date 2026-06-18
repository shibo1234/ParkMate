package com.example.parkmate.auth

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AuthFormValidatorTest {
    @Test
    fun validateLogin_returnsErrorForMissingEmail() {
        val result = AuthFormValidator.validateLogin("", "password123")

        assertEquals("Email is required.", result)
    }

    @Test
    fun validateLogin_returnsErrorForShortPassword() {
        val result = AuthFormValidator.validateLogin("demo@parkmate.app", "123")

        assertEquals("Password must be at least 6 characters.", result)
    }

    @Test
    fun validateLogin_acceptsValidCredentials() {
        val result = AuthFormValidator.validateLogin("demo@parkmate.app", "password123")

        assertNull(result)
    }

    @Test
    fun validateSignUp_requiresDisplayName() {
        val result = AuthFormValidator.validateSignUp("", "demo@parkmate.app", "password123")

        assertEquals("Display name is required.", result)
    }
}

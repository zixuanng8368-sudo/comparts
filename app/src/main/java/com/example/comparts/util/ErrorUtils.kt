package com.example.comparts.util

/**
 * Maps common Supabase and network errors to user-friendly messages.
 */
fun mapThrowableToMessage(throwable: Throwable): String {
    val message = throwable.message ?: "An unexpected error occurred."
    
    return when {
        // Authentication Errors
        message.contains("Invalid login credentials", ignoreCase = true) -> 
            "Incorrect email or password. Please try again."
        
        message.contains("User not found", ignoreCase = true) -> 
            "No account found with this email."
        
        message.contains("Email already registered", ignoreCase = true) || 
        message.contains("already exists", ignoreCase = true) -> 
            "This email is already in use. Try logging in or use another email."
        
        message.contains("Password should be at least", ignoreCase = true) -> 
            "Password is too short. It must be at least 6 characters."

        message.contains("Email not confirmed", ignoreCase = true) ->
            "Please confirm your email address before logging in."

        // Database / Postgrest Errors
        message.contains("23505", ignoreCase = true) -> 
            "This item (or SKU) already exists in the database."

        message.contains("42501", ignoreCase = true) -> 
            "You don't have permission to perform this action."

        // Network Errors
        message.contains("Unable to resolve host", ignoreCase = true) || 
        message.contains("timeout", ignoreCase = true) -> 
            "Network connection error. Please check your internet and try again."

        // Generic fallback
        else -> message
    }
}

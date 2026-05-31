package com.example.comparts.data.repository

import com.example.comparts.data.remote.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email

class AuthRepository {
    suspend fun signUp(email: String, password: String) {
        SupabaseClient.client.auth.signUpWith(Email) {
            this.email = email
            this.password = password
        }
    }

    suspend fun signIn(email: String, password: String) {
        SupabaseClient.client.auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    suspend fun signOut() {
        SupabaseClient.client.auth.signOut()
    }

    fun getCurrentUser() = SupabaseClient.client.auth.currentUserOrNull()
}

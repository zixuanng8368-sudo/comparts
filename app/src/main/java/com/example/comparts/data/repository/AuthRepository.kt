package com.example.comparts.data.repository

import com.example.comparts.data.remote.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class AuthRepository {
    suspend fun signUp(email: String, password: String, username: String) {
        SupabaseClient.client.auth.signUpWith(Email) {
            this.email = email
            this.password = password
            // Stores the username safely in Supabase Auth metadata
            this.data = buildJsonObject {
                put("username", username)
            }
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

    suspend fun retrieveUser() = SupabaseClient.client.auth.retrieveUserForCurrentSession(updateSession = true)

    fun getCurrentUser() = SupabaseClient.client.auth.currentUserOrNull()

    suspend fun updateUsername(username: String) {
        SupabaseClient.client.auth.updateUser {
            data = buildJsonObject {
                put("username", username)
            }
        }
    }
}

package com.example.comparts.data.remote

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

object SupabaseClient {

    val client = createSupabaseClient(
        supabaseUrl = "https://cbsvkqnnyvjjgjwqbacs.supabase.co",
        supabaseKey = "sb_publishable_qIrTYfKUCKEvzUh1qPLfaA_4jhuiUIY"
    ) {
        install(Auth)
        install(Postgrest)
        install(Storage)
    }
}
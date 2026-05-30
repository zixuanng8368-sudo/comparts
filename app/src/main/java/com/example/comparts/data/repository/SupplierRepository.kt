package com.example.comparts.data.repository

import com.example.comparts.data.model.Supplier
import com.example.comparts.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.from

class SupplierRepository {

    suspend fun getSuppliers(): List<Supplier> {
        return SupabaseClient.client
            .from("supplier")
            .select()
            .decodeList<Supplier>()
    }
}

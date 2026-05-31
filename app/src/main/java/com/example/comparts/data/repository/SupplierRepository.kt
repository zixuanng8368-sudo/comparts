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

    suspend fun addSupplier(supplier: Supplier) {
        SupabaseClient.client
            .from("supplier")
            .insert(supplier)
    }

    suspend fun updateSupplier(supplier: Supplier) {
        SupabaseClient.client
            .from("supplier")
            .update(supplier) {
                filter {
                    eq("supplier_id", supplier.supplierId)
                }
            }
    }
}

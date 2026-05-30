package com.example.comparts.data.repository

import com.example.comparts.data.model.Transaction
import com.example.comparts.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.from

class TransactionRepository {

    suspend fun getTransactions(): List<Transaction> {
        return SupabaseClient.client
            .from("transaction")
            .select()
            .decodeList<Transaction>()
    }
}

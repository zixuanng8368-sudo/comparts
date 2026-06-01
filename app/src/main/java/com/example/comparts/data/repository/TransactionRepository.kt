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

    suspend fun addTransaction(transaction: Transaction) {
        SupabaseClient.client
            .from("transaction")
            .insert(transaction)
            
        // Also update item quantity
        updateItemQuantity(transaction.itemId, transaction.transactionType, transaction.transactionQuantity)
    }

    suspend fun updateTransaction(transaction: Transaction) {
        // We might need the old transaction to revert its stock change before applying the new one
        val oldTransaction = SupabaseClient.client.from("transaction").select {
            filter { eq("transaction_id", transaction.transactionId) }
        }.decodeSingleOrNull<Transaction>()

        oldTransaction?.let { old ->
            // Revert old stock change
            val revertType = if (old.transactionType == "IN") "OUT" else "IN"
            updateItemQuantity(old.itemId, revertType, old.transactionQuantity)
        }

        // Apply new transaction
        SupabaseClient.client
            .from("transaction")
            .update(transaction) {
                filter { eq("transaction_id", transaction.transactionId) }
            }
        
        // Apply new stock change
        updateItemQuantity(transaction.itemId, transaction.transactionType, transaction.transactionQuantity)
    }

    private suspend fun updateItemQuantity(itemId: String, type: String, qty: Int) {
        val item = SupabaseClient.client.from("item").select {
            filter { eq("item_id", itemId) }
        }.decodeSingleOrNull<com.example.comparts.data.model.Item>()
        
        item?.let {
            val newQty = if (type == "IN") {
                it.itemStockQuantity + qty
            } else {
                it.itemStockQuantity - qty
            }
            
            SupabaseClient.client.from("item").update(mapOf("item_stock_quantity" to newQty)) {
                filter { eq("item_id", itemId) }
            }
        }
    }
}

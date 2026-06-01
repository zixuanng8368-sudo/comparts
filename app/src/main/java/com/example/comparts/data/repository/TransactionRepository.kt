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
        // 1. Add the transaction record
        SupabaseClient.client
            .from("transaction")
            .insert(transaction)
            
        // 2. Fetch current item to update stock
        val item = SupabaseClient.client.from("item").select {
            filter { eq("item_id", transaction.itemId) }
        }.decodeSingleOrNull<com.example.comparts.data.model.Item>()
        
        item?.let {
            // Delta calculation
            val newQty = if (transaction.transactionType == "IN") {
                it.itemStockQuantity + transaction.transactionQuantity
            } else {
                it.itemStockQuantity - transaction.transactionQuantity
            }
            
            // Save back to DB
            SupabaseClient.client.from("item").update(mapOf("item_stock_quantity" to newQty)) {
                filter { eq("item_id", transaction.itemId) }
            }
        }
    }

    suspend fun updateTransaction(transaction: Transaction) {
        // 1. Fetch the old transaction state
        val oldTransaction = SupabaseClient.client.from("transaction").select {
            filter { eq("transaction_id", transaction.transactionId) }
        }.decodeSingleOrNull<Transaction>()

        // 2. Fetch the current item state
        val item = SupabaseClient.client.from("item").select {
            filter { eq("item_id", transaction.itemId) }
        }.decodeSingleOrNull<com.example.comparts.data.model.Item>()

        if (oldTransaction != null && item != null) {
            // Delta Reconciliation Logic
            var adjustedStock = item.itemStockQuantity

            // Step 1: Revert previous state impact
            if (oldTransaction.transactionType == "IN") {
                adjustedStock -= oldTransaction.transactionQuantity
            } else {
                adjustedStock += oldTransaction.transactionQuantity
            }

            // Step 2: Inject new input parameters
            if (transaction.transactionType == "IN") {
                adjustedStock += transaction.transactionQuantity
            } else {
                adjustedStock -= transaction.transactionQuantity
            }

            // Step 3: Save adjustedStock back to the item table
            SupabaseClient.client.from("item").update(mapOf("item_stock_quantity" to adjustedStock)) {
                filter { eq("item_id", transaction.itemId) }
            }
            
            // Step 4: Update the transaction record itself
            SupabaseClient.client
                .from("transaction")
                .update(transaction) {
                    filter { eq("transaction_id", transaction.transactionId) }
                }
        }
    }
}

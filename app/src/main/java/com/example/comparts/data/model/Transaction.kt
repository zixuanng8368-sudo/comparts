// File path: app/src/main/java/com/example/comparts/data/model/Transaction.kt
package com.example.comparts.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Transaction(
    @SerialName("transaction_id") val transactionId: String = "", // UUID from Supabase
    @SerialName("item_id") val itemId: String, // Foreign Key linking to Item
    @SerialName("transaction_type") val transactionType: String, // e.g., "IN" or "OUT"
    @SerialName("transaction_quantity") val transactionQuantity: Int,
    @SerialName("transaction_date") val transactionDate: String? = null,
    @SerialName("transaction_notes") val transactionNotes: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)
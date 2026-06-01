// File path: app/src/main/java/com/example/comparts/data/model/Transaction.kt
package com.example.comparts.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Transaction(
    @SerialName("transaction_id") val transactionId: String = "",
    @SerialName("item_id") val itemId: String,
    @SerialName("transaction_type") val transactionType: String, // "IN" or "OUT"
    @SerialName("transaction_quantity") val transactionQuantity: Int,
    @SerialName("transaction_reference_number") val transactionReferenceNumber: String? = null,
    @SerialName("transaction_date") val transactionDate: String? = null,
    @SerialName("transaction_notes") val transactionNotes: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null,
    @SerialName("created_by") val createdBy: String,
    @SerialName("updated_by") val updatedBy: String
)

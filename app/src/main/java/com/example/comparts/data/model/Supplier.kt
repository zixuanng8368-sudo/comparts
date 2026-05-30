// File path: app/src/main/java/com/example/comparts/data/model/Supplier.kt
package com.example.comparts.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Supplier(
    @SerialName("supplier_id") val supplierId: String = "", // UUID
    @SerialName("supplier_name") val supplierName: String,
    @SerialName("supplier_phone_number") val supplierPhoneNumber: String? = null,
    @SerialName("supplier_email") val supplierEmail: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)
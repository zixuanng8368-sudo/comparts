// File path: app/src/main/java/com/example/comparts/data/model/Item.kt
package com.example.comparts.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Item(
    @SerialName("item_id") val itemId: String = "",
    @SerialName("item_sku") val itemSku: String,
    @SerialName("item_name") val itemName: String,
    @SerialName("category_id") val categoryId: String? = null, // Foreign Key
    @SerialName("item_reference") val itemReference: String? = null,
    @SerialName("item_price") val itemPrice: Double,
    @SerialName("item_stock_quantity") val itemStockQuantity: Int,
    @SerialName("item_min_stock_level") val itemMinStockLevel: Int,
    @SerialName("supplier_id") val supplierId: String? = null, // Foreign Key
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)
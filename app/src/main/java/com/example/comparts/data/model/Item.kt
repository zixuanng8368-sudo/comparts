package com.example.comparts.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Item(
    val id: Long,
    @SerialName("item_name") val itemName: String,
    @SerialName("item_quantity") val itemQuantity: Int,
    @SerialName("created_at") val createdAt: String? = null
)
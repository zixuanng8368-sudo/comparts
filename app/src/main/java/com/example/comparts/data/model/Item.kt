package com.example.comparts.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Item(
    val id: Long,
    val item_name: String,
    val item_quantity: Int,
    val created_at: String? = null
)
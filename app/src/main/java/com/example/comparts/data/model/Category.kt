// File path: app/src/main/java/com/example/comparts/data/model/Category.kt
package com.example.comparts.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Category(
    @SerialName("category_id") val categoryId: String = "", // UUID
    @SerialName("category_name") val categoryName: String,
    @SerialName("category_description") val categoryDescription: String? = null,
    @SerialName("created_at") val createdAt: String? = null,
    @SerialName("updated_at") val updatedAt: String? = null
)
package com.example.comparts.data.repository

import com.example.comparts.data.model.Category
import com.example.comparts.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.from

class CategoryRepository {

    suspend fun getCategories(): List<Category> {
        return SupabaseClient.client
            .from("category")
            .select()
            .decodeList<Category>()
    }

    suspend fun getCategoryById(categoryId: String): Category? {
        return SupabaseClient.client
            .from("category")
            .select {
                filter {
                    eq("category_id", categoryId)
                }
            }
            .decodeSingleOrNull<Category>()
    }

    suspend fun addCategory(category: Category) {
        SupabaseClient.client
            .from("category")
            .insert(category)
    }

    suspend fun updateCategory(category: Category) {
        SupabaseClient.client
            .from("category")
            .update(category) {
                filter {
                    eq("category_id", category.categoryId)
                }
            }
    }
    
    suspend fun deleteCategory(categoryId: String) {
        SupabaseClient.client
            .from("category")
            .delete {
                filter {
                    eq("category_id", categoryId)
                }
            }
    }
}

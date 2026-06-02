package com.example.comparts.data.repository

import com.example.comparts.data.model.Item
import com.example.comparts.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.storage.storage

class ItemRepository {

    suspend fun getItems(): List<Item> {
        return SupabaseClient.client
            .from("item")
            .select()
            .decodeList<Item>()
    }

    suspend fun getItemById(itemId: String): Item? {
        return SupabaseClient.client
            .from("item")
            .select {
                filter {
                    eq("item_id", itemId)
                }
            }
            .decodeSingleOrNull<Item>()
    }

    suspend fun addItem(item: Item) {
        SupabaseClient.client
            .from("item")
            .insert(item)
    }

    suspend fun updateItem(item: Item) {
        SupabaseClient.client
            .from("item")
            .update(item) {
                filter {
                    eq("item_id", item.itemId)
                }
            }
    }

    suspend fun uploadItemImage(byteArray: ByteArray, fileName: String, oldImageUrl: String? = null): String {
        val bucket = SupabaseClient.client.storage.from("item-images")
        
        // Improve deletion logic: only delete if it's a known Supabase URL and different file
        oldImageUrl?.let { url ->
            if (url.contains("supabase.co") && url.contains("item-images/")) {
                try {
                    val oldFileName = url.split("item-images/").last().split("?").first()
                    if (oldFileName != fileName) {
                        bucket.delete(oldFileName)
                    }
                } catch (e: Exception) {
                    println("Non-critical: Failed to delete old image: ${e.message}")
                }
            }
        }

        bucket.upload(fileName, byteArray) {
            upsert = true
        }
        
        // Ensure we get a valid public URL
        return bucket.publicUrl(fileName)
    }
}

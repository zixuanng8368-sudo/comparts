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

    suspend fun uploadItemImage(itemId: String, imageBytes: ByteArray): String {
        val fileName = "item_$itemId.jpg"
        val bucket = SupabaseClient.client.storage["item-images"]
        bucket.upload(fileName, imageBytes) {
            upsert = true
        }
        return bucket.publicUrl(fileName)
    }
}
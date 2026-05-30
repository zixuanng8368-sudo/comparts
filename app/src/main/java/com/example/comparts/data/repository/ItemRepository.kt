package com.example.comparts.data.repository

import com.example.comparts.data.model.Item
import com.example.comparts.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.from

class ItemRepository {

    suspend fun getItems(): List<Item> {

        return SupabaseClient.client
            .from("item")
            .select()
            .decodeList<Item>()
    }
}
package com.example.comparts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.comparts.data.model.Item
import com.example.comparts.data.repository.ItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ItemViewModel : ViewModel() {

    private val repository = ItemRepository()

    private val _items = MutableStateFlow<List<Item>>(emptyList())
    val items: StateFlow<List<Item>> = _items

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadItems()
    }

    fun loadItems() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _items.value = repository.getItems()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    suspend fun getItemById(itemId: String): Item? {
        return repository.getItemById(itemId)
    }

    fun addItem(item: Item, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                repository.addItem(item)
                loadItems()
                onResult(true, null)
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false, e.message)
            }
        }
    }

    fun updateItem(item: Item, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                repository.updateItem(item)
                loadItems()
                onResult(true, null)
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false, e.message)
            }
        }
    }

    fun uploadImage(itemId: String, imageBytes: ByteArray, oldImageUrl: String? = null, onComplete: (String?) -> Unit) {
        viewModelScope.launch {
            try {
                // Use a timestamped filename to avoid caching issues
                val fileName = "item_${itemId}_${System.currentTimeMillis()}.jpg"
                val url = repository.uploadItemImage(imageBytes, fileName, oldImageUrl)
                onComplete(url)
            } catch (e: Exception) {
                println("ERROR: Image upload failed: ${e.message}")
                e.printStackTrace()
                onComplete(null)
            }
        }
    }
}

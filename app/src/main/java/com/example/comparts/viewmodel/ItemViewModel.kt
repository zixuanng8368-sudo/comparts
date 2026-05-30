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

    private val _items =
        MutableStateFlow<List<Item>>(emptyList())

    val items: StateFlow<List<Item>>
        get() = _items

    init {
        loadItems()
    }

    private fun loadItems() {

        viewModelScope.launch {

            _items.value =
                repository.getItems()
        }
    }
}
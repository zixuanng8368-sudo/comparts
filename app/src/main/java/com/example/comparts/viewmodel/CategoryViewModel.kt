package com.example.comparts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.comparts.data.model.Category
import com.example.comparts.data.repository.CategoryRepository
import com.example.comparts.util.mapThrowableToMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CategoryViewModel : ViewModel() {

    private val repository = CategoryRepository()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadCategories()
    }

    fun loadCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _categories.value = repository.getCategories()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    suspend fun getCategoryById(categoryId: String): Category? {
        return repository.getCategoryById(categoryId)
    }

    fun addCategory(category: Category, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                repository.addCategory(category)
                loadCategories()
                onResult(true, null)
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false, mapThrowableToMessage(e))
            }
        }
    }

    fun updateCategory(category: Category, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                repository.updateCategory(category)
                loadCategories()
                onResult(true, null)
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false, mapThrowableToMessage(e))
            }
        }
    }
}

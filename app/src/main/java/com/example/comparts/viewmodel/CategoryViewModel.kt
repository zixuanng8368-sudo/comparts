package com.example.comparts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.comparts.data.model.Category
import com.example.comparts.data.repository.CategoryRepository
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

    fun addCategory(category: Category, onComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                repository.addCategory(category)
                loadCategories()
                onComplete()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateCategory(category: Category, onComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                repository.updateCategory(category)
                loadCategories()
                onComplete()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

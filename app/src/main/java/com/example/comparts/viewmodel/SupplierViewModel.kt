package com.example.comparts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.comparts.data.model.Supplier
import com.example.comparts.data.repository.SupplierRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SupplierViewModel : ViewModel() {

    private val repository = SupplierRepository()

    private val _suppliers = MutableStateFlow<List<Supplier>>(emptyList())
    val suppliers: StateFlow<List<Supplier>> = _suppliers

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadSuppliers()
    }

    fun loadSuppliers() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _suppliers.value = repository.getSuppliers()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    suspend fun getSupplierById(supplierId: String): Supplier? {
        return repository.getSupplierById(supplierId)
    }

    fun addSupplier(supplier: Supplier, onComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                repository.addSupplier(supplier)
                loadSuppliers()
                onComplete()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateSupplier(supplier: Supplier, onComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                repository.updateSupplier(supplier)
                loadSuppliers()
                onComplete()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

package com.example.comparts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.comparts.data.model.Transaction
import com.example.comparts.data.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TransactionViewModel : ViewModel() {

    private val repository = TransactionRepository()

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadTransactions()
    }

    fun loadTransactions() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _transactions.value = repository.getTransactions()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}

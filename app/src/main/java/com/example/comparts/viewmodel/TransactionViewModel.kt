package com.example.comparts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.comparts.data.model.Transaction
import com.example.comparts.data.repository.TransactionRepository
import com.example.comparts.util.mapThrowableToMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

import com.example.comparts.data.remote.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.rpc

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
                _transactions.value = repository.getTransactions().sortedByDescending { it.createdAt }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun addTransaction(transaction: Transaction, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                repository.addTransaction(transaction)
                loadTransactions()
                onResult(true, null)
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false, mapThrowableToMessage(e))
            }
        }
    }

    fun updateTransaction(transaction: Transaction, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                repository.updateTransaction(transaction)
                loadTransactions()
                onResult(true, null)
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false, mapThrowableToMessage(e))
            }
        }
    }

    suspend fun getUserNameById(userId: String?): String {
        if (userId.isNullOrBlank()) return "System"
        
        return try {
            val response = SupabaseClient.client.postgrest.rpc("get_user_name", mapOf("user_uuid" to userId))
            response.decodeAs<String>()
        } catch (e: Exception) { 
            e.printStackTrace()
            "Unknown User" 
        }
    }
}

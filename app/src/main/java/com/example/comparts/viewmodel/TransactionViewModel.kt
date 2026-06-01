package com.example.comparts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.comparts.data.model.Transaction
import com.example.comparts.data.repository.TransactionRepository
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

    fun addTransaction(transaction: Transaction, onComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                repository.addTransaction(transaction)
                loadTransactions()
                onComplete()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateTransaction(transaction: Transaction, onComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                repository.updateTransaction(transaction)
                loadTransactions()
                onComplete()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    suspend fun getUserNameById(userId: String?): String {
        if (userId.isNullOrBlank()) return "System"
        
        return try {
            // Call RPC to get the display name (full_name or similar) from profiles/auth
            val response = SupabaseClient.client.postgrest.rpc("get_user_name", mapOf("user_uuid" to userId))
            response.decodeAs<String>()
        } catch (e: Exception) { 
            e.printStackTrace()
            "Unknown User" 
        }
    }
}

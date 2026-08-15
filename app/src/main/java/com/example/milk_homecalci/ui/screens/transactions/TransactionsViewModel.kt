package com.example.milk_homecalci.ui.screens.transactions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.milk_homecalci.data.entity.MilkTransaction
import com.example.milk_homecalci.data.entity.Session
import com.example.milk_homecalci.data.repository.MilkRepository
import kotlinx.coroutines.flow.*
import java.text.SimpleDateFormat
import java.util.*

class TransactionsViewModel(private val repository: MilkRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedSession = MutableStateFlow<Session?>(null)
    val selectedSession: StateFlow<Session?> = _selectedSession.asStateFlow()

    val filteredTransactions: StateFlow<List<MilkTransaction>> = combine(
        repository.getAllTransactions(),
        _searchQuery,
        _selectedSession
    ) { transactions, query, session ->
        transactions.filter { transaction ->
            val matchesQuery = if (query.isBlank()) true else {
                val dateStr = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(transaction.date))
                dateStr.contains(query, ignoreCase = true) ||
                        (transaction.receiptNumber?.contains(query, ignoreCase = true) ?: false) ||
                        (transaction.notes?.contains(query, ignoreCase = true) ?: false)
            }
            val matchesSession = if (session == null) true else transaction.session == session
            matchesQuery && matchesSession
        }.sortedByDescending { it.date }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
    }

    fun onSessionFilterChange(session: Session?) {
        _selectedSession.value = session
    }

    class Factory(private val repository: MilkRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TransactionsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return TransactionsViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

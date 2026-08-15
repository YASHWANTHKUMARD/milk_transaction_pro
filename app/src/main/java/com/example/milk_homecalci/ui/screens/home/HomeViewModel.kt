package com.example.milk_homecalci.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.milk_homecalci.data.entity.MilkTransaction
import com.example.milk_homecalci.data.entity.Session
import com.example.milk_homecalci.data.repository.MilkRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class HomeDashboardState(
    val totalQuantity: Double = 0.0,
    val totalAmount: Double = 0.0,
    val morningQuantity: Double = 0.0,
    val eveningQuantity: Double = 0.0,
    val recentTransactions: List<MilkTransaction> = emptyList()
)

class HomeViewModel(private val repository: MilkRepository) : ViewModel() {

    val dashboardState: StateFlow<HomeDashboardState> = repository.getAllTransactions()
        .map { transactions ->
            HomeDashboardState(
                totalQuantity = transactions.sumOf { it.quantity },
                totalAmount = transactions.sumOf { it.amount },
                morningQuantity = transactions.filter { it.session == Session.MORNING }.sumOf { it.quantity },
                eveningQuantity = transactions.filter { it.session == Session.EVENING }.sumOf { it.quantity },
                recentTransactions = transactions.take(10)
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = HomeDashboardState()
        )

    class Factory(private val repository: MilkRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return HomeViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

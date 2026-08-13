package com.example.milk_homecalci.ui.screens.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.milk_homecalci.data.entity.MilkTransaction
import com.example.milk_homecalci.data.repository.MilkRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class MonthlySummary(
    val monthName: String,
    val totalQuantity: Double,
    val totalAmount: Double,
    val transactions: List<MilkTransaction>
)

class ReportsViewModel(private val repository: MilkRepository) : ViewModel() {

    val monthlySummaries: StateFlow<List<MonthlySummary>> = repository.getAllTransactions()
        .map { transactions ->
            transactions.groupBy {
                val cal = Calendar.getInstance().apply { timeInMillis = it.date }
                SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(cal.time)
            }.map { (month, transList) ->
                MonthlySummary(
                    monthName = month,
                    totalQuantity = transList.sumOf { it.quantity },
                    totalAmount = transList.sumOf { it.amount },
                    transactions = transList.sortedByDescending { it.date }
                )
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun clearAllData() {
        viewModelScope.launch {
            repository.deleteAllTransactions()
        }
    }

    fun generateCsvData(transactions: List<MilkTransaction>): String {
        val builder = StringBuilder()
        builder.append("Date,Session,Quantity,Rate,Amount,Receipt No,Notes\n")
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        transactions.forEach {
            builder.append("${sdf.format(Date(it.date))},${it.session},${it.quantity},${it.rate},${it.amount},${it.receiptNumber ?: ""},${it.notes ?: ""}\n")
        }
        return builder.toString()
    }

    class Factory(private val repository: MilkRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ReportsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ReportsViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

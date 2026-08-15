package com.example.milk_homecalci.ui.screens.reports

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.milk_homecalci.data.entity.MilkTransaction
import com.example.milk_homecalci.data.entity.Session
import com.example.milk_homecalci.data.repository.MilkRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

enum class ReportType {
    USER_DEFINED, WEEKLY, MONTHLY
}

data class ReportSummary(
    val title: String,
    val totalTransactions: Int,
    val totalQuantity: Double,
    val totalAmount: Double,
    val morningQuantity: Double,
    val morningAmount: Double,
    val eveningQuantity: Double,
    val eveningAmount: Double,
    val transactions: List<MilkTransaction>,
    val dateRange: String
)

class ReportsViewModel(private val repository: MilkRepository) : ViewModel() {

    private val _reportType = MutableStateFlow(ReportType.MONTHLY)
    val reportType: StateFlow<ReportType> = _reportType.asStateFlow()

    private val _startDate = MutableStateFlow<Long?>(null)
    val startDate: StateFlow<Long?> = _startDate.asStateFlow()

    private val _endDate = MutableStateFlow<Long?>(null)
    val endDate: StateFlow<Long?> = _endDate.asStateFlow()

    val reportSummaries: StateFlow<List<ReportSummary>> = combine(
        repository.getAllTransactions(),
        _reportType,
        _startDate,
        _endDate
    ) { transactions, type, start, end ->
        when (type) {
            ReportType.MONTHLY -> generateMonthlySummaries(transactions)
            ReportType.WEEKLY -> generateWeeklySummaries(transactions)
            ReportType.USER_DEFINED -> generateUserDefinedSummary(transactions, start, end)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setReportType(type: ReportType) {
        _reportType.value = type
    }

    fun setDateRange(start: Long?, end: Long?) {
        _startDate.value = start
        _endDate.value = end
    }

    private fun generateMonthlySummaries(transactions: List<MilkTransaction>): List<ReportSummary> {
        val sdf = SimpleDateFormat("MMMM yyyy", Locale.getDefault())
        return transactions.groupBy {
            val cal = Calendar.getInstance().apply { timeInMillis = it.date }
            sdf.format(cal.time)
        }.map { (month, transList) ->
            ReportSummary(
                title = month,
                totalTransactions = transList.size,
                totalQuantity = transList.sumOf { it.quantity },
                totalAmount = transList.sumOf { it.amount },
                morningQuantity = transList.filter { it.session == Session.MORNING }.sumOf { it.quantity },
                morningAmount = transList.filter { it.session == Session.MORNING }.sumOf { it.amount },
                eveningQuantity = transList.filter { it.session == Session.EVENING }.sumOf { it.quantity },
                eveningAmount = transList.filter { it.session == Session.EVENING }.sumOf { it.amount },
                transactions = transList.sortedByDescending { it.date },
                dateRange = month
            )
        }.sortedByDescending { it.transactions.firstOrNull()?.date ?: 0L }
    }

    private fun generateWeeklySummaries(transactions: List<MilkTransaction>): List<ReportSummary> {
        // Weekly report (Saturday morning to next Friday evening - 7 days)
        if (transactions.isEmpty()) return emptyList()
        
        val sdf = SimpleDateFormat("dd MMM", Locale.getDefault())
        val grouped = transactions.groupBy { trans ->
            val cal = Calendar.getInstance()
            cal.timeInMillis = trans.date
            // Find the start of the week (the previous Saturday)
            while (cal.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
                cal.add(Calendar.DAY_OF_YEAR, -1)
            }
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            cal.timeInMillis
        }

        return grouped.map { (startTime, transList) ->
            val startCal = Calendar.getInstance().apply { timeInMillis = startTime }
            val endCal = Calendar.getInstance().apply { 
                timeInMillis = startTime
                add(Calendar.DAY_OF_YEAR, 6)
            }
            ReportSummary(
                title = "Week: ${sdf.format(startCal.time)} - ${sdf.format(endCal.time)}",
                totalTransactions = transList.size,
                totalQuantity = transList.sumOf { it.quantity },
                totalAmount = transList.sumOf { it.amount },
                morningQuantity = transList.filter { it.session == Session.MORNING }.sumOf { it.quantity },
                morningAmount = transList.filter { it.session == Session.MORNING }.sumOf { it.amount },
                eveningQuantity = transList.filter { it.session == Session.EVENING }.sumOf { it.quantity },
                eveningAmount = transList.filter { it.session == Session.EVENING }.sumOf { it.amount },
                transactions = transList.sortedByDescending { it.date },
                dateRange = "${sdf.format(startCal.time)} to ${sdf.format(endCal.time)}"
            )
        }.sortedByDescending { it.transactions.firstOrNull()?.date ?: 0L }
    }

    private fun generateUserDefinedSummary(transactions: List<MilkTransaction>, start: Long?, end: Long?): List<ReportSummary> {
        if (start == null || end == null) return emptyList()
        
        val filtered = transactions.filter { it.date in start..end }
        if (filtered.isEmpty()) return emptyList()

        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return listOf(
            ReportSummary(
                title = "Custom Range",
                totalTransactions = filtered.size,
                totalQuantity = filtered.sumOf { it.quantity },
                totalAmount = filtered.sumOf { it.amount },
                morningQuantity = filtered.filter { it.session == Session.MORNING }.sumOf { it.quantity },
                morningAmount = filtered.filter { it.session == Session.MORNING }.sumOf { it.amount },
                eveningQuantity = filtered.filter { it.session == Session.EVENING }.sumOf { it.quantity },
                eveningAmount = filtered.filter { it.session == Session.EVENING }.sumOf { it.amount },
                transactions = filtered.sortedByDescending { it.date },
                dateRange = "${sdf.format(Date(start))} - ${sdf.format(Date(end))}"
            )
        )
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.deleteAllTransactions()
        }
    }

    fun generateCsvData(summary: ReportSummary): String {
        val builder = StringBuilder()
        val displayLocale = Locale.getDefault()
        val csvLocale = Locale.US // Use US locale for CSV to ensure dot as decimal separator
        
        builder.append("Report: ${summary.title}\n")
        builder.append("Date Range: ${summary.dateRange}\n")
        builder.append("Total Transactions: ${summary.totalTransactions}\n")
        builder.append("Total Quantity: ${String.format(csvLocale, "%.2f", summary.totalQuantity)} L\n")
        builder.append("Total Amount: ${String.format(csvLocale, "%.2f", summary.totalAmount)}\n")
        builder.append("Morning Total: ${String.format(csvLocale, "%.2f", summary.morningQuantity)} L (${String.format(csvLocale, "%.2f", summary.morningAmount)})\n")
        builder.append("Evening Total: ${String.format(csvLocale, "%.2f", summary.eveningQuantity)} L (${String.format(csvLocale, "%.2f", summary.eveningAmount)})\n")
        builder.append("\n")
        builder.append("Date,Session,Quantity,Rate,Amount,Receipt No,Notes\n")
        
        val sdf = SimpleDateFormat("dd/MM/yyyy", displayLocale)
        summary.transactions.forEach {
            val receipt = it.receiptNumber?.replace("\"", "\"\"") ?: ""
            val notes = it.notes?.replace("\"", "\"\"") ?: ""
            builder.append("${sdf.format(Date(it.date))},")
            builder.append("${it.session},")
            builder.append("${String.format(csvLocale, "%.2f", it.quantity)},")
            builder.append("${String.format(csvLocale, "%.2f", it.rate)},")
            builder.append("${String.format(csvLocale, "%.2f", it.amount)},")
            builder.append("\"$receipt\",")
            builder.append("\"$notes\"\n")
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

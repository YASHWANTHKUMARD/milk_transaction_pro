package com.example.milk_homecalci.ui.screens.add_transaction

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.milk_homecalci.data.entity.MilkTransaction
import com.example.milk_homecalci.data.entity.Session
import com.example.milk_homecalci.data.repository.MilkRepository
import kotlinx.coroutines.launch
import java.util.Calendar

class AddTransactionViewModel(private val repository: MilkRepository) : ViewModel() {

    var date by mutableStateOf(System.currentTimeMillis())
    var session by mutableStateOf(Session.MORNING)
    var quantity by mutableStateOf("")
    var rate by mutableStateOf("")
    var receiptNumber by mutableStateOf("")
    var notes by mutableStateOf("")

    var isSaving by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    fun saveTransaction(onSuccess: () -> Unit) {
        val qty = quantity.toDoubleOrNull()
        val rt = rate.toDoubleOrNull()

        if (qty == null || qty <= 0) {
            errorMessage = "Invalid quantity"
            return
        }
        if (rt == null || rt <= 0) {
            errorMessage = "Invalid rate"
            return
        }

        viewModelScope.launch {
            isSaving = true
            errorMessage = null
            try {
                // Check for existing transaction
                val existing = repository.getTransactionByDateAndSession(truncateDate(date), session)
                if (existing != null) {
                    errorMessage = "Transaction already exists for this date and session"
                } else {
                    val amount = qty * rt
                    val transaction = MilkTransaction(
                        date = truncateDate(date),
                        session = session,
                        quantity = qty,
                        rate = rt,
                        amount = amount,
                        receiptNumber = receiptNumber.ifBlank { null },
                        notes = notes.ifBlank { null }
                    )
                    repository.insertTransaction(transaction)
                    onSuccess()
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: "Failed to save transaction"
            } finally {
                isSaving = false
            }
        }
    }

    private fun truncateDate(timestamp: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = timestamp
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    class Factory(private val repository: MilkRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AddTransactionViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AddTransactionViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

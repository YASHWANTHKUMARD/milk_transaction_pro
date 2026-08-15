package com.example.milk_homecalci.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

enum class Session {
    MORNING, EVENING
}

@Entity(
    tableName = "milk_transactions",
    indices = [Index(value = ["date", "session"], unique = true)]
)
data class MilkTransaction(
    @PrimaryKey(autoGenerate = true)
    val transactionId: Long = 0,
    val date: Long = 0,
    val session: Session = Session.MORNING,
    val quantity: Double = 0.0,
    val rate: Double = 0.0,
    val amount: Double = 0.0,
    val receiptNumber: String? = null,
    val depotName: String? = null,
    val receiptImagePath: String? = null,
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

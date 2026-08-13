package com.example.milk_homecalci.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bank_deposits")
data class BankDeposit(
    @PrimaryKey(autoGenerate = true)
    val depositId: Long = 0,
    val billingPeriodStart: Long,
    val billingPeriodEnd: Long,
    val paymentDate: Long,
    val expectedAmount: Double,
    val actualAmount: Double,
    val difference: Double,
    val referenceNumber: String? = null,
    val notes: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

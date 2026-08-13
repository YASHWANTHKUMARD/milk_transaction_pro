package com.example.milk_homecalci.data.database

import androidx.room.*
import com.example.milk_homecalci.data.entity.BankDeposit
import kotlinx.coroutines.flow.Flow

@Dao
interface BankDepositDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeposit(deposit: BankDeposit)

    @Update
    suspend fun updateDeposit(deposit: BankDeposit)

    @Delete
    suspend fun deleteDeposit(deposit: BankDeposit)

    @Query("SELECT * FROM bank_deposits WHERE paymentDate = :paymentDate LIMIT 1")
    suspend fun getDepositByPaymentDate(paymentDate: Long): BankDeposit?

    @Query("SELECT * FROM bank_deposits ORDER BY paymentDate DESC")
    fun getAllDeposits(): Flow<List<BankDeposit>>

    @Query("SELECT * FROM bank_deposits WHERE billingPeriodStart = :start AND billingPeriodEnd = :end LIMIT 1")
    suspend fun getDepositForPeriod(start: Long, end: Long): BankDeposit?
}

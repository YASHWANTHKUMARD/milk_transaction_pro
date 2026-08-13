package com.example.milk_homecalci.data.database

import androidx.room.*
import com.example.milk_homecalci.data.entity.MilkTransaction
import com.example.milk_homecalci.data.entity.Session
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertTransaction(transaction: MilkTransaction)

    @Update
    suspend fun updateTransaction(transaction: MilkTransaction)

    @Delete
    suspend fun deleteTransaction(transaction: MilkTransaction)

    @Query("SELECT * FROM milk_transactions WHERE date = :date AND session = :session LIMIT 1")
    suspend fun getTransactionByDateAndSession(date: Long, session: Session): MilkTransaction?

    @Query("SELECT * FROM milk_transactions ORDER BY date DESC, session DESC")
    fun getAllTransactions(): Flow<List<MilkTransaction>>

    @Query("SELECT * FROM milk_transactions WHERE date BETWEEN :startDate AND :endDate ORDER BY date ASC, session ASC")
    fun getTransactionsInRange(startDate: Long, endDate: Long): Flow<List<MilkTransaction>>

    @Query("SELECT * FROM milk_transactions WHERE transactionId = :id")
    suspend fun getTransactionById(id: Long): MilkTransaction?

    @Query("DELETE FROM milk_transactions")
    suspend fun deleteAllTransactions()
}

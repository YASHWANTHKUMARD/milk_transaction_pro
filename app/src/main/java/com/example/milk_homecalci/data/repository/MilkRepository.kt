package com.example.milk_homecalci.data.repository

import com.example.milk_homecalci.data.database.BankDepositDao
import com.example.milk_homecalci.data.database.TransactionDao
import com.example.milk_homecalci.data.entity.BankDeposit
import com.example.milk_homecalci.data.entity.MilkTransaction
import com.example.milk_homecalci.data.entity.Session
import kotlinx.coroutines.flow.Flow

class MilkRepository(
    private val transactionDao: TransactionDao,
    private val bankDepositDao: BankDepositDao
) {
    // Transaction operations
    fun getAllTransactions(): Flow<List<MilkTransaction>> = transactionDao.getAllTransactions()

    fun getTransactionsInRange(startDate: Long, endDate: Long): Flow<List<MilkTransaction>> =
        transactionDao.getTransactionsInRange(startDate, endDate)

    suspend fun getTransactionByDateAndSession(date: Long, session: Session): MilkTransaction? =
        transactionDao.getTransactionByDateAndSession(date, session)

    suspend fun insertTransaction(transaction: MilkTransaction) =
        transactionDao.insertTransaction(transaction)

    suspend fun updateTransaction(transaction: MilkTransaction) =
        transactionDao.updateTransaction(transaction)

    suspend fun deleteTransaction(transaction: MilkTransaction) =
        transactionDao.deleteTransaction(transaction)

    suspend fun deleteAllTransactions() =
        transactionDao.deleteAllTransactions()

    // Bank Deposit operations
    fun getAllDeposits(): Flow<List<BankDeposit>> = bankDepositDao.getAllDeposits()

    suspend fun insertDeposit(deposit: BankDeposit) =
        bankDepositDao.insertDeposit(deposit)

    suspend fun getDepositForPeriod(start: Long, end: Long): BankDeposit? =
        bankDepositDao.getDepositForPeriod(start, end)
}

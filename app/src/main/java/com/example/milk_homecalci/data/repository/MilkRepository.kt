package com.example.milk_homecalci.data.repository

import com.example.milk_homecalci.data.database.TransactionDao
import com.example.milk_homecalci.data.entity.MilkTransaction
import com.example.milk_homecalci.data.entity.Session
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class MilkRepository(
    private val transactionDao: TransactionDao,
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    // Helper to get user-specific collection path
    private val userCollection
        get() = auth.currentUser?.let { user ->
            firestore.collection("users").document(user.uid).collection("transactions")
        }

    // Transaction operations
    fun getAllTransactions(): Flow<List<MilkTransaction>> = transactionDao.getAllTransactions()

    fun getTransactionsInRange(startDate: Long, endDate: Long): Flow<List<MilkTransaction>> =
        transactionDao.getTransactionsInRange(startDate, endDate)

    suspend fun getTransactionByDateAndSession(date: Long, session: Session): MilkTransaction? =
        transactionDao.getTransactionByDateAndSession(date, session)

    suspend fun insertTransaction(transaction: MilkTransaction) {
        // 1. Save locally
        transactionDao.insertTransaction(transaction)
        
        // 2. Sync to Cloud
        syncToFirestore(transaction)
    }

    suspend fun updateTransaction(transaction: MilkTransaction) {
        transactionDao.updateTransaction(transaction)
        syncToFirestore(transaction)
    }

    suspend fun deleteTransaction(transaction: MilkTransaction) {
        transactionDao.deleteTransaction(transaction)
        userCollection?.document(transaction.transactionId.toString())?.delete()?.await()
    }

    suspend fun deleteAllTransactions() {
        transactionDao.deleteAllTransactions()
        // Note: For full safety, you'd loop and delete in Firestore too
    }

    private suspend fun syncToFirestore(transaction: MilkTransaction) {
        try {
            userCollection?.document(transaction.transactionId.toString())
                ?.set(transaction)
                ?.await()
        } catch (e: Exception) {
            // Silently fail - we can retry later or via a periodic sync
            e.printStackTrace()
        }
    }
    
    // Initial migration: Upload all local data to cloud
    suspend fun syncAllLocalDataToCloud(transactions: List<MilkTransaction>) {
        val collection = userCollection ?: return
        transactions.forEach { transaction ->
            collection.document(transaction.transactionId.toString()).set(transaction)
        }
    }

    suspend fun syncCloudDataToLocal() {
        val collection = userCollection ?: return
        try {
            val snapshot = collection.get().await()
            val cloudTransactions = snapshot.toObjects(MilkTransaction::class.java)
            cloudTransactions.forEach { transaction ->
                transactionDao.insertTransaction(transaction)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

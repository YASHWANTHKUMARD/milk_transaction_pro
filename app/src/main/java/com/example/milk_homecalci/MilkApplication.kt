package com.example.milk_homecalci

import android.app.Application
import com.example.milk_homecalci.data.database.AppDatabase
import com.example.milk_homecalci.data.repository.AuthRepository
import com.example.milk_homecalci.data.repository.MilkRepository
import com.google.firebase.FirebaseApp

class MilkApplication : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { MilkRepository(database.transactionDao()) }
    val authRepository by lazy { AuthRepository() }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}

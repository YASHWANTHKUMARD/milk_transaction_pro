package com.example.milk_homecalci.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.milk_homecalci.data.entity.BankDeposit
import com.example.milk_homecalci.data.entity.MilkTransaction

@Database(entities = [MilkTransaction::class, BankDeposit::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun bankDepositDao(): BankDepositDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "milk_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

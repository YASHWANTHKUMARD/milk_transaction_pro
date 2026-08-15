package com.example.milk_homecalci.ui.screens.profile

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.milk_homecalci.data.repository.AuthRepository
import com.example.milk_homecalci.data.repository.MilkRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val authRepository: AuthRepository,
    private val milkRepository: MilkRepository
) : ViewModel() {

    val currentUser: StateFlow<FirebaseUser?> = authRepository.currentUser
    
    var isSyncing by mutableStateOf(false)
    var syncMessage by mutableStateOf<String?>(null)

    fun signOut() {
        authRepository.signOut()
    }

    fun syncDataFromCloud() {
        viewModelScope.launch {
            isSyncing = true
            syncMessage = "Syncing data from cloud..."
            try {
                milkRepository.syncCloudDataToLocal()
                syncMessage = "Sync completed successfully!"
            } catch (e: Exception) {
                syncMessage = "Sync failed: ${e.localizedMessage}"
            } finally {
                isSyncing = false
            }
        }
    }

    fun clearSyncMessage() {
        syncMessage = null
    }

    class Factory(
        private val authRepository: AuthRepository,
        private val milkRepository: MilkRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return ProfileViewModel(authRepository, milkRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

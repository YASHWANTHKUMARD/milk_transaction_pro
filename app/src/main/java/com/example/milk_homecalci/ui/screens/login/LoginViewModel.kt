package com.example.milk_homecalci.ui.screens.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.milk_homecalci.data.repository.AuthRepository
import com.example.milk_homecalci.data.repository.MilkRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginViewModel(
    private val authRepository: AuthRepository,
    private val milkRepository: MilkRepository
) : ViewModel() {

    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    private val _loginSuccess = MutableSharedFlow<Boolean>()
    val loginSuccess = _loginSuccess.asSharedFlow()

    fun onLoginClick() {
        if (email.isBlank() || password.isBlank()) {
            errorMessage = "Please enter email and password"
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).await()
                handleSuccessfulLogin()
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "Login failed"
            } finally {
                isLoading = false
            }
        }
    }

    fun onSignUpClick() {
        if (email.isBlank() || password.isBlank()) {
            errorMessage = "Please enter email and password"
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).await()
                handleSuccessfulLogin()
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "Registration failed"
            } finally {
                isLoading = false
            }
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                FirebaseAuth.getInstance().signInWithCredential(credential).await()
                handleSuccessfulLogin()
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "Google Sign-In failed"
            } finally {
                isLoading = false
            }
        }
    }

    private suspend fun handleSuccessfulLogin() {
        // Migration: Upload all existing local data to the new cloud account
        try {
            val localTransactions = milkRepository.getAllTransactions().first()
            if (localTransactions.isNotEmpty()) {
                milkRepository.syncAllLocalDataToCloud(localTransactions)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        _loginSuccess.emit(true)
    }

    class Factory(
        private val authRepository: AuthRepository,
        private val milkRepository: MilkRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return LoginViewModel(authRepository, milkRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

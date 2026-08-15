package com.example.milk_homecalci.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.milk_homecalci.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {

    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")
    
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)

    val currentUser = authRepository.currentUser

    fun signUp(onSuccess: () -> Unit) {
        if (password != confirmPassword) {
            errorMessage = "Passwords do not match"
            return
        }
        if (email.isBlank() || password.isBlank()) {
            errorMessage = "Please fill all fields"
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).await()
                onSuccess()
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "Registration failed"
            } finally {
                isLoading = false
            }
        }
    }

    fun signIn(onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            errorMessage = "Please fill all fields"
            return
        }

        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).await()
                onSuccess()
            } catch (e: Exception) {
                errorMessage = e.localizedMessage ?: "Login failed"
            } finally {
                isLoading = false
            }
        }
    }

    fun clearErrors() {
        errorMessage = null
    }

    class Factory(private val authRepository: AuthRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return AuthViewModel(authRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

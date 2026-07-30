package com.zztx.browse.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zztx.browse.data.entity.User
import com.zztx.browse.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {
    private val _isLoading = MutableStateFlow(false)
    private val _error = MutableStateFlow<String?>(null)
    
    val authState: StateFlow<AuthUiState> = combine(
        authRepository.isLoggedIn,
        authRepository.currentUser,
        _isLoading,
        _error
    ) { isLoggedIn, user, isLoading, error ->
        AuthUiState(
            isLoggedIn = isLoggedIn,
            user = user,
            isLoading = isLoading,
            error = error
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AuthUiState())

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = authRepository.login(email, password)
            result.onSuccess {
                _isLoading.value = false
            }.onFailure {
                _error.value = it.message
                _isLoading.value = false
            }
        }
    }

    fun register(email: String, password: String, name: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            val result = authRepository.register(email, password, name)
            result.onSuccess {
                _isLoading.value = false
            }.onFailure {
                _error.value = it.message
                _isLoading.value = false
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    fun clearError() {
        _error.value = null
    }
}

data class AuthUiState(
    val isLoggedIn: Boolean = false,
    val user: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)
package no.janksoft.liftlog.feature.user.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.janksoft.liftlog.core.util.ApiState
import no.janksoft.liftlog.feature.user.data.dto.LoginRequest
import no.janksoft.liftlog.feature.user.data.model.User
import no.janksoft.liftlog.feature.user.repository.UserRepository

class LoginViewModel : ViewModel() {

    private val TAG = "LoginViewModel"

    private val _inputValue = MutableStateFlow("")
    val inputValue: StateFlow<String> = _inputValue.asStateFlow()

    private val _loginState = MutableStateFlow<ApiState<User>>(ApiState.Idle)
    val loginState: StateFlow<ApiState<User>> = _loginState.asStateFlow()

    private val userRepository = UserRepository()

    fun updateInputValue(newText: String) {
        _inputValue.value = newText
    }

    fun resetLoginState() {
        _loginState.value = ApiState.Idle
        _inputValue.value = ""
    }

    fun login(request: LoginRequest) {
        viewModelScope.launch {
            _loginState.value = ApiState.Loading

            val result = userRepository.login(request)

            if (result is ApiState.Error) {
                logError(result, "Error fetching exercise")
            }

            _loginState.value = result
        }
    }

    fun logError(error: ApiState.Error, errorHeader: String) {
        Log.e(TAG, "${errorHeader}: ${error.message}")
        error.errorResponse?.let {
            Log.e(TAG, "Backend error - Code: ${it.code}, Status: ${it.status}")
        }
    }
}
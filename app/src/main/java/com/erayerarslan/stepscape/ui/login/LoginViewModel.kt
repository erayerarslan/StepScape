package com.erayerarslan.stepscape.ui.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erayerarslan.stepscape.core.Response
import com.erayerarslan.stepscape.core.Response.*
import com.erayerarslan.stepscape.model.User
import com.erayerarslan.stepscape.repository.AuthenticationRepository
import com.erayerarslan.stepscape.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthenticationRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<Response<Any>>(Init)
    val loginState: StateFlow<Response<Any>> = _loginState


    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            Log.d("LoginViewModel", "loginWithGoogle called with idToken: ${idToken.take(20)}...")
            authRepository.loginWithGoogle(idToken).collect { response ->
                Log.d("LoginViewModel", "Response received: ${response::class.simpleName}")
                when (response) {
                    is Loading -> {
                        _loginState.value = Loading
                    }
                    is Success -> {
                        Log.d("LoginViewModel", "Google Sign-In successful - saving user to database")
                        // Kullanıcıyı veritabanına kaydet
                        saveUserToDatabase()
                        _loginState.value = Success(response.data)
                    }
                    is Error -> {
                        Log.e("LoginViewModel", "Error: ${response.message}")
                        _loginState.value = Error(response.message)
                    }
                    is Init -> {
                        _loginState.value = Init
                    }
                }
            }
        }
    }

    private suspend fun saveUserToDatabase() {
        try {
            val currentUser = authRepository.userEmail()
            val displayName = "" // Google'dan display name alınabilir, şimdilik boş
            
            // Kullanıcı veritabanında var mı kontrol et
            try {
                userRepository.getUserData().first()
                Log.d("LoginViewModel", "User already exists in database")
            } catch (e: Exception) {
                // Kullanıcı yoksa yeni kullanıcı oluştur
                Log.d("LoginViewModel", "User not found, creating new user in database")
                val newUser = User(
                    firstName = null,
                    lastName = null,
                    email = currentUser,
                    gender = null
                )
                userRepository.updateUserData(newUser).catch { e ->
                    Log.e("LoginViewModel", "Error saving user to database", e)
                }.collect { }
            }
        } catch (e: Exception) {
            Log.e("LoginViewModel", "Error in saveUserToDatabase", e)
        }
    }
}
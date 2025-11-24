package com.erayerarslan.stepscape.repository

import com.erayerarslan.stepscape.core.Response
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.Flow

interface AuthenticationRepository {

    suspend fun login(email: String, password: String): Flow<Response<AuthResult>>

    suspend fun register(email: String, password: String): Flow<Response<AuthResult>>
    suspend fun loginWithGoogle(idToken: String): Flow<Response<AuthResult>>

    suspend fun resetPassword(email: String): Flow<Response<Void?>>

    suspend fun logout()

    suspend fun userUid(): String

    suspend fun userEmail(): String

    suspend fun isLoggedIn(): Boolean

    suspend fun signInAnonymously(): Flow<Response<AuthResult>>
}
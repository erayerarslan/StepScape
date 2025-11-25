package com.erayerarslan.stepscape.repository

import com.erayerarslan.stepscape.core.Response
import com.erayerarslan.stepscape.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getUserData(): Flow<Response<User>>

    suspend fun updateUserData(user: User): Flow<Response<User>>
}
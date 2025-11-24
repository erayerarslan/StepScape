package com.erayerarslan.stepscape.di

import com.erayerarslan.stepscape.repository.AuthenticationRepository
import com.erayerarslan.stepscape.repository.AuthenticationRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthenticationModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideAuthenticationRepository(auth: FirebaseAuth): AuthenticationRepository =
        AuthenticationRepositoryImpl(auth)

}
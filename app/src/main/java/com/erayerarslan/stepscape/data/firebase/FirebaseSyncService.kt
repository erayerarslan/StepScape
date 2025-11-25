package com.erayerarslan.stepscape.data.firebase

import android.util.Log
import com.erayerarslan.stepscape.data.local.entity.StepLog
import com.erayerarslan.stepscape.repository.StepLogRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseSyncService @Inject constructor(
    private val stepLogRepository: StepLogRepository,
    private val databaseReference: DatabaseReference,
    private val firebaseAuth: FirebaseAuth
) {
    
    suspend fun syncUnsyncedStepLogs(): Result<Int> {
        return try {
            val uid = firebaseAuth.currentUser?.uid
                ?: return Result.failure(Exception("User not logged in"))
            
            Log.d("FirebaseSyncService", "Starting sync for user: $uid")
            val unsyncedLogs = stepLogRepository.getUnsyncedStepLogs()
            Log.d("FirebaseSyncService", "Found ${unsyncedLogs.size} unsynced logs")
            
            if (unsyncedLogs.isEmpty()) {
                Log.d("FirebaseSyncService", "No unsynced logs to sync")
                return Result.success(0)
            }
            
            val syncedIds = mutableListOf<Long>()
            val userStepLogsRef = databaseReference.child("StepLogs").child(uid)
            Log.d("FirebaseSyncService", "Firebase path: StepLogs/$uid")
            
            unsyncedLogs.forEach { stepLog ->
                try {
                    val logRef = userStepLogsRef.child(stepLog.id.toString())
                    val data = mapOf(
                        "date" to stepLog.date,
                        "steps" to stepLog.steps,
                        "syncedToFirebase" to true,
                        "timestamp" to System.currentTimeMillis()
                    )
                    Log.d("FirebaseSyncService", "Syncing log ${stepLog.id} with data: $data")
                    
                    logRef.setValue(data).await()
                    
                    syncedIds.add(stepLog.id)
                    Log.d("FirebaseSyncService", "Successfully synced step log: ${stepLog.id} to Firebase")
                } catch (e: Exception) {
                    Log.e("FirebaseSyncService", "Error syncing step log ${stepLog.id} to Firebase", e)
                    Log.e("FirebaseSyncService", "Error message: ${e.message}")
                    Log.e("FirebaseSyncService", "Error stack trace: ${e.stackTraceToString()}")
                }
            }
            
            if (syncedIds.isNotEmpty()) {
                stepLogRepository.markMultipleAsSynced(syncedIds)
                Log.d("FirebaseSyncService", "Marked ${syncedIds.size} logs as synced in local database")
            } else {
                Log.w("FirebaseSyncService", "No logs were successfully synced")
            }
            
            Result.success(syncedIds.size)
        } catch (e: Exception) {
            Log.e("FirebaseSyncService", "Error in syncUnsyncedStepLogs", e)
            Log.e("FirebaseSyncService", "Error message: ${e.message}")
            Log.e("FirebaseSyncService", "Error stack trace: ${e.stackTraceToString()}")
            Result.failure(e)
        }
    }
    
    suspend fun syncStepLog(stepLog: StepLog): Result<Unit> {
        return try {
            val uid = firebaseAuth.currentUser?.uid
                ?: return Result.failure(Exception("User not logged in"))
            
            val userStepLogsRef = databaseReference.child("StepLogs").child(uid)
            val logRef = userStepLogsRef.child(stepLog.id.toString())
            
            logRef.setValue(
                mapOf(
                    "date" to stepLog.date,
                    "steps" to stepLog.steps,
                    "syncedToFirebase" to true,
                    "timestamp" to System.currentTimeMillis()
                )
            ).await()
            
            stepLogRepository.markAsSynced(stepLog.id)
            Log.d("FirebaseSyncService", "Synced step log: ${stepLog.id}")
            
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirebaseSyncService", "Error syncing step log", e)
            Result.failure(e)
        }
    }
}


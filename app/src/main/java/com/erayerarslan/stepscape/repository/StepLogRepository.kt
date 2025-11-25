package com.erayerarslan.stepscape.repository

import com.erayerarslan.stepscape.data.local.entity.StepLog
import kotlinx.coroutines.flow.Flow

interface StepLogRepository {
    fun getAllStepLogs(): Flow<List<StepLog>>
    suspend fun getStepLogByDate(date: Long): StepLog?
    suspend fun getUnsyncedStepLogs(): List<StepLog>
    fun getStepLogsByDateRange(startDate: Long, endDate: Long): Flow<List<StepLog>>
    suspend fun insertStepLog(stepLog: StepLog): Long
    suspend fun updateStepLog(stepLog: StepLog)
    suspend fun markAsSynced(id: Long)
    suspend fun markMultipleAsSynced(ids: List<Long>)
    suspend fun deleteStepLog(stepLog: StepLog)
}


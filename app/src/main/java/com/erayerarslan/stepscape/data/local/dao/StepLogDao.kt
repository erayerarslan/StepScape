package com.erayerarslan.stepscape.data.local.dao

import androidx.room.*
import com.erayerarslan.stepscape.data.local.entity.StepLog
import kotlinx.coroutines.flow.Flow

@Dao
interface StepLogDao {
    
    @Query("SELECT * FROM step_logs ORDER BY date DESC")
    fun getAllStepLogs(): Flow<List<StepLog>>
    
    @Query("SELECT * FROM step_logs WHERE date = :date LIMIT 1")
    suspend fun getStepLogByDate(date: Long): StepLog?
    
    @Query("SELECT * FROM step_logs WHERE syncedToFirebase = 0")
    suspend fun getUnsyncedStepLogs(): List<StepLog>
    
    @Query("SELECT * FROM step_logs WHERE date >= :startDate AND date <= :endDate ORDER BY date ASC")
    fun getStepLogsByDateRange(startDate: Long, endDate: Long): Flow<List<StepLog>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStepLog(stepLog: StepLog): Long
    
    @Update
    suspend fun updateStepLog(stepLog: StepLog)
    
    @Query("UPDATE step_logs SET syncedToFirebase = 1 WHERE id = :id")
    suspend fun markAsSynced(id: Long)
    
    @Query("UPDATE step_logs SET syncedToFirebase = 1 WHERE id IN (:ids)")
    suspend fun markMultipleAsSynced(ids: List<Long>)
    
    @Delete
    suspend fun deleteStepLog(stepLog: StepLog)
}


package com.erayerarslan.stepscape.repository

import com.erayerarslan.stepscape.data.local.dao.StepLogDao
import com.erayerarslan.stepscape.data.local.entity.StepLog
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class StepLogRepositoryImpl @Inject constructor(
    private val stepLogDao: StepLogDao
) : StepLogRepository {
    
    override fun getAllStepLogs(): Flow<List<StepLog>> = stepLogDao.getAllStepLogs()
    
    override suspend fun getStepLogByDate(date: Long): StepLog? = stepLogDao.getStepLogByDate(date)
    
    override suspend fun getUnsyncedStepLogs(): List<StepLog> = stepLogDao.getUnsyncedStepLogs()
    
    override fun getStepLogsByDateRange(startDate: Long, endDate: Long): Flow<List<StepLog>> =
        stepLogDao.getStepLogsByDateRange(startDate, endDate)
    
    override suspend fun insertStepLog(stepLog: StepLog): Long = stepLogDao.insertStepLog(stepLog)
    
    override suspend fun updateStepLog(stepLog: StepLog) = stepLogDao.updateStepLog(stepLog)
    
    override suspend fun markAsSynced(id: Long) = stepLogDao.markAsSynced(id)
    
    override suspend fun markMultipleAsSynced(ids: List<Long>) = stepLogDao.markMultipleAsSynced(ids)
    
    override suspend fun deleteStepLog(stepLog: StepLog) = stepLogDao.deleteStepLog(stepLog)
}


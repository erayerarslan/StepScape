package com.erayerarslan.stepscape.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erayerarslan.stepscape.data.firebase.FirebaseSyncService
import com.erayerarslan.stepscape.data.health.HealthConnectManager
import com.erayerarslan.stepscape.data.local.entity.StepLog
import com.erayerarslan.stepscape.repository.StepLogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val healthConnectManager: HealthConnectManager,
    private val stepLogRepository: StepLogRepository,
    private val firebaseSyncService: FirebaseSyncService
) : ViewModel() {
    
    private val _todaySteps = MutableStateFlow(0L)
    val todaySteps: StateFlow<Long> = _todaySteps.asStateFlow()
    
    private val _goalSteps = MutableStateFlow(10000)
    val goalSteps: StateFlow<Int> = _goalSteps.asStateFlow()
    
    private val _motivationalMessage = MutableStateFlow("")
    val motivationalMessage: StateFlow<String> = _motivationalMessage.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _weeklySteps = MutableStateFlow<List<StepLog>>(emptyList())
    val weeklySteps: StateFlow<List<StepLog>> = _weeklySteps.asStateFlow()
    
    init {
        loadTodaySteps()
        loadWeeklyData()
    }
    
    fun loadTodaySteps() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                if (healthConnectManager.isAvailable && healthConnectManager.hasAllPermissions()) {
                    val steps = healthConnectManager.getTodaySteps()
                    _todaySteps.value = steps
                    
                    saveTodayStepsToDatabase(steps)
                    
                    syncToFirebase()
                    
                    updateMotivationalMessage(steps, _goalSteps.value)
                } else {
                    val todayStart = LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
                    val todayEnd = LocalDate.now().atTime(LocalTime.MAX).atZone(ZoneOffset.UTC).toInstant().toEpochMilli()
                    val todayLog = stepLogRepository.getStepLogByDate(todayStart)
                    _todaySteps.value = todayLog?.steps?.toLong() ?: 0L
                    updateMotivationalMessage(_todaySteps.value, _goalSteps.value)
                    
                    syncToFirebase()
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error loading today steps", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    private suspend fun saveTodayStepsToDatabase(steps: Long) {
        try {
            val todayStart = LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
            val existingLog = stepLogRepository.getStepLogByDate(todayStart)
            
            if (existingLog != null) {
                val updatedLog = existingLog.copy(
                    steps = steps.toInt(),
                    syncedToFirebase = false
                )
                stepLogRepository.updateStepLog(updatedLog)
                Log.d("HomeViewModel", "Updated existing log for date: $todayStart, steps: $steps")
            } else {
                val newLog = StepLog(
                    id = 0,
                    date = todayStart,
                    steps = steps.toInt(),
                    syncedToFirebase = false
                )
                val insertedId = stepLogRepository.insertStepLog(newLog)
                Log.d("HomeViewModel", "Inserted new log with id: $insertedId, date: $todayStart, steps: $steps")
            }
        } catch (e: Exception) {
            Log.e("HomeViewModel", "Error saving steps to database", e)
        }
    }
    
    private suspend fun syncToFirebase() {
        try {
            firebaseSyncService.syncUnsyncedStepLogs()
        } catch (e: Exception) {
            Log.e("HomeViewModel", "Error syncing to Firebase", e)
        }
    }
    
    private fun loadWeeklyData() {
        viewModelScope.launch {
            try {
                val endDate = LocalDate.now().atTime(LocalTime.MAX).atZone(ZoneOffset.UTC).toInstant().toEpochMilli()
                val startDate = LocalDate.now().minusDays(6).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
                
                stepLogRepository.getStepLogsByDateRange(startDate, endDate).collect { logs ->
                    _weeklySteps.value = logs
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error loading weekly data", e)
            }
        }
    }
    
    private fun updateMotivationalMessage(currentSteps: Long, goalSteps: Int) {
        val percentage = (currentSteps.toFloat() / goalSteps * 100).toInt()
        val remaining = goalSteps - currentSteps.toInt()
        
        _motivationalMessage.value = when {
            percentage >= 100 -> "Congratulations! You've reached your goal! 🎉"
            percentage >= 75 -> "You're very close to your goal, so keep pushing forward!"
            percentage >= 50 -> "You're halfway there! Keep going! 💪"
            percentage >= 25 -> "Great start! You've got $remaining steps to go!"
            else -> "Let's get moving! You've got $remaining steps to reach your goal!"
        }
    }
    
    fun refreshData() {
        loadTodaySteps()
        loadWeeklyData()
        syncUnsyncedToFirebase()
    }
    
    private fun syncUnsyncedToFirebase() {
        viewModelScope.launch {
            try {
                val result = firebaseSyncService.syncUnsyncedStepLogs()
                if (result.isSuccess) {
                    val syncedCount = result.getOrNull() ?: 0
                    if (syncedCount > 0) {
                        Log.d("HomeViewModel", "Successfully synced $syncedCount step logs to Firebase")
                    }
                } else {
                    Log.e("HomeViewModel", "Failed to sync to Firebase", result.exceptionOrNull())
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error syncing unsynced logs to Firebase", e)
            }
        }
    }
    
    fun loadDataForPeriod(period: String) {
        viewModelScope.launch {
            try {
                val endDate = LocalDate.now().atTime(LocalTime.MAX).atZone(ZoneOffset.UTC).toInstant().toEpochMilli()
                val startDate = when (period) {
                    "day" -> LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
                    "week" -> LocalDate.now().minusDays(6).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
                    "month" -> LocalDate.now().minusDays(29).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
                    "6month" -> LocalDate.now().minusMonths(6).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
                    "year" -> LocalDate.now().minusYears(1).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
                    else -> LocalDate.now().minusDays(6).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
                }
                
                stepLogRepository.getStepLogsByDateRange(startDate, endDate).collect { logs ->
                    _weeklySteps.value = logs
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error loading data for period: $period", e)
            }
        }
    }
}

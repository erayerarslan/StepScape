package com.erayerarslan.stepscape.ui.logs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erayerarslan.stepscape.data.local.entity.StepLog
import com.erayerarslan.stepscape.repository.StepLogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class LogsViewModel @Inject constructor(
    private val stepLogRepository: StepLogRepository
) : ViewModel() {
    
    val stepLogs: StateFlow<List<StepLog>> = stepLogRepository.getAllStepLogs()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
}

package com.erayerarslan.stepscape.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "step_logs")
data class StepLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: Long, // Timestamp
    val steps: Int,
    val syncedToFirebase: Boolean = false
)


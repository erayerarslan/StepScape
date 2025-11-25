package com.erayerarslan.stepscape.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.erayerarslan.stepscape.data.local.dao.StepLogDao
import com.erayerarslan.stepscape.data.local.entity.StepLog

@Database(
    entities = [StepLog::class],
    version = 1,
    exportSchema = false
)
abstract class StepScapeDatabase : RoomDatabase() {
    abstract fun stepLogDao(): StepLogDao
    
    companion object {
        const val DATABASE_NAME = "stepscape_database"
    }
}


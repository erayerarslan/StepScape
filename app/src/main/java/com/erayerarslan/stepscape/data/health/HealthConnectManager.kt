package com.erayerarslan.stepscape.data.health

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.aggregate.AggregationResult
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HealthConnectManager @Inject constructor(
    private val context: Context
) {
    
    private val healthConnectClient: HealthConnectClient? by lazy {
        try {
            HealthConnectClient.getOrCreate(context)
        } catch (e: Exception) {
            null
        }
    }
    
    val isAvailable: Boolean
        get() = healthConnectClient != null
    
    suspend fun hasAllPermissions(): Boolean {
        val client = healthConnectClient ?: return false
        return try {
            val requiredPermissions = getRequiredPermissions()
            val grantedPermissions: Set<String> = client.permissionController.getGrantedPermissions()
            grantedPermissions.containsAll(requiredPermissions)
        } catch (e: Exception) {
            false
        }
    }
    
    fun getPermissionController(): PermissionController? {
        return healthConnectClient?.permissionController
    }
    
    fun getRequiredPermissions(): Set<String> {
        val readPerm = HealthPermission.getReadPermission(StepsRecord::class)
        val writePerm = HealthPermission.getWritePermission(StepsRecord::class)
        return setOf(readPerm.toString(), writePerm.toString())
    }
    
    suspend fun getTodaySteps(): Long {
        val client = healthConnectClient ?: return 0L
        
        val startOfDay = LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant()
        val endOfDay = LocalDate.now().atTime(LocalTime.MAX).atZone(ZoneOffset.UTC).toInstant()
        
        return try {
            val request = AggregateRequest(
                metrics = setOf(StepsRecord.COUNT_TOTAL),
                timeRangeFilter = TimeRangeFilter.between(startOfDay, endOfDay)
            )
            
            val result: AggregationResult = client.aggregate(request)
            val count = result[StepsRecord.COUNT_TOTAL]
            when {
                count != null -> {
                    try {
                        (count as? Long) ?: (count.toString().toLongOrNull() ?: 0L)
                    } catch (e: Exception) {
                        0L
                    }
                }
                else -> 0L
            }
        } catch (e: Exception) {
            e.printStackTrace()
            0L
        }
    }
    
    suspend fun getStepsForDateRange(startDate: Instant, endDate: Instant): Long {
        val client = healthConnectClient ?: return 0L
        
        return try {
            val request = AggregateRequest(
                metrics = setOf(StepsRecord.COUNT_TOTAL),
                timeRangeFilter = TimeRangeFilter.between(startDate, endDate)
            )
            
            val result: AggregationResult = client.aggregate(request)
            val count = result[StepsRecord.COUNT_TOTAL]
            when {
                count != null -> {
                    try {
                        (count as? Long) ?: (count.toString().toLongOrNull() ?: 0L)
                    } catch (e: Exception) {
                        0L
                    }
                }
                else -> 0L
            }
        } catch (e: Exception) {
            e.printStackTrace()
            0L
        }
    }
}

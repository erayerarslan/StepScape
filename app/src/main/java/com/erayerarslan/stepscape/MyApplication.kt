package com.erayerarslan.stepscape

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.jakewharton.threetenabp.AndroidThreeTen
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class MyApplication : Application(), Application.ActivityLifecycleCallbacks {

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        Timber.plant(Timber.DebugTree())
        registerActivityLifecycleCallbacks(this)
        setInstance(this)
    }

    companion object {
        @Volatile
        private var _instance: MyApplication? = null

        fun getInstance(): MyApplication {
            return _instance ?: synchronized(this) {
                _instance ?: throw IllegalStateException("MyApplication is not initialized")
            }
        }

        @Synchronized
        private fun setInstance(app: MyApplication) {
            if (_instance == null) {
                _instance = app
            }
        }
    }

    override fun onActivityStarted(activity: Activity) {
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityResumed(activity: Activity) {}
    override fun onActivityPaused(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}
}
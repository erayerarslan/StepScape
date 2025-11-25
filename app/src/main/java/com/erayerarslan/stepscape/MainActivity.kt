package com.erayerarslan.stepscape

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.StepsRecord
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.erayerarslan.stepscape.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController

    private val healthConnectPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController
        
        requestHealthConnectPermissions()
    }
    
    private fun requestHealthConnectPermissions() {
        lifecycleScope.launch {
            try {
                val client = HealthConnectClient.getOrCreate(this@MainActivity)
                val readPerm = HealthPermission.getReadPermission(StepsRecord::class)
                val writePerm = HealthPermission.getWritePermission(StepsRecord::class)
                val requiredPermissions = setOf(readPerm.toString(), writePerm.toString())
                
                val grantedPermissions: Set<String> = client.permissionController.getGrantedPermissions()
                
                if (!grantedPermissions.containsAll(requiredPermissions)) {
                    val intent = Intent("androidx.health.ACTION_SHOW_PERMISSIONS_RATIONALE")
                    intent.setPackage("com.google.android.apps.healthdata")
                    try {
                        startActivity(intent)
                    } catch (e: Exception) {
                        try {
                            val healthConnectIntent = packageManager.getLaunchIntentForPackage("com.google.android.apps.healthdata")
                            if (healthConnectIntent != null) {
                                startActivity(healthConnectIntent)
                            } else {
                                android.util.Log.e("MainActivity", "Health Connect app not found")
                            }
                        } catch (e2: Exception) {
                            android.util.Log.e("MainActivity", "Failed to open Health Connect", e2)
                        }
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("MainActivity", "Health Connect not available", e)
            }
        }
    }
    
    override fun onSupportNavigateUp(): Boolean {
        return  navController.navigateUp() ||   super.onSupportNavigateUp()
    }
}


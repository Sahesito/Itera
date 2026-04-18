package com.sahe.itera

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import com.sahe.itera.data.preferences.SettingsRepository
import com.sahe.itera.presentation.navigation.IteraNavGraph
import com.sahe.itera.presentation.theme.IteraTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { /* no-op */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        setContent {
            val settingsRepo = remember { SettingsRepository(applicationContext) }
            val darkTheme by settingsRepo.darkTheme.collectAsState(initial = false)

            IteraTheme(darkTheme = darkTheme) {
                val navController = rememberNavController()

                BackHandler(enabled = true) {
                    if (navController.previousBackStackEntry != null) {
                        navController.popBackStack()
                    }
                }

                IteraNavGraph(navController = navController)
            }
        }
    }
}
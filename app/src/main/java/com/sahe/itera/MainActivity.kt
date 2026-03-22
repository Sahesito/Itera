package com.sahe.itera

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
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

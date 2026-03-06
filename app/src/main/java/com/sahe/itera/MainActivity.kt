package com.sahe.itera

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.sahe.itera.presentation.navigation.IteraNavGraph
import com.sahe.itera.presentation.theme.IteraTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            IteraTheme {
                val navController = rememberNavController()
                IteraNavGraph(navController = navController)
            }
        }
    }
}

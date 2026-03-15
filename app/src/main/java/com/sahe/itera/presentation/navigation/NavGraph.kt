package com.sahe.itera.presentation.navigation
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sahe.itera.presentation.schedule.ScheduleScreen
import com.sahe.itera.presentation.screens.grades.GradesScreen
//import com.sahe.itera.presentation.screens.grades.GradesScreen
import com.sahe.itera.presentation.screens.home.HomeScreen
import com.sahe.itera.presentation.screens.subjects.SubjectDetailScreen
import com.sahe.itera.presentation.screens.subjects.SubjectsScreen
import com.sahe.itera.presentation.screens.tasks.TaskDetailScreen
import com.sahe.itera.presentation.screens.tasks.TasksScreen

private fun NavHostController.safePopBackStack() {
    if (previousBackStackEntry != null) popBackStack()
}

@Composable
fun IteraNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }

        composable(Screen.Subjects.route) {
            SubjectsScreen(
                onBack = { navController.safePopBackStack() },
                navController = navController
            )
        }

        composable(
            route = Screen.SubjectDetail.route,
            arguments = listOf(navArgument("subjectId") { type = NavType.LongType })
        ) { backStackEntry ->
            val subjectId = backStackEntry.arguments?.getLong("subjectId") ?: return@composable
            SubjectDetailScreen(
                subjectId = subjectId,
                onBack = { navController.safePopBackStack() },
                onTaskClick = { taskId ->
                    navController.navigate(Screen.TaskDetail.createRoute(taskId))
                }
            )
        }

        composable(Screen.Tasks.route) {
            TasksScreen(
                onBack = { navController.safePopBackStack() },
                navController = navController
            )
        }

        composable(
            route = Screen.TaskDetail.route,
            arguments = listOf(navArgument("taskId") { type = NavType.LongType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getLong("taskId") ?: return@composable
            TaskDetailScreen(
                taskId = taskId,
                onBack = { navController.safePopBackStack() }
            )
        }

        composable(
            route = Screen.Notes.route,
            arguments = listOf(navArgument("subjectId") {
                type = NavType.LongType
                defaultValue = -1L
            })
        ) { backStackEntry ->
            val subjectId = backStackEntry.arguments?.getLong("subjectId") ?: -1L
            GradesScreen(
                initialSubjectId = if (subjectId == -1L) null else subjectId,
                onBack = { navController.safePopBackStack() }
            )
        }

        composable(Screen.Schedule.route) {
            ScheduleScreen(onBack = { navController.safePopBackStack() })
        }

        composable(Screen.Calendar.route) {
            PlaceholderScreen("Calendario") { navController.safePopBackStack() }
        }

        composable(Screen.Settings.route) {
            PlaceholderScreen("Ajustes") { navController.safePopBackStack() }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlaceholderScreen(title: String, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.ArrowBackIosNew, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Próximamente",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
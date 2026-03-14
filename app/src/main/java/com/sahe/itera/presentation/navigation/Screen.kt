package com.sahe.itera.presentation.navigation

sealed class Screen(val route: String) {
    object Home         : Screen("home")
    object Subjects     : Screen("subjects")
    object SubjectDetail: Screen("subject_detail/{subjectId}") {
        fun createRoute(subjectId: Long) = "subject_detail/$subjectId"
    }
    object Tasks        : Screen("tasks")
    object TaskDetail   : Screen("task_detail/{taskId}") {
        fun createRoute(taskId: Long) = "task_detail/$taskId"
    }
    object Schedule     : Screen("schedule")
    object Calendar     : Screen("calendar")
    object Notes : Screen("notes?subjectId={subjectId}") {
        fun createRoute(subjectId: Long? = null) =
            if (subjectId != null) "notes?subjectId=$subjectId"
            else "notes?subjectId=-1"
    }
    object Settings     : Screen("settings")
}
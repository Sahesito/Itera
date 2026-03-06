package com.sahe.itera.presentation.navigation

sealed class Screen(val route: String) {
    object Home     : Screen("home")
    object Subjects : Screen("subjects")
    object Tasks    : Screen("tasks")
    object Schedule : Screen("schedule")
    object Calendar : Screen("calendar")
}
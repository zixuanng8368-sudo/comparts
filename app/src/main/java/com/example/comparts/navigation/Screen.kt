package com.example.comparts.navigation

sealed class Screen(val route: String) {

    object Home : Screen("home")
    object Items : Screen("items")
    object Notification : Screen("notification")
    object Profile : Screen("profile")
    object Settings : Screen("settings")

    object Login : Screen("login")
    object Signup : Screen("signup")
}
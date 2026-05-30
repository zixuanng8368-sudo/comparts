// File path: app/src/main/java/com/example/comparts/navigation/Screen.kt
package com.example.comparts.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Items : Screen("items")
    object Transaction : Screen("transaction")
    object Supplier : Screen("supplier")
    object Report : Screen("report")

    // Existing authentication pages
    object Login : Screen("login")
    object Signup : Screen("signup")
}
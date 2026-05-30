// File path: app/src/main/java/com/example/comparts/ui/components/BottomBar.kt
package com.example.comparts.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.comparts.navigation.Screen

@Composable
fun BottomBar(navController: NavController) {
    // Collect backstack changes as state to dynamically check active route
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        // 1. Inventory / Items Page
        NavigationBarItem(
            selected = currentRoute == Screen.Items.route,
            onClick = { navigateToTab(navController, Screen.Items.route) },
            icon = { Icon(Icons.Default.List, contentDescription = "Inventory") },
            label = { Text("Inventory") }
        )

        // 2. Transaction Page
        NavigationBarItem(
            selected = currentRoute == Screen.Transaction.route,
            onClick = { navigateToTab(navController, Screen.Transaction.route) },
            icon = { Icon(Icons.Default.Refresh, contentDescription = "Transactions") },
            label = { Text("Transactions") }
        )

        // 3. Home Page
        NavigationBarItem(
            selected = currentRoute == Screen.Home.route,
            onClick = { navigateToTab(navController, Screen.Home.route) },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") }
        )

        // 4. Supplier Page
        NavigationBarItem(
            selected = currentRoute == Screen.Supplier.route,
            onClick = { navigateToTab(navController, Screen.Supplier.route) },
            icon = { Icon(Icons.Default.Person, contentDescription = "Suppliers") },
            label = { Text("Suppliers") }
        )

        // 5. Report Page
        NavigationBarItem(
            selected = currentRoute == Screen.Report.route,
            onClick = { navigateToTab(navController, Screen.Report.route) },
            icon = { Icon(Icons.Default.Info, contentDescription = "Reports") },
            label = { Text("Reports") }
        )
    }
}

// Utility function to cleanly pop back to start screen to prevent excessive stack building
private fun navigateToTab(navController: NavController, route: String) {
    if (navController.currentDestination?.route != route) {
        navController.navigate(route) {
            popUpTo(Screen.Home.route) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}
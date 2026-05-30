// File path: app/src/main/java/com/example/comparts/MainActivity.kt
package com.example.comparts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.comparts.navigation.Screen
import com.example.comparts.ui.components.BottomBar
import com.example.comparts.ui.pages.auth.LoginScreen
import com.example.comparts.ui.pages.auth.SignupScreen
import com.example.comparts.ui.pages.home.HomeScreen
import com.example.comparts.ui.pages.items.AddItemScreen
import com.example.comparts.ui.pages.items.EditItemScreen
import com.example.comparts.ui.pages.items.ItemScreen
import com.example.comparts.ui.pages.report.ReportScreen
import com.example.comparts.ui.pages.supplier.SupplierScreen
import com.example.comparts.ui.pages.transaction.TransactionScreen
import com.example.comparts.ui.theme.ComPartsTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ComPartsTheme {
                val navController = rememberNavController()

                // Track current route to hide bottom bar on Auth screens
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                val showBottomBar = currentRoute in listOf(
                    Screen.Home.route,
                    Screen.Items.route,
                    Screen.Transaction.route,
                    Screen.Supplier.route,
                    Screen.Report.route
                )

                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            BottomBar(navController)
                        }
                    }
                ) { padding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Home.route, // Set Login as the initial screen
                        modifier = Modifier.padding(padding)
                    ) {
                        // --- Auth Routes ---
                        composable(Screen.Login.route) {
                            LoginScreen(navController)
                        }

                        composable(Screen.Signup.route) {
                            SignupScreen(navController)
                        }

                        // --- Main App Routes ---
                        composable(Screen.Home.route) {
                            HomeScreen()
                        }

                        composable(Screen.Items.route) {
                            ItemScreen(navController)
                        }

                        composable("add_item") {
                            AddItemScreen(navController)
                        }
                        composable("edit_item") {
                            EditItemScreen(navController)
                        }

                        composable(Screen.Transaction.route) {
                            TransactionScreen()
                        }

                        composable(Screen.Supplier.route) {
                            SupplierScreen()
                        }

                        composable(Screen.Report.route) {
                            ReportScreen()
                        }
                    }
                }
            }
        }
    }
}
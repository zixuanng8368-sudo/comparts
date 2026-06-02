// File path: app/src/main/java/com/example/comparts/MainActivity.kt
package com.example.comparts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.example.comparts.ui.pages.items.ItemDetailScreen
import com.example.comparts.ui.pages.items.ItemScreen
import com.example.comparts.ui.pages.profile.ProfileScreen
import com.example.comparts.ui.pages.profile.EditProfileScreen
import com.example.comparts.ui.pages.report.ReportScreen
import com.example.comparts.ui.pages.category.CategoryScreen
import com.example.comparts.ui.pages.category.AddCategoryScreen
import com.example.comparts.ui.pages.category.EditCategoryScreen
import com.example.comparts.ui.pages.supplier.AddSupplierScreen
import com.example.comparts.ui.pages.supplier.EditSupplierScreen
import com.example.comparts.ui.pages.supplier.SupplierScreen
import com.example.comparts.ui.pages.transaction.AddTransactionScreen
import com.example.comparts.ui.pages.transaction.EditTransactionScreen
import com.example.comparts.ui.pages.transaction.TransactionDetailScreen
import com.example.comparts.ui.pages.transaction.TransactionScreen
import com.example.comparts.ui.theme.ComPartsTheme
import com.example.comparts.viewmodel.AuthViewModel
import com.example.comparts.viewmodel.ItemViewModel
import com.example.comparts.viewmodel.SupplierViewModel
import com.example.comparts.viewmodel.TransactionViewModel
import com.example.comparts.viewmodel.CategoryViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ComPartsTheme {
                val navController = rememberNavController()

                // Initialize ViewModels here so they share a single instance across screens
                val itemViewModel: ItemViewModel = viewModel()
                val supplierViewModel: SupplierViewModel = viewModel()
                val transactionViewModel: TransactionViewModel = viewModel()
                val categoryViewModel: CategoryViewModel = viewModel()
                val authViewModel: AuthViewModel = viewModel()

                // Track current route to hide bottom bar on Auth screens
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                val showBottomBar = currentRoute in listOf(
                    Screen.Home.route,
                    Screen.Items.route,
                    Screen.Transaction.route,
                    Screen.Supplier.route,
                    Screen.Report.route,
                    Screen.Category.route,
                    "add_item",
                    "add_transaction",
                    "add_supplier",
                    "transaction" // Adding "transaction" route explicitly if needed
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
                        startDestination = Screen.Login.route,
                        modifier = Modifier.padding(padding)
                    ) {
                        // --- Auth Routes ---
                        composable(Screen.Login.route) { LoginScreen(navController) }
                        composable(Screen.Signup.route) { SignupScreen(navController) }

                        // --- Main App Routes ---
                        composable(Screen.Home.route) { HomeScreen(navController, itemViewModel, supplierViewModel, transactionViewModel) }

                        // --- Profile ---
                        composable(Screen.Profile.route) { ProfileScreen(navController, authViewModel) }
                        composable("edit_profile") { EditProfileScreen(navController, authViewModel) }

                        // --- Items / Inventory ---
                        composable(Screen.Items.route) { ItemScreen(navController, itemViewModel) }
                        composable("add_item") { AddItemScreen(navController, itemViewModel) }
                        composable("item_detail/{itemId}") { backStackEntry ->
                            val itemId = backStackEntry.arguments?.getString("itemId")
                            ItemDetailScreen(navController, itemId, itemViewModel, transactionViewModel, categoryViewModel, supplierViewModel)
                        }
                        composable("edit_item/{itemId}") { backStackEntry ->
                            val itemId = backStackEntry.arguments?.getString("itemId")
                            EditItemScreen(navController, itemViewModel, itemId)
                        }

                        // --- Suppliers ---
                        composable(Screen.Supplier.route) { SupplierScreen(navController, supplierViewModel) }
                        composable("add_supplier") { AddSupplierScreen(navController, supplierViewModel) }
                        composable("edit_supplier/{supplierId}") { backStackEntry ->
                            val supplierId = backStackEntry.arguments?.getString("supplierId")
                            EditSupplierScreen(navController, supplierViewModel, supplierId)
                        }

                        // --- Transactions ---
                        composable(Screen.Transaction.route) { TransactionScreen(navController, transactionViewModel, itemViewModel) }
                        composable("add_transaction") { AddTransactionScreen(navController, transactionViewModel, itemViewModel, authViewModel) }
                        composable("transaction_detail/{transactionId}") { backStackEntry ->
                            val transactionId = backStackEntry.arguments?.getString("transactionId")
                            TransactionDetailScreen(navController, transactionId, transactionViewModel, itemViewModel)
                        }
                        composable("edit_transaction/{transactionId}") { backStackEntry ->
                            val transactionId = backStackEntry.arguments?.getString("transactionId")
                            EditTransactionScreen(navController, transactionId, transactionViewModel, itemViewModel, authViewModel)
                        }

                        // --- Reports ---
                        composable(Screen.Report.route) { ReportScreen() }

                        // --- Categories ---
                        composable(Screen.Category.route) { CategoryScreen(navController, categoryViewModel) }
                        composable("add_category") { AddCategoryScreen(navController, categoryViewModel) }
                        composable("edit_category/{categoryId}") { backStackEntry ->
                            val categoryId = backStackEntry.arguments?.getString("categoryId")
                            EditCategoryScreen(navController, categoryId, categoryViewModel)
                        }
                    }
                }
            }
        }
    }
}
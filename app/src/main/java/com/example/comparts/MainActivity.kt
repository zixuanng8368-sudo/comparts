// File path: app/src/main/java/com/example/comparts/MainActivity.kt
package com.example.comparts

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
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
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                registerForActivityResult(ActivityResultContracts.RequestPermission()) {}.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        setContent {
            ComPartsTheme {
                val navController = rememberNavController()

                val itemViewModel: ItemViewModel = viewModel()
                val supplierViewModel: SupplierViewModel = viewModel()
                val transactionViewModel: TransactionViewModel = viewModel()
                val categoryViewModel: CategoryViewModel = viewModel()
                val authViewModel: AuthViewModel = viewModel()

                // Auto-login logic
                LaunchedEffect(Unit) {
                    val user = authViewModel.currentUser()
                    if (user != null) {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                }

                LaunchedEffect(intent) {
                    val navigateTo = intent.getStringExtra("navigate_to")
                    if (navigateTo == "items") {
                        navController.navigate(Screen.Items.route)
                    }
                }

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
                    "transaction"
                )

                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            BottomBar(navController)
                        }
                    }
                ) { paddingValues ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Login.route,
                        modifier = Modifier.padding(paddingValues)
                    ) {
                        composable(Screen.Login.route) { LoginScreen(navController) }
                        composable(Screen.Signup.route) { SignupScreen(navController) }
                        composable(Screen.Home.route) { HomeScreen(navController, itemViewModel, supplierViewModel, transactionViewModel, authViewModel) }
                        composable(Screen.Profile.route) { ProfileScreen(navController, authViewModel) }
                        composable("edit_profile") { EditProfileScreen(navController, authViewModel) }
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
                        composable(Screen.Supplier.route) { SupplierScreen(navController, supplierViewModel) }
                        composable("add_supplier") { AddSupplierScreen(navController, supplierViewModel) }
                        composable("edit_supplier/{supplierId}") { backStackEntry ->
                            val supplierId = backStackEntry.arguments?.getString("supplierId")
                            EditSupplierScreen(navController, supplierViewModel, supplierId)
                        }
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
                        composable(Screen.Report.route) { ReportScreen(itemViewModel, transactionViewModel, categoryViewModel) }
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

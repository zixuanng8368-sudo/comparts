package com.example.comparts.ui.pages.items

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Category
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.comparts.ui.components.EmptyState
import com.example.comparts.ui.components.InventoryCard
import com.example.comparts.ui.components.LoadingState
import com.example.comparts.viewmodel.ItemViewModel
import com.example.comparts.viewmodel.CategoryViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemScreen(
    navController: NavController,
    viewModel: ItemViewModel = viewModel(),
    categoryViewModel: CategoryViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    val items by viewModel.items.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val categories by categoryViewModel.categories.collectAsState()

    var isRefreshing by remember { mutableStateOf(false) }

    val primaryBlue = Color(0xFF4A61F7)
    val criticalRed = Color(0xFFFF4C4C)
    val healthyGreen = Color(0xFF00C853)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Inventory", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Text("${items.size} items tracked", fontSize = 14.sp, color = Color.Gray)
            }
            IconButton(onClick = { navController.navigate("category") }) {
                Icon(Icons.Default.Category, contentDescription = "Categories", tint = primaryBlue)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF0F0F0),
                unfocusedContainerColor = Color(0xFFF0F0F0),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Content Area
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                viewModel.loadItems()
                categoryViewModel.loadCategories()
                isRefreshing = false
            },
            modifier = Modifier.weight(1f)
        ) {
            if (isLoading && items.isEmpty()) {
                LoadingState()
            } else if (items.isEmpty()) {
                EmptyState(message = "Start by adding your first computer part.")
            } else {
                val filteredItems = items.filter { it.itemName.contains(searchQuery, ignoreCase = true) }
                if (filteredItems.isEmpty()) {
                    EmptyState(message = "No items match your search.")
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredItems) { item ->
                            val categoryName = categories.find { it.categoryId == item.categoryId }?.categoryName ?: "General"
                            InventoryCard(
                                category = categoryName,
                                name = item.itemName,
                                sku = item.itemSku,
                                price = "RM ${String.format(Locale.US, "%.2f", item.itemPrice)}",
                                quantity = item.itemStockQuantity.toString(),
                                stockStatus = if (item.itemStockQuantity > item.itemMinStockLevel) "IN STOCK" else if (item.itemStockQuantity > 0) "LOW STOCK" else "OUT OF STOCK",
                                badgeColor = if (item.itemStockQuantity > item.itemMinStockLevel) healthyGreen else criticalRed,
                                cardColor = primaryBlue,
                                imageUrl = item.itemImageUrl,
                                onClick = { navController.navigate("edit_item/${item.itemId}") }
                            )
                        }
                    }
                }
            }
        }

        // Add Button
        Button(
            onClick = { navController.navigate("add_item") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF000080)),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text("+ Add New Item", fontSize = 16.sp, color = Color.White)
        }
    }
}

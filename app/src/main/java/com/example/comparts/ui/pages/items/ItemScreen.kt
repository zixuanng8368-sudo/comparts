// File path: app/src/main/java/com/example/comparts/ui/pages/items/ItemScreen.kt
package com.example.comparts.ui.pages.items

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemScreen(navController: NavController, viewModel: ItemViewModel = viewModel()) {
    var searchQuery by remember { mutableStateOf("") }
    val items by viewModel.items.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

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
        Text("Inventory", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text("${items.size} items tracked", fontSize = 14.sp, color = Color.Gray)

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
        Box(modifier = Modifier.weight(1f)) {
            if (isLoading) {
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
                            InventoryCard(
                                category = "ITEM",
                                name = item.itemName,
                                sku = "ID: ${item.itemId}",
                                price = "Qty: ${item.itemStockQuantity}",
                                stockStatus = if (item.itemStockQuantity > 0) "${item.itemStockQuantity} UNITS" else "OUT OF STOCK",
                                badgeColor = if (item.itemStockQuantity > 5) healthyGreen else criticalRed,
                                cardColor = primaryBlue,
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

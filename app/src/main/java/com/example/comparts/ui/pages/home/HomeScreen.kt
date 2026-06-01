package com.example.comparts.ui.pages.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.comparts.viewmodel.ItemViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, itemViewModel: ItemViewModel = viewModel()) {
    val items by itemViewModel.items.collectAsState()
    
    val totalPartTypes = items.size
    val lowInStockCount = items.count { it.itemStockQuantity <= it.itemMinStockLevel }
    val totalInventoryValue = items.sumOf { it.itemPrice * it.itemStockQuantity }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard", style = MaterialTheme.typography.headlineMedium) },
                actions = {
                    IconButton(onClick = { navController.navigate("profile") }) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile",
                            modifier = Modifier.size(32.dp),
                            tint = Color(0xFF4A61F7)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Good Morning, Operator!",
                style = MaterialTheme.typography.titleMedium,
                color = Color.Gray
            )

            // Metrics Grid
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    MetricCard(
                        title = "Total Part Types",
                        value = totalPartTypes.toString(),
                        modifier = Modifier.weight(1f),
                        containerColor = Color(0xFFF0F2FF)
                    )
                    MetricCard(
                        title = "Low In Stock",
                        value = lowInStockCount.toString(),
                        modifier = Modifier.weight(1f),
                        containerColor = if (lowInStockCount > 0) Color(0xFFFFEBEE) else Color(0xFFE8F5E9),
                        valueColor = if (lowInStockCount > 0) Color(0xFFFF4C4C) else Color(0xFF00C853)
                    )
                }
                MetricCard(
                    title = "Total Inventory Value",
                    value = "RM ${String.format(Locale.US, "%,.2f", totalInventoryValue)}",
                    containerColor = Color(0xFFE3F2FD)
                )
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    valueColor: Color = Color.Unspecified
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = valueColor
            )
        }
    }
}

package com.example.comparts.ui.pages.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.comparts.viewmodel.ItemViewModel
import com.example.comparts.viewmodel.SupplierViewModel
import com.example.comparts.viewmodel.TransactionViewModel
import java.util.Locale

@Composable
fun HomeScreen(
    navController: NavController,
    itemViewModel: ItemViewModel = viewModel(),
    supplierViewModel: SupplierViewModel = viewModel(),
    transactionViewModel: TransactionViewModel = viewModel()
) {
    val items by itemViewModel.items.collectAsState()
    val suppliers by supplierViewModel.suppliers.collectAsState()
    val transactions by transactionViewModel.transactions.collectAsState()

    val totalPartTypes = items.size
    val lowInStockCount = items.count { it.itemStockQuantity <= it.itemMinStockLevel }
    val totalInventoryValue = items.sumOf { it.itemPrice * it.itemStockQuantity }

    val primaryBlue = Color(0xFF4A61F7)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // A. Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Dashboard",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "Good Morning, Operator!",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
            Surface(
                modifier = Modifier
                    .size(48.dp)
                    .clickable { navController.navigate("profile") },
                shape = CircleShape,
                color = Color(0xFFF0F0F0)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    modifier = Modifier.padding(8.dp),
                    tint = primaryBlue
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // B. 2x2 Metric Grid
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MetricCard(
                    title = "Total Parts",
                    value = totalPartTypes.toString(),
                    containerColor = Color(0xFFE8EAF6),
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Low In Stock",
                    value = lowInStockCount.toString(),
                    containerColor = if (lowInStockCount > 0) Color(0xFFFFEBEE) else Color(0xFFE8F5E9),
                    valueColor = if (lowInStockCount > 0) Color(0xFFFF4C4C) else Color(0xFF00C853),
                    modifier = Modifier.weight(1f)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MetricCard(
                    title = "Total Value",
                    value = "RM ${String.format(Locale.US, "%.2f", totalInventoryValue)}",
                    containerColor = Color(0xFFE1F5FE),
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Suppliers",
                    value = suppliers.size.toString(),
                    containerColor = Color(0xFFF3E5F5),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // C. Quick Actions Row
        Text("Quick Actions", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            QuickActionButton("Add Item", Icons.Default.Add, primaryBlue) { navController.navigate("add_item") }
            QuickActionButton("Transaction", Icons.Default.SwapHoriz, Color(0xFF00C853)) { navController.navigate("transaction") }
            QuickActionButton("Supplier", Icons.Default.LocalShipping, Color(0xFFFF9800)) { navController.navigate("add_supplier") }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // D. Low Stock Alerts
        val lowStockItems = items.filter { it.itemStockQuantity <= it.itemMinStockLevel }.take(3)
        if (lowStockItems.isNotEmpty()) {
            Text(
                text = "Low Stock Alerts",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            lowStockItems.forEach { item ->
                com.example.comparts.ui.components.InventoryCard(
                    category = "Part",
                    name = item.itemName,
                    sku = item.itemSku,
                    price = "RM ${String.format(Locale.US, "%.2f", item.itemPrice)}",
                    quantity = item.itemStockQuantity.toString(),
                    stockStatus = if (item.itemStockQuantity == 0) "OUT OF STOCK" else "LOW STOCK",
                    badgeColor = Color(0xFFFF4C4C),
                    cardColor = primaryBlue,
                    imageUrl = item.itemImageUrl,
                    onClick = { navController.navigate("item_detail/${item.itemId}") }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
            Spacer(modifier = Modifier.height(12.dp))
        }

        // E. Weekly Transaction Flow (Placeholder)
        Text("Weekly Flow", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA))
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "Chart Component Placeholder\n(IN vs OUT Activity)",
                    textAlign = TextAlign.Center,
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // E. Recent Activity List
        Text("Recent Activity", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
        val recentTransactions = transactions.take(3)
        if (recentTransactions.isEmpty()) {
            Text("No recent transactions", color = Color.Gray, fontSize = 14.sp)
        } else {
            recentTransactions.forEach { transaction ->
                RecentActivityItem(transaction, items.find { it.itemId == transaction.itemId }?.itemName ?: "Unknown Item")
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    containerColor: Color,
    modifier: Modifier = Modifier,
    valueColor: Color = Color.Black
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontSize = 12.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = valueColor)
        }
    }
}

@Composable
fun QuickActionButton(label: String, icon: ImageVector, color: Color, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.1f))
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = color)
        }
        Text(label, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp), fontWeight = FontWeight.Medium)
    }
}

@Composable
fun RecentActivityItem(transaction: com.example.comparts.data.model.Transaction, itemName: String) {
    val isIncoming = transaction.transactionType.uppercase() == "IN"
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F3F4))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isIncoming) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (isIncoming) Icons.Default.Add else Icons.Default.SwapHoriz,
                    contentDescription = null,
                    tint = if (isIncoming) Color(0xFF00C853) else Color(0xFFFF4C4C),
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(if (isIncoming) "Stock In" else "Stock Out", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text(itemName, fontSize = 12.sp, color = Color.Gray, maxLines = 1)
            }
            Text(
                text = "${if (isIncoming) "+" else "-"}${transaction.transactionQuantity}",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = if (isIncoming) Color(0xFF00C853) else Color(0xFFFF4C4C)
            )
        }
    }
}

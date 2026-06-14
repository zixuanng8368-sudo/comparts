package com.example.comparts.ui.pages.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.comparts.viewmodel.ItemViewModel
import com.example.comparts.viewmodel.SupplierViewModel
import com.example.comparts.viewmodel.TransactionViewModel
import com.example.comparts.viewmodel.AuthViewModel
import java.util.Locale

@Composable
fun HomeScreen(
    navController: NavController,
    itemViewModel: ItemViewModel = viewModel(),
    supplierViewModel: SupplierViewModel = viewModel(),
    transactionViewModel: TransactionViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val items by itemViewModel.items.collectAsState()
    val suppliers by supplierViewModel.suppliers.collectAsState()
    val transactions by transactionViewModel.transactions.collectAsState()
    val notificationsEnabled by authViewModel.notificationsEnabled.collectAsState()

    val totalPartTypes = items.size
    val lowStockList = items.filter { it.itemStockQuantity <= it.itemMinStockLevel }
    val lowInStockCount = lowStockList.size
    val totalInventoryValue = items.sumOf { it.itemPrice * it.itemStockQuantity }

    val primaryBlue = Color(0xFF4A61F7)
    val context = androidx.compose.ui.platform.LocalContext.current

    // Low Stock Notification Logic (System Alert)
    LaunchedEffect(lowInStockCount) {
        if (notificationsEnabled && lowInStockCount > 0) {
            com.example.comparts.util.NotificationHelper.showLowStockNotification(context, lowInStockCount)
        }
    }

    Scaffold(
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { /* View Notifications */ }) {
                        BadgedBox(
                            badge = {
                                if (lowInStockCount > 0) {
                                    Badge(containerColor = Color.Red) { Text(lowInStockCount.toString()) }
                                }
                            }
                        ) {
                            Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = primaryBlue)
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
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

            // C. Quick Actions
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
            val alertItems = lowStockList.take(3)
            if (alertItems.isNotEmpty()) {
                Text("Low Stock Alerts", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
                alertItems.forEach { item ->
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
            }

            // E. Weekly Transaction Flow
            Text("Weekly Flow", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    WeeklyFlowChart(transactions = transactions)
                    
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFF00C853)))
                        Text(" Stock In", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(end = 16.dp))
                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFFFF4C4C)))
                        Text(" Stock Out", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // F. Recent Activity List
            Text("Recent Activity", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
            val recentTransactions = transactions.take(3)
            if (recentTransactions.isEmpty()) {
                Text("No recent transactions", color = Color.Gray, fontSize = 14.sp)
            } else {
                recentTransactions.forEach { transaction ->
                    RecentActivityItem(transaction, items.find { it.itemId == transaction.itemId }?.itemName ?: "Unknown Item")
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun MetricCard(title: String, value: String, containerColor: Color, modifier: Modifier = Modifier, valueColor: Color = Color.Black) {
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
    val accentColor = if (isIncoming) Color(0xFF00C853) else Color(0xFFFF4C4C)
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isIncoming) Icons.Default.Add else Icons.Default.SwapHoriz,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isIncoming) "Stock In" else "Stock Out",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.Black
                )
                Text(
                    text = itemName,
                    fontSize = 13.sp,
                    color = Color.Gray,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
            }
            Text(
                text = "${if (isIncoming) "+" else "-"}${transaction.transactionQuantity}",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 18.sp,
                color = accentColor
            )
        }
    }
}

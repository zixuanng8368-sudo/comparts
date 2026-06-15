package com.example.comparts.ui.pages.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.comparts.viewmodel.ItemViewModel
import com.example.comparts.viewmodel.SupplierViewModel
import com.example.comparts.viewmodel.TransactionViewModel
import com.example.comparts.viewmodel.AuthViewModel
import kotlinx.coroutines.launch
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

    val context = androidx.compose.ui.platform.LocalContext.current
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    var alertsSectionY by remember { mutableFloatStateOf(0f) }

    // Low Stock Notification Logic (System Alert)
    LaunchedEffect(lowInStockCount) {
        if (notificationsEnabled && lowInStockCount > 0) {
            com.example.comparts.util.NotificationHelper.showLowStockNotification(context, lowInStockCount)
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
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
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Good Morning, Operator!",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
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
                            Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        modifier = Modifier
                            .size(48.dp)
                            .clickable { navController.navigate("profile") },
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            modifier = Modifier.padding(8.dp),
                            tint = MaterialTheme.colorScheme.primary
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
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Low In Stock",
                        value = lowInStockCount.toString(),
                        containerColor = if (lowInStockCount > 0) Color(0xFFFFEBEE).copy(alpha = if(isSystemInDarkTheme()) 0.2f else 1f) else Color(0xFFE8F5E9).copy(alpha = if(isSystemInDarkTheme()) 0.2f else 1f),
                        valueColor = if (lowInStockCount > 0) Color(0xFFFF4C4C) else Color(0xFF00C853),
                        modifier = Modifier.weight(1f),
                        onClick = {
                            if (lowInStockCount > 0) {
                                scope.launch {
                                    scrollState.animateScrollTo(alertsSectionY.toInt())
                                }
                            }
                        }
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    MetricCard(
                        title = "Total Value",
                        value = "RM ${String.format(Locale.US, "%.2f", totalInventoryValue)}",
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Suppliers",
                        value = suppliers.size.toString(),
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // C. Quick Actions
            Text("Quick Actions", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp), color = MaterialTheme.colorScheme.onBackground)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuickActionButton("Add Item", Icons.Default.Add, MaterialTheme.colorScheme.primary) { navController.navigate("add_item") }
                QuickActionButton("Transaction", Icons.Default.SwapHoriz, Color(0xFF00C853)) { navController.navigate("transaction") }
                QuickActionButton("Supplier", Icons.Default.LocalShipping, Color(0xFFFF9800)) { navController.navigate("add_supplier") }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // D. Low Stock Alerts
            val alertItems = lowStockList.take(3)
            if (alertItems.isNotEmpty()) {
                Text(
                    "Low Stock Alerts", 
                    fontSize = 18.sp, 
                    fontWeight = FontWeight.Bold, 
                    modifier = Modifier
                        .padding(bottom = 12.dp)
                        .onGloballyPositioned { coordinates ->
                            alertsSectionY = coordinates.positionInParent().y
                        }, 
                    color = MaterialTheme.colorScheme.onBackground
                )
                alertItems.forEach { item ->
                    com.example.comparts.ui.components.InventoryCard(
                        category = "Part",
                        name = item.itemName,
                        sku = item.itemSku,
                        price = "RM ${String.format(Locale.US, "%.2f", item.itemPrice)}",
                        quantity = item.itemStockQuantity.toString(),
                        stockStatus = if (item.itemStockQuantity == 0) "OUT OF STOCK" else "LOW STOCK",
                        badgeColor = Color(0xFFFF4C4C),
                        cardColor = MaterialTheme.colorScheme.primary,
                        imageUrl = item.itemImageUrl,
                        onClick = { navController.navigate("item_detail/${item.itemId}") }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            // E. Weekly Transaction Flow
            Text("Weekly Flow", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp), color = MaterialTheme.colorScheme.onBackground)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                        Text(" Stock In", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(end = 16.dp))
                        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(Color(0xFFFF4C4C)))
                        Text(" Stock Out", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // F. Recent Activity List
            Text("Recent Activity", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp), color = MaterialTheme.colorScheme.onBackground)
            val recentTransactions = transactions.take(3)
            if (recentTransactions.isEmpty()) {
                Text("No recent transactions", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
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
fun MetricCard(
    title: String, 
    value: String, 
    containerColor: Color, 
    modifier: Modifier = Modifier, 
    valueColor: Color = Color.Unspecified,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = if (onClick != null) modifier.clickable { onClick() } else modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = if(valueColor == Color.Unspecified) MaterialTheme.colorScheme.onSurface else valueColor)
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
        Text(label, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp), fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onBackground)
    }
}

@Composable
fun RecentActivityItem(transaction: com.example.comparts.data.model.Transaction, itemName: String) {
    val isIncoming = transaction.transactionType.uppercase() == "IN"
    val accentColor = if (isIncoming) Color(0xFF00C853) else Color(0xFFFF4C4C)
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = itemName,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
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

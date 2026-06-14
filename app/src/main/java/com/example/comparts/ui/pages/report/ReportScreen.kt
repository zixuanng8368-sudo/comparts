package com.example.comparts.ui.pages.report

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.comparts.viewmodel.ItemViewModel
import com.example.comparts.viewmodel.TransactionViewModel
import com.example.comparts.viewmodel.CategoryViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ReportScreen(
    itemViewModel: ItemViewModel = viewModel(),
    transactionViewModel: TransactionViewModel = viewModel(),
    categoryViewModel: CategoryViewModel = viewModel()
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    
    // 1. Backend Data Collection
    val items by itemViewModel.items.collectAsState()
    val transactions by transactionViewModel.transactions.collectAsState()
    val categories by categoryViewModel.categories.collectAsState()

    // 2. Tab State
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Monthly", "Quarterly", "Yearly")

    // 3. Time Filtering Logic
    val calendar = Calendar.getInstance()
    val currentMonth = calendar.get(Calendar.MONTH)
    val currentQuarter = currentMonth / 3
    val currentYear = calendar.get(Calendar.YEAR)

    // Derived Data based on real transactions and selected tab
    val filteredTransactions = transactions.filter { trans ->
        val transDate = try {
            val formats = listOf(
                SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()),
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            )
            var parsed: Date? = null
            for (format in formats) {
                try {
                    parsed = format.parse(trans.transactionDate ?: "")
                    if (parsed != null) break
                } catch (e: Exception) {}
            }
            parsed
        } catch (e: Exception) {
            null
        }
        
        if (transDate != null) {
            val transCal = Calendar.getInstance().apply { time = transDate }
            when (selectedTabIndex) {
                0 -> transCal.get(Calendar.MONTH) == currentMonth && transCal.get(Calendar.YEAR) == currentYear
                1 -> (transCal.get(Calendar.MONTH) / 3) == currentQuarter && transCal.get(Calendar.YEAR) == currentYear
                2 -> transCal.get(Calendar.YEAR) == currentYear
                else -> true
            }
        } else false
    }

    // Calculations
    val netStockIn = filteredTransactions.filter { it.transactionType == "IN" }.sumOf { it.transactionQuantity }
    val netStockOut = filteredTransactions.filter { it.transactionType == "OUT" }.sumOf { it.transactionQuantity }
    val totalInventoryValue = items.sumOf { it.itemPrice * it.itemStockQuantity }

    // Statistics logic
    val categoryDistribution = items.groupBy { it.categoryId }.map { (catId, itemList) ->
        val catName = categories.find { it.categoryId == catId }?.categoryName ?: "Uncategorized"
        val catValue = itemList.sumOf { it.itemPrice * it.itemStockQuantity }
        val percentage = if (totalInventoryValue > 0) (catValue / totalInventoryValue).toFloat() else 0f
        catName to percentage
    }.sortedByDescending { it.second }.take(5)

    val topMovingItems = filteredTransactions
        .filter { it.transactionType.uppercase() == "OUT" }
        .groupBy { it.itemId }
        .map { (itemId, transList) ->
            val itemName = items.find { it.itemId == itemId }?.itemName ?: "Unknown"
            val totalQty = transList.sumOf { it.transactionQuantity }
            itemName to totalQty
        }
        .sortedByDescending { it.second }
        .take(5)

    // Export Logic
    val csvContent = remember(filteredTransactions, items) {
        val header = "Date,Type,Item,Quantity,Reference\n"
        val rows = filteredTransactions.joinToString("\n") { trans ->
            val itemName = items.find { it.itemId == trans.itemId }?.itemName ?: "Unknown"
            "${trans.transactionDate},${trans.transactionType},${itemName},${trans.transactionQuantity},${trans.transactionReferenceNumber ?: ""}"
        }
        header + rows
    }

    val excelLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/csv")) { uri: Uri? ->
        uri?.let {
            context.contentResolver.openOutputStream(it)?.use { stream ->
                stream.write(csvContent.toByteArray())
            }
        }
    }

    val pdfLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("text/plain")) { uri: Uri? ->
        uri?.let {
            val report = "INVENTORY REPORT - ${tabs[selectedTabIndex].uppercase()}\n" +
                         "Generated on: ${SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())}\n\n" +
                         "Net In: +$netStockIn units\nNet Out: -$netStockOut units\nTotal Value: RM ${String.format(Locale.US, "%.2f", totalInventoryValue)}\n\n" +
                         "Transaction Summary:\n" + csvContent
            context.contentResolver.openOutputStream(it)?.use { stream ->
                stream.write(report.toByteArray())
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        Text(
            text = "Analytics & Reports",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = Color.White,
            contentColor = Color(0xFF4A61F7),
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = Color(0xFF4A61F7)
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    text = { Text(title, fontWeight = FontWeight.SemiBold) }
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ReportMetricCard(
                    title = "Net Stock In",
                    value = "+$netStockIn units",
                    textColor = Color(0xFF00C853),
                    modifier = Modifier.weight(1f)
                )
                ReportMetricCard(
                    title = "Net Stock Out",
                    value = "-$netStockOut units",
                    textColor = Color(0xFFFF4C4C),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Category Value Distribution", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (categoryDistribution.isEmpty()) {
                        Text("No data available", color = Color.Gray, modifier = Modifier.padding(vertical = 24.dp).align(Alignment.CenterHorizontally))
                    } else {
                        categoryDistribution.forEach { (name, percent) ->
                            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(name, fontSize = 14.sp, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text("${(percent * 100).toInt()}%", fontSize = 14.sp, color = Color.Gray)
                                }
                                LinearProgressIndicator(
                                    progress = { percent },
                                    modifier = Modifier.fillMaxWidth().height(8.dp).padding(top = 4.dp),
                                    color = Color(0xFF4A61F7),
                                    trackColor = Color(0xFFF1F3F4),
                                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Top Moving Parts", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (topMovingItems.isEmpty()) {
                        Text("No movement in this period", color = Color.Gray, modifier = Modifier.padding(vertical = 24.dp).align(Alignment.CenterHorizontally))
                    } else {
                        topMovingItems.forEachIndexed { index, (name, qty) ->
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier.size(32.dp).background(Color(0xFF4A61F7).copy(alpha = 0.1f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("${index + 1}", color = Color(0xFF4A61F7), fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(name, modifier = Modifier.weight(1f), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                Text("$qty units out", color = Color(0xFFFF4C4C), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                            if (index < topMovingItems.size - 1) HorizontalDivider(color = Color(0xFFF1F3F4))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF4A61F7)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Total Inventory Value", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
                        Text(
                            text = "RM ${String.format(Locale.US, "%.2f", totalInventoryValue)}",
                            color = Color.White,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text("Export Reports", fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { pdfLauncher.launch("report_${System.currentTimeMillis()}.txt") },
                    modifier = Modifier.weight(1f).height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Export Summary", fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = { excelLauncher.launch("inventory_${System.currentTimeMillis()}.csv") },
                    modifier = Modifier.weight(1f).height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D6F42)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Excel CSV", fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun ReportMetricCard(title: String, value: String, textColor: Color, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = title, fontSize = 12.sp, color = Color.Gray)
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = textColor)
        }
    }
}

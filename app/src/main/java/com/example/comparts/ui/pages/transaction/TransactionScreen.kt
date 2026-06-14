package com.example.comparts.ui.pages.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
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
import com.example.comparts.data.model.Transaction
import com.example.comparts.ui.components.EmptyState
import com.example.comparts.ui.components.LoadingState
import com.example.comparts.viewmodel.TransactionViewModel
import com.example.comparts.viewmodel.ItemViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionScreen(
    navController: NavController,
    viewModel: TransactionViewModel = viewModel(),
    itemViewModel: ItemViewModel = viewModel()
) {
    val transactions by viewModel.transactions.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val items by itemViewModel.items.collectAsState()
    
    var searchQuery by remember { mutableStateOf("") }
    var isRefreshing by remember { mutableStateOf(false) }
    
    var filterType by remember { mutableStateOf("All") } // "All", "Before", "After", "Between"
    var startDate by remember { mutableStateOf<Long?>(null) }
    var endDate by remember { mutableStateOf<Long?>(null) }
    
    var showDatePicker by remember { mutableStateOf(false) }
    var pickingEndDate by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    if (pickingEndDate) {
                        endDate = datePickerState.selectedDateMillis
                    } else {
                        startDate = datePickerState.selectedDateMillis
                    }
                    showDatePicker = false
                }) { Text("OK") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Transactions", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                Text("Audit trail", fontSize = 14.sp, color = Color.Gray)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Advanced Filter Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            var filterMenuExpanded by remember { mutableStateOf(false) }
            Box {
                FilterChip(
                    selected = filterType != "All",
                    onClick = { filterMenuExpanded = true },
                    label = { Text(filterType) },
                    trailingIcon = { Icon(Icons.Default.FilterList, contentDescription = null, modifier = Modifier.size(18.dp)) }
                )
                DropdownMenu(expanded = filterMenuExpanded, onDismissRequest = { filterMenuExpanded = false }) {
                    listOf("All", "Before", "After", "Between").forEach {
                        DropdownMenuItem(text = { Text(it) }, onClick = {
                            filterType = it
                            filterMenuExpanded = false
                            if (it == "All") {
                                startDate = null
                                endDate = null
                            }
                        })
                    }
                }
            }

            if (filterType != "All") {
                FilterChip(
                    selected = startDate != null,
                    onClick = {
                        pickingEndDate = false
                        showDatePicker = true
                    },
                    label = {
                        Text(if (startDate != null) SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(Date(startDate!!)) else "Start")
                    }
                )
            }
            if (filterType == "Between") {
                FilterChip(
                    selected = endDate != null,
                    onClick = {
                        pickingEndDate = true
                        showDatePicker = true
                    },
                    label = {
                        Text(if (endDate != null) SimpleDateFormat("dd/MM/yy", Locale.getDefault()).format(Date(endDate!!)) else "End")
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search part or ref...", color = Color.Gray) },
            trailingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
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

        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = {
                isRefreshing = true
                viewModel.loadTransactions()
                itemViewModel.loadItems()
                isRefreshing = false
            },
            modifier = Modifier.weight(1f)
        ) {
            val filteredTransactions = transactions.filter { transaction ->
                val itemName = items.find { it.itemId == transaction.itemId }?.itemName ?: ""
                val matchesSearch = itemName.contains(searchQuery, ignoreCase = true) || 
                                   transaction.transactionReferenceNumber?.contains(searchQuery, ignoreCase = true) == true
                
                val matchesDate = when (filterType) {
                    "Before" -> startDate?.let { s -> 
                        transaction.createdAt?.let { c ->
                            val transDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(c)
                            transDate?.before(Date(s)) ?: true
                        } ?: true
                    } ?: true
                    "After" -> startDate?.let { s -> 
                        transaction.createdAt?.let { c ->
                            val transDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(c)
                            transDate?.after(Date(s)) ?: true
                        } ?: true
                    } ?: true
                    "Between" -> startDate?.let { s -> 
                        endDate?.let { e ->
                            transaction.createdAt?.let { c ->
                                val transDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(c)
                                transDate?.after(Date(s)) == true && transDate.before(Date(e))
                            } ?: true
                        } ?: true
                    } ?: true
                    else -> true
                }
                
                matchesSearch && matchesDate
            }

            if (isLoading && transactions.isEmpty()) {
                LoadingState()
            } else if (filteredTransactions.isEmpty()) {
                EmptyState(message = "No transactions match your criteria.")
            } else {
                // Group transactions by date for timeline headers
                val groupedTransactions = filteredTransactions.groupBy { transaction ->
                    formatTimelineHeader(transaction.transactionDate)
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    groupedTransactions.forEach { (dateHeader, transactionList) ->
                        item {
                            Text(
                                text = dateHeader,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                            )
                        }
                        items(transactionList) { transaction ->
                            val item = items.find { it.itemId == transaction.itemId }
                            TransactionCard(transaction, item?.itemName ?: "Unknown Item", onClick = {
                                navController.navigate("transaction_detail/${transaction.transactionId}")
                            })
                        }
                    }
                }
            }
        }

        Button(
            onClick = { navController.navigate("add_transaction") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF000080)),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text("+ Add New Record", fontSize = 16.sp, color = Color.White)
        }
    }
}

@Composable
fun TransactionCard(transaction: Transaction, itemName: String, onClick: () -> Unit) {
    val isIncoming = transaction.transactionType.uppercase() == "IN"
    val accentColor = if (isIncoming) Color(0xFF00C853) else Color(0xFFFF4C4C)

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (isIncoming) Color(0xFFE8F5E9) else Color(0xFFFFEBEE))
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Type Circle
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(accentColor, shape = RoundedCornerShape(25.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isIncoming) "IN" else "OUT",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = itemName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = "Ref: ${transaction.transactionReferenceNumber ?: "N/A"}", fontSize = 12.sp, color = Color.Gray)
                transaction.transactionNotes?.let {
                    Text(text = it, fontSize = 12.sp, color = Color.Gray, maxLines = 1)
                }
            }

            Text(
                text = "${if (isIncoming) "+" else "-"}${transaction.transactionQuantity}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = accentColor
            )
        }
    }
}

private fun formatTimelineHeader(dateString: String?): String {
    if (dateString.isNullOrBlank()) return "Unknown Date"
    val displayFormat = SimpleDateFormat("d MMM yyyy", Locale.getDefault())

    // Try standard database format first, then ISO-8601
    val formats = listOf(
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()),
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    )

    for (format in formats) {
        try {
            val date = format.parse(dateString)
            if (date != null) return displayFormat.format(date)
        } catch (e: Exception) { /* Continue */ }
    }
    
    // Fallback to just the date part if parsing fails
    return dateString.split("T")[0].split(" ")[0]
}

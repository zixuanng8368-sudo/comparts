package com.example.comparts.ui.pages.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.comparts.viewmodel.TransactionViewModel
import com.example.comparts.viewmodel.ItemViewModel
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun TransactionDetailScreen(
    navController: NavController,
    transactionId: String?,
    viewModel: TransactionViewModel = viewModel(),
    itemViewModel: ItemViewModel = viewModel()
) {
    val transactions by viewModel.transactions.collectAsState()
    val transaction = transactions.find { it.transactionId == transactionId }
    val items by itemViewModel.items.collectAsState()
    val item = items.find { it.itemId == transaction?.itemId }

    var createdByName by remember { mutableStateOf("Loading...") }
    var updatedByName by remember { mutableStateOf("Loading...") }

    LaunchedEffect(transaction) {
        transaction?.let {
            createdByName = viewModel.getUserNameById(it.createdBy)
            updatedByName = viewModel.getUserNameById(it.updatedBy)
        }
    }

    val primaryBlue = MaterialTheme.colorScheme.primary
    val accentColor = if (transaction?.transactionType == "IN") Color(0xFF00C853) else Color(0xFFFF4C4C)

    val isoFormatter = DateTimeFormatter.ISO_DATE_TIME
    val displayFormatter = DateTimeFormatter.ofPattern("MMM dd, yyyy - hh:mm a", Locale.US)

    fun formatTimestamp(isoString: String?): String {
        return try {
            if (isoString.isNullOrBlank()) "N/A"
            else ZonedDateTime.parse(isoString, isoFormatter).format(displayFormatter)
        } catch (e: Exception) {
            isoString ?: "N/A"
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 24.dp, top = 8.dp)) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.clickable { navController.popBackStack() },
                    tint = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text("Transaction Details", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            }

            if (transaction == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Transaction not found", color = MaterialTheme.colorScheme.onBackground)
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        DetailRow("Item Name", item?.itemName ?: "Unknown")
                        DetailRow("Type", transaction.transactionType, textColor = accentColor)
                        DetailRow("Quantity", transaction.transactionQuantity.toString())
                        DetailRow("Reference", transaction.transactionReferenceNumber ?: "N/A")
                        DetailRow("Date", transaction.transactionDate ?: "N/A")
                        DetailRow("Notes", transaction.transactionNotes ?: "None")
                        DetailRow("Created By", createdByName)
                        DetailRow("Updated By", updatedByName)
                        DetailRow("Created At", formatTimestamp(transaction.createdAt))
                        DetailRow("Updated At", formatTimestamp(transaction.updatedAt))
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { navController.navigate("edit_transaction/${transaction.transactionId}") },
                    modifier = Modifier.fillMaxWidth().height(55.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryBlue),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Edit Transaction", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String, textColor: Color = MaterialTheme.colorScheme.onSurface) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp)
        Text(value, color = textColor, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
    }
}

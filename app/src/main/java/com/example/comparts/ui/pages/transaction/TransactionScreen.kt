// File path: app/src/main/java/com/example/comparts/ui/pages/transaction/TransactionScreen.kt
package com.example.comparts.ui.pages.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.comparts.data.model.Transaction
import com.example.comparts.ui.components.EmptyState
import com.example.comparts.ui.components.LoadingState
import com.example.comparts.viewmodel.TransactionViewModel

@Composable
fun TransactionScreen(viewModel: TransactionViewModel = viewModel()) {
    val transactions by viewModel.transactions.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text("Transactions", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text("${transactions.size} records found", fontSize = 14.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.weight(1f)) {
            if (isLoading) {
                LoadingState()
            } else if (transactions.isEmpty()) {
                EmptyState(message = "No transactions recorded yet.")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(transactions) { transaction ->
                        TransactionCard(transaction)
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionCard(transaction: Transaction) {
    val isIncoming = transaction.transactionType.uppercase() == "IN"
    val accentColor = if (isIncoming) Color(0xFF00C853) else Color(0xFFFF4C4C)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA))
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isIncoming) "Stock In" else "Stock Out",
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
                Text(text = "Item ID: ${transaction.itemId}", fontSize = 12.sp, color = Color.Gray)
                Text(text = transaction.transactionDate ?: "No Date", fontSize = 12.sp, color = Color.Gray)
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

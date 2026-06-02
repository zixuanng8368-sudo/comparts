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
import com.example.comparts.data.model.Transaction
import com.example.comparts.ui.components.BlueTextField
import com.example.comparts.viewmodel.ItemViewModel
import com.example.comparts.viewmodel.TransactionViewModel
import com.example.comparts.viewmodel.AuthViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    navController: NavController,
    transactionViewModel: TransactionViewModel = viewModel(),
    itemViewModel: ItemViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    var selectedItemId by remember { mutableStateOf<String?>(null) }
    var partSearchQuery by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var referenceNumber by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var transactionType by remember { mutableStateOf("IN") } // "IN" or "OUT"

    val items by itemViewModel.items.collectAsState()
    var userId by remember { mutableStateOf<String?>(null) }
    var partExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val user = authViewModel.getFullUser()
        userId = user?.id
    }

    val fieldColor = Color(0xFF5A75FF)
    val textColor = Color.White
    val currentTimestamp = SimpleDateFormat("d MMMM yyyy - hh:mm a", Locale.getDefault()).format(Date())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 24.dp, top = 8.dp)) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", modifier = Modifier.clickable { navController.popBackStack() })
            Spacer(modifier = Modifier.width(16.dp))
            Text("Record Transaction", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }

        // Searchable Part Selection
        Text(text = "Part*", color = Color.Gray, fontSize = 14.sp, modifier = Modifier.padding(bottom = 4.dp))
        ExposedDropdownMenuBox(
            expanded = partExpanded,
            onExpandedChange = { partExpanded = it },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            OutlinedTextField(
                value = partSearchQuery,
                onValueChange = { 
                    partSearchQuery = it
                    selectedItemId = null 
                    partExpanded = true 
                },
                placeholder = { Text("Search and Select Part", color = textColor.copy(alpha = 0.6f)) },
                modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable).fillMaxWidth(),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = partExpanded) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = fieldColor,
                    unfocusedContainerColor = fieldColor,
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor,
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                ),
                shape = RoundedCornerShape(12.dp)
            )
            
            val filteredItems = items.filter { it.itemName.contains(partSearchQuery, ignoreCase = true) }
            
            ExposedDropdownMenu(
                expanded = partExpanded && filteredItems.isNotEmpty(),
                onDismissRequest = { partExpanded = false }
            ) {
                filteredItems.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item.itemName) },
                        onClick = {
                            selectedItemId = item.itemId
                            partSearchQuery = item.itemName
                            partExpanded = false
                        }
                    )
                }
            }
        }

        BlueTextField(value = quantity, onValueChange = { quantity = it }, label = "Quantity*", placeholder = "e.g. 4", bgColor = fieldColor, textColor = textColor)
        BlueTextField(value = referenceNumber, onValueChange = { referenceNumber = it }, label = "Reference Number", placeholder = "e.g. PO-0482", bgColor = fieldColor, textColor = textColor)
        BlueTextField(value = notes, onValueChange = { notes = it }, label = "Notes", placeholder = "Optional remarks", bgColor = fieldColor, textColor = textColor)

        // Timestamp (Read-only)
        Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
            Text(text = "Timestamp", color = Color.Gray, fontSize = 14.sp, modifier = Modifier.padding(bottom = 4.dp))
            Surface(
                color = fieldColor,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = currentTimestamp, color = textColor)
                    Text(text = "Auto-recorded on save", color = textColor.copy(alpha = 0.6f), fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Toggle Switch for IN/OUT
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Surface(
                color = fieldColor,
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.height(50.dp).width(200.dp)
            ) {
                Row(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(if (transactionType == "IN") Color.White.copy(alpha = 0.3f) else Color.Transparent)
                            .clickable { transactionType = "IN" },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Stock In", color = if (transactionType == "IN") Color.White else Color.White.copy(alpha = 0.6f), fontWeight = FontWeight.Bold)
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(if (transactionType == "OUT") Color.White.copy(alpha = 0.3f) else Color.Transparent)
                            .clickable { transactionType = "OUT" },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Stock Out", color = if (transactionType == "OUT") Color.White else Color.White.copy(alpha = 0.6f), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (selectedItemId != null && quantity.isNotBlank() && userId != null) {
                    val finalUserId = userId!!
                    val newTransaction = Transaction(
                        itemId = selectedItemId!!,
                        transactionType = transactionType,
                        transactionQuantity = quantity.toIntOrNull() ?: 0,
                        transactionReferenceNumber = referenceNumber,
                        transactionNotes = notes,
                        transactionDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
                        createdBy = finalUserId,
                        updatedBy = finalUserId
                    )
                    transactionViewModel.addTransaction(newTransaction) {
                        navController.popBackStack()
                    }
                }
            },
            enabled = userId != null,
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF000080)),
            shape = RoundedCornerShape(24.dp)
        ) {
            if (userId == null) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("Add Record", fontSize = 16.sp, color = Color.White)
            }
        }
    }
}

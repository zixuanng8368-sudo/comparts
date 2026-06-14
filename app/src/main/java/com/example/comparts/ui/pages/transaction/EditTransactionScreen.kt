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
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTransactionScreen(
    navController: NavController,
    transactionId: String?,
    viewModel: TransactionViewModel = viewModel(),
    itemViewModel: ItemViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val transactions by viewModel.transactions.collectAsState()
    val existingTransaction = transactions.find { it.transactionId == transactionId }
    val items by itemViewModel.items.collectAsState()
    val currentUser = authViewModel.currentUser()

    var selectedItemId by remember { mutableStateOf<String?>(null) }
    var partSearchQuery by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var referenceNumber by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var transactionType by remember { mutableStateOf("IN") }
    
    var createdByName by remember { mutableStateOf("Loading...") }
    var updatedByName by remember { mutableStateOf("Loading...") }

    var partExpanded by remember { mutableStateOf(false) }
    
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var isSaving by remember { mutableStateOf(false) }

    LaunchedEffect(existingTransaction, items) {
        existingTransaction?.let {
            selectedItemId = it.itemId
            partSearchQuery = items.find { item -> item.itemId == it.itemId }?.itemName ?: ""
            quantity = it.transactionQuantity.toString()
            referenceNumber = it.transactionReferenceNumber ?: ""
            notes = it.transactionNotes ?: ""
            transactionType = it.transactionType
            
            createdByName = viewModel.getUserNameById(it.createdBy)
            updatedByName = viewModel.getUserNameById(it.updatedBy)
        }
    }

    val fieldColor = Color(0xFF5A75FF)
    val textColor = Color.White

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 24.dp, top = 8.dp)) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", modifier = Modifier.clickable { navController.popBackStack() })
                Spacer(modifier = Modifier.width(16.dp))
                Text("Edit Transaction", fontSize = 22.sp, fontWeight = FontWeight.Bold)
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

            Spacer(modifier = Modifier.height(16.dp))
            
            // Display User Info
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                Text("Created By: $createdByName", color = Color.Gray, fontSize = 14.sp)
                Text("Last Updated By: $updatedByName", color = Color.Gray, fontSize = 14.sp)
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
                    val qtyInt = quantity.toIntOrNull()
                    existingTransaction?.let { trans ->
                        if (selectedItemId == null) {
                            scope.launch { snackbarHostState.showSnackbar("Please select a part") }
                        } else if (qtyInt == null || qtyInt <= 0) {
                            scope.launch { snackbarHostState.showSnackbar("Please enter a valid quantity (> 0)") }
                        } else {
                            isSaving = true
                            val updatedTransaction = trans.copy(
                                itemId = selectedItemId!!,
                                transactionType = transactionType,
                                transactionQuantity = qtyInt,
                                transactionReferenceNumber = referenceNumber,
                                transactionNotes = notes,
                                updatedBy = currentUser?.id ?: "unknown"
                            )
                            viewModel.updateTransaction(updatedTransaction) { success, error ->
                                isSaving = false
                                if (success) {
                                    navController.popBackStack()
                                } else {
                                    scope.launch { snackbarHostState.showSnackbar(error ?: "Failed to update record") }
                                }
                            }
                        }
                    }
                },
                enabled = !isSaving,
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF000080)),
                shape = RoundedCornerShape(24.dp)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Update Record", fontSize = 16.sp, color = Color.White)
                }
            }
        }
    }
}

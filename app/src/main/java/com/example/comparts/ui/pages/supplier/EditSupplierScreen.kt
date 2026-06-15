package com.example.comparts.ui.pages.supplier

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
import com.example.comparts.data.model.Supplier
import com.example.comparts.ui.components.BlueTextField
import com.example.comparts.viewmodel.SupplierViewModel
import kotlinx.coroutines.launch

@Composable
fun EditSupplierScreen(navController: NavController, viewModel: SupplierViewModel = viewModel(), supplierId: String?) {
    var supplierName by remember { mutableStateOf("") }
    var supplierEmail by remember { mutableStateOf("") }
    var supplierPhone by remember { mutableStateOf("") }

    var existingSupplier by remember { mutableStateOf<Supplier?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var isSaving by remember { mutableStateOf(false) }

    LaunchedEffect(supplierId) {
        if (supplierId != null) {
            val supplier = viewModel.getSupplierById(supplierId)
            if (supplier != null) {
                existingSupplier = supplier
                supplierName = supplier.supplierName
                supplierEmail = supplier.supplierEmail ?: ""
                supplierPhone = supplier.supplierPhoneNumber ?: ""
            }
        }
        isLoading = false
    }

    val fieldColor = MaterialTheme.colorScheme.surfaceVariant
    val textColor = MaterialTheme.colorScheme.onSurface

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else {
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
                    Text("Edit Supplier", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                }

                BlueTextField(value = supplierName, onValueChange = { supplierName = it }, label = "Supplier Name*", placeholder = "", bgColor = fieldColor, textColor = textColor)
                BlueTextField(value = supplierEmail, onValueChange = { supplierEmail = it }, label = "Email", placeholder = "", bgColor = fieldColor, textColor = textColor)
                BlueTextField(value = supplierPhone, onValueChange = { supplierPhone = it }, label = "Phone Number", placeholder = "", bgColor = fieldColor, textColor = textColor)

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (supplierName.isBlank()) {
                            scope.launch { snackbarHostState.showSnackbar("Supplier Name is required") }
                        } else {
                            existingSupplier?.let {
                                isSaving = true
                                val updatedSupplier = it.copy(
                                    supplierName = supplierName,
                                    supplierEmail = if (supplierEmail.isBlank()) null else supplierEmail,
                                    supplierPhoneNumber = if (supplierPhone.isBlank()) null else supplierPhone
                                )
                                viewModel.updateSupplier(updatedSupplier) { success, error ->
                                    isSaving = false
                                    if (success) {
                                        navController.popBackStack()
                                    } else {
                                        scope.launch { snackbarHostState.showSnackbar(error ?: "Failed to update supplier") }
                                    }
                                }
                            }
                        }
                    },
                    enabled = !isSaving,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                    } else {
                        Text("Update Supplier", fontSize = 16.sp, color = MaterialTheme.colorScheme.onPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("Cancel", fontSize = 16.sp, color = MaterialTheme.colorScheme.onError)
                }
            }
        }
    }
}

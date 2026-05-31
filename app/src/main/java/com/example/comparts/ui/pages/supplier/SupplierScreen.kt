// File path: app/src/main/java/com/example/comparts/ui/pages/supplier/SupplierScreen.kt
package com.example.comparts.ui.pages.supplier

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
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.comparts.data.model.Supplier
import com.example.comparts.ui.components.EmptyState
import com.example.comparts.ui.components.LoadingState
import com.example.comparts.viewmodel.SupplierViewModel

@Composable
fun SupplierScreen(navController: NavController, viewModel: SupplierViewModel = viewModel()) {
    val suppliers by viewModel.suppliers.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text("Suppliers", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text("${suppliers.size} suppliers linked", fontSize = 14.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.weight(1f)) {
            if (isLoading) {
                LoadingState()
            } else if (suppliers.isEmpty()) {
                EmptyState(message = "Keep track of your parts providers here.")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(suppliers) { supplier ->
                        SupplierCard(supplier)
                    }
                }
            }
        }

        // Add Button
        Button(
            onClick = { navController.navigate("add_supplier") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF000080)),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text("+ Add New Supplier", fontSize = 16.sp, color = Color.White)
        }
    }
}

@Composable
fun SupplierCard(supplier: Supplier) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F2FF))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = supplier.supplierName, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF4A61F7))
            if (!supplier.supplierEmail.isNullOrBlank()) {
                Text(text = "Email: ${supplier.supplierEmail}", fontSize = 14.sp, color = Color.DarkGray)
            }
            if (!supplier.supplierPhoneNumber.isNullOrBlank()) {
                Text(text = "Phone: ${supplier.supplierPhoneNumber}", fontSize = 14.sp, color = Color.DarkGray)
            }
        }
    }
}

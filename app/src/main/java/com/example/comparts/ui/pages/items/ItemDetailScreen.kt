package com.example.comparts.ui.pages.items

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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.comparts.viewmodel.ItemViewModel
import com.example.comparts.viewmodel.TransactionViewModel
import com.example.comparts.viewmodel.CategoryViewModel
import com.example.comparts.viewmodel.SupplierViewModel
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun ItemDetailScreen(
    navController: NavController,
    itemId: String?,
    itemViewModel: ItemViewModel = viewModel(),
    transactionViewModel: TransactionViewModel = viewModel(),
    categoryViewModel: CategoryViewModel = viewModel(),
    supplierViewModel: SupplierViewModel = viewModel()
) {
    val items by itemViewModel.items.collectAsState()
    val item = items.find { it.itemId == itemId }
    
    val categories by categoryViewModel.categories.collectAsState()
    val categoryName = categories.find { it.categoryId == item?.categoryId }?.categoryName ?: "N/A"
    
    val suppliers by supplierViewModel.suppliers.collectAsState()
    val supplierName = suppliers.find { it.supplierId == item?.supplierId }?.supplierName ?: "N/A"

    val primaryBlue = Color(0xFF4A61F7)
    
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 24.dp, top = 8.dp)) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier.clickable { navController.popBackStack() }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text("Item Details", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }

        if (item == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Item not found")
            }
        } else {
            // Item Image
            if (!item.itemImageUrl.isNullOrEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF0F0F0)),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = item.itemImageUrl,
                        contentDescription = item.itemName,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA))
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    DetailRow("Item Name", item.itemName)
                    DetailRow("SKU", item.itemSku)
                    DetailRow("Category", categoryName)
                    DetailRow("Supplier", supplierName)
                    DetailRow("Price", "RM ${String.format(Locale.US, "%.2f", item.itemPrice)}")
                    DetailRow("Stock Quantity", item.itemStockQuantity.toString())
                    DetailRow("Min. Stock Level", item.itemMinStockLevel.toString())
                    DetailRow("Description", item.itemReference ?: "None")
                    DetailRow("Created At", formatTimestamp(item.createdAt))
                    DetailRow("Updated At", formatTimestamp(item.updatedAt))
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { navController.navigate("edit_item/${item.itemId}") },
                modifier = Modifier.fillMaxWidth().height(55.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryBlue),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Edit Item", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String, textColor: Color = Color.Black) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(label, color = Color.Gray, fontSize = 14.sp)
        Text(value, color = textColor, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
    }
}

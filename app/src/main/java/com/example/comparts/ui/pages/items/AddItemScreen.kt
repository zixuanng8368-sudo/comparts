package com.example.comparts.ui.pages.items

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.comparts.ui.components.BlueTextField

@Composable
fun AddItemScreen(navController: NavController) {
    var itemName by remember { mutableStateOf("") }
    var itemSku by remember { mutableStateOf("") }
    var itemCategory by remember { mutableStateOf("") }
    var itemPrice by remember { mutableStateOf("") }
    var currentStock by remember { mutableStateOf("") }
    var minThreshold by remember { mutableStateOf("") }
    var supplier by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    val fieldColor = Color(0xFF5A75FF)
    val textColor = Color.White

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 24.dp, top = 8.dp)) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", modifier = Modifier.clickable { navController.popBackStack() })
            Spacer(modifier = Modifier.width(16.dp))
            Text("Add New Item", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }

        BlueTextField(value = itemName, onValueChange = { itemName = it }, label = "Item Name*", placeholder = "Enter item name", bgColor = fieldColor, textColor = textColor)
        BlueTextField(value = itemSku, onValueChange = { itemSku = it }, label = "SKU", placeholder = "Enter SKU", bgColor = fieldColor, textColor = textColor)

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(modifier = Modifier.weight(1f)) {
                BlueTextField(value = itemCategory, onValueChange = { itemCategory = it }, label = "Category*", placeholder = "e.g. GPU", bgColor = fieldColor, textColor = textColor)
            }
            Box(modifier = Modifier.weight(1f)) {
                BlueTextField(value = itemPrice, onValueChange = { itemPrice = it }, label = "Unit Price (RM)*", placeholder = "0.00", bgColor = fieldColor, textColor = textColor)
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(modifier = Modifier.weight(1f)) {
                BlueTextField(value = currentStock, onValueChange = { currentStock = it }, label = "Current Stock*", placeholder = "0", bgColor = fieldColor, textColor = textColor)
            }
            Box(modifier = Modifier.weight(1f)) {
                BlueTextField(value = minThreshold, onValueChange = { minThreshold = it }, label = "Min. Threshold", placeholder = "0", bgColor = fieldColor, textColor = textColor)
            }
        }

        BlueTextField(value = supplier, onValueChange = { supplier = it }, label = "Supplier", placeholder = "Enter supplier name", bgColor = fieldColor, textColor = textColor)
        BlueTextField(value = description, onValueChange = { description = it }, label = "Description", placeholder = "Enter description", bgColor = fieldColor, textColor = textColor, singleLine = false, modifier = Modifier.height(100.dp))

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF000080)),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text("Add Item", fontSize = 16.sp, color = Color.White)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4C4C)),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text("Cancel", fontSize = 16.sp, color = Color.White)
        }
    }
}

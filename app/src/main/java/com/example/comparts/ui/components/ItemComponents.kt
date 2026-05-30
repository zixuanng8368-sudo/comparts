package com.example.comparts.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EmptyState(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "No Data Found", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            Text(text = message, fontSize = 14.sp, color = Color.Gray)
        }
    }
}

@Composable
fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = Color(0xFF4A61F7))
    }
}

@Composable
fun InventoryCard(
    category: String, name: String, sku: String, price: String,
    stockStatus: String, badgeColor: Color, cardColor: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Circle Badge
            Box(
                modifier = Modifier.size(50.dp).clip(CircleShape).background(Color(0xFF2C3E50)),
                contentAlignment = Alignment.Center
            ) {
                Text(category, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Item Info
            Column(modifier = Modifier.weight(1f)) {
                Text(name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(sku, color = Color(0xFFB0C4DE), fontSize = 12.sp)
                Text(price, color = Color(0xFFB0C4DE), fontSize = 12.sp)
            }

            // Stock Badge
            Box(
                modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(badgeColor).padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(stockStatus, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BlueTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    bgColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true
) {
    Column(modifier = modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Text(text = label, color = Color.Gray, fontSize = 14.sp, modifier = Modifier.padding(bottom = 4.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = textColor.copy(alpha = 0.6f)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = singleLine,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = bgColor,
                unfocusedContainerColor = bgColor,
                focusedTextColor = textColor,
                unfocusedTextColor = textColor,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            )
        )
    }
}

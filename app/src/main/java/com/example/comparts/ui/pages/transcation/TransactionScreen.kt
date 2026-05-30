// File path: app/src/main/java/com/example/comparts/ui/pages/transaction/TransactionScreen.kt
package com.example.comparts.ui.pages.transaction

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun TransactionScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Transaction Page", style = MaterialTheme.typography.headlineMedium)
    }
}
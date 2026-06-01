package com.example.comparts.ui.pages.category

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.comparts.data.model.Category
import com.example.comparts.ui.components.EmptyState
import com.example.comparts.ui.components.LoadingState
import com.example.comparts.viewmodel.CategoryViewModel

@Composable
fun CategoryScreen(navController: NavController, viewModel: CategoryViewModel = viewModel()) {
    val categories by viewModel.categories.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Categories", fontSize = 28.sp, fontWeight = FontWeight.Bold)
        }
        Text("${categories.size} categories total", fontSize = 14.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.weight(1f)) {
            if (isLoading) {
                LoadingState()
            } else if (categories.isEmpty()) {
                EmptyState(message = "Define categories for your computer parts.")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(categories) { category ->
                        CategoryCard(category, onClick = {
                            navController.navigate("edit_category/${category.categoryId}")
                        })
                    }
                }
            }
        }

        // Add Button
        Button(
            onClick = { navController.navigate("add_category") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF000080)),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text("+ Add New Category", fontSize = 16.sp, color = Color.White)
        }
    }
}

@Composable
fun CategoryCard(category: Category, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F2FF))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = category.categoryName, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color(0xFF4A61F7))
            if (!category.categoryDescription.isNullOrBlank()) {
                Text(text = category.categoryDescription, fontSize = 14.sp, color = Color.DarkGray)
            }
        }
    }
}

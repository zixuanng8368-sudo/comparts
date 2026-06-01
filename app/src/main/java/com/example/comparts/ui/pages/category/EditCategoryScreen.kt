package com.example.comparts.ui.pages.category

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
import com.example.comparts.data.model.Category
import com.example.comparts.ui.components.BlueTextField
import com.example.comparts.viewmodel.CategoryViewModel

@Composable
fun EditCategoryScreen(navController: NavController, categoryId: String?, viewModel: CategoryViewModel = viewModel()) {
    var categoryName by remember { mutableStateOf("") }
    var categoryDescription by remember { mutableStateOf("") }

    var existingCategory by remember { mutableStateOf<Category?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(categoryId) {
        if (categoryId != null) {
            val category = viewModel.getCategoryById(categoryId)
            if (category != null) {
                existingCategory = category
                categoryName = category.categoryName
                categoryDescription = category.categoryDescription ?: ""
            }
        }
        isLoading = false
    }

    val fieldColor = Color(0xFF5A75FF)
    val textColor = Color.White

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
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
                Text("Edit Category", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            }

            BlueTextField(value = categoryName, onValueChange = { categoryName = it }, label = "Category Name*", placeholder = "", bgColor = fieldColor, textColor = textColor)
            BlueTextField(value = categoryDescription, onValueChange = { categoryDescription = it }, label = "Description", placeholder = "", bgColor = fieldColor, textColor = textColor, singleLine = false, modifier = Modifier.height(100.dp))

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    existingCategory?.let {
                        val updatedCategory = it.copy(
                            categoryName = categoryName,
                            categoryDescription = if (categoryDescription.isBlank()) null else categoryDescription
                        )
                        viewModel.updateCategory(updatedCategory) {
                            navController.popBackStack()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF000080)),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text("Update Category", fontSize = 16.sp, color = Color.White)
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
}

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
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun AddCategoryScreen(navController: NavController, viewModel: CategoryViewModel = viewModel()) {
    var categoryName by remember { mutableStateOf("") }
    var categoryDescription by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var isSaving by remember { mutableStateOf(false) }

    val fieldColor = MaterialTheme.colorScheme.surfaceVariant
    val textColor = MaterialTheme.colorScheme.onSurface

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
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
                Text("Add New Category", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            }

            BlueTextField(value = categoryName, onValueChange = { categoryName = it }, label = "Category Name*", placeholder = "Enter name", bgColor = fieldColor, textColor = textColor)
            BlueTextField(value = categoryDescription, onValueChange = { categoryDescription = it }, label = "Description", placeholder = "Enter description", bgColor = fieldColor, textColor = textColor, singleLine = false, modifier = Modifier.height(100.dp))

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (categoryName.isBlank()) {
                        scope.launch { snackbarHostState.showSnackbar("Category Name is required") }
                    } else {
                        isSaving = true
                        val newCategory = Category(
                            categoryId = UUID.randomUUID().toString(),
                            categoryName = categoryName,
                            categoryDescription = if (categoryDescription.isBlank()) null else categoryDescription
                        )
                        viewModel.addCategory(newCategory) { success, error ->
                            isSaving = false
                            if (success) {
                                navController.popBackStack()
                            } else {
                                scope.launch { snackbarHostState.showSnackbar(error ?: "Failed to add category") }
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
                    Text("Add Category", fontSize = 16.sp, color = MaterialTheme.colorScheme.onPrimary)
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

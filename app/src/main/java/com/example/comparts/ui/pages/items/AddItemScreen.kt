package com.example.comparts.ui.pages.items

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.comparts.data.model.Item
import com.example.comparts.ui.components.BlueTextField
import com.example.comparts.viewmodel.ItemViewModel
import com.example.comparts.viewmodel.CategoryViewModel
import com.example.comparts.viewmodel.SupplierViewModel
import com.example.comparts.util.SkuGenerator
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemScreen(
    navController: NavController,
    itemViewModel: ItemViewModel = viewModel(),
    categoryViewModel: CategoryViewModel = viewModel(),
    supplierViewModel: SupplierViewModel = viewModel()
) {
    var itemName by remember { mutableStateOf("") }
    var itemSku by remember { mutableStateOf(SkuGenerator.generateSku()) }
    var selectedCategoryId by remember { mutableStateOf<String?>(null) }
    var selectedSupplierId by remember { mutableStateOf<String?>(null) }
    var itemPrice by remember { mutableStateOf("") }
    var currentStock by remember { mutableStateOf("") }
    var minThreshold by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var isSaving by remember { mutableStateOf(false) }
    
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    val context = LocalContext.current
    val primaryBlue = MaterialTheme.colorScheme.primary

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            bitmap = if (Build.VERSION.SDK_INT < 28) {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(context.contentResolver, it)
            } else {
                val source = ImageDecoder.createSource(context.contentResolver, it)
                ImageDecoder.decodeBitmap(source)
            }
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { b: Bitmap? ->
        bitmap = b
    }

    val categories by categoryViewModel.categories.collectAsState()
    val suppliers by supplierViewModel.suppliers.collectAsState()

    var categoryExpanded by remember { mutableStateOf(false) }
    var supplierExpanded by remember { mutableStateOf(false) }

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
                Text("Add New Item", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
            }

            // Image Picker UI
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap!!.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("Add Item Image", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { cameraLauncher.launch() },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Camera")
                }
                OutlinedButton(
                    onClick = { galleryLauncher.launch("image/*") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Gallery")
                }
            }

            BlueTextField(value = itemName, onValueChange = { itemName = it }, label = "Item Name*", placeholder = "Enter item name", bgColor = fieldColor, textColor = textColor)
            BlueTextField(value = itemSku, onValueChange = { itemSku = it }, label = "SKU*", placeholder = "Generated SKU", bgColor = fieldColor, textColor = textColor)

            // Category Dropdown
            Text(text = "Category*", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp, modifier = Modifier.padding(bottom = 4.dp))
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = !categoryExpanded },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                val selectedCategory = categories.find { it.categoryId == selectedCategoryId }
                OutlinedTextField(
                    value = selectedCategory?.categoryName ?: "Select Category",
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
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
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.categoryName) },
                            onClick = {
                                selectedCategoryId = category.categoryId
                                categoryExpanded = false
                            }
                        )
                    }
                    DropdownMenuItem(
                        text = { Text("+ Add New Category", color = primaryBlue) },
                        onClick = {
                            navController.navigate("add_category")
                            categoryExpanded = false
                        }
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    BlueTextField(value = itemPrice, onValueChange = { itemPrice = it }, label = "Unit Price (RM)*", placeholder = "0.00", bgColor = fieldColor, textColor = textColor)
                }
                Box(modifier = Modifier.weight(1f)) {
                    BlueTextField(value = currentStock, onValueChange = { currentStock = it }, label = "Current Stock*", placeholder = "0", bgColor = fieldColor, textColor = textColor)
                }
            }

            BlueTextField(value = minThreshold, onValueChange = { minThreshold = it }, label = "Min. Threshold", placeholder = "0", bgColor = fieldColor, textColor = textColor)

            // Supplier Dropdown
            Text(text = "Supplier", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 14.sp, modifier = Modifier.padding(bottom = 4.dp))
            ExposedDropdownMenuBox(
                expanded = supplierExpanded,
                onExpandedChange = { supplierExpanded = !supplierExpanded },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                val selectedSupplier = suppliers.find { it.supplierId == selectedSupplierId }
                OutlinedTextField(
                    value = selectedSupplier?.supplierName ?: "Select Supplier",
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = supplierExpanded) },
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
                ExposedDropdownMenu(
                    expanded = supplierExpanded,
                    onDismissRequest = { supplierExpanded = false }
                ) {
                    suppliers.forEach { supplier ->
                        DropdownMenuItem(
                            text = { Text(supplier.supplierName) },
                            onClick = {
                                selectedSupplierId = supplier.supplierId
                                supplierExpanded = false
                            }
                        )
                    }
                }
            }

            BlueTextField(value = description, onValueChange = { description = it }, label = "Description", placeholder = "Enter description", bgColor = fieldColor, textColor = textColor, singleLine = false, modifier = Modifier.height(100.dp))

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val priceVal = itemPrice.toDoubleOrNull()
                    val stockVal = currentStock.toIntOrNull()
                    
                    if (itemName.isBlank()) {
                        scope.launch { snackbarHostState.showSnackbar("Item Name is required") }
                    } else if (itemSku.isBlank()) {
                        scope.launch { snackbarHostState.showSnackbar("SKU is required") }
                    } else if (selectedCategoryId == null) {
                        scope.launch { snackbarHostState.showSnackbar("Please select a Category") }
                    } else if (itemPrice.isBlank() || priceVal == null) {
                        scope.launch { snackbarHostState.showSnackbar("Invalid or missing Unit Price") }
                    } else if (currentStock.isBlank() || stockVal == null) {
                        scope.launch { snackbarHostState.showSnackbar("Invalid or missing Current Stock") }
                    } else {
                        isSaving = true
                        val itemId = UUID.randomUUID().toString()
                        
                        val saveItem = { imageUrl: String? ->
                            val newItem = Item(
                                itemId = itemId,
                                itemName = itemName,
                                itemSku = itemSku,
                                itemPrice = priceVal,
                                itemStockQuantity = stockVal,
                                itemMinStockLevel = minThreshold.toIntOrNull() ?: 0,
                                categoryId = selectedCategoryId,
                                supplierId = selectedSupplierId,
                                itemReference = description,
                                itemImageUrl = imageUrl
                            )
                            itemViewModel.addItem(newItem) { success, error ->
                                isSaving = false
                                if (success) {
                                    navController.popBackStack()
                                } else {
                                    scope.launch {
                                        snackbarHostState.showSnackbar(error ?: "Failed to add item")
                                    }
                                }
                            }
                        }

                        if (bitmap != null) {
                            val stream = ByteArrayOutputStream()
                            bitmap!!.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                            itemViewModel.uploadImage(itemId, stream.toByteArray()) { imageUrl ->
                                saveItem(imageUrl)
                            }
                        } else {
                            saveItem(null)
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
                    Text("Add Item", fontSize = 16.sp, color = MaterialTheme.colorScheme.onPrimary)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

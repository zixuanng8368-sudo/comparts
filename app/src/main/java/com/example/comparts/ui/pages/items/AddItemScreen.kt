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
    
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    val context = LocalContext.current

    val primaryBlue = Color(0xFF4A61F7)

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            bitmap = if (Build.VERSION.SDK_INT < 28) {
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
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", modifier = Modifier.clickable { navController.popBackStack() })
            Spacer(modifier = Modifier.width(16.dp))
            Text("Add New Item", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }

        // Image Picker UI
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF0F0F0)),
            contentAlignment = Alignment.Center
        ) {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap!!.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color.Gray)
                    Text("Add Item Image", color = Color.Gray)
                }
            }
        }
        
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(onClick = { cameraLauncher.launch() }) {
                Icon(Icons.Default.CameraAlt, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Camera")
            }
            Button(onClick = { galleryLauncher.launch("image/*") }) {
                Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Gallery")
            }
        }

        BlueTextField(value = itemName, onValueChange = { itemName = it }, label = "Item Name*", placeholder = "Enter item name", bgColor = fieldColor, textColor = textColor)
        BlueTextField(value = itemSku, onValueChange = { itemSku = it }, label = "SKU*", placeholder = "Generated SKU", bgColor = fieldColor, textColor = textColor)

        // Category Dropdown
        Text(text = "Category*", color = Color.Gray, fontSize = 14.sp, modifier = Modifier.padding(bottom = 4.dp))
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

        // Supplier Dropdown
        Text(text = "Supplier", color = Color.Gray, fontSize = 14.sp, modifier = Modifier.padding(bottom = 4.dp))
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
                if (itemName.isNotBlank() && itemSku.isNotBlank()) {
                    val itemId = UUID.randomUUID().toString()
                    
                    if (bitmap != null) {
                        val stream = ByteArrayOutputStream()
                        bitmap!!.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                        val byteArray = stream.toByteArray()
                        
                        itemViewModel.uploadImage(itemId, byteArray) { imageUrl ->
                            val newItem = Item(
                                itemId = itemId,
                                itemName = itemName,
                                itemSku = itemSku,
                                itemPrice = itemPrice.toDoubleOrNull() ?: 0.0,
                                itemStockQuantity = currentStock.toIntOrNull() ?: 0,
                                itemMinStockLevel = minThreshold.toIntOrNull() ?: 0,
                                categoryId = selectedCategoryId,
                                supplierId = selectedSupplierId,
                                itemReference = description,
                                itemImageUrl = imageUrl
                            )
                            itemViewModel.addItem(newItem) {
                                navController.popBackStack()
                            }
                        }
                    } else {
                        val newItem = Item(
                            itemId = itemId,
                            itemName = itemName,
                            itemSku = itemSku,
                            itemPrice = itemPrice.toDoubleOrNull() ?: 0.0,
                            itemStockQuantity = currentStock.toIntOrNull() ?: 0,
                            itemMinStockLevel = minThreshold.toIntOrNull() ?: 0,
                            categoryId = selectedCategoryId,
                            supplierId = selectedSupplierId,
                            itemReference = description
                        )
                        itemViewModel.addItem(newItem) {
                            navController.popBackStack()
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF000080)),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text("Add Item", fontSize = 16.sp, color = Color.White)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

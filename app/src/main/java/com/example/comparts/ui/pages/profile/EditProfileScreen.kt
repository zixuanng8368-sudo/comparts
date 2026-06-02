package com.example.comparts.ui.pages.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.comparts.ui.components.BlueTextField
import com.example.comparts.viewmodel.AuthState
import com.example.comparts.viewmodel.AuthViewModel
import kotlinx.serialization.json.jsonPrimitive

@Composable
fun EditProfileScreen(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    var username by remember { mutableStateOf("") }
    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(Unit) {
        val user = authViewModel.getFullUser()
        username = user?.userMetadata?.get("username")?.jsonPrimitive?.content ?: ""
    }

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            navController.popBackStack()
        }
    }

    val primaryPurple = Color(0xFF6B58F5)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 24.dp, top = 8.dp)) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier.clickable { navController.popBackStack() }
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text("Edit Profile", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }

        BlueTextField(
            value = username,
            onValueChange = { username = it },
            label = "Username",
            placeholder = "Enter your username",
            bgColor = Color(0xFFF0F0F0),
            textColor = Color.Black
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { authViewModel.updateUsername(username) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = primaryPurple),
            shape = RoundedCornerShape(12.dp),
            enabled = authState !is AuthState.Loading
        ) {
            if (authState is AuthState.Loading) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
            } else {
                Text("Save Changes", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }

        if (authState is AuthState.Error) {
            Text(
                text = (authState as AuthState.Error).message,
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

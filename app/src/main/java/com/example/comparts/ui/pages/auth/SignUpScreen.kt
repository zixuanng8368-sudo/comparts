package com.example.comparts.ui.pages.auth

import androidx.compose.material3.*
import androidx.compose.runtime.*

@Composable
fun SignupScreen() {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    OutlinedTextField(
        value = email,
        onValueChange = { email = it }
    )

    OutlinedTextField(
        value = password,
        onValueChange = { password = it }
    )

    Button(
        onClick = { }
    ) {
        Text("Create Account")
    }
}
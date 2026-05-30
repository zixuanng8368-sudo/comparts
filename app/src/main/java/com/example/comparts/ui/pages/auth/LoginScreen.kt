package com.example.comparts.ui.pages.auth

import androidx.compose.material3.*
import androidx.compose.runtime.*

@Composable
fun LoginScreen() {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    OutlinedTextField(
        value = email,
        onValueChange = { email = it },
        label = { Text("Email") }
    )

    OutlinedTextField(
        value = password,
        onValueChange = { password = it },
        label = { Text("Password") }
    )

    Button(
        onClick = { }
    ) {
        Text("Login")
    }
}
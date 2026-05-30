package com.example.comparts.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun BottomBar(
    navController: NavController
) {

    NavigationBar {

        NavigationBarItem(
            selected = false,
            onClick = {
                navController.navigate("home")
            },
            icon = {
                Icon(Icons.Default.Home, null)
            },
            label = {
                Text("Home")
            }
        )

        NavigationBarItem(
            selected = false,
            onClick = {
                navController.navigate("items")
            },
            icon = {
                Icon(Icons.Default.List, null)
            },
            label = {
                Text("Items")
            }
        )

        NavigationBarItem(
            selected = false,
            onClick = {
                navController.navigate("notification")
            },
            icon = {
                Icon(Icons.Default.Notifications, null)
            },
            label = {
                Text("Alert")
            }
        )

        NavigationBarItem(
            selected = false,
            onClick = {
                navController.navigate("profile")
            },
            icon = {
                Icon(Icons.Default.Person, null)
            },
            label = {
                Text("Profile")
            }
        )

        NavigationBarItem(
            selected = false,
            onClick = {
                navController.navigate("settings")
            },
            icon = {
                Icon(Icons.Default.Settings, null)
            },
            label = {
                Text("Settings")
            }
        )
    }
}
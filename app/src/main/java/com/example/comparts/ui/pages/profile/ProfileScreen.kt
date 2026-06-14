package com.example.comparts.ui.pages.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
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
import com.example.comparts.viewmodel.AuthViewModel
import io.github.jan.supabase.auth.user.UserInfo
import kotlinx.serialization.json.jsonPrimitive

@Composable
fun ProfileScreen(navController: NavController, authViewModel: AuthViewModel = viewModel()) {
    val notificationEnabled by authViewModel.notificationsEnabled.collectAsState()
    var user by remember { mutableStateOf<UserInfo?>(null) }
    
    LaunchedEffect(Unit) {
        user = authViewModel.getFullUser()
    }

    val username = user?.userMetadata?.get("username")?.jsonPrimitive?.content ?: "User"
    val email = user?.email ?: "No Email"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        // Top Header
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 24.dp, top = 8.dp)) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", modifier = Modifier.clickable { navController.popBackStack() })
            Spacer(modifier = Modifier.width(16.dp))
            Text("My Profile", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }

        // User Avatar & Info
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(72.dp),
                shape = CircleShape,
                color = Color(0xFFF0F0F0)
            ) {
                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.padding(16.dp), tint = Color.Gray)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(username, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(email, fontSize = 14.sp, color = Color.Gray)
                Text("Operator", fontSize = 14.sp, color = Color(0xFF4A61F7), fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Edit Profile Link
        ListItem(
            headlineContent = { Text("Edit Profile Information") },
            trailingContent = { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null) },
            modifier = Modifier.clickable { navController.navigate("edit_profile") },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
        )

        HorizontalDivider(color = Color(0xFFF0F0F0))

        // Notification Toggle
        ListItem(
            headlineContent = { Text("Notification") },
            supportingContent = { Text("Items that are low in stocks") },
            trailingContent = {
                Switch(
                    checked = notificationEnabled,
                    onCheckedChange = { authViewModel.setNotificationsEnabled(it) },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF4A61F7), checkedTrackColor = Color(0x664A61F7))
                )
            },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
        )

        HorizontalDivider(color = Color(0xFFF0F0F0))

        // Change Password Link
        ListItem(
            headlineContent = { Text("Change Password") },
            trailingContent = { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null) },
            modifier = Modifier.clickable { /* Navigate to change password */ },
            colors = ListItemDefaults.colors(containerColor = Color.Transparent)
        )

        Spacer(modifier = Modifier.weight(1f))

        // Logout Button
        Button(
            onClick = {
                authViewModel.signOut()
                navController.navigate("login") {
                    popUpTo(0) { inclusive = true }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4C4C)),
            shape = RoundedCornerShape(24.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Logout", tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Logout", fontSize = 16.sp, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

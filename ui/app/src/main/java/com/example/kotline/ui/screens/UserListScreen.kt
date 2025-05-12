package com.example.kotline.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotline.ui.viewmodels.UserListState
import com.example.kotline.ui.viewmodels.UserListViewModel
import com.example.kotline.AuthManager
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserListScreen(
    onBack: () -> Unit,
    viewModel: UserListViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    val userListState by viewModel.userListState.collectAsState()
    val context = LocalContext.current
    val isAdmin = AuthManager.roleId == 1

    LaunchedEffect(Unit) {
        viewModel.fetchUsers()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        // Top bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Users",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            placeholder = { Text("Search") },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFFFF8800),
                unfocusedBorderColor = Color.Gray
            )
        )
        when (val state = userListState) {
            is UserListState.Loading, is UserListState.Deleting -> {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFFFF8800))
                }
            }
            is UserListState.Success -> {
                val users = state.users.filter {
                    (it.firstname?.contains(searchQuery, ignoreCase = true) == true) ||
                    (it.lastname?.contains(searchQuery, ignoreCase = true) == true) ||
                    (it.profession?.contains(searchQuery, ignoreCase = true) == true)
                }
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    users.forEach { user ->
                        UserCard(user, isAdmin, onDelete = {
                            viewModel.deleteUser(user.userid ?: "")
                            Toast.makeText(context, "User deleted", Toast.LENGTH_SHORT).show()
                        })
                    }
                }
            }
            is UserListState.Error, is UserListState.DeleteError -> {
                val msg = when (state) {
                    is UserListState.Error -> state.message
                    is UserListState.DeleteError -> state.message
                    else -> "Unknown error"
                }
                Text(
                    text = msg,
                    color = Color.Red,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun UserCard(user: com.example.kotline.ui.api.UserListItem, isAdmin: Boolean, onDelete: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F2F2)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF222222)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Avatar",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "${user.firstname ?: ""} ${user.lastname ?: ""}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.Black
                )
                Text(
                    text = user.profession ?: "",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
            }
            if (isAdmin) {
                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete User",
                        tint = Color(0xFFFF8800)
                    )
                }
            }
        }
    }
} 
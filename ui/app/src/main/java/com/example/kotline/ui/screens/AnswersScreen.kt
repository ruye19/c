package com.example.kotline.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.example.kotline.ui.api.Answer
import com.example.kotline.ui.viewmodels.AnswerState
import com.example.kotline.ui.viewmodels.AnswerViewModel
import com.example.kotline.AuthManager
import androidx.compose.foundation.border

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnswersScreen(
    questionId: String,
    onBack: () -> Unit,
    username: String = AuthManager.firstName ?: "User",
    answerViewModel: AnswerViewModel = viewModel()
) {
    var answerText by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf<String?>(null) }
    val answerState by answerViewModel.answerState.collectAsState()

    // Fetch answers when screen loads
    LaunchedEffect(questionId) {
        answerViewModel.fetchAnswers(questionId)
    }

    // Handle post success/error states
    LaunchedEffect(answerState) {
        when (answerState) {
            is AnswerState.PostSuccess -> {
                answerText = "" // Clear the text field
                showError = null
            }
            is AnswerState.PostError -> {
                showError = (answerState as AnswerState.PostError).message
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        // Top bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Text(
                text = "Answers",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            // Empty space to balance the back button
            Spacer(modifier = Modifier.width(48.dp))
        }

        // Answers list
        when (val state = answerState) {
            is AnswerState.Loading -> {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFFFF8800))
                }
            }
            is AnswerState.Success -> {
                if (state.answers.isEmpty()) {
                    Text("No answers yet.", color = Color.Gray, modifier = Modifier.padding(8.dp))
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        state.answers.forEach { answer ->
                            AnswerCard(answer)
                        }
                    }
                }
            }
            is AnswerState.Error -> {
                Text(
                    text = state.message,
                    color = Color.Red,
                    modifier = Modifier.padding(8.dp)
                )
            }
            is AnswerState.Posting -> {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFFFF8800))
                }
            }
            else -> {}
        }

        Spacer(modifier = Modifier.weight(1f))

        // Answer input section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Text(
                text = "Post Your Answer",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            OutlinedTextField(
                value = answerText,
                onValueChange = { answerText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF8800),
                    unfocusedBorderColor = Color.Gray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color(0xFFFF8800)
                ),
                placeholder = { Text("Write your answer here...", color = Color.Gray) },
                maxLines = 5
            )

            if (showError != null) {
                Text(
                    text = showError!!,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Button(
                onClick = {
                    if (answerText.isBlank()) {
                        showError = "Answer cannot be empty."
                    } else {
                        showError = null
                        answerViewModel.postAnswer(questionId, answerText, AuthManager.userId ?: "")
                    }
                },
                enabled = answerState !is AnswerState.Posting,
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8800)),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .padding(top = 8.dp)
            ) {
                Text(
                    text = if (answerState is AnswerState.Posting) "Posting..." else "Post",
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun AnswerCard(answer: Answer) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E1E)
        ),
        border = BorderStroke(1.dp, Color(0xFFFF8800))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = answer.username ?: "Anonymous",
                    color = Color(0xFFFF8800),
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = answer.answer,
                color = Color.White,
                fontSize = 16.sp
            )
        }
    }
} 
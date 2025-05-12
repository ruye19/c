package com.example.kotline.ui.screens

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

    LaunchedEffect(questionId) {
        answerViewModel.fetchAnswers(questionId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp)
    ) {
        // Top bar
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Tips",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Read the question carefully before answering.\n" +
                    "Stay on topic—make sure your answer directly addresses the question.\n" +
                    "Be clear and concise—explain your solution step-by-step.",
            color = Color.White,
            fontSize = 13.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
        Text(
            text = "View Answers",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        // Answers list
        when (val state = answerState) {
            is AnswerState.Loading -> {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.White)
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
            AnswerState.Idle, is AnswerState.Posting, is AnswerState.PostSuccess, is AnswerState.PostError -> {}
        }
        Spacer(modifier = Modifier.height(24.dp))
        Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
        Text(
            text = "Submit an answer",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = answerText,
            onValueChange = { answerText = it },
            placeholder = { Text("Type your answer here", color = Color.Gray) },
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = Color(0xFFF7F2F2),
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                cursorColor = Color.Black
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
        if (showError != null) {
            Text(
                text = showError!!,
                color = Color.Red,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        Button(
            onClick = {
                if (answerText.isBlank()) {
                    showError = "Answer cannot be empty."
                } else {
                    showError = null
                    answerViewModel.postAnswer(questionId, answerText, username)
                    answerText = ""
                }
            },
            shape = RoundedCornerShape(4.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF8800)),
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
        ) {
            Text("Post", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun AnswerCard(answer: Answer) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF7F2F2)),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF222222)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Avatar",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = answer.username ?: "User",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.Black
                    )
                    Text(
                        text = "Developer",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = answer.answer,
                color = Color.Black,
                fontSize = 14.sp
            )
        }
    }
} 
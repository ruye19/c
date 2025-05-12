import androidx.compose.runtime.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.kotline.ui.viewmodels.QuestionViewModel
import com.example.kotline.ui.viewmodels.QuestionState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.BorderStroke

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionScreen(
    questionId: String,
    onBack: () -> Unit,
    questionViewModel: QuestionViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    LaunchedEffect(questionId) {
        questionViewModel.fetchSingleQuestion(questionId)
    }
    val questionState by questionViewModel.singleQuestionState.collectAsState()

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
                text = "Question",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            // Empty space to balance the back button
            Spacer(modifier = Modifier.width(48.dp))
        }

        when (val state = questionState) {
            is QuestionState.Loading -> {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFFFF8800))
                }
            }
            is QuestionState.Success -> {
                val question = state.questions.firstOrNull()
                if (question != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
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
                            Text(
                                text = question.title,
                                color = Color.White,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Text(
                                text = question.description,
                                color = Color.White,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Asked by ${question.username}",
                                    color = Color(0xFFFF8800),
                                    fontSize = 14.sp
                                )
                                if (question.tag != null) {
                                    Text(
                                        text = "#${question.tag}",
                                        color = Color(0xFFFF8800),
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
            is QuestionState.Error -> {
                Text(
                    text = state.message,
                    color = Color.Red,
                    modifier = Modifier.padding(8.dp)
                )
            }
            else -> {}
        }
    }
} 
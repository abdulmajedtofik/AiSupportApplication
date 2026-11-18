package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.AppStrings
import com.example.data.model.Subject
import com.example.ui.MainViewModel
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.RoseError

@Composable
fun QuizExamScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val lang by viewModel.language.collectAsState()
    val subject by viewModel.selectedQuizSubject.collectAsState()
    val questions by viewModel.currentQuizQuestions.collectAsState()
    val currentIndex by viewModel.currentQuestionIndex.collectAsState()
    val selectedOption by viewModel.selectedOptionIndex.collectAsState()
    val isSubmitted by viewModel.isAnswerSubmitted.collectAsState()
    val score by viewModel.quizScore.collectAsState()
    val isFinished by viewModel.isQuizFinished.collectAsState()

    val isAmharic = (lang == AppLanguage.AMHARIC)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = AppStrings.tabQuiz(lang),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = if (isAmharic)
                    "የ AI ፈተና አመንጪ: የብቃት መለኪያ ጥያቄዎች በፈጣን ውጤት እና ዝርዝር ማብራሪያ።"
                else
                    "AI Quiz & Examination Generator: instant scoring and comprehensive rationale.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Subject filter chips
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(listOf(Subject.MATHEMATICS, Subject.PHYSICS, Subject.CHEMISTRY, Subject.COMPUTER_SCIENCE, Subject.BIOLOGY)) { s ->
                    val isSelected = s == subject
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.startQuiz(s) },
                        label = {
                            Text(
                                text = if (isAmharic) s.amharicName else s.englishName,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }
        }

        // If quiz is not started yet or empty
        if (questions.isEmpty()) {
            item {
                ElevatedCard(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(IndigoPrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Quiz,
                                contentDescription = null,
                                tint = IndigoPrimary,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = if (isAmharic)
                                "የ${subject.amharicName} የፈተና ጥያቄዎችን ጀምር"
                            else
                                "Start ${subject.englishName} Practice Examination",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (isAmharic)
                                "ለሀገር አቀፍ እና ለዩኒቨርሲቲ ፈተናዎች የሚሆኑ 3 ደረጃቸውን የጠበቁ ጥያቄዎች።"
                            else
                                "3 standard examination questions designed for matriculation & finals.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = { viewModel.startQuiz(subject) },
                            modifier = Modifier.fillMaxWidth().testTag("start_quiz_button")
                        ) {
                            Icon(imageVector = Icons.Default.PlayArrow, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isAmharic) "ፈተናውን ጀምር" else "Start Examination",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        } else if (isFinished) {
            // Quiz Results Celebration Card
            item {
                ElevatedCard(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(AmberWarning.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = "Trophy",
                                tint = AmberWarning,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = if (isAmharic) "ፈተናውን በስኬት አጠናቀዋል!" else "Exam Completed Successfully!",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        val percentage = (score.toFloat() / questions.size.toFloat() * 100).toInt()
                        Text(
                            text = "$percentage%",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (percentage >= 70) EmeraldSuccess else AmberWarning
                        )

                        Text(
                            text = if (isAmharic)
                                "ውጤት: $score ከ ${questions.size} ጥያቄዎች ተመልሰዋል"
                            else
                                "Score: $score of ${questions.size} answered correctly",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = { viewModel.startQuiz(subject) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Default.Replay, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isAmharic) "እንደገና ሞክር" else "Retake Quiz",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        } else {
            // Active Quiz Question
            val currentQ = questions.getOrNull(currentIndex)
            if (currentQ != null) {
                // Progress Bar
                item {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (isAmharic)
                                    "ጥያቄ ${currentIndex + 1} ከ ${questions.size}"
                                else
                                    "Question ${currentIndex + 1} of ${questions.size}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "${subject.name}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        val progress = (currentIndex + 1).toFloat() / questions.size.toFloat()
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = IndigoPrimary,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    }
                }

                // Question Card
                item {
                    ElevatedCard(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = if (isAmharic) currentQ.questionAm else currentQ.questionEn,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                lineHeight = 22.sp
                            )
                        }
                    }
                }

                // Options
                val options = if (isAmharic) currentQ.optionsAm else currentQ.optionsEn
                items(options.indices.toList()) { index ->
                    val optText = options[index]
                    val isChosen = selectedOption == index
                    val isCorrectOption = index == currentQ.correctIndex

                    val borderColor = when {
                        isSubmitted && isCorrectOption -> EmeraldSuccess
                        isSubmitted && isChosen && !isCorrectOption -> RoseError
                        isChosen -> IndigoPrimary
                        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    }

                    val containerColor = when {
                        isSubmitted && isCorrectOption -> EmeraldSuccess.copy(alpha = 0.12f)
                        isSubmitted && isChosen && !isCorrectOption -> RoseError.copy(alpha = 0.12f)
                        isChosen -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                        else -> MaterialTheme.colorScheme.surface
                    }

                    OutlinedCard(
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = !isSubmitted) {
                                viewModel.selectQuizOption(index)
                            },
                        colors = CardDefaults.outlinedCardColors(containerColor = containerColor),
                        border = CardDefaults.outlinedCardBorder().copy(
                            brush = androidx.compose.ui.graphics.SolidColor(borderColor)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .border(1.dp, borderColor, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                val letter = ('A'.code + index).toChar().toString()
                                Text(
                                    text = letter,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Text(
                                text = optText,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )

                            if (isSubmitted && isCorrectOption) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Correct",
                                    tint = EmeraldSuccess,
                                    modifier = Modifier.size(20.dp)
                                )
                            } else if (isSubmitted && isChosen && !isCorrectOption) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Incorrect",
                                    tint = RoseError,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                // Action: Submit or Next Question
                item {
                    if (!isSubmitted) {
                        Button(
                            onClick = { viewModel.submitQuizAnswer() },
                            enabled = selectedOption != null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("submit_quiz_answer_button")
                        ) {
                            Text(
                                text = if (isAmharic) "መልሱን አረጋግጥ" else "Submit Answer",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            // Detailed Pedagogical Explanation
                            ElevatedCard(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.elevatedCardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.HelpOutline,
                                            contentDescription = null,
                                            tint = IndigoPrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (isAmharic) "የመልሱ ዝርዝር ማብራሪያ" else "Detailed Solution Explanation",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = IndigoPrimary
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = if (isAmharic) currentQ.explanationAm else currentQ.explanationEn,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        lineHeight = 18.sp
                                    )
                                }
                            }

                            Button(
                                onClick = { viewModel.nextQuizQuestion() },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("next_quiz_question_button")
                            ) {
                                Text(
                                    text = if (currentIndex + 1 < questions.size) {
                                        if (isAmharic) "ቀጣይ ጥያቄ →" else "Next Question →"
                                    } else {
                                        if (isAmharic) "ውጤቴን እይ" else "View Results"
                                    },
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

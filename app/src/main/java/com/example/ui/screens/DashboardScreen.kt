package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.AppStrings
import com.example.data.model.UserRole
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoLight
import com.example.ui.theme.IndigoPrimary

@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val lang by viewModel.language.collectAsState()
    val role by viewModel.userRole.collectAsState()
    val streak by viewModel.streakDays.collectAsState()
    val hours by viewModel.studyHours.collectAsState()
    val solvedCount by viewModel.problemsSolvedCount.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val countdowns by viewModel.examCountdowns.collectAsState()

    val isAmharic = (lang == AppLanguage.AMHARIC)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Quick Navigation Pills to specialized hubs
        item {
            Spacer(modifier = Modifier.height(4.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    HubPill(
                        icon = Icons.Default.MenuBook,
                        title = AppStrings.tabNotes(lang),
                        onClick = { viewModel.navigateTo(Screen.NOTES) }
                    )
                }
                item {
                    HubPill(
                        icon = Icons.Default.Group,
                        title = AppStrings.tabCommunity(lang),
                        onClick = { viewModel.navigateTo(Screen.COMMUNITY) }
                    )
                }
                item {
                    HubPill(
                        icon = Icons.Default.School,
                        title = AppStrings.tabTeacher(lang),
                        onClick = { viewModel.navigateTo(Screen.TEACHER) }
                    )
                }
                item {
                    HubPill(
                        icon = Icons.Default.Person,
                        title = if (isAmharic) "ስለ አልሚው (Developer)" else "About Developer",
                        onClick = { viewModel.navigateTo(Screen.PROFILE) }
                    )
                }
            }
        }

        // Hero Banner
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(IndigoPrimary, ElectricBlue, Color(0xFF1E1B4B))
                        )
                    )
                    .testTag("dashboard_hero_card")
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White.copy(alpha = 0.18f)
                        ) {
                            Text(
                                text = "🔥 $streak ${if (isAmharic) "ቀናት የማያቋርጥ ጥናት" else "Days Streak"}",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = CyanAccent.copy(alpha = 0.25f)
                        ) {
                            Text(
                                text = "AI Level 4 • Scholar",
                                color = CyanAccent,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = if (isAmharic) "እንኳን ደህና መጡ! ዛሬ ምን እናጠና?" else "Welcome back! What are we mastering today?",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = if (isAmharic)
                            "የቤት ሥራ መፍቻ፣ የ AI አስተማሪ እና የፈተና ጥያቄዎች ዝግጁ ናቸው።"
                        else
                            "AI Homework solver, personalized tutor, and examination drills are ready.",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Metrics Strip
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MetricItem(
                            value = "%.1f h".format(hours),
                            label = if (isAmharic) "የጥናት ሰዓት" else "Hours Studied"
                        )
                        MetricItem(
                            value = "$solvedCount",
                            label = if (isAmharic) "የተፈቱ ጥያቄዎች" else "Solved Problems"
                        )
                        MetricItem(
                            value = "88%",
                            label = if (isAmharic) "አማካይ ውጤት" else "Avg Score"
                        )
                    }
                }
            }
        }

        // Section: Primary Learning Actions
        item {
            Text(
                text = if (isAmharic) "ፈጣን የ AI አገልግሎቶች" else "AI Learning Modules",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionCard(
                    modifier = Modifier.weight(1f),
                    title = if (isAmharic) "የቤት ሥራ አጋዥ" else "Homework Solver",
                    subtitle = if (isAmharic) "ደረጃ በደረጃ መፍትሔ" else "Step-by-step logic",
                    icon = Icons.Default.AutoAwesome,
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    onClick = { viewModel.navigateTo(Screen.HOMEWORK) }
                )

                ActionCard(
                    modifier = Modifier.weight(1f),
                    title = if (isAmharic) "AI አስተማሪ" else "AI Tutor",
                    subtitle = if (isAmharic) "ፅንሰ ሀሳብ አብራሪ" else "Concept explorer",
                    icon = Icons.Default.Chat,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    onClick = { viewModel.navigateTo(Screen.TUTOR) }
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ActionCard(
                    modifier = Modifier.weight(1f),
                    title = if (isAmharic) "የፈተና ልምምድ" else "Exam Quiz Drills",
                    subtitle = if (isAmharic) "በቀጥታ ራስን መፈተሽ" else "Real-time scoring",
                    icon = Icons.Default.Quiz,
                    containerColor = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f),
                    contentColor = MaterialTheme.colorScheme.primary,
                    onClick = { viewModel.navigateTo(Screen.QUIZ) }
                )

                ActionCard(
                    modifier = Modifier.weight(1f),
                    title = if (isAmharic) "የጥናት እቅድ" else "Study Timetable",
                    subtitle = if (isAmharic) "የፈተና ቆጠራ" else "Exam countdown",
                    icon = Icons.Default.DateRange,
                    containerColor = EmeraldSuccess.copy(alpha = 0.15f),
                    contentColor = EmeraldSuccess,
                    onClick = { viewModel.navigateTo(Screen.PLANNER) }
                )
            }
        }

        // Section: Upcoming Exam Countdowns
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isAmharic) "የቀጣይ ፈተናዎች ቆጠራ" else "Upcoming Exam Countdowns",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (isAmharic) "ሁሉንም እይ" else "View all",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { viewModel.navigateTo(Screen.PLANNER) }
                )
            }
        }

        items(countdowns) { exam ->
            val now = System.currentTimeMillis()
            val diffDays = ((exam.targetDateEpochMillis - now) / (1000 * 60 * 60 * 24)).coerceAtLeast(0)
            ElevatedCard(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isAmharic) exam.titleAm else exam.titleEn,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isAmharic)
                                    "${exam.subject.amharicName} • ${exam.coveredTopics} ከ ${exam.totalTopics} ርዕሶች ተጠናቀዋል"
                                else
                                    "${exam.subject.englishName} • ${exam.coveredTopics} of ${exam.totalTopics} topics ready",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = AmberWarning.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "$diffDays ${if (isAmharic) "ቀናት ይቀራሉ" else "days left"}",
                                color = AmberWarning,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    val progress = exam.coveredTopics.toFloat() / exam.totalTopics.toFloat()
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
        }

        // Section: Today's Tasks
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isAmharic) "የዛሬ የጥናት ግቦች" else "Today's Study Schedule",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (isAmharic) "መርሃ ግብር ክፈት" else "Open Planner",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.clickable { viewModel.navigateTo(Screen.PLANNER) }
                )
            }
        }

        items(tasks.take(3)) { task ->
            OutlinedCard(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.outlinedCardColors(
                    containerColor = if (task.isCompleted) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = task.isCompleted,
                        onCheckedChange = { viewModel.toggleTask(task) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = task.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (task.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${task.subject} • ${task.deadline} • ${task.durationMinutes} min",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun MetricItem(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = Color.White,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 18.sp
        )
        Text(
            text = label,
            color = Color.White.copy(alpha = 0.75f),
            fontSize = 11.sp
        )
    }
}

@Composable
private fun HubPill(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    containerColor: Color,
    contentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(contentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

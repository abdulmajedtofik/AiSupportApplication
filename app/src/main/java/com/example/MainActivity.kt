package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MainViewModel
import com.example.ui.Screen
import com.example.ui.components.SmartStudyBottomBar
import com.example.ui.components.SmartStudyTopBar
import com.example.ui.screens.AITutorChatScreen
import com.example.ui.screens.CommunityScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.HomeworkAssistantScreen
import com.example.ui.screens.NotesAndFlashcardsScreen
import com.example.ui.screens.ProfileAndDeveloperScreen
import com.example.ui.screens.QuizExamScreen
import com.example.ui.screens.StudyPlannerScreen
import com.example.ui.screens.TeacherDashboardScreen
import com.example.ui.theme.SmartStudyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmartStudyTheme {
                MainAppContent()
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: MainViewModel = viewModel()) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val language by viewModel.language.collectAsState()
    val userRole by viewModel.userRole.collectAsState()
    val streakDays by viewModel.streakDays.collectAsState()

    // Handle Android system back press to return to Dashboard if on a sub-screen
    BackHandler(enabled = currentScreen != Screen.DASHBOARD) {
        viewModel.navigateTo(Screen.DASHBOARD)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            SmartStudyTopBar(
                language = language,
                userRole = userRole,
                streakDays = streakDays,
                onLanguageToggle = { viewModel.toggleLanguage() },
                onRoleSwitch = {
                    val nextRole = when (userRole) {
                        com.example.data.model.UserRole.STUDENT -> com.example.data.model.UserRole.TEACHER
                        com.example.data.model.UserRole.TEACHER -> com.example.data.model.UserRole.ADMIN
                        com.example.data.model.UserRole.ADMIN -> com.example.data.model.UserRole.STUDENT
                    }
                    viewModel.setRole(nextRole)
                },
                onProfileClick = { viewModel.navigateTo(Screen.PROFILE) }
            )
        },
        bottomBar = {
            SmartStudyBottomBar(
                currentScreen = currentScreen,
                language = language,
                userRole = userRole,
                onNavigate = { screen -> viewModel.navigateTo(screen) }
            )
        }
    ) { innerPadding ->
        val screenModifier = Modifier.padding(innerPadding)

        when (currentScreen) {
            Screen.DASHBOARD -> DashboardScreen(viewModel = viewModel, modifier = screenModifier)
            Screen.HOMEWORK -> HomeworkAssistantScreen(viewModel = viewModel, modifier = screenModifier)
            Screen.TUTOR -> AITutorChatScreen(viewModel = viewModel, modifier = screenModifier)
            Screen.PLANNER -> StudyPlannerScreen(viewModel = viewModel, modifier = screenModifier)
            Screen.QUIZ -> QuizExamScreen(viewModel = viewModel, modifier = screenModifier)
            Screen.NOTES -> NotesAndFlashcardsScreen(viewModel = viewModel, modifier = screenModifier)
            Screen.COMMUNITY -> CommunityScreen(viewModel = viewModel, modifier = screenModifier)
            Screen.TEACHER -> TeacherDashboardScreen(viewModel = viewModel, modifier = screenModifier)
            Screen.PROFILE -> ProfileAndDeveloperScreen(viewModel = viewModel, modifier = screenModifier)
        }
    }
}

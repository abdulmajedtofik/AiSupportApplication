package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.StudyAiEngine
import com.example.data.local.AppDatabase
import com.example.data.local.FlashcardEntity
import com.example.data.local.QuizRecordEntity
import com.example.data.local.StudyNoteEntity
import com.example.data.local.StudyTaskEntity
import com.example.data.model.AppLanguage
import com.example.data.model.ChatMessage
import com.example.data.model.CommunityPost
import com.example.data.model.ExamCountdown
import com.example.data.model.HomeworkSolution
import com.example.data.model.MessageSender
import com.example.data.model.QuizQuestion
import com.example.data.model.SolverMode
import com.example.data.model.Subject
import com.example.data.model.TeacherAssignment
import com.example.data.model.UserRole
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class Screen {
    DASHBOARD,
    HOMEWORK,
    TUTOR,
    PLANNER,
    QUIZ,
    NOTES,
    COMMUNITY,
    TEACHER,
    PROFILE
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val taskDao = db.studyTaskDao()
    private val noteDao = db.studyNoteDao()
    private val flashcardDao = db.flashcardDao()
    private val quizDao = db.quizRecordDao()

    // Global App States
    val language = MutableStateFlow(AppLanguage.AMHARIC)
    val userRole = MutableStateFlow(UserRole.STUDENT)
    val currentScreen = MutableStateFlow(Screen.DASHBOARD)
    val customApiKey = MutableStateFlow("")
    val streakDays = MutableStateFlow(7)
    val studyHours = MutableStateFlow(28.5f)
    val problemsSolvedCount = MutableStateFlow(42)

    // Room DB Flows
    val tasks: StateFlow<List<StudyTaskEntity>> = taskDao.getAllTasks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedNotes: StateFlow<List<StudyNoteEntity>> = noteDao.getAllNotes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val flashcards: StateFlow<List<FlashcardEntity>> = flashcardDao.getAllFlashcards()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val quizRecords: StateFlow<List<QuizRecordEntity>> = quizDao.getAllRecords()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Homework Assistant State
    val homeworkQuery = MutableStateFlow("")
    val selectedHomeworkSubject = MutableStateFlow(Subject.MATHEMATICS)
    val homeworkSolverMode = MutableStateFlow(SolverMode.STEP_BY_STEP)
    val isSolvingHomework = MutableStateFlow(false)
    val currentHomeworkSolution = MutableStateFlow<HomeworkSolution?>(null)

    // Tutor Chat State
    val selectedTutorSubject = MutableStateFlow(Subject.PHYSICS)
    val tutorMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val tutorInputText = MutableStateFlow("")
    val isTutorTyping = MutableStateFlow(false)

    // Quiz State
    val selectedQuizSubject = MutableStateFlow(Subject.MATHEMATICS)
    val currentQuizQuestions = MutableStateFlow<List<QuizQuestion>>(emptyList())
    val currentQuestionIndex = MutableStateFlow(0)
    val selectedOptionIndex = MutableStateFlow<Int?>(null)
    val isAnswerSubmitted = MutableStateFlow(false)
    val quizScore = MutableStateFlow(0)
    val isQuizFinished = MutableStateFlow(false)

    // Flashcards Interactive State
    val currentCardIndex = MutableStateFlow(0)
    val isCardFlipped = MutableStateFlow(false)

    // Exam Countdowns
    val examCountdowns = MutableStateFlow<List<ExamCountdown>>(emptyList())

    // Community Posts
    val communityPosts = MutableStateFlow<List<CommunityPost>>(emptyList())

    // Teacher Assignments
    val teacherAssignments = MutableStateFlow<List<TeacherAssignment>>(emptyList())

    init {
        seedInitialDataIfEmpty()
        initCountdowns()
        initCommunity()
        initTeacherAssignments()
        initTutorWelcome()
        // Pre-solve one demo problem so screen starts ready
        viewModelScope.launch {
            val initial = StudyAiEngine.solveHomework(
                query = "Solve: 2x² + 5x - 3 = 0",
                subject = Subject.MATHEMATICS,
                mode = SolverMode.STEP_BY_STEP,
                lang = language.value
            )
            currentHomeworkSolution.value = initial
        }
    }

    private fun seedInitialDataIfEmpty() {
        viewModelScope.launch {
            // Seed sample tasks if empty
            taskDao.insertTask(
                StudyTaskEntity(
                    title = "Review Calculus Integration & Substitution Rules",
                    subject = "MATHEMATICS",
                    deadline = "Today, 6:00 PM",
                    isCompleted = false,
                    priority = "High",
                    durationMinutes = 60
                )
            )
            taskDao.insertTask(
                StudyTaskEntity(
                    title = "Physics Kinematics: Torricelli Formula practice",
                    subject = "PHYSICS",
                    deadline = "Tomorrow, 4:00 PM",
                    isCompleted = true,
                    priority = "Normal",
                    durationMinutes = 45
                )
            )
            taskDao.insertTask(
                StudyTaskEntity(
                    title = "Chemistry: Stoichiometry & Molarity problems",
                    subject = "CHEMISTRY",
                    deadline = "Friday, 10:00 AM",
                    isCompleted = false,
                    priority = "High",
                    durationMinutes = 50
                )
            )

            // Seed sample flashcards
            flashcardDao.insertFlashcard(
                FlashcardEntity(
                    subject = "MATHEMATICS",
                    frontEn = "What is the Quadratic Formula?",
                    frontAm = "የኳድራቲክ ፎርሙላ ምንድን ነው?",
                    backEn = "x = (-b ± √(b² - 4ac)) / (2a)",
                    backAm = "x = (-b ± √(b² - 4ac)) / (2a)",
                    isMastered = true
                )
            )
            flashcardDao.insertFlashcard(
                FlashcardEntity(
                    subject = "PHYSICS",
                    frontEn = "State Newton's Second Law of Motion",
                    frontAm = "የኒውተን ሁለተኛውን የእንቅስቃሴ ህግ ጥቀስ",
                    backEn = "Net Force = mass × acceleration (F = m · a).",
                    backAm = "ሃይል = መጠነ-ቁስ × ፍጥነት መጨመር (F = m · a)።",
                    isMastered = false
                )
            )
            flashcardDao.insertFlashcard(
                FlashcardEntity(
                    subject = "CHEMISTRY",
                    frontEn = "What is Avogadro's constant?",
                    frontAm = "የአቮጋድሮ ቁጥር ስንት ነው?",
                    backEn = "6.022 × 10²³ particles per mole.",
                    backAm = "6.022 × 10²³ ቅንጣቶች በ 1 ሞል ውስጥ።",
                    isMastered = false
                )
            )
            flashcardDao.insertFlashcard(
                FlashcardEntity(
                    subject = "COMPUTER_SCIENCE",
                    frontEn = "What is the time complexity of QuickSort average case?",
                    frontAm = "የ QuickSort አልጎሪዝም አማካይ የጊዜ ውስብስብነት ስንት ነው?",
                    backEn = "O(N log N) average, O(N²) worst case.",
                    backAm = "አማካይ O(N log N) ሲሆን ከፍተኛው O(N²) ነው።",
                    isMastered = true
                )
            )
        }
    }

    private fun initCountdowns() {
        val now = System.currentTimeMillis()
        val oneDayMillis = 24 * 60 * 60 * 1000L
        examCountdowns.value = listOf(
            ExamCountdown(
                id = "1",
                titleEn = "National University Entrance Exam (EUEE)",
                titleAm = "የሀገር አቀፍ የዩኒቨርሲቲ መግቢያ ፈተና",
                targetDateEpochMillis = now + (38 * oneDayMillis),
                subject = Subject.MATHEMATICS,
                totalTopics = 24,
                coveredTopics = 18
            ),
            ExamCountdown(
                id = "2",
                titleEn = "Physics & Applied Mechanics Midterm",
                titleAm = "የፊዚክስ አጋማሽ ፈተና (Midterm)",
                targetDateEpochMillis = now + (12 * oneDayMillis),
                subject = Subject.PHYSICS,
                totalTopics = 10,
                coveredTopics = 8
            ),
            ExamCountdown(
                id = "3",
                titleEn = "Chemistry Organic Synthesis Final Exam",
                titleAm = "የኬሚስትሪ የፍፃሜ ፈተና (Final Exam)",
                targetDateEpochMillis = now + (22 * oneDayMillis),
                subject = Subject.CHEMISTRY,
                totalTopics = 15,
                coveredTopics = 11
            )
        )
    }

    private fun initCommunity() {
        communityPosts.value = listOf(
            CommunityPost(
                id = "p1",
                authorName = "Dawit Haile",
                authorRole = "Grade 12 STEM Student",
                subject = Subject.MATHEMATICS,
                title = "How do you intuitively derive integration by parts?",
                body = "I understand the product rule d(uv) = u dv + v du, but during exam pressure I sometimes mix up which term to differentiate (LIATE rule). Any memory tips?",
                upvotes = 34,
                replyCount = 8,
                timestamp = "2 hours ago",
                isSolved = true
            ),
            CommunityPost(
                id = "p2",
                authorName = "Selamawit Kebede",
                authorRole = "University Freshman",
                subject = Subject.PHYSICS,
                title = "Rotational inertia of a hollow vs solid cylinder",
                body = "Why does a solid cylinder always roll down an inclined plane faster than a hollow ring of the same total mass? Verified step by step!",
                upvotes = 28,
                replyCount = 5,
                timestamp = "5 hours ago",
                isSolved = true
            ),
            CommunityPost(
                id = "p3",
                authorName = "Yonas Bekele",
                authorRole = "Software Engineering Student",
                subject = Subject.COMPUTER_SCIENCE,
                title = "Dynamic Programming memoization vs tabulation in Kotlin",
                body = "Sharing my notes on 0/1 Knapsack problem with Amharic explanations for my fellow Ethiopian CS students!",
                upvotes = 52,
                replyCount = 14,
                timestamp = "Yesterday",
                isSolved = true
            )
        )
    }

    private fun initTeacherAssignments() {
        teacherAssignments.value = listOf(
            TeacherAssignment(
                id = "a1",
                title = "Unit 4: Quadratic Equations & Applications",
                subject = Subject.MATHEMATICS,
                gradeLevel = "Grade 11 - Section B",
                dueDate = "Nov 12, 2026",
                totalStudents = 45,
                submittedCount = 38,
                averageScore = 84
            ),
            TeacherAssignment(
                id = "a2",
                title = "Laboratory Report: Torricelli Velocity & Pendulums",
                subject = Subject.PHYSICS,
                gradeLevel = "Grade 12 Honors",
                dueDate = "Nov 15, 2026",
                totalStudents = 40,
                submittedCount = 36,
                averageScore = 91
            ),
            TeacherAssignment(
                id = "a3",
                title = "Data Structures: Binary Trees and Recursion",
                subject = Subject.COMPUTER_SCIENCE,
                gradeLevel = "Year 2 CS Lab",
                dueDate = "Nov 18, 2026",
                totalStudents = 32,
                submittedCount = 20,
                averageScore = 78
            )
        )
    }

    private fun initTutorWelcome() {
        val isAmharic = (language.value == AppLanguage.AMHARIC)
        val text = if (isAmharic) {
            "ሰላም! እኔ 'ትምህርት አጋዥ' የ AI የግል አስተማሪዎ ነኝ። በሒሳብ፣ ፊዚክስ፣ ኬሚስትሪ፣ ባዮሎጂ ወይም ኮምፒውተር ሳይንስ አስቸጋሪ የሆነብዎትን ፅንሰ ሀሳብ ይጠይቁኝ። ደረጃ በደረጃ አብረን እንማማራለን!"
        } else {
            "Hello! I am your Smart Study AI personal tutor. Ask me any challenging concept in Mathematics, Physics, Chemistry, Biology, or Computer Science. Let's learn step-by-step together!"
        }

        tutorMessages.value = listOf(
            ChatMessage(
                sender = MessageSender.AI_TUTOR,
                text = text,
                isAmharic = isAmharic,
                suggestedFollowUps = if (isAmharic) {
                    listOf("የኳድራቲክ ቀመርን አስረዳኝ", "የኒውተን ህጎችን በምሳሌ አስረዳኝ", "ባይነሪ ሰርች እንዴት ይሰራል?")
                } else {
                    listOf("Explain Quadratic Formula", "Explain Newton's Laws with examples", "How does Binary Search work?")
                }
            )
        )
    }

    // Language Toggle
    fun toggleLanguage() {
        language.value = if (language.value == AppLanguage.AMHARIC) AppLanguage.ENGLISH else AppLanguage.AMHARIC
    }

    // Role Switch
    fun setRole(role: UserRole) {
        userRole.value = role
    }

    // Navigate
    fun navigateTo(screen: Screen) {
        currentScreen.value = screen
    }

    // Solve Homework
    fun solveCurrentHomework() {
        val query = homeworkQuery.value
        val subject = selectedHomeworkSubject.value
        val mode = homeworkSolverMode.value
        val lang = language.value

        isSolvingHomework.value = true
        viewModelScope.launch {
            val solution = StudyAiEngine.solveHomework(
                query = query,
                subject = subject,
                mode = mode,
                lang = lang,
                customApiKey = customApiKey.value
            )
            currentHomeworkSolution.value = solution
            isSolvingHomework.value = false
            problemsSolvedCount.value += 1
            studyHours.value += 0.25f
        }
    }

    // Send Tutor Message
    fun sendTutorMessage(text: String = tutorInputText.value) {
        if (text.isBlank()) return
        val userMsg = ChatMessage(
            sender = MessageSender.USER,
            text = text,
            isAmharic = (language.value == AppLanguage.AMHARIC)
        )
        tutorMessages.value = tutorMessages.value + userMsg
        tutorInputText.value = ""
        isTutorTyping.value = true

        viewModelScope.launch {
            kotlinx.coroutines.delay(600) // Realistic thoughtful AI delay
            val aiResponse = StudyAiEngine.getTutorResponse(
                userPrompt = text,
                subject = selectedTutorSubject.value,
                lang = language.value
            )
            tutorMessages.value = tutorMessages.value + aiResponse
            isTutorTyping.value = false
            studyHours.value += 0.15f
        }
    }

    // Quiz Actions
    fun startQuiz(subject: Subject = selectedQuizSubject.value) {
        selectedQuizSubject.value = subject
        currentQuizQuestions.value = StudyAiEngine.getQuizQuestionsForSubject(subject)
        currentQuestionIndex.value = 0
        selectedOptionIndex.value = null
        isAnswerSubmitted.value = false
        quizScore.value = 0
        isQuizFinished.value = false
    }

    fun selectQuizOption(index: Int) {
        if (!isAnswerSubmitted.value) {
            selectedOptionIndex.value = index
        }
    }

    fun submitQuizAnswer() {
        val selected = selectedOptionIndex.value ?: return
        val questions = currentQuizQuestions.value
        val currentQ = questions.getOrNull(currentQuestionIndex.value) ?: return

        isAnswerSubmitted.value = true
        if (selected == currentQ.correctIndex) {
            quizScore.value += 1
        }
    }

    fun nextQuizQuestion() {
        val questions = currentQuizQuestions.value
        if (currentQuestionIndex.value + 1 < questions.size) {
            currentQuestionIndex.value += 1
            selectedOptionIndex.value = null
            isAnswerSubmitted.value = false
        } else {
            isQuizFinished.value = true
            // Save to Room DB
            viewModelScope.launch {
                quizDao.insertRecord(
                    QuizRecordEntity(
                        subject = selectedQuizSubject.value.name,
                        score = quizScore.value,
                        total = questions.size,
                        difficulty = "Standard"
                    )
                )
            }
        }
    }

    // Task Management
    fun toggleTask(task: StudyTaskEntity) {
        viewModelScope.launch {
            taskDao.updateTask(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun addTask(title: String, subject: String, deadline: String, duration: Int) {
        viewModelScope.launch {
            taskDao.insertTask(
                StudyTaskEntity(
                    title = title,
                    subject = subject,
                    deadline = deadline,
                    durationMinutes = duration
                )
            )
        }
    }

    fun deleteTask(taskId: Long) {
        viewModelScope.launch {
            taskDao.deleteTask(taskId)
        }
    }

    // Flashcard Actions
    fun nextFlashcard() {
        val cards = flashcards.value
        if (cards.isNotEmpty()) {
            currentCardIndex.value = (currentCardIndex.value + 1) % cards.size
            isCardFlipped.value = false
        }
    }

    fun flipCard() {
        isCardFlipped.value = !isCardFlipped.value
    }

    fun markFlashcardMastery(isMastered: Boolean) {
        val cards = flashcards.value
        val card = cards.getOrNull(currentCardIndex.value) ?: return
        viewModelScope.launch {
            flashcardDao.updateFlashcard(
                card.copy(
                    isMastered = isMastered,
                    reviewCount = card.reviewCount + 1
                )
            )
            nextFlashcard()
        }
    }

    // Generate AI Note
    fun generateNote(topic: String, subject: Subject) {
        viewModelScope.launch {
            val note = StudyAiEngine.generateStudyNote(topic, subject)
            noteDao.insertNote(note)
        }
    }

    // Community Upvote
    fun upvotePost(postId: String) {
        communityPosts.value = communityPosts.value.map {
            if (it.id == postId) it.copy(upvotes = it.upvotes + 1) else it
        }
    }
}

package com.example.data.model

enum class AppLanguage {
    AMHARIC,
    ENGLISH
}

enum class UserRole {
    STUDENT,
    TEACHER,
    ADMIN
}

enum class Subject(
    val englishName: String,
    val amharicName: String,
    val iconName: String,
    val colorHex: Long
) {
    MATHEMATICS("Mathematics", "ሒሳብ", "calculate", 0xFF4338CA),
    PHYSICS("Physics", "ፊዚክስ", "science", 0xFF2563EB),
    CHEMISTRY("Chemistry", "ኬሚስትሪ", "biotech", 0xFF059669),
    BIOLOGY("Biology", "ሥነ-ሕይወት", "eco", 0xFF10B981),
    COMPUTER_SCIENCE("Computer Science", "ኮምፒውተር ሳይንስ", "code", 0xFF7C3AED),
    ENGLISH("English", "እንግሊዝኛ", "menu_book", 0xFFD97706),
    CIVICS_HISTORY("History & Civics", "ታሪክ እና ዜግነት", "account_balance", 0xFFDC2626)
}

enum class SolverMode {
    HINT_ONLY,
    STEP_BY_STEP,
    FULL_SOLUTION
}

data class SolutionStep(
    val stepNumber: Int,
    val titleEn: String,
    val titleAm: String,
    val contentEn: String,
    val contentAm: String,
    val formulaOrRule: String? = null
)

data class HomeworkSolution(
    val problemTitleEn: String,
    val problemTitleAm: String,
    val subject: Subject,
    val difficulty: String,
    val hintEn: String,
    val hintAm: String,
    val steps: List<SolutionStep>,
    val finalAnswerEn: String,
    val finalAnswerAm: String,
    val whyCorrectEn: String,
    val whyCorrectAm: String,
    val alternativeSolutionEn: String,
    val alternativeSolutionAm: String
)

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: MessageSender,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isAmharic: Boolean = false,
    val suggestedFollowUps: List<String> = emptyList()
)

enum class MessageSender {
    USER,
    AI_TUTOR,
    SYSTEM
}

data class ExamCountdown(
    val id: String,
    val titleEn: String,
    val titleAm: String,
    val targetDateEpochMillis: Long,
    val subject: Subject,
    val totalTopics: Int,
    val coveredTopics: Int
)

data class QuizQuestion(
    val id: Int,
    val subject: Subject,
    val questionEn: String,
    val questionAm: String,
    val optionsEn: List<String>,
    val optionsAm: List<String>,
    val correctIndex: Int,
    val explanationEn: String,
    val explanationAm: String
)

data class CommunityPost(
    val id: String,
    val authorName: String,
    val authorRole: String,
    val authorAvatarUrl: String? = null,
    val subject: Subject,
    val title: String,
    val body: String,
    val upvotes: Int,
    val replyCount: Int,
    val timestamp: String,
    val isSolved: Boolean
)

data class TeacherAssignment(
    val id: String,
    val title: String,
    val subject: Subject,
    val gradeLevel: String,
    val dueDate: String,
    val totalStudents: Int,
    val submittedCount: Int,
    val averageScore: Int
)

package com.example.data.model

object AppStrings {
    fun appTitle(lang: AppLanguage) = when (lang) {
        AppLanguage.AMHARIC -> "ትምህርት አጋዥ AI"
        AppLanguage.ENGLISH -> "Smart Study AI"
    }

    fun tagline(lang: AppLanguage) = when (lang) {
        AppLanguage.AMHARIC -> "ቀጣዩ ትውልድ የማሰብ ችሎታ ያለው የትምህርት መድረክ"
        AppLanguage.ENGLISH -> "Next-Gen AI Learning & Homework Assistant"
    }

    // Tabs
    fun tabHome(lang: AppLanguage) = when (lang) {
        AppLanguage.AMHARIC -> "መነሻ"
        AppLanguage.ENGLISH -> "Dashboard"
    }

    fun tabHomework(lang: AppLanguage) = when (lang) {
        AppLanguage.AMHARIC -> "የቤት ሥራ አጋዥ"
        AppLanguage.ENGLISH -> "Homework"
    }

    fun tabTutor(lang: AppLanguage) = when (lang) {
        AppLanguage.AMHARIC -> "AI አስተማሪ"
        AppLanguage.ENGLISH -> "AI Tutor"
    }

    fun tabPlanner(lang: AppLanguage) = when (lang) {
        AppLanguage.AMHARIC -> "መርሃ ግብር"
        AppLanguage.ENGLISH -> "Planner"
    }

    fun tabQuiz(lang: AppLanguage) = when (lang) {
        AppLanguage.AMHARIC -> "ፈተና / ፈተሽ"
        AppLanguage.ENGLISH -> "Quiz & Exam"
    }

    fun tabNotes(lang: AppLanguage) = when (lang) {
        AppLanguage.AMHARIC -> "ማስታወሻና ፍላሽካርድ"
        AppLanguage.ENGLISH -> "Notes & Cards"
    }

    fun tabCommunity(lang: AppLanguage) = when (lang) {
        AppLanguage.AMHARIC -> "የተማሪዎች ማህበር"
        AppLanguage.ENGLISH -> "Community"
    }

    fun tabTeacher(lang: AppLanguage) = when (lang) {
        AppLanguage.AMHARIC -> "መምህር ክፍል"
        AppLanguage.ENGLISH -> "Teacher Hub"
    }

    fun tabProfile(lang: AppLanguage) = when (lang) {
        AppLanguage.AMHARIC -> "መገለጫና አልሚ"
        AppLanguage.ENGLISH -> "Profile & Dev"
    }

    // Modes
    fun hintMode(lang: AppLanguage) = when (lang) {
        AppLanguage.AMHARIC -> "ፍንጭ ብቻ"
        AppLanguage.ENGLISH -> "Hint Only"
    }

    fun stepByStepMode(lang: AppLanguage) = when (lang) {
        AppLanguage.AMHARIC -> "ደረጃ በደረጃ"
        AppLanguage.ENGLISH -> "Step-by-Step"
    }

    fun fullAnswerMode(lang: AppLanguage) = when (lang) {
        AppLanguage.AMHARIC -> "ሙሉ መፍትሔ"
        AppLanguage.ENGLISH -> "Full Solution"
    }

    // Actions
    fun solveButton(lang: AppLanguage) = when (lang) {
        AppLanguage.AMHARIC -> "በ AI ፍታ / አስረዳ"
        AppLanguage.ENGLISH -> "Solve with AI"
    }

    fun askQuestionPlaceholder(lang: AppLanguage) = when (lang) {
        AppLanguage.AMHARIC -> "ጥያቄዎን ወይም የቤት ሥራዎን እዚህ ይጻፉ..."
        AppLanguage.ENGLISH -> "Type your homework problem or question here..."
    }

    fun sampleProblems(lang: AppLanguage) = when (lang) {
        AppLanguage.AMHARIC -> "ምሳሌ ጥያቄዎች"
        AppLanguage.ENGLISH -> "Sample Problems"
    }

    fun stepTitle(lang: AppLanguage, step: Int) = when (lang) {
        AppLanguage.AMHARIC -> "ደረጃ $step"
        AppLanguage.ENGLISH -> "Step $step"
    }

    fun whyCorrect(lang: AppLanguage) = when (lang) {
        AppLanguage.AMHARIC -> "ይህ መልስ ለምን ትክክል ሆነ?"
        AppLanguage.ENGLISH -> "Why is this answer correct?"
    }

    fun alternativeApproach(lang: AppLanguage) = when (lang) {
        AppLanguage.AMHARIC -> "አማራጭ የመፍቻ መንገድ"
        AppLanguage.ENGLISH -> "Alternative Solution Method"
    }

    fun askFollowUp(lang: AppLanguage) = when (lang) {
        AppLanguage.AMHARIC -> "ተጨማሪ ጥያቄ ጠይቅ..."
        AppLanguage.ENGLISH -> "Ask a follow-up question..."
    }

    fun studyStreak(lang: AppLanguage, days: Int) = when (lang) {
        AppLanguage.AMHARIC -> "የትምህርት ቅደም ተከተል: $days ቀናት 🔥"
        AppLanguage.ENGLISH -> "Study Streak: $days Days 🔥"
    }

    fun totalHours(lang: AppLanguage) = when (lang) {
        AppLanguage.AMHARIC -> "ጠቅላላ የጥናት ሰዓት"
        AppLanguage.ENGLISH -> "Total Study Hours"
    }

    fun problemsSolved(lang: AppLanguage) = when (lang) {
        AppLanguage.AMHARIC -> "የተፈቱ ጥያቄዎች"
        AppLanguage.ENGLISH -> "Problems Solved"
    }

    fun quizAverage(lang: AppLanguage) = when (lang) {
        AppLanguage.AMHARIC -> "አማካይ የፈተና ውጤት"
        AppLanguage.ENGLISH -> "Quiz Average Score"
    }

    fun roleLabel(lang: AppLanguage, role: UserRole) = when (role) {
        UserRole.STUDENT -> if (lang == AppLanguage.AMHARIC) "ተማሪ" else "Student"
        UserRole.TEACHER -> if (lang == AppLanguage.AMHARIC) "መምህር" else "Teacher"
        UserRole.ADMIN -> if (lang == AppLanguage.AMHARIC) "አስተዳዳሪ" else "Administrator"
    }

    fun developerTitle(lang: AppLanguage) = when (lang) {
        AppLanguage.AMHARIC -> "ስለ መተግበሪያው አልሚ"
        AppLanguage.ENGLISH -> "About the Developer"
    }

    fun developerRole(lang: AppLanguage) = when (lang) {
        AppLanguage.AMHARIC -> "አልሚ እና AI አርክቴክት: አብዱልመጂድ ተውፊቅ"
        AppLanguage.ENGLISH -> "Lead AI Architect & Software Engineer: Abdulmajed Tofik"
    }

    fun developerBio(lang: AppLanguage) = when (lang) {
        AppLanguage.AMHARIC -> "ትምህርት አጋዥ (Smart Study AI) የተገነባው በአብዱልመጂድ ተውፊቅ ነው። የኢትዮጵያ እና የአፍሪካ ተማሪዎች አስቸጋሪ የትምህርት አይነቶችን በቀላሉ እንዲረዱ፣ የቤት ሥራዎችን በደረጃ እንዲፈቱ እና ለሀገር አቀፍ እንዲሁም ለዩኒቨርሲቲ ፈተናዎች በላቀ ደረጃ እንዲዘጋጁ የተፈጠረ የላቀ የ AI የትምህርት መድረክ ነው።"
        AppLanguage.ENGLISH -> "Smart Study AI is engineered by Abdulmajed Tofik. It is an intelligent learning ecosystem designed to empower students across Ethiopia and worldwide to master complex STEM and humanities concepts, solve homework step-by-step with verified pedagogical explanations, and excel in national and university examinations."
    }
}

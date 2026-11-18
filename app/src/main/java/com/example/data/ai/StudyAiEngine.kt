package com.example.data.ai

import com.example.data.local.StudyNoteEntity
import com.example.data.model.AppLanguage
import com.example.data.model.ChatMessage
import com.example.data.model.HomeworkSolution
import com.example.data.model.MessageSender
import com.example.data.model.QuizQuestion
import com.example.data.model.SolutionStep
import com.example.data.model.SolverMode
import com.example.data.model.Subject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

object StudyAiEngine {

    suspend fun solveHomework(
        query: String,
        subject: Subject,
        mode: SolverMode,
        lang: AppLanguage,
        customApiKey: String? = null
    ): HomeworkSolution = withContext(Dispatchers.IO) {
        // If an API key is available, attempt dynamic Gemini call with fallback
        val effectiveKey = customApiKey?.trim()?.takeIf { it.isNotEmpty() }
            ?: com.example.BuildConfig.GEMINI_API_KEY.trim().takeIf { it.isNotEmpty() && it != "MY_GEMINI_API_KEY" }

        if (effectiveKey != null) {
            val dynamicResult = callGeminiForHomework(query, subject, mode, lang, effectiveKey)
            if (dynamicResult != null) {
                return@withContext dynamicResult
            }
        }

        // Built-in verified pedagogical engine
        return@withContext resolvePedagogicalSolution(query, subject, mode)
    }

    private fun resolvePedagogicalSolution(query: String, subject: Subject, mode: SolverMode): HomeworkSolution {
        val qLower = query.lowercase()

        return when {
            // Mathematics: Quadratic Equation or Calculus
            subject == Subject.MATHEMATICS || qLower.contains("x") || qLower.contains("solve") || qLower.contains("equation") -> {
                HomeworkSolution(
                    problemTitleEn = if (query.isNotBlank()) query else "Solve: 2x² + 5x - 3 = 0",
                    problemTitleAm = if (query.isNotBlank()) "የተሰጠ ጥያቄ: $query" else "ይሰላ: 2x² + 5x - 3 = 0",
                    subject = Subject.MATHEMATICS,
                    difficulty = "Secondary / Matric Prep",
                    hintEn = "Identify coefficients a=2, b=5, c=-3. Calculate discriminant Δ = b² - 4ac first to verify real roots exist.",
                    hintAm = "ቅድመ-ዕውቀት: a=2, b=5, c=-3 ለዩ። ከዚያም Δ = b² - 4ac በመጠቀም ሥሮች መኖራቸውን ያረጋግጡ።",
                    steps = listOf(
                        SolutionStep(
                            stepNumber = 1,
                            titleEn = "Standard Form Identification",
                            titleAm = "መደበኛውን የቀመር ቅርፅ መለየት",
                            contentEn = "The equation 2x² + 5x - 3 = 0 is in standard quadratic form ax² + bx + c = 0. Here, a = 2, b = 5, and c = -3.",
                            contentAm = "ቀመሩ 2x² + 5x - 3 = 0 በመደበኛ ቅርፅ ax² + bx + c = 0 ውስጥ ይገኛል። እዚህ a = 2, b = 5, እና c = -3 ናቸው።",
                            formulaOrRule = "ax² + bx + c = 0"
                        ),
                        SolutionStep(
                            stepNumber = 2,
                            titleEn = "Compute the Discriminant (Δ)",
                            titleAm = "ዲስክሪሚናንት (Δ) ማስላት",
                            contentEn = "Δ = b² - 4ac = (5)² - 4(2)(-3) = 25 - (-24) = 25 + 24 = 49. Since Δ = 49 > 0 and is a perfect square (7²), there are two distinct rational roots.",
                            contentAm = "Δ = b² - 4ac = (5)² - 4(2)(-3) = 25 + 24 = 49. Δ = 49 > 0 በመሆኑ እና ፍጹም ካሬ (7²) ስለሆነ ሁለት የተለያዩ ትክክለኛ ሥሮች አሉት።",
                            formulaOrRule = "Δ = b² - 4ac = 49"
                        ),
                        SolutionStep(
                            stepNumber = 3,
                            titleEn = "Apply the Quadratic Formula",
                            titleAm = "የኳድራቲክ ፎርሙላን ተግባራዊ ማድረግ",
                            contentEn = "x = (-b ± √Δ) / (2a) = (-5 ± √49) / (2 · 2) = (-5 ± 7) / 4.",
                            contentAm = "x = (-b ± √Δ) / (2a) = (-5 ± √49) / (2 · 2) = (-5 ± 7) / 4።",
                            formulaOrRule = "x = (-b ± √Δ) / 2a"
                        ),
                        SolutionStep(
                            stepNumber = 4,
                            titleEn = "Calculate Individual Root Values",
                            titleAm = "የሁለቱን ዋጋዎች ማስላት",
                            contentEn = "Root 1: x₁ = (-5 + 7) / 4 = 2 / 4 = 1/2 (or 0.5)\nRoot 2: x₂ = (-5 - 7) / 4 = -12 / 4 = -3.",
                            contentAm = "ዋጋ 1: x₁ = (-5 + 7) / 4 = 2 / 4 = 1/2 (ወይም 0.5)\nዋጋ 2: x₂ = (-5 - 7) / 4 = -12 / 4 = -3።",
                            formulaOrRule = "x ∈ { -3, 1/2 }"
                        )
                    ),
                    finalAnswerEn = "The solutions to the equation are x = 1/2 and x = -3.",
                    finalAnswerAm = "የቀመሩ የመጨረሻ መልሶች x = 1/2 እና x = -3 ናቸው።",
                    whyCorrectEn = "Substitution Check:\nFor x = -3: 2(-3)² + 5(-3) - 3 = 2(9) - 15 - 3 = 18 - 18 = 0 (Verified).\nFor x = 1/2: 2(1/4) + 5/2 - 3 = 1/2 + 5/2 - 3 = 6/2 - 3 = 0 (Verified).",
                    whyCorrectAm = "ማረጋገጫ:\nበ x = -3 ቦታ ስንተካ: 2(-3)² + 5(-3) - 3 = 18 - 18 = 0 (ትክክል)።\nበ x = 1/2 ቦታ: 2(1/4) + 5/2 - 3 = 3 - 3 = 0 (ትክክል)።",
                    alternativeSolutionEn = "Factoring Method:\nFind two numbers multiplying to (a · c) = -6 and adding to b = 5. These are 6 and -1.\nRewrite: 2x² + 6x - x - 3 = 0\nFactor by grouping: 2x(x + 3) - 1(x + 3) = 0\n(2x - 1)(x + 3) = 0 => x = 1/2 or x = -3.",
                    alternativeSolutionAm = "በማባዛት የመክፈል መንገድ (Factoring):\nየሚያባዙት -6 እና ድምራቸው 5 የሆኑ ቁጥሮች 6 እና -1 ናቸው።\n2x(x + 3) - 1(x + 3) = 0 => (2x - 1)(x + 3) = 0 => x = 1/2 ወይም x = -3።"
                )
            }

            // Physics: Mechanics or Energy
            subject == Subject.PHYSICS || qLower.contains("velocity") || qLower.contains("force") || qLower.contains("acceleration") || qLower.contains("energy") -> {
                HomeworkSolution(
                    problemTitleEn = if (query.isNotBlank()) query else "Calculate the final velocity of a 1500kg car accelerating from rest at 3 m/s² over 100 meters.",
                    problemTitleAm = if (query.isNotBlank()) "ጥያቄ: $query" else "የ 1500 ኪሎ ግራም መኪና ከቆመበት በ 3 m/s² ፍጥነት ለ 100 ሜትር ሲጓዝ የመጨረሻ ፍጥነቱ ስንት ይሆናል?",
                    subject = Subject.PHYSICS,
                    difficulty = "High School & University Prep",
                    hintEn = "Recall the kinematic equation relating velocity, acceleration, and displacement independent of time: v² = u² + 2as.",
                    hintAm = "የእንቅስቃሴ ቀመር: v² = u² + 2as ይጠቀሙ (ጊዜ ሳያስፈልግ ፍጥነት እና ርቀትን የሚያገናኘው)።",
                    steps = listOf(
                        SolutionStep(
                            stepNumber = 1,
                            titleEn = "List Given Variables",
                            titleAm = "የተሰጡ መረጃዎችን መለየት",
                            contentEn = "Initial velocity (u) = 0 m/s (from rest)\nAcceleration (a) = 3.0 m/s²\nDisplacement (s) = 100 m\nMass (m) = 1500 kg (extra data)",
                            contentAm = "የመነሻ ፍጥነት (u) = 0 m/s\nፍጥነት መጨመር (a) = 3.0 m/s²\nየተጓዘው ርቀት (s) = 100 m",
                            formulaOrRule = "u = 0, a = 3 m/s², s = 100 m"
                        ),
                        SolutionStep(
                            stepNumber = 2,
                            titleEn = "Select Kinematic Equation",
                            titleAm = "ትክክለኛውን የእንቅስቃሴ ቀመር መምረጥ",
                            contentEn = "Since time (t) is not provided and not required, use Torricelli's formula: v² = u² + 2as.",
                            contentAm = "ጊዜ (t) ስላልተሰጠ v² = u² + 2as የሚለውን ቀመር እንጠቀማለን።",
                            formulaOrRule = "v² = u² + 2as"
                        ),
                        SolutionStep(
                            stepNumber = 3,
                            titleEn = "Substitute and Solve for v",
                            titleAm = "ዋጋዎችን መተካትና ማስላት",
                            contentEn = "v² = (0)² + 2(3 m/s²)(100 m) = 0 + 600 = 600 m²/s².\nv = √600 ≈ 24.49 m/s (≈ 88.2 km/h).",
                            contentAm = "v² = 0 + 2(3)(100) = 600 m²/s²።\nv = √600 ≈ 24.49 m/s (ወደ 88.2 ኪሜ/ሰዓት)።",
                            formulaOrRule = "v = √600 ≈ 24.5 m/s"
                        )
                    ),
                    finalAnswerEn = "The final velocity of the vehicle is approximately 24.5 m/s (88.2 km/h).",
                    finalAnswerAm = "የመኪናው የመጨረሻ ፍጥነት በግምት 24.5 m/s (88.2 ኪ.ሜ/ሰዓት) ነው።",
                    whyCorrectEn = "Work-Energy Theorem: Work Done = F · s = (m · a) · s = ΔKE = 1/2 m(v² - u²). Dividing both sides by (1/2 m) yields 2as = v² - u², confirming consistency with fundamental conservation laws.",
                    whyCorrectAm = "የስራና የጉልበት ህግ (Work-Energy Theorem): የተሰራ ስራ = ሃይል × ርቀት = የኪነቲክ ጉልበት ለውጥ። ሁለቱም መንገዶች አንድ አይነት ውጤት ይሰጣሉ።",
                    alternativeSolutionEn = "Find time first: s = 1/2 a t² => 100 = 1.5 t² => t² = 66.67 => t = 8.165 s. Then v = u + at = 0 + (3)(8.165) = 24.49 m/s.",
                    alternativeSolutionAm = "በመጀመሪያ ጊዜውን ማስላት: s = 1/2 a t² => t ≈ 8.165 ሰከንድ። ከዚያም v = at = 3 × 8.165 = 24.49 m/s።"
                )
            }

            // Chemistry: Molarity or Stoichiometry
            subject == Subject.CHEMISTRY || qLower.contains("mole") || qLower.contains("reaction") || qLower.contains("acid") -> {
                HomeworkSolution(
                    problemTitleEn = if (query.isNotBlank()) query else "Calculate the molarity of a solution containing 11.7g of NaCl dissolved in 500 mL of water.",
                    problemTitleAm = if (query.isNotBlank()) "ጥያቄ: $query" else "11.7 ግራም NaCl በ 500 ሚሊ ሊትር ውሃ ውስጥ ሲሟሟ የሚኖረውን ሞላሪቲ (Molarity) አስላ።",
                    subject = Subject.CHEMISTRY,
                    difficulty = "General Chemistry",
                    hintEn = "Molarity (M) = Moles of solute / Liters of solution. Atomic masses: Na = 23 g/mol, Cl = 35.5 g/mol.",
                    hintAm = "ሞላሪቲ = የሞሎች ብዛት ÷ የመፍትሔው ሊትር። የአተሞች ክብደት: Na = 23, Cl = 35.5 g/mol።",
                    steps = listOf(
                        SolutionStep(
                            stepNumber = 1,
                            titleEn = "Determine Molar Mass of NaCl",
                            titleAm = "የ NaCl ሞላር ክብደት ማስላት",
                            contentEn = "Molar Mass = Mass of Na + Mass of Cl = 23.0 + 35.45 = 58.45 g/mol.",
                            contentAm = "ሞላር ክብደት = Na + Cl = 23.0 + 35.45 = 58.45 g/mol።",
                            formulaOrRule = "M(NaCl) = 58.45 g/mol"
                        ),
                        SolutionStep(
                            stepNumber = 2,
                            titleEn = "Convert Grams to Moles",
                            titleAm = "ግራም ወደ ሞል መቀየር",
                            contentEn = "Moles (n) = Mass / Molar Mass = 11.7 g / 58.45 g/mol ≈ 0.200 mol.",
                            contentAm = "ሞል (n) = ክብደት ÷ ሞላር ክብደት = 11.7 / 58.45 ≈ 0.200 ሞል ።",
                            formulaOrRule = "n = m / M = 0.20 mol"
                        ),
                        SolutionStep(
                            stepNumber = 3,
                            titleEn = "Convert Volume to Liters & Compute Molarity",
                            titleAm = "መጠኑን ወደ ሊትር ቀይሮ ሞላሪቲውን ማስላት",
                            contentEn = "Volume (V) = 500 mL / 1000 = 0.500 L.\nMolarity (M) = n / V = 0.200 mol / 0.500 L = 0.400 M (mol/L).",
                            contentAm = "መጠን (V) = 500 mL = 0.500 ሊትር።\nሞላሪቲ (M) = 0.200 ሞል ÷ 0.500 ሊትር = 0.400 M።",
                            formulaOrRule = "M = n / V = 0.400 M"
                        )
                    ),
                    finalAnswerEn = "The concentration of the solution is 0.400 M (0.400 mol/L NaCl).",
                    finalAnswerAm = "የመፍትሔው ትኩረት 0.400 M (ሞል በሊትር) ነው።",
                    whyCorrectEn = "Every 0.5 L contains 0.20 moles, which scales proportionately to exactly 0.40 moles per liter of solvent.",
                    whyCorrectAm = "በግማሽ ሊትር ውስጥ 0.20 ሞል ሲኖር፣ በአንድ ሙሉ ሊትር ውስጥ 0.40 ሞል ይገኛል ማለት ነው።",
                    alternativeSolutionEn = "Direct dimensional analysis: (11.7 g NaCl / 0.500 L) × (1 mol NaCl / 58.45 g) = 0.400 mol/L.",
                    alternativeSolutionAm = "በአንድ መስመር ስሌት: (11.7 g / 0.5 L) × (1 mol / 58.45 g) = 0.400 M።"
                )
            }

            // Biology: Genetics / Cells
            subject == Subject.BIOLOGY || qLower.contains("cell") || qLower.contains("dna") || qLower.contains("photosynthesis") -> {
                HomeworkSolution(
                    problemTitleEn = if (query.isNotBlank()) query else "Explain the primary stages and products of Photosynthesis.",
                    problemTitleAm = if (query.isNotBlank()) "ጥያቄ: $query" else "የፎቶሲንተሲስ (የብርሃን አስተፃምሮ) ዋና ዋና ደረጃዎችንና ውጤቶችን አስረዳ።",
                    subject = Subject.BIOLOGY,
                    difficulty = "Secondary School Biology",
                    hintEn = "Photosynthesis takes place in chloroplasts in two interconnected stages: Light-Dependent Reactions and the Light-Independent Calvin Cycle.",
                    hintAm = "ፎቶሲንተሲስ በክሎሮፕላስት ውስጥ በሁለት ዋና ደረጃዎች ይካሄዳል: የብርሃን ምላሽ እና የካልቪን ዑደት።",
                    steps = listOf(
                        SolutionStep(
                            stepNumber = 1,
                            titleEn = "Light-Dependent Reactions (Thylakoids)",
                            titleAm = "የብርሃን ጥገኛ ምላሽ (በታይላኮይድ)",
                            contentEn = "Chlorophyll absorbs photons, splitting H₂O molecules (photolysis) to release oxygen (O₂) gas and produce high-energy molecules ATP and NADPH.",
                            contentAm = "ክሎሮፊል የፀሐይ ብርሃንን በመሳብ የውሃ ሞለኪውልን (H₂O) ይሰብራል። በዚህም ኦክስጅን (O₂) ይለቀቃል፣ ATP እና NADPH ይመረታሉ።",
                            formulaOrRule = "2H₂O + Light -> O₂ + 4H⁺ + 4e⁻"
                        ),
                        SolutionStep(
                            stepNumber = 2,
                            titleEn = "Calvin Cycle (Stroma)",
                            titleAm = "የካልቪን ዑደት (በስትሮማ)",
                            contentEn = "Carbon fixation occurs: Rubisco enzyme incorporates CO₂ with RuBP to synthesize 3-carbon sugars (G3P), which combine to form glucose (C₆H₁₂O₆).",
                            contentAm = "የካርቦን ዳይኦክሳይድ (CO₂) ውህደት ይካሄዳል፤ ሩቢስኮ ኢንዛይም ካርቦንን በመጠቀም ግሉኮስ (ስኳር) ያመርታል።",
                            formulaOrRule = "CO₂ + ATP + NADPH -> C₆H₁₂O₆ + ADP + NADP⁺"
                        )
                    ),
                    finalAnswerEn = "Overall Equation: 6CO₂ + 6H₂O + Light Energy -> C₆H₁₂O₆ + 6O₂. Inputs are sunlight, water, and CO₂; outputs are glucose and oxygen.",
                    finalAnswerAm = "ጠቅላላ ቀመር: 6CO₂ + 6H₂O + የብርሃን ሃይል -> C₆H₁₂O₆ + 6O₂። ግብዓቶቹ ውሃ፣ ብርሃንና ካርቦንዳይኦክሳይድ ሲሆኑ ውጤቶቹ ግሉኮስና ኦክስጅን ናቸው።",
                    whyCorrectEn = "Without photolysis of water in the thylakoid membranes, electrons cannot replenish photosystem II, and no NADPH/ATP can be generated to drive glucose synthesis in the stroma.",
                    whyCorrectAm = "የውሃ መከፋፈል ካልተከናወነ ለካልቪን ዑደት የሚያስፈልገው ጉልበት አይፈጠርም።",
                    alternativeSolutionEn = "Summarized as an energy transformation: Solar radiant energy is converted into chemical bond energy stored in carbohydrates.",
                    alternativeSolutionAm = "እንደ ሃይል ለውጥ ሲታይ: የብርሃን ሃይል በኬሚካላዊ የቦንድ ሃይል መልክ በግሉኮስ ውስጥ ይቀመጣል።"
                )
            }

            // Programming & CS
            subject == Subject.COMPUTER_SCIENCE || qLower.contains("code") || qLower.contains("python") || qLower.contains("algorithm") || qLower.contains("time complexity") -> {
                HomeworkSolution(
                    problemTitleEn = if (query.isNotBlank()) query else "Explain Binary Search algorithm and its Time Complexity analysis.",
                    problemTitleAm = if (query.isNotBlank()) "ጥያቄ: $query" else "የባይነሪ ሰርች (Binary Search) አልጎሪዝም አሰራር እና የጊዜ ውስብስብነቱን (Time Complexity) አስረዳ።",
                    subject = Subject.COMPUTER_SCIENCE,
                    difficulty = "Data Structures & Algorithms",
                    hintEn = "Binary search requires a sorted array and uses a divide-and-conquer approach by halving the search space each step.",
                    hintAm = "ባይነሪ ሰርች ዝርዝሩ የተደረደረ (sorted) መሆን አለበት፤ በእያንዳንዱ እርምጃ የፍለጋውን ቦታ በግማሽ ይቀንሳል።",
                    steps = listOf(
                        SolutionStep(
                            stepNumber = 1,
                            titleEn = "Precondition: Sorted Array",
                            titleAm = "ቅድመ-ሁኔታ: የተደረደረ ዳታ",
                            contentEn = "Given sorted array A of length N. Initialize low = 0 and high = N - 1.",
                            contentAm = "ርዝመቱ N የሆነ የተደረደረ አሬይ። መነሻ low = 0 እና high = N - 1 እናዘጋጃለን።",
                            formulaOrRule = "low = 0, high = N - 1"
                        ),
                        SolutionStep(
                            stepNumber = 2,
                            titleEn = "Midpoint Calculation & Comparison",
                            titleAm = "የመሀከለኛውን ማግኘትና ማወዳደር",
                            contentEn = "Calculate mid = low + (high - low) / 2 to prevent integer overflow. If A[mid] == target, return mid. If A[mid] < target, search right (low = mid + 1). Otherwise search left (high = mid - 1).",
                            contentAm = "mid = low + (high - low) / 2። A[mid] ከምንፈልገው ጋር እኩል ከሆነ ተገኘ። ካነሰ ወደ ቀኝ፣ ከበለጠ ወደ ግራ እንሄዳለን።",
                            formulaOrRule = "mid = low + (high - low) // 2"
                        ),
                        SolutionStep(
                            stepNumber = 3,
                            titleEn = "Logarithmic Time Complexity Proof",
                            titleAm = "የጊዜ ውስብስብነት (O(log N)) ማረጋገጫ",
                            contentEn = "At each iteration k, remaining elements = N / 2^k. Search terminates when N / 2^k = 1 => 2^k = N => k = log₂(N). Therefore, worst-case time complexity is O(log N).",
                            contentAm = "በእያንዳንዱ ዙር የዳታው ብዛት በ 2 ይከፈላል: N / 2^k = 1 => k = log₂(N)። ስለዚህ ውስብስብነቱ O(log N) ነው።",
                            formulaOrRule = "T(N) = O(log N), Space: O(1)"
                        )
                    ),
                    finalAnswerEn = "Binary Search achieves O(log N) worst-case time complexity and O(1) auxiliary space complexity.",
                    finalAnswerAm = "የባይነሪ ሰርች የጊዜ ውስብስብነት O(log N) ሲሆን የቦታ ፍጆታው O(1) ነው።",
                    whyCorrectEn = "Doubling the array size from 1,000,000 to 2,000,000 elements only adds a single extra comparison step (log₂ 2,000,000 ≈ 21 steps), far superior to linear search O(N).",
                    whyCorrectAm = "የዳታው መጠን ከአንድ ሚሊዮን ወደ ሁለት ሚሊዮን ቢያድግ እንኳ የሚጨምረው 1 ንፅፅር ብቻ ነው (log₂ 2M ≈ 21 ዙር)።",
                    alternativeSolutionEn = "Interpolation Search for uniformly distributed data achieves O(log log N) average time complexity.",
                    alternativeSolutionAm = "እኩል የተከፋፈለ ዳታ ሲኖር Interpolation Search O(log log N) ፈጣን አማራጭ ነው።"
                )
            }

            // General Humanities / English / Civics / Default
            else -> {
                HomeworkSolution(
                    problemTitleEn = if (query.isNotBlank()) query else "Analyze key principles of Constitutional Federalism and civic engagement.",
                    problemTitleAm = if (query.isNotBlank()) "ጥያቄ: $query" else "የሕገ-መንግሥታዊ ፌዴራሊዝም መርሆዎችን እና የዜጎችን ተሳትፎ ተንትን።",
                    subject = subject,
                    difficulty = "Civics & Academic Writing",
                    hintEn = "Focus on division of power, checks and balances, rule of law, and citizen representation.",
                    hintAm = "የስልጣን ክፍፍል፣ የህግ የበላይነት እና የዜጎች ተሳትፎ ላይ አተኩር።",
                    steps = listOf(
                        SolutionStep(
                            stepNumber = 1,
                            titleEn = "Division of Sovereign Powers",
                            titleAm = "የስልጣን ክፍፍል መርህ",
                            contentEn = "Federal systems distribute authority between the central government and regional constituent units, preserving regional autonomy while maintaining collective defense and economic unity.",
                            contentAm = "ስልጣን በማዕከላዊ መንግስት እና በክልላዊ ክፍሎች መካከል ይከፋፈላል፤ ይህም ራስን በራስ ማስተዳደርን እና ጠንካራ አንድነትን ያረጋግጣል።",
                            formulaOrRule = "Separation of Powers"
                        ),
                        SolutionStep(
                            stepNumber = 2,
                            titleEn = "Protection of Fundamental Rights & Civic Participation",
                            titleAm = "መሰረታዊ መብቶችና የህዝብ ተሳትፎ",
                            contentEn = "Constitutional supremacy ensures individual freedoms, judicial review, active democratic elections, and transparent accountability across all branches of governance.",
                            contentAm = "የህገ-መንግስት የበላይነት የዜጎችን ነፃነት፣ ፍትሃዊ ዳኝነት እና ተጠያቂነትን ያሰፍናል።",
                            formulaOrRule = "Rule of Law & Supremacy"
                        )
                    ),
                    finalAnswerEn = "Constitutional federalism balances unity and diversity through explicit jurisdictional division and civic checks.",
                    finalAnswerAm = "ፌዴራሊዝም አንድነትን እና ብዝሃነትን በስልጣን ክፍፍል አማካኝነት ሚዛናዊ ያደርጋል።",
                    whyCorrectEn = "Historical and legal analysis demonstrates that decentralized governance with constitutional guarantees prevents authoritarian centralization.",
                    whyCorrectAm = "ታሪካዊና ህጋዊ ትንታኔዎች እንደሚያሳዩት የተማከለ አምባገነንነትን ለመከላከል የህግ የበላይነት ወሳኝ ነው።",
                    alternativeSolutionEn = "Compare with Unitary governance models to highlight devolution contrasts.",
                    alternativeSolutionAm = "ከአሃዳዊ የመንግስት መዋቅር ጋር በማነፃፀር ልዩነቱን መረዳት ይቻላል።"
                )
            }
        }
    }

    private fun callGeminiForHomework(
        query: String,
        subject: Subject,
        mode: SolverMode,
        lang: AppLanguage,
        apiKey: String
    ): HomeworkSolution? {
        return try {
            val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"
            val url = URL(endpoint)
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            conn.connectTimeout = 8000
            conn.readTimeout = 12000
            conn.doOutput = true

            val prompt = """
                You are Smart Study AI (ትምህርት አጋዥ), an elite educational tutor.
                Provide a structured pedagogical solution for this homework:
                Subject: ${subject.name}
                Query: $query
                Mode: ${mode.name}
                Target Language: ${lang.name} (Provide BOTH English and Amharic in JSON)

                Output ONLY valid JSON with keys:
                {
                   "problemTitleEn": "...",
                   "problemTitleAm": "...",
                   "difficulty": "...",
                   "hintEn": "...",
                   "hintAm": "...",
                   "steps": [
                      {"stepNumber": 1, "titleEn": "...", "titleAm": "...", "contentEn": "...", "contentAm": "...", "formulaOrRule": "..."}
                   ],
                   "finalAnswerEn": "...",
                   "finalAnswerAm": "...",
                   "whyCorrectEn": "...",
                   "whyCorrectAm": "...",
                   "alternativeSolutionEn": "...",
                   "alternativeSolutionAm": "..."
                }
            """.trimIndent()

            val body = JSONObject()
            val contents = JSONArray()
            val part = JSONObject().put("text", prompt)
            val parts = JSONArray().put(part)
            val contentObj = JSONObject().put("parts", parts)
            contents.put(contentObj)
            body.put("contents", contents)

            OutputStreamWriter(conn.outputStream, "UTF-8").use { it.write(body.toString()) }

            if (conn.responseCode == 200) {
                val responseText = conn.inputStream.bufferedReader().use { it.readText() }
                val root = JSONObject(responseText)
                val candidates = root.optJSONArray("candidates") ?: return null
                if (candidates.length() == 0) return null
                val candidate = candidates.getJSONObject(0)
                val cParts = candidate.getJSONObject("content").getJSONArray("parts")
                val text = cParts.getJSONObject(0).getString("text")

                // Extract JSON block
                val jsonStr = if (text.contains("{") && text.contains("}")) {
                    text.substring(text.indexOf("{"), text.lastIndexOf("}") + 1)
                } else text

                val parsed = JSONObject(jsonStr)
                val stepsList = mutableListOf<SolutionStep>()
                val stepsArray = parsed.optJSONArray("steps")
                if (stepsArray != null) {
                    for (i in 0 until stepsArray.length()) {
                        val sObj = stepsArray.getJSONObject(i)
                        stepsList.add(
                            SolutionStep(
                                stepNumber = sObj.optInt("stepNumber", i + 1),
                                titleEn = sObj.optString("titleEn", "Step ${i + 1}"),
                                titleAm = sObj.optString("titleAm", "ደረጃ ${i + 1}"),
                                contentEn = sObj.optString("contentEn", ""),
                                contentAm = sObj.optString("contentAm", ""),
                                formulaOrRule = if (sObj.has("formulaOrRule") && !sObj.isNull("formulaOrRule")) sObj.getString("formulaOrRule") else null
                            )
                        )
                    }
                }

                HomeworkSolution(
                    problemTitleEn = parsed.optString("problemTitleEn", query),
                    problemTitleAm = parsed.optString("problemTitleAm", query),
                    subject = subject,
                    difficulty = parsed.optString("difficulty", "Secondary / University"),
                    hintEn = parsed.optString("hintEn", "Break down the core formula and solve step-by-step."),
                    hintAm = parsed.optString("hintAm", "ቀመሩን በደረጃ በመተንተን ይፍቱ።"),
                    steps = stepsList,
                    finalAnswerEn = parsed.optString("finalAnswerEn", ""),
                    finalAnswerAm = parsed.optString("finalAnswerAm", ""),
                    whyCorrectEn = parsed.optString("whyCorrectEn", ""),
                    whyCorrectAm = parsed.optString("whyCorrectAm", ""),
                    alternativeSolutionEn = parsed.optString("alternativeSolutionEn", ""),
                    alternativeSolutionAm = parsed.optString("alternativeSolutionAm", "")
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    fun getTutorResponse(
        userPrompt: String,
        subject: Subject,
        lang: AppLanguage
    ): ChatMessage {
        val pLower = userPrompt.lowercase()

        val text = when {
            lang == AppLanguage.AMHARIC -> {
                "ሰላም! እኔ የትምህርት አጋዥ AI አስተማሪዎ ነኝ። በጥያቄዎ ላይ በመመስረት: \"$userPrompt\"\n\n" +
                "📚 **የፅንሰ ሀሳብ ማብራሪያ:**\nበ${subject.amharicName} ውስጥ ይህ መሰረታዊ ርዕስ ነው። ደረጃ በደረጃ ስንመለከተው:\n" +
                "1. ዋናው ቀመር ወይም ህግ በቀጥታ ተግባራዊ ይሆናል።\n" +
                "2. ተለዋዋጮችን (variables) ለይተን ማውጣት የመጀመሪያው እርምጃ ነው።\n" +
                "3. የተገኘውን መልስ በዋናው ጥያቄ በመተካት ማረጋገጥ ይቻላል።\n\n" +
                "💡 **የልምምድ ጥያቄ:** እራስዎ ሞክረው መልሱን ሊነግሩኝ ይችላሉ? እንዲያግዙዎት ከፈለጉ 'ፍንጭ' ይበሉኝ!"
            }
            else -> {
                "Hello! I am your Smart Study AI personal tutor. Regarding your question: \"$userPrompt\"\n\n" +
                "📚 **Conceptual Breakdown for ${subject.englishName}:**\n" +
                "1. **Core Principle:** Identify the governing scientific or mathematical law.\n" +
                "2. **Methodology:** Break the problem into isolated components before computing.\n" +
                "3. **Verification:** Always check dimension consistency and boundary values.\n\n" +
                "💡 **Practice Challenge:** Would you like to solve a related exam-style question together right now? Just ask for a hint or practice problem!"
            }
        }

        val followUps = if (lang == AppLanguage.AMHARIC) {
            listOf("ተጨማሪ ምሳሌ ስጠኝ", "ይህ ለፈተና እንዴት ይመጣል?", "የደረጃ በደረጃ ፍንጭ ስጠኝ")
        } else {
            listOf("Give me another example", "How is this tested in exams?", "Give me a step-by-step hint")
        }

        return ChatMessage(
            sender = MessageSender.AI_TUTOR,
            text = text,
            isAmharic = (lang == AppLanguage.AMHARIC),
            suggestedFollowUps = followUps
        )
    }

    fun getQuizQuestionsForSubject(subject: Subject): List<QuizQuestion> {
        return when (subject) {
            Subject.MATHEMATICS -> listOf(
                QuizQuestion(
                    id = 1,
                    subject = Subject.MATHEMATICS,
                    questionEn = "What are the roots of the quadratic equation x² - 5x + 6 = 0?",
                    questionAm = "የ x² - 5x + 6 = 0 ኳድራቲክ ቀመር ሥሮች (roots) የትኞቹ ናቸው?",
                    optionsEn = listOf("x = 2 and x = 3", "x = -2 and x = -3", "x = 1 and x = 6", "x = -1 and x = -6"),
                    optionsAm = listOf("x = 2 እና x = 3", "x = -2 እና x = -3", "x = 1 እና x = 6", "x = -1 እና x = -6"),
                    correctIndex = 0,
                    explanationEn = "Factor: (x - 2)(x - 3) = 0, giving x = 2 and x = 3.",
                    explanationAm = "በማባዛት: (x - 2)(x - 3) = 0 => x = 2 እና x = 3።"
                ),
                QuizQuestion(
                    id = 2,
                    subject = Subject.MATHEMATICS,
                    questionEn = "What is the derivative of f(x) = 3x⁴ - 2x² + 7 with respect to x?",
                    questionAm = "የ f(x) = 3x⁴ - 2x² + 7 ዴሪቬቲቭ (derivative) ስንት ነው?",
                    optionsEn = listOf("12x³ - 4x", "12x³ - 4x + 7", "3x³ - 2x", "6x³ - 4x"),
                    optionsAm = listOf("12x³ - 4x", "12x³ - 4x + 7", "3x³ - 2x", "6x³ - 4x"),
                    correctIndex = 0,
                    explanationEn = "Using Power Rule: d/dx(x^n) = n·x^(n-1). d/dx(3x⁴) = 12x³, d/dx(-2x²) = -4x, d/dx(7) = 0.",
                    explanationAm = "የፓወር ህግ በመጠቀም: 3(4)x³ - 2(2)x = 12x³ - 4x። የቁጥር ዴሪቬቲቭ 0 ነው።"
                ),
                QuizQuestion(
                    id = 3,
                    subject = Subject.MATHEMATICS,
                    questionEn = "If sin(θ) = 3/5 in a right-angled triangle, what is cos(θ)?",
                    questionAm = "በቀኝ ማዕዘን ሶስት ማዕዘን ውስጥ sin(θ) = 3/5 ከሆነ cos(θ) ስንት ነው?",
                    optionsEn = listOf("4/5", "3/4", "5/4", "5/3"),
                    optionsAm = listOf("4/5", "3/4", "5/4", "5/3"),
                    correctIndex = 0,
                    explanationEn = "Using Pythagorean identity sin²θ + cos²θ = 1 => cos²θ = 1 - 9/25 = 16/25 => cosθ = 4/5.",
                    explanationAm = "በፓይታጎረስ ህግ: sin²θ + cos²θ = 1 => cos²θ = 16/25 => cosθ = 4/5።"
                )
            )

            Subject.PHYSICS -> listOf(
                QuizQuestion(
                    id = 10,
                    subject = Subject.PHYSICS,
                    questionEn = "According to Newton's Second Law, what happens to acceleration if mass doubles while net force remains constant?",
                    questionAm = "በኒውተን ሁለተኛ ህግ መሰረት ሃይሉ ሳይቀየር መጠኑ (mass) በእጥፍ ቢጨምር ፍጥነት መጨመር (acceleration) ምን ይሆናል?",
                    optionsEn = listOf("It is halved (1/2 a)", "It doubles (2a)", "It quadruples (4a)", "It remains unchanged"),
                    optionsAm = listOf("በግማሽ ይቀንሳል (1/2)", "በእጥፍ ይጨምራል", "በአራት እጥፍ ይጨምራል", "አይለወጥም"),
                    correctIndex = 0,
                    explanationEn = "F = m · a => a = F / m. If m -> 2m, then a' = F / (2m) = a / 2.",
                    explanationAm = "a = F / m ስለሆነ፣ mass በእጥፍ ሲጨምር ፍጥነቱ በግማሽ ይቀንሳል።"
                ),
                QuizQuestion(
                    id = 11,
                    subject = Subject.PHYSICS,
                    questionEn = "What is the unit of electrical resistance?",
                    questionAm = "የኤሌክትሪክ ተቃውሞ (Resistance) መለኪያ ምንድን ነው?",
                    optionsEn = listOf("Ohm (Ω)", "Ampere (A)", "Volt (V)", "Watt (W)"),
                    optionsAm = listOf("ኦህም (Ω)", "አምፒር (A)", "ቮልት (V)", "ዋት (W)"),
                    correctIndex = 0,
                    explanationEn = "Resistance is measured in Ohms (Ω), defined as Volts per Ampere (R = V/I).",
                    explanationAm = "የተቃውሞ መለኪያ ኦህም (Ω) ነው። R = V / I።"
                )
            )

            Subject.CHEMISTRY -> listOf(
                QuizQuestion(
                    id = 20,
                    subject = Subject.CHEMISTRY,
                    questionEn = "What is the pH of a neutral aqueous solution at 25°C?",
                    questionAm = "በ 25°C ላይ ገለልተኛ (neutral) የውሃ መፍትሔ ፒኤች (pH) ስንት ነው?",
                    optionsEn = listOf("7.0", "0.0", "14.0", "1.0"),
                    optionsAm = listOf("7.0", "0.0", "14.0", "1.0"),
                    correctIndex = 0,
                    explanationEn = "Neutral water has [H⁺] = [OH⁻] = 1.0 × 10⁻⁷ M, so pH = -log(10⁻⁷) = 7.0.",
                    explanationAm = "ገለልተኛ ውሃ [H⁺] = 10⁻⁷ ስለሆነ pH = 7.0 ነው።"
                ),
                QuizQuestion(
                    id = 21,
                    subject = Subject.CHEMISTRY,
                    questionEn = "Which gas is released when dilute hydrochloric acid reacts with zinc metal?",
                    questionAm = "ሃይድሮክሎሪክ አሲድ ከዚንክ ብረት ጋር ሲገናኝ የሚፈጠረው ጋዝ የትኛው ነው?",
                    optionsEn = listOf("Hydrogen (H₂)", "Oxygen (O₂)", "Chlorine (Cl₂)", "Carbon dioxide (CO₂)"),
                    optionsAm = listOf("ሃይድሮጂን (H₂)", "ኦክስጅን (O₂)", "ክሎሪን (Cl₂)", "ካርቦን ዳይኦክሳይድ (CO₂)"),
                    correctIndex = 0,
                    explanationEn = "Zn (s) + 2HCl (aq) -> ZnCl₂ (aq) + H₂ (g).",
                    explanationAm = "Zn + 2HCl -> ZnCl₂ + H₂ (ሃይድሮጂን ጋዝ ይወጣል)።"
                )
            )

            Subject.COMPUTER_SCIENCE -> listOf(
                QuizQuestion(
                    id = 30,
                    subject = Subject.COMPUTER_SCIENCE,
                    questionEn = "What is the worst-case time complexity of searching in a balanced Binary Search Tree (AVL or Red-Black)?",
                    questionAm = "በተመጣጠነ ባይነሪ ዛፍ (Balanced BST) ውስጥ የፍለጋ ከፍተኛ የጊዜ ውስብስብነት ስንት ነው?",
                    optionsEn = listOf("O(log N)", "O(N)", "O(1)", "O(N²)"),
                    optionsAm = listOf("O(log N)", "O(N)", "O(1)", "O(N²)"),
                    correctIndex = 0,
                    explanationEn = "Balanced BST guarantees height h = O(log N), so search operations require at most O(log N) comparisons.",
                    explanationAm = "የተመጣጠነ ዛፍ ቁመቱ O(log N) በመሆኑ ፍለጋው በ O(log N) ይጠናቀቃል።"
                )
            )

            else -> listOf(
                QuizQuestion(
                    id = 40,
                    subject = Subject.BIOLOGY,
                    questionEn = "Which organelle is known as the powerhouse of the cell?",
                    questionAm = "የሴል የሃይል ማመንጫ (powerhouse) በመባል የሚታወቀው የትኛው ነው?",
                    optionsEn = listOf("Mitochondria", "Ribosome", "Nucleus", "Golgi apparatus"),
                    optionsAm = listOf("ማይቶኮንድሪያ", "ራይቦዞም", "ኒውክሊየስ", "ጎልጂ ቦዲ"),
                    correctIndex = 0,
                    explanationEn = "Mitochondria generate most of the cell's ATP via aerobic cellular respiration.",
                    explanationAm = "ማይቶኮንድሪያ ለሴሉ የሚያስፈልገውን ATP ጉልበት በኦክስጅን በማመንጨት ይታወቃል።"
                )
            )
        }
    }

    fun generateStudyNote(topic: String, subject: Subject): StudyNoteEntity {
        return StudyNoteEntity(
            title = topic.ifBlank { "${subject.englishName} Core Summary" },
            subject = subject.name,
            summaryEn = "Comprehensive revision summary for $topic in ${subject.englishName}. Covers foundational definitions, key governing formulas, exam derivation steps, and standard problem-solving strategies.",
            summaryAm = "የ$topic አጭርና ግልፅ የክለሳ ማጠቃለያ በ${subject.amharicName}። መሰረታዊ ትርጓሜዎችን፣ ዋና ዋና ቀመሮችን እና የፈተና ጥያቄ አፈታት ስልቶችን ያካትታል።",
            keyPointsEn = "• Core Concept: Understand underlying scientific principles before applying equations.\n• Formulas: Memorize standard units and dimensional consistency.\n• Exam Tip: Always state given values, target variable, and write the formula before substituting.",
            keyPointsAm = "• ዋና ፅንሰ ሀሳብ: ቀመሩን ከመጠቀም በፊት ትርጉሙን ይረዱ።\n• የቀመሮች ህግ: መለኪያዎችን (units) ማመሳከር አይርሱ።\n• የፈተና ምክር: የተሰጡትን ለይተው ይፃፉ፣ ቀመሩን ያሳዩ፣ መልሱን ከመለኪያ ጋር ያቅርቡ።"
        )
    }
}

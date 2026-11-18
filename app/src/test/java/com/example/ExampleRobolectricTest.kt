package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.ai.StudyAiEngine
import com.example.data.model.AppLanguage
import com.example.data.model.AppStrings
import com.example.data.model.SolverMode
import com.example.data.model.Subject
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("ትምህርት አጋዥ", appName)
    }

    @Test
    fun `verify bilingual strings for Amharic and English`() {
        assertEquals("ትምህርት አጋዥ AI", AppStrings.appTitle(AppLanguage.AMHARIC))
        assertEquals("Smart Study AI", AppStrings.appTitle(AppLanguage.ENGLISH))
    }

    @Test
    fun `verify StudyAiEngine math solver produces steps`() = runBlocking {
        val solution = StudyAiEngine.solveHomework(
            query = "2x² + 5x - 3 = 0",
            subject = Subject.MATHEMATICS,
            mode = SolverMode.STEP_BY_STEP,
            lang = AppLanguage.AMHARIC
        )
        assertNotNull(solution)
        assertTrue(solution.steps.isNotEmpty())
        assertEquals(Subject.MATHEMATICS, solution.subject)
    }
}

package app.turp.chat.ui

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OnboardingUxContractTest {
    @Test
    fun `welcome stays concise and defers optional setup`() {
        val source = java.io.File("src/main/java/app/turp/chat/ui/OnboardingScreen.kt").readText()

        assertTrue(source.contains("Welcome to Turp"))
        assertTrue(source.contains("Everything here can be changed later in Settings"))
        assertTrue(source.contains("PrimaryNextButton(\"Continue\""))
        assertFalse(source.contains("Starting fresh? Ignore the restore card"))
        assertFalse(source.contains("Optional tools stay optional"))
        assertFalse(source.contains("Start setup"))
    }
}

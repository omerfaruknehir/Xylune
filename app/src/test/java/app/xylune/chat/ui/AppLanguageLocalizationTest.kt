package app.xylune.chat.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class AppLanguageLocalizationTest {
    private fun repositoryFile(path: String): File = sequenceOf(File(path), File("..", path))
        .firstOrNull(File::isFile)
        ?: error("Could not locate repository file: $path")

    @Test
    fun `Turkish locale is advertised and covers every Android string resource`() {
        val base = repositoryFile("app/src/main/res/values/strings.xml").readText()
        val turkish = repositoryFile("app/src/main/res/values-tr/strings.xml").readText()
        val localeConfig = repositoryFile("app/src/main/res/xml/locales_config.xml").readText()

        fun names(xml: String): Set<String> = Regex("""<string\s+name="([^"]+)"""")
            .findAll(xml)
            .map { it.groupValues[1] }
            .toSet()

        assertEquals(names(base), names(turkish))
        assertTrue(localeConfig.contains("android:name=\"tr\""))
        assertTrue(localeConfig.contains("android:name=\"tr-TR\""))
    }

    @Test
    fun `settings exposes real application locale switching`() {
        val main = repositoryFile("app/src/main/java/app/xylune/chat/MainActivity.kt").readText()
        val controller = repositoryFile("app/src/main/java/app/xylune/chat/settings/AppLanguage.kt").readText()
        val action = repositoryFile("app/src/main/java/app/xylune/chat/ui/AppLanguageMenuButton.kt").readText()

        assertTrue(main.contains("localizedAppContext(newBase)"))
        assertTrue(main.contains("screen == Screen.SETTINGS"))
        assertTrue(main.contains("AppLanguageMenuButton("))
        assertTrue(controller.contains("LocaleManager::class.java"))
        assertTrue(controller.contains("applicationLocales"))
        assertTrue(controller.contains("AppLanguage.TURKISH"))
        assertTrue(action.contains("setAppLanguage(context, AppLanguage.TURKISH)"))
        assertTrue(action.contains("R.string.language_turkish"))
    }
}

package app.xylune.chat.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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
        assertTrue(turkish.contains(">Ayarlar<"))
        assertTrue(turkish.contains(">Uygulama dili<"))
    }

    @Test
    fun `language is a real settings route without a delayed overlay host`() {
        val settings = repositoryFile("app/src/main/java/app/xylune/chat/ui/SettingsScreen.kt").readText()
        val routes = repositoryFile("app/src/main/java/app/xylune/chat/ui/SettingsRoute.kt").readText()
        val host = sequenceOf(
            File("app/src/main/java/app/xylune/chat/ui/SettingsHostScreen.kt"),
            File("..", "app/src/main/java/app/xylune/chat/ui/SettingsHostScreen.kt"),
        ).firstOrNull(File::isFile)

        assertTrue(routes.contains("LANGUAGE(R.string.language_dialog_title)"))
        assertTrue(settings.contains("SettingsRoute.LANGUAGE -> LanguageSettingsPage()"))
        assertTrue(settings.contains("onOpen(SettingsRoute.LANGUAGE)"))
        assertTrue(settings.contains("title = stringResource(currentRoute.titleRes)"))
        assertFalse(settings.contains("delay(300)"))
        assertFalse(settings.contains("delay(90)"))
        assertTrue(host == null)
    }

    @Test
    fun `static localized copy resolves through android resources before compatibility formatters`() {
        val localized = repositoryFile("app/src/main/java/app/xylune/chat/ui/LocalizedText.kt").readText()
        val mapping = repositoryFile("app/src/main/java/app/xylune/chat/ui/UiStringResources.kt").readText()
        assertTrue(localized.contains("xyluneUiStringResource(text)"))
        assertTrue(localized.contains("stringResource(staticResource)"))
        assertTrue(mapping.contains("R.string.ui_copy_"))
        assertTrue(mapping.contains("\"Settings\" -> R.string."))
    }
}

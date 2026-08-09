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

    private fun repositoryDirectory(path: String): File = sequenceOf(File(path), File("..", path))
        .firstOrNull(File::isDirectory)
        ?: error("Could not locate repository directory: $path")

    private fun stringResources(path: String): Map<String, String> {
        val strings = linkedMapOf<String, String>()
        repositoryDirectory(path)
            .listFiles()
            .orEmpty()
            .filter { it.isFile && it.name.startsWith("strings") && it.extension == "xml" }
            .sortedBy(File::getName)
            .forEach { file ->
                Regex("""<string\s+name="([^"]+)"[^>]*>(.*?)</string>""", setOf(RegexOption.DOT_MATCHES_ALL))
                    .findAll(file.readText())
                    .forEach { match -> strings[match.groupValues[1]] = match.groupValues[2] }
            }
        return strings
    }

    @Test
    fun `explicit locales cover every Android string resource`() {
        val base = stringResources("app/src/main/res/values")
        val english = stringResources("app/src/main/res/values-en")
        val turkish = stringResources("app/src/main/res/values-tr")
        val localeConfig = repositoryFile("app/src/main/res/xml/locales_config.xml").readText()

        assertEquals(base.keys, english.keys)
        assertEquals(base.keys, turkish.keys)
        assertTrue(localeConfig.contains("android:name=\"en\""))
        assertTrue(localeConfig.contains("android:name=\"tr\""))
        assertTrue(localeConfig.contains("android:name=\"tr-TR\""))
        assertEquals("Ayarlar", turkish.values.first { it == "Ayarlar" })
        assertEquals("Sohbet modeli seç", turkish["tr_complete_choose_chat_model"])
        assertEquals("Görseller", turkish["tr_complete_images"])
        assertTrue(turkish.getValue("tr_complete_detailed_metrics_explanation").startsWith("Ayrıntılı mod"))
        assertTrue(turkish.getValue("tr_complete_as_is_notice").contains("OLDUĞU GİBİ"))
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
    fun `completion mapping only references resource backed strings`() {
        val mapping = repositoryFile("app/src/main/java/app/xylune/chat/ui/TurkishCompletionResources.kt").readText()
        val base = stringResources("app/src/main/res/values")
        val mappedNames = Regex("""R\.string\.(tr_complete_[A-Za-z0-9_]+)""")
            .findAll(mapping)
            .map { it.groupValues[1] }
            .toSet()

        assertTrue(mappedNames.isNotEmpty())
        assertEquals(mappedNames, base.keys.filterTo(linkedSetOf()) { it.startsWith("tr_complete_") })
    }
}

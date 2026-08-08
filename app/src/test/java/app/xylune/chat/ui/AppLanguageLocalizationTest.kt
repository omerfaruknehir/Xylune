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

    @Test
    fun `major Compose surfaces route owned String labels through localization`() {
        listOf(
            "SettingsScreen.kt",
            "OnboardingScreen.kt",
            "ConversationSidebar.kt",
            "ChatScreen.kt",
            "CloudBackupUi.kt",
            "DirectCloudProvidersUi.kt",
            "ImageGenerationScreen.kt",
            "LinuxTerminalScreen.kt",
            "SearchScreen.kt",
        ).forEach { name ->
            val source = repositoryFile("app/src/main/java/app/xylune/chat/ui/$name").readText()
            assertTrue("$name must route String labels through the localized Text facade", source.contains("import androidx.compose.material3.Text as MaterialText"))
        }
    }

    @Test
    fun `Turkish catalogs translate representative app copy and preserve unknown text`() {
        fun translate(value: String): String {
            val primary = TurkishUiCopy.translate(value)
            if (primary != value) return primary
            val secondary = TurkishUiCopyExtra2.translate(value)
            if (secondary != value) return secondary
            return TurkishUiCopyExtra.translate(value)
        }

        assertEquals("Ayarlar", translate("Settings"))
        assertEquals("Yeni sohbet", translate("New chat"))
        assertEquals("Sağlayıcılar ve modeller", translate("Providers & models"))
        assertEquals("Yedekleme ve aktarım", translate("Backup & transfer"))
        assertEquals("Görsel oluşturma", translate("Image generation"))
        assertEquals("Yerel çalıştırma", translate("Local execution"))
        assertEquals("Arşivin kilidi açılamadı", translate("Could not unlock archive"))
        assertEquals("Akıl yürütme ayrıntıları", translate("Reasoning details"))
        assertEquals("3 adımın 2. adımı", translate("Step 2 of 3"))
        assertEquals("My custom project title", translate("My custom project title"))
    }
}

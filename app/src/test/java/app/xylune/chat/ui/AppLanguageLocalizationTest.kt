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
    }

    @Test
    fun `settings exposes application locale switching as a normal destination`() {
        val main = repositoryFile("app/src/main/java/app/xylune/chat/MainActivity.kt").readText()
        val controller = repositoryFile("app/src/main/java/app/xylune/chat/settings/AppLanguage.kt").readText()
        val legacyOverlay = repositoryFile("app/src/main/java/app/xylune/chat/ui/AppLanguageMenuButton.kt").readText()
        val app = repositoryFile("app/src/main/java/app/xylune/chat/ui/XyluneApp.kt").readText()
        val host = repositoryFile("app/src/main/java/app/xylune/chat/ui/SettingsHostScreen.kt").readText()

        assertTrue(main.contains("localizedAppContext(newBase)"))
        assertTrue(controller.contains("LocaleManager::class.java"))
        assertTrue(controller.contains("applicationLocales"))
        assertTrue(controller.contains("AppLanguage.TURKISH"))

        assertTrue(app.contains("SettingsHostScreen(viewModel, compactOpenDrawer)"))
        assertTrue(host.contains("R.string.language_dialog_title"))
        assertTrue(host.contains("currentAppLanguage(context)"))
        assertTrue(host.contains("setAppLanguage(context, AppLanguage.SYSTEM)"))
        assertTrue(host.contains("setAppLanguage(context, AppLanguage.ENGLISH)"))
        assertTrue(host.contains("setAppLanguage(context, AppLanguage.TURKISH)"))
        assertTrue(host.contains("PredictiveNavigationHost("))

        assertFalse(legacyOverlay.contains("IconButton("))
        assertFalse(legacyOverlay.contains("XyluneAlertDialog("))
    }

    @Test
    fun `major Compose surfaces route owned String labels through localization`() {
        listOf(
            "SettingsScreen.kt",
            "SettingsHostScreen.kt",
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
            val tertiary = TurkishUiCopyExtra.translate(value)
            if (tertiary != value) return tertiary
            return TurkishUiCopyExtra3.translate(value)
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

        assertEquals("Yalnızca sağlayıcının yerel araması", translate("Provider native only"))
        assertEquals("Xylune arama motoru", translate("Xylune search engine"))
        assertEquals("Brave Search kimlik bilgisi", translate("Brave Search credential"))
        assertEquals("Otomatik · DuckDuckGo yedeği", translate("Auto · DuckDuckGo fallback"))
        assertEquals(
            "API anahtarları Android'in şifreli tercihlerinde saklanır. Yalnızca sağlayıcının yerel araması modu hiçbir zaman sessizce bir Xylune motoruna geçmez; Otomatik mod gerektiğinde geçer.",
            translate("API keys are stored in Android encrypted preferences. Native-only mode never silently switches to a Xylune engine; Automatic mode does."),
        )

        assertEquals("My custom project title", translate("My custom project title"))
    }
}

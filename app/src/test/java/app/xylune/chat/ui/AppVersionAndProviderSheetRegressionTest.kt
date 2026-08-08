package app.xylune.chat.ui

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class AppVersionAndProviderSheetRegressionTest {
    private fun repositoryFile(path: String): File = sequenceOf(File(path), File("..", path))
        .firstOrNull(File::isFile)
        ?: error("Could not locate repository file: $path")

    @Test
    fun `runtime version comes from installed package metadata`() {
        val version = repositoryFile("app/src/main/java/app/xylune/chat/AppVersion.kt").readText()
        val updates = repositoryFile("app/src/main/java/app/xylune/chat/update/RepositoryUpdateManager.kt").readText()
        val settings = repositoryFile("app/src/main/java/app/xylune/chat/ui/SettingsScreen.kt").readText()
        val archive = repositoryFile("app/src/main/java/app/xylune/chat/transfer/XyluneArchiveManager.kt").readText()

        assertTrue(version.contains("packageManager.getPackageInfo"))
        assertTrue(version.contains("BuildConfig.VERSION_NAME"))
        assertTrue(version.contains("BuildConfig.VERSION_CODE"))
        assertTrue(updates.contains("private val installedVersion = appContext.installedAppVersion()"))
        assertTrue(updates.contains("currentVersion = installedVersion.versionName"))
        assertTrue(updates.contains("currentVersionCode = installedVersion.versionCode"))
        assertTrue(updates.contains("Xylune/\${installedVersion.versionName}"))
        assertTrue(settings.contains("val installedVersion = remember(context) { context.installedAppVersion() }"))
        assertTrue(settings.contains("AboutInfoRow(\"Version\", installedVersion.versionName)"))
        assertTrue(settings.contains("AboutInfoRow(\"Build\", \"\${installedVersion.versionCode} · \${BuildConfig.BUILD_TYPE}\")"))
        assertTrue(archive.contains("appVersion = installedVersion.versionName"))
    }

    @Test
    fun `add provider is an adaptive ime safe sheet`() {
        val source = repositoryFile("app/src/main/java/app/xylune/chat/ui/SettingsScreen.kt").readText()
        val dialog = source.substringAfter("private fun AddProviderDialog(")
            .substringBefore("private fun providerKindLabel(")

        assertTrue(dialog.contains("ModalBottomSheet("))
        assertTrue(dialog.contains("skipPartiallyExpanded = true"))
        assertTrue(dialog.contains(".fillMaxHeight(0.94f)"))
        assertTrue(dialog.contains(".imePadding()"))
        assertTrue(dialog.contains(".navigationBarsPadding()"))
        assertTrue(dialog.contains(".weight(1f)"))
        assertTrue(dialog.contains("if (maxWidth < 360.dp)"))
        assertTrue(dialog.contains("onClick = ::submitProvider"))
        assertFalse(dialog.contains("heightIn(max = 590.dp)"))
        assertFalse(dialog.contains("XyluneAlertDialog("))
    }
}

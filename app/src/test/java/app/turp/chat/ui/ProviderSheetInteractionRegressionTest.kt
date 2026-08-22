package app.turp.chat.ui

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ProviderSheetInteractionRegressionTest {
    private fun repositoryFile(path: String): File = sequenceOf(File(path), File("..", path))
        .firstOrNull(File::isFile)
        ?: error("Could not locate repository file: $path")

    @Test
    fun `provider editors preserve scroll state across expanding content`() {
        val source = repositoryFile("app/src/main/java/app/turp/chat/ui/SettingsScreen.kt").readText()

        assertTrue(source.contains("rememberSaveable(saver = ScrollState.Saver) { ScrollState(0) }"))
        assertTrue(source.contains("editConnectionScrollState"))
        assertTrue(source.contains("rememberSaveable(provider.id) { mutableStateOf(false) }"))
        assertTrue(source.contains("verticalScroll(modelListScrollState)"))
        assertTrue(source.contains("sheetState = editConnectionSheetState"))
        assertTrue(source.contains(".fillMaxHeight(0.94f)"))
    }

    @Test
    fun `model picker is a bottom sheet with animated dismissal`() {
        val source = repositoryFile("app/src/main/java/app/turp/chat/ui/ModelPickerSheet.kt").readText()

        assertTrue(source.contains("ModalBottomSheet("))
        assertTrue(source.contains("rememberModalBottomSheetState(skipPartiallyExpanded = true)"))
        assertTrue(source.contains("sheetState.hide()"))
        assertTrue(source.contains("selectAndDismiss(choice.provider.id, choice.model.modelId)"))
        assertFalse(source.contains("Dialog("))
        assertFalse(source.contains("DialogProperties"))
    }

    @Test
    fun `sidebar omits redundant on device byok footer`() {
        val sidebar = repositoryFile("app/src/main/java/app/turp/chat/ui/ConversationSidebar.kt").readText()
        val turkish = repositoryFile("app/src/main/java/app/turp/chat/ui/TurkishUiCopy.kt").readText()

        assertFalse(sidebar.contains("On-device history • BYOK"))
        assertFalse(turkish.contains("On-device history • BYOK"))
    }
}

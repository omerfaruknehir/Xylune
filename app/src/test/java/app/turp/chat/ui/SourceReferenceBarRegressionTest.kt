package app.turp.chat.ui

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class SourceReferenceBarRegressionTest {
    private fun repositoryFile(path: String): File = sequenceOf(File(path), File("..", path))
        .firstOrNull(File::isFile)
        ?: error("Could not locate repository file: $path")

    @Test
    fun `bottom source bar stays horizontal and opens anchored preview`() {
        val source = repositoryFile(
            "app/src/main/java/app/turp/chat/ui/SourceReferenceBar.kt",
        ).readText()
        val richMessage = repositoryFile(
            "app/src/main/java/app/turp/chat/ui/RichMessage.kt",
        ).readText()
        val preview = repositoryFile(
            "app/src/main/java/app/turp/chat/ui/LinkPreview.kt",
        ).readText()

        assertTrue(source.contains("LowSensitivityHorizontalScroll"))
        assertTrue(source.contains("horizontalArrangement = Arrangement.spacedBy(8.dp)"))
        assertTrue(source.contains("var pendingSource"))
        assertTrue(source.contains("onGloballyPositioned"))
        assertTrue(source.contains("anchorBoundsInWindow = anchor"))
        assertTrue(source.contains("AnchoredLinkPreview("))
        assertFalse(source.contains("MorphingSourcePreview("))
        assertTrue(source.contains("widthIn(max = 230.dp)"))
        assertTrue(source.contains("anchorBounds.width > 0 && anchorBounds.height > 0"))
        assertFalse(source.contains("animateContentSize("))
        assertFalse(source.contains("var expandedTarget"))
        assertTrue(richMessage.contains("SourceReferenceBar("))
        assertFalse(richMessage.contains("sourceReferencesFooterMarkdown"))

        val anchored = preview.substringAfter("internal fun AnchoredLinkPreview(")
            .substringBefore("internal fun LinkPreviewDetails(")
        assertTrue(anchored.contains(".width(330.dp)"))
        assertTrue(anchored.contains(".heightIn(max = 420.dp)"))
        assertTrue(anchored.contains("AnimatedVisibility("))
        assertTrue(anchored.contains("dismissOnBackPress = true"))
        assertTrue(anchored.contains("dismissOnClickOutside = true"))
        assertTrue(anchored.contains("onDismissRequest = requestDismiss"))
    }
}

package app.xylune.chat.ui

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class PopupAndSourcePreviewRegressionTest {
    private fun repositoryFile(path: String): File = sequenceOf(File(path), File("..", path))
        .firstOrNull(File::isFile)
        ?: error("Could not locate repository file: $path")

    @Test
    fun `modal Back is keyboard first and tap away waits for release`() {
        val source = repositoryFile("app/src/main/java/app/xylune/chat/ui/ReleaseDismissPopup.kt").readText()
        val dialog = source.substringAfter("fun XyluneAlertDialog(")
        val dropdown = source.substringAfter("internal fun XyluneDropdownMenu(")

        assertTrue(source.contains("val imeVisibleAtGestureStart = imeInsets.getBottom(density) > 0"))
        assertTrue(source.contains("events.collect { }"))
        assertTrue(source.contains("keyboard?.hide()"))
        assertTrue(source.contains("focusManager.clearFocus(force = true)"))
        assertTrue(source.contains("BasicAlertDialog("))
        assertTrue(source.contains("dismissOnOutsideRelease("))
        assertTrue(source.contains("awaitFirstDown("))
        assertTrue(source.contains("if (event.changes.none { it.pressed }) break"))
        assertTrue(source.contains("val wasTap = maxTravelSquared <= slop * slop"))
        assertTrue(source.contains("startedInBackEdge"))
        assertTrue(dialog.contains("usePlatformDefaultWidth = false"))
        assertTrue(dialog.contains("dismissOnBackPress = false"))
        assertTrue(dialog.contains("dismissOnClickOutside = false"))

        // Small menus must still be dismissible by an ordinary tap away.
        assertTrue(dropdown.contains("dismissOnBackPress = true"))
        assertTrue(dropdown.contains("dismissOnClickOutside = dismissOnClickOutside"))
        assertFalse(dropdown.contains("ReleaseDismissOutsideLayer("))
    }

    @Test
    fun `source preview uses visible platform dismissable popup`() {
        val sourceBar = repositoryFile("app/src/main/java/app/xylune/chat/ui/SourceReferenceBar.kt").readText()
        val linkPreview = repositoryFile("app/src/main/java/app/xylune/chat/ui/LinkPreview.kt").readText()

        assertTrue(sourceBar.contains("var pendingSource"))
        assertTrue(sourceBar.contains("AnchoredLinkPreview("))
        assertTrue(sourceBar.contains("anchorBoundsInWindow = anchor"))
        assertTrue(sourceBar.contains("widthIn(max = 230.dp)"))
        assertTrue(sourceBar.contains("anchorBounds.width > 0 && anchorBounds.height > 0"))
        assertFalse(sourceBar.contains("MorphingSourcePreview("))
        assertFalse(sourceBar.contains("animateContentSize("))
        assertFalse(sourceBar.contains("var expandedTarget"))

        val anchored = linkPreview.substringAfter("internal fun AnchoredLinkPreview(")
            .substringBefore("internal fun LinkPreviewDetails(")
        assertTrue(anchored.contains("Popup("))
        assertTrue(anchored.contains("focusable = true"))
        assertTrue(anchored.contains("dismissOnBackPress = true"))
        assertTrue(anchored.contains("dismissOnClickOutside = true"))
        assertTrue(anchored.contains("onDismissRequest = requestDismiss"))
        assertTrue(anchored.contains("AnimatedVisibility("))
    }
}

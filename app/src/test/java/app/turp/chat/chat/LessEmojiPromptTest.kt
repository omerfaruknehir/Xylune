package app.turp.chat.chat

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LessEmojiPromptTest {
    @Test
    fun enabledModeDiscouragesDecorativeEmojiWithoutBlockingMeaningfulUse() {
        val layer = lessEmojiPromptLayer(true)

        assertTrue(layer.contains("Less emoji is enabled"))
        assertTrue(layer.contains("Do not decorate headings"))
        assertTrue(layer.contains("when the user explicitly asks"))
        assertFalse(layer.contains("never use emoji", ignoreCase = true))
    }

    @Test
    fun disabledModeAddsNoStyleInstruction() {
        assertTrue(lessEmojiPromptLayer(false).isBlank())
    }
}

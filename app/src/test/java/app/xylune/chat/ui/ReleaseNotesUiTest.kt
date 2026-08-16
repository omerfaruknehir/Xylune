package app.xylune.chat.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ReleaseNotesUiTest {
    @Test
    fun releaseHeadingIsNotRepeatedInsideWhatsNewBody() {
        val blocks = parseReleaseNotes(
            """
            # Xylune 0.24.29

            ## Update flow

            - Shows **release notes** after an update.
            - Keeps `code` readable.
            """.trimIndent(),
        )

        assertEquals(3, blocks.size)
        assertEquals(ReleaseNotesBlock.Heading("Update flow"), blocks[0])
        assertEquals(ReleaseNotesBlock.Bullet("Shows release notes after an update."), blocks[1])
        assertEquals(ReleaseNotesBlock.Bullet("Keeps code readable."), blocks[2])
    }

    @Test
    fun markdownLinksAreReducedToReadableLabels() {
        val blocks = parseReleaseNotes("- See [the release](https://example.com) for details.")
        assertTrue(blocks.single() is ReleaseNotesBlock.Bullet)
        assertEquals(
            "See the release for details.",
            (blocks.single() as ReleaseNotesBlock.Bullet).text,
        )
    }
}

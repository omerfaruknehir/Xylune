package app.turp.chat

import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LegalDocumentsConsistencyTest {
    private val repositoryRoot = File("..")

    @Test
    fun `published legal pages mirror repository documents`() {
        assertLocalizedSiteMirror(
            sourcePath = "PRIVACY.md",
            englishSitePath = "docs/privacy/index.md",
            turkishSitePath = "docs/tr/privacy/index.md",
        )
        assertLocalizedSiteMirror(
            sourcePath = "TERMS.md",
            englishSitePath = "docs/terms/index.md",
            turkishSitePath = "docs/tr/terms/index.md",
        )
        assertLocalizedSiteMirror(
            sourcePath = "DATA_DELETION.md",
            englishSitePath = "docs/data-deletion/index.md",
            turkishSitePath = "docs/tr/data-deletion/index.md",
        )
    }

    @Test
    fun `legal boundary does not imply a hosted service or maintainer data access`() {
        val privacy = repositoryRoot.resolve("PRIVACY.md").readText()
        val terms = repositoryRoot.resolve("TERMS.md").readText()
        val deletion = repositoryRoot.resolve("DATA_DELETION.md").readText()

        assertTrue(
            privacy.contains("This is a factual privacy notice, not a contract or a request for consent"),
        )
        assertTrue(privacy.contains("does not receive, collect, store, or have technical access"))
        assertTrue(
            privacy.contains(
                "[GitHub](https://docs.github.com/site-policy/privacy-policies/github-privacy-statement)" +
                    "—not the Turp maintainer—operates",
            ),
        )
        assertTrue(
            terms.contains(
                "open-source client software—not a hosted AI, cloud, account, or support service",
            ),
        )
        assertTrue(terms.contains("Apache License 2.0"))
        assertFalse(terms.contains("indemn", ignoreCase = true))
        assertFalse(terms.contains("cap is currently", ignoreCase = true))
        assertFalse(terms.contains("EUR 10"))
        assertTrue(deletion.contains("Do not put a privacy request"))
        assertTrue("Privacy notice should stay concise", privacy.lines().size <= 120)
        assertTrue("Terms should stay concise", terms.lines().size <= 90)
    }

    private fun assertLocalizedSiteMirror(
        sourcePath: String,
        englishSitePath: String,
        turkishSitePath: String,
    ) {
        val source = normalize(repositoryRoot.resolve(sourcePath).readText())
        val sections = source.split("\n\n---\n\n", limit = 2)
        assertEquals("$sourcePath must contain English and Turkish sections", 2, sections.size)

        val englishSource = normalize(
            sections[0].replace(
                Regex("(?m)^\\[Türkçe metin aşağıdadır\\.\\]\\([^\\n]+\\)\\n+"),
                "",
            ),
        )
        val turkishSource = normalize(localizeTurkishInternalLinks(sections[1]))

        assertEquals(
            "$englishSitePath must mirror the English section of $sourcePath",
            englishSource,
            siteBody(englishSitePath),
        )
        assertEquals(
            "$turkishSitePath must mirror the Turkish section of $sourcePath",
            turkishSource,
            siteBody(turkishSitePath),
        )
    }

    private fun localizeTurkishInternalLinks(value: String): String =
        value.replace(
            "https://omerfaruknehir.github.io/Turp/data-deletion/",
            "https://omerfaruknehir.github.io/Turp/tr/data-deletion/",
        )

    private fun siteBody(sitePath: String): String {
        val site = normalize(repositoryRoot.resolve(sitePath).readText())
        val body = site.substringAfter("\n---\n\n", missingDelimiterValue = "")
        assertTrue("$sitePath must contain Jekyll front matter", body.isNotEmpty())
        return normalize(body)
    }

    private fun normalize(value: String): String =
        value.replace("\r\n", "\n").trim() + "\n"
}

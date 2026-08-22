package app.turp.chat

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class ReleaseVersionRegressionTest {
    private fun repositoryFile(path: String): File = sequenceOf(File(path), File("..", path))
        .firstOrNull(File::isFile)
        ?: error("Could not locate repository file: $path")

    @Test
    fun `release metadata is scoped to 0_24_30`() {
        val gradle = repositoryFile("app/build.gradle.kts").readText()
        val english = repositoryFile("docs/releases/RELEASE_NOTES_0.24.30.md").readText()
        val turkish = repositoryFile("docs/releases/tr/RELEASE_NOTES_0.24.30.md").readText()

        assertTrue(gradle.contains("versionCode = 219"))
        assertTrue(gradle.contains("versionName = \"0.24.30\""))
        assertTrue(english.startsWith("# Turp 0.24.30"))
        assertTrue(turkish.startsWith("# Turp 0.24.30"))
    }
}

package app.turp.chat

import java.io.File
import org.junit.Assert.fail
import org.junit.Test

class TurpIdentityRegressionTest {
    @Test
    fun `repository paths and bytes contain only Turp identity`() {
        val start = File(System.getProperty("user.dir")).canonicalFile
        val root = generateSequence(start) { it.parentFile }
            .firstOrNull { File(it, "settings.gradle.kts").isFile }
            ?: error("Unable to locate project root from $start")

        val lower = byteArrayOf(120, 121, 108, 117, 110, 101)
        val title = byteArrayOf(88, 121, 108, 117, 110, 101)
        val upper = byteArrayOf(88, 89, 76, 85, 78, 69)
        val forbidden = listOf(lower, title, upper)
        val failures = mutableListOf<String>()

        root.walkTopDown()
            .onEnter { directory ->
                directory == root || directory.name !in EXCLUDED_DIRECTORIES
            }
            .forEach { file ->
                if (file == root) return@forEach
                val relativePath = file.relativeTo(root).invariantSeparatorsPath
                val pathBytes = relativePath.toByteArray()
                if (forbidden.any { pathBytes.containsSubsequence(it) }) {
                    failures += "path: $relativePath"
                }

                if (!file.isFile) return@forEach
                val bytes = file.readBytes()
                if (forbidden.any { bytes.containsSubsequence(it) }) {
                    failures += "content: $relativePath"
                }
            }

        if (failures.isNotEmpty()) {
            fail("Legacy identity remains in repository files:\n" + failures.take(200).joinToString("\n"))
        }
    }

    private fun ByteArray.containsSubsequence(needle: ByteArray): Boolean {
        if (needle.isEmpty()) return true
        if (needle.size > size) return false
        outer@ for (start in 0..size - needle.size) {
            for (offset in needle.indices) {
                if (this[start + offset] != needle[offset]) continue@outer
            }
            return true
        }
        return false
    }

    private companion object {
        val EXCLUDED_DIRECTORIES = setOf(".git", ".gradle", "build")
    }
}

package app.turp.chat

import java.io.File
import org.junit.Assert.fail
import org.junit.Test

class TurpIdentityRegressionTest {
    @Test
    fun `tracked repository paths and bytes contain only Turp identity`() {
        val start = File(System.getProperty("user.dir")).canonicalFile
        val root = generateSequence(start) { it.parentFile }
            .firstOrNull { File(it, ".git").exists() }
            ?: error("Unable to locate repository root from $start")

        val process = ProcessBuilder("git", "-C", root.absolutePath, "ls-files", "-z")
            .redirectErrorStream(true)
            .start()
        val output = process.inputStream.readBytes()
        check(process.waitFor() == 0) { "git ls-files failed: ${output.decodeToString()}" }

        val lower = byteArrayOf(120, 121, 108, 117, 110, 101)
        val title = byteArrayOf(88, 121, 108, 117, 110, 101)
        val upper = byteArrayOf(88, 89, 76, 85, 78, 69)
        val forbidden = listOf(lower, title, upper)
        val failures = mutableListOf<String>()

        output.toString(Charsets.UTF_8)
            .split('\u0000')
            .filter(String::isNotBlank)
            .forEach { relativePath ->
                val pathBytes = relativePath.toByteArray()
                if (forbidden.any { pathBytes.containsSubsequence(it) }) {
                    failures += "path: $relativePath"
                }

                val file = File(root, relativePath)
                if (!file.isFile) return@forEach
                val bytes = file.readBytes()
                val matches = forbidden.filter { bytes.containsSubsequence(it) }
                if (matches.isNotEmpty()) {
                    failures += "content: $relativePath"
                }
            }

        if (failures.isNotEmpty()) {
            fail("Legacy identity remains in tracked files:\n" + failures.take(200).joinToString("\n"))
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
}

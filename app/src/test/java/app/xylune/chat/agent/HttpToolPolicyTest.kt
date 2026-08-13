package app.xylune.chat.agent

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HttpToolPolicyTest {
    @Test fun readPostIsAllowedButDeleteCannotPretendToBeReadOnly() {
        assertEquals("POST", HttpToolPolicy.normalizeMethod("post"))
        assertEquals("read", HttpToolPolicy.normalizeEffect("read", "POST"))
        val failure = runCatching { HttpToolPolicy.normalizeEffect("read", "DELETE") }.exceptionOrNull()
        assertTrue(failure is IllegalArgumentException)
    }

    @Test fun writesRequireExplicitConfirmation() {
        val failure = runCatching {
            HttpToolPolicy.validateRequest("PATCH", "write", confirmed = false, body = "{}", contentType = "application/json")
        }.exceptionOrNull()
        assertTrue(failure is IllegalArgumentException)
        assertEquals("{}", HttpToolPolicy.validateRequest("PATCH", "write", confirmed = true, body = "{}", contentType = "application/json"))
    }

    @Test fun secretAndHopByHopHeadersAreRejected() {
        assertTrue(runCatching { HttpToolPolicy.validateHeaders(mapOf("Authorization" to "Bearer secret")) }.isFailure)
        assertTrue(runCatching { HttpToolPolicy.validateHeaders(mapOf("X-Api-Key" to "secret")) }.isFailure)
        assertTrue(runCatching { HttpToolPolicy.validateHeaders(mapOf("Host" to "example.com")) }.isFailure)
        assertEquals("application/json", HttpToolPolicy.validateHeaders(mapOf("Accept" to "application/json"))["Accept"])
    }

    @Test fun responseHeaderFilteringProtectsCookiesAndAuthMetadata() {
        assertTrue(HttpToolPolicy.isSensitiveResponseHeader("Set-Cookie"))
        assertTrue(HttpToolPolicy.isSensitiveResponseHeader("X-Auth-Token"))
        assertFalse(HttpToolPolicy.isSensitiveResponseHeader("ETag"))
    }
}

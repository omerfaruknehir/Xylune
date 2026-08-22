package app.turp.chat.agent

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HttpToolPolicyTest {
    @Test fun allGenericNonGetMethodsRequireWriteApproval() {
        assertFalse(HttpToolPolicy.requiresWriteApproval("GET"))
        assertFalse(HttpToolPolicy.requiresWriteApproval("HEAD"))
        listOf("POST", "PUT", "PATCH", "DELETE").forEach { assertTrue(HttpToolPolicy.requiresWriteApproval(it)) }
    }

    @Test fun requestValidationRejectsBodiesOnReadsAndBoundsApiResponses() {
        assertTrue(runCatching { HttpToolPolicy.validateRequest("GET", "{}", "application/json") }.isFailure)
        assertEquals("{}", HttpToolPolicy.validateRequest("POST", "{}", "application/json"))
        assertEquals(HttpToolPolicy.MAX_API_RESPONSE_BYTES, HttpToolPolicy.responseLimit(2_000_000))
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

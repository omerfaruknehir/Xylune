package app.turp.chat.agent

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HttpWriteApprovalGuardTest {
    private var clock = 1_000L
    private val guard = HttpWriteApprovalGuard(now = { clock }, newId = { "ABCDEF12-3456-7890" }, ttlMillis = 10_000)
    private val request = HttpWriteRequestIdentity("POST", "https://example.com/items", mapOf("Accept" to "application/json"), "{\"a\":1}", "application/json")

    @Test fun requiresExactConfirmationFromLaterUserTurnAndConsumesApproval() {
        val first = guard.authorize("chat", "user-1", "create it", request, null) as HttpWriteApprovalDecision.Required
        assertEquals("Approve HTTP write ABCDEF123456", first.confirmationText)
        assertTrue(runCatching { guard.authorize("chat", "user-1", first.confirmationText, request, first.approvalId) }.isFailure)
        assertTrue(runCatching { guard.authorize("chat", "user-2", "yes", request, first.approvalId) }.isFailure)
        assertTrue(guard.authorize("chat", "user-2", first.confirmationText, request, first.approvalId) is HttpWriteApprovalDecision.Approved)
        assertTrue(runCatching { guard.authorize("chat", "user-3", first.confirmationText, request, first.approvalId) }.isFailure)
    }

    @Test fun changedRequestCannotRedeemAndExpiredApprovalIsRejected() {
        val first = guard.authorize("chat", "user-1", "create it", request, null) as HttpWriteApprovalDecision.Required
        val changed = request.copy(body = "{\"a\":2}")
        assertTrue(runCatching { guard.authorize("chat", "user-2", first.confirmationText, changed, first.approvalId) }.isFailure)
        clock += 20_000
        assertTrue(runCatching { guard.authorize("chat", "user-2", first.confirmationText, request, first.approvalId) }.isFailure)
    }
}

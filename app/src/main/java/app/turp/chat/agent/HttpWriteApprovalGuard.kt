package app.turp.chat.agent

import java.security.MessageDigest
import java.util.UUID

internal data class HttpWriteRequestIdentity(
    val method: String,
    val url: String,
    val headers: Map<String, String>,
    val body: String,
    val contentType: String,
) {
    val bodySha256: String get() = sha256(body)
    fun fingerprint(): String = sha256(buildString {
        append(method.uppercase()).append('\n')
        append(url.trim()).append('\n')
        headers.toSortedMap(String.CASE_INSENSITIVE_ORDER).forEach { (name, value) ->
            append(name.lowercase()).append(':').append(value).append('\n')
        }
        append(contentType.trim().lowercase()).append('\n')
        append(bodySha256)
    })
}

internal sealed interface HttpWriteApprovalDecision {
    data class Required(
        val approvalId: String,
        val confirmationText: String,
        val expiresAt: Long,
    ) : HttpWriteApprovalDecision
    data object Approved : HttpWriteApprovalDecision
}

internal class HttpWriteApprovalGuard(
    private val now: () -> Long = System::currentTimeMillis,
    private val newId: () -> String = { UUID.randomUUID().toString() },
    private val ttlMillis: Long = 10 * 60 * 1_000L,
    private val maxPending: Int = 32,
) {
    private data class Pending(
        val conversationId: String,
        val issuedUserNodeId: String,
        val fingerprint: String,
        val confirmationText: String,
        val expiresAt: Long,
    )

    private val pending = LinkedHashMap<String, Pending>()

    @Synchronized
    fun authorize(
        conversationId: String,
        latestUserNodeId: String,
        latestUserText: String,
        request: HttpWriteRequestIdentity,
        approvalId: String?,
    ): HttpWriteApprovalDecision {
        val time = now()
        pending.entries.removeAll { it.value.expiresAt <= time }
        if (!approvalId.isNullOrBlank()) {
            val challenge = pending[approvalId] ?: error("HTTP write approval is missing or expired; request a new confirmation")
            require(challenge.conversationId == conversationId) { "HTTP write approval belongs to another conversation" }
            require(challenge.fingerprint == request.fingerprint()) { "HTTP write request changed after approval was requested" }
            require(challenge.issuedUserNodeId != latestUserNodeId) { "HTTP write approval requires a later user turn" }
            require(normalize(latestUserText) == normalize(challenge.confirmationText)) {
                "The latest user message does not exactly confirm this HTTP write. Ask the user to reply with the required confirmation text."
            }
            pending.remove(approvalId)
            return HttpWriteApprovalDecision.Approved
        }

        while (pending.size >= maxPending) pending.remove(pending.keys.first())
        val id = newId()
        val code = id.filter(Char::isLetterOrDigit).take(12).uppercase().ifBlank { UUID.randomUUID().toString().take(12).uppercase() }
        val confirmationText = "Approve HTTP write $code"
        val expiresAt = time + ttlMillis
        pending[id] = Pending(conversationId, latestUserNodeId, request.fingerprint(), confirmationText, expiresAt)
        return HttpWriteApprovalDecision.Required(id, confirmationText, expiresAt)
    }

    private fun normalize(value: String): String = value.trim().replace(Regex("\\s+"), " ").lowercase()
}

private fun sha256(value: String): String = MessageDigest.getInstance("SHA-256")
    .digest(value.toByteArray(Charsets.UTF_8)).joinToString("") { "%02x".format(it) }

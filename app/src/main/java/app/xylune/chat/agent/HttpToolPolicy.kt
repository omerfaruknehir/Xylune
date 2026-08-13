package app.xylune.chat.agent

internal object HttpToolPolicy {
    const val MAX_API_RESPONSE_BYTES = 120_000
    const val MAX_FEED_RESPONSE_BYTES = 2_000_000
    private val allowedMethods = setOf("GET", "HEAD", "POST", "PUT", "PATCH", "DELETE")
    private val blockedHeaders = setOf(
        "host", "connection", "content-length", "transfer-encoding", "upgrade", "te", "trailer",
        "proxy-authorization", "proxy-authenticate", "authorization", "cookie", "set-cookie",
    )
    private val secretHeader = Regex("(?i)(api[-_]?key|token|secret|credential|auth)")
    private val headerName = Regex("^[!#$%&'*+.^_`|~0-9A-Za-z-]{1,100}$")

    fun normalizeMethod(value: String?): String {
        val method = value.orEmpty().ifBlank { "GET" }.uppercase()
        require(method in allowedMethods) { "Unsupported HTTP method: $method" }
        return method
    }

    fun requiresWriteApproval(method: String): Boolean = normalizeMethod(method) !in setOf("GET", "HEAD")

    fun validateHeaders(values: Map<String, String>): Map<String, String> {
        require(values.size <= 32) { "HTTP requests support at most 32 custom headers" }
        return values.map { (rawName, rawValue) ->
            val name = rawName.trim()
            val value = rawValue.trim()
            require(headerName.matches(name)) { "Invalid HTTP header name" }
            val lower = name.lowercase()
            require(lower !in blockedHeaders && !secretHeader.containsMatchIn(lower)) {
                "Secret-bearing, authentication, cookie, host, and hop-by-hop headers are not accepted by the HTTP tool"
            }
            require(value.length <= 4_000 && '\n' !in value && '\r' !in value) { "Invalid or oversized HTTP header value" }
            name to value
        }.toMap(linkedMapOf())
    }

    fun validateRequest(method: String, body: String?, contentType: String?): String? {
        if (method in setOf("GET", "HEAD")) require(body.isNullOrBlank()) { "$method requests cannot include a body" }
        require((body?.length ?: 0) <= 256_000) { "HTTP request bodies are limited to 256,000 characters" }
        require(contentType == null || (contentType.length <= 200 && '\n' !in contentType && '\r' !in contentType)) {
            "Invalid Content-Type"
        }
        return body
    }

    fun responseLimit(value: Int?, hardMax: Int = MAX_API_RESPONSE_BYTES): Int =
        (value ?: hardMax).coerceIn(1_024, hardMax)

    fun isTextualContentType(value: String): Boolean {
        if (value.isBlank()) return true
        val lower = value.lowercase()
        return lower.startsWith("text/") || listOf("json", "xml", "graphql", "javascript", "x-www-form-urlencoded").any(lower::contains)
    }

    fun isSensitiveResponseHeader(name: String): Boolean {
        val lower = name.lowercase()
        return lower in setOf("set-cookie", "proxy-authenticate") || secretHeader.containsMatchIn(lower)
    }
}

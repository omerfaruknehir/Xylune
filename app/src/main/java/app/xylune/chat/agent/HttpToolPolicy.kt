package app.xylune.chat.agent

internal object HttpToolPolicy {
    private val allowedMethods = setOf("GET", "HEAD", "POST", "PUT", "PATCH", "DELETE")
    private val readMethods = setOf("GET", "HEAD", "POST")
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

    fun normalizeEffect(value: String?, method: String): String {
        val effect = value.orEmpty().ifBlank { if (method in setOf("GET", "HEAD")) "read" else "" }.lowercase()
        require(effect in setOf("read", "write")) { "HTTP effect must be read or write" }
        if (effect == "read") require(method in readMethods) {
            "$method cannot be declared read-only; use effect=write and obtain user confirmation"
        }
        if (effect == "write") require(method !in setOf("GET", "HEAD")) {
            "$method cannot be declared as a write"
        }
        return effect
    }

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

    fun validateRequest(method: String, effect: String, confirmed: Boolean, body: String?, contentType: String?): String? {
        if (effect == "write") require(confirmed) {
            "Write-intent HTTP requires explicit user confirmation before the tool call"
        }
        if (method in setOf("GET", "HEAD")) require(body.isNullOrBlank()) { "$method requests cannot include a body" }
        require((body?.length ?: 0) <= 256_000) { "HTTP request bodies are limited to 256,000 characters" }
        require(contentType == null || (contentType.length <= 200 && '\n' !in contentType && '\r' !in contentType)) {
            "Invalid Content-Type"
        }
        return body
    }

    fun responseLimit(value: Int?): Int = (value ?: 512_000).coerceIn(1_024, 2_000_000)

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

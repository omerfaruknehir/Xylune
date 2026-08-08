package app.xylune.chat.ui

/**
 * Turkish copy added after the first full localization pass.
 * Keep this limited to Xylune-owned phrases and structured labels so provider,
 * model, file, user, and assistant content is never translated accidentally.
 */
internal object TurkishUiCopyExtra3 {
    fun translate(text: String): String = exact[text] ?: dynamic(text) ?: text

    private val exact = mapOf(
        "Provider native only" to "Yalnızca sağlayıcının yerel araması",
        "Require the selected provider/model to perform search itself. Unsupported models fail instead of silently switching engines." to
            "Seçili sağlayıcı/modelin aramayı kendisinin yapmasını zorunlu kılar. Desteklenmeyen modeller sessizce başka bir motora geçmek yerine hata verir.",
        "Xylune search engine" to "Xylune arama motoru",
        "Always use the selected search engine through Xylune's client-side web_search tool." to
            "Her zaman Xylune'un istemci tarafındaki web_search aracı üzerinden seçili arama motorunu kullanır.",
        "Use the model provider's native server-side search when supported, otherwise use the selected Xylune search engine." to
            "Destekleniyorsa model sağlayıcısının sunucu tarafındaki yerel aramasını, aksi halde seçili Xylune arama motorunu kullanır.",
        "No API key. Uses DuckDuckGo's lightweight HTML results endpoint." to
            "API anahtarı gerekmez. DuckDuckGo'nun hafif HTML sonuç uç noktasını kullanır.",
        "Uses the official Brave Search API." to "Resmî Brave Search API'sini kullanır.",
        "Search API optimized for AI research and concise result content." to
            "Yapay zekâ araştırması ve kısa sonuç içeriği için optimize edilmiş arama API'si.",
        "Google-result API supplied by Serper." to "Serper tarafından sunulan Google sonuç API'si.",
        "Uses a user-supplied public HTTPS SearXNG instance with JSON output enabled." to
            "JSON çıktısı etkin, kullanıcının sağladığı herkese açık bir HTTPS SearXNG örneğini kullanır.",
        "Native only" to "Yalnızca sağlayıcının yerel araması",
        "API keys are stored in Android encrypted preferences. Native-only mode never silently switches to a Xylune engine; Automatic mode does." to
            "API anahtarları Android'in şifreli tercihlerinde saklanır. Yalnızca sağlayıcının yerel araması modu hiçbir zaman sessizce bir Xylune motoruna geçmez; Otomatik mod gerektiğinde geçer."
    )

    private fun dynamic(text: String): String? {
        Regex("""(.+) credential""").matchEntire(text)?.let { match ->
            return "${match.groupValues[1]} kimlik bilgisi"
        }
        Regex("""Auto · (.+) fallback""").matchEntire(text)?.let { match ->
            return "Otomatik · ${match.groupValues[1]} yedeği"
        }
        return null
    }
}

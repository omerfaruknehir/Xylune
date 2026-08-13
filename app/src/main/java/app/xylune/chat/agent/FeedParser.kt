package app.xylune.chat.agent

import kotlinx.serialization.Serializable
import java.net.URI

@Serializable
internal data class FeedDocument(
    val url: String,
    val title: String,
    val format: String,
    val items: List<FeedItem>,
)

@Serializable
internal data class FeedItem(
    val id: String = "",
    val title: String = "",
    val url: String = "",
    val publishedAt: String = "",
    val author: String = "",
    val summary: String = "",
)

internal object FeedParser {
    private val rssItem = Regex("(?is)<item\\b[^>]*>(.*?)</item>")
    private val atomEntry = Regex("(?is)<entry\\b[^>]*>(.*?)</entry>")

    fun parse(xml: String, sourceUrl: String, limit: Int): FeedDocument {
        val safeLimit = limit.coerceIn(1, 50)
        val rss = rssItem.findAll(xml).map { it.groupValues[1] }.take(safeLimit).toList()
        val atom = if (rss.isEmpty()) atomEntry.findAll(xml).map { it.groupValues[1] }.take(safeLimit).toList() else emptyList()
        require(rss.isNotEmpty() || atom.isNotEmpty()) { "The response does not contain recognizable RSS <item> or Atom <entry> elements" }
        val format = if (rss.isNotEmpty()) "rss" else "atom"
        val blocks = if (rss.isNotEmpty()) rss else atom
        val items = blocks.map { block ->
            val rawLink = if (format == "atom") atomLink(block) else tag(block, "link")
            FeedItem(
                id = tag(block, if (format == "atom") "id" else "guid").take(1_000),
                title = tag(block, "title").take(1_000),
                url = resolve(sourceUrl, rawLink).take(8_192),
                publishedAt = tag(block, if (format == "atom") "published" else "pubDate")
                    .ifBlank { tag(block, "updated") }.take(200),
                author = (if (format == "atom") tag(tagRaw(block, "author"), "name")
                    .ifBlank { tag(block, "author") } else tag(block, "author").ifBlank { tag(block, "dc:creator") }).take(500),
                summary = tag(block, if (format == "atom") "summary" else "description")
                    .ifBlank { tag(block, "content") }.take(2_000),
            )
        }
        val titleSource = if (format == "rss") tagRaw(xml, "channel") else xml.substringBefore(blocks.firstOrNull().orEmpty())
        return FeedDocument(sourceUrl.take(8_192), tag(titleSource, "title").take(1_000), format, items)
    }

    private fun atomLink(block: String): String {
        val links = Regex("(?is)<link\\b([^>]*)>").findAll(block).map { it.groupValues[1] }.toList()
        val preferred = links.firstOrNull { attrs ->
            val rel = attr(attrs, "rel")
            rel.isBlank() || rel.equals("alternate", ignoreCase = true)
        } ?: links.firstOrNull().orEmpty()
        return attr(preferred, "href")
    }

    private fun attr(attributes: String, name: String): String =
        Regex("(?is)\\b${Regex.escape(name)}\\s*=\\s*(['\"])(.*?)\\1").find(attributes)?.groupValues?.get(2).orEmpty()

    private fun tag(value: String, name: String): String = clean(tagRaw(value, name))

    private fun tagRaw(value: String, name: String): String =
        Regex("(?is)<${Regex.escape(name)}\\b[^>]*>(.*?)</${Regex.escape(name)}>")
            .find(value)?.groupValues?.get(1).orEmpty()

    private fun clean(value: String): String = value
        .replace(Regex("(?is)<!\\[CDATA\\[(.*?)]]>")) { it.groupValues[1] }
        .replace(Regex("(?is)<br\\s*/?>"), "\n")
        .replace(Regex("(?is)<[^>]+>"), " ")
        .replace("&amp;", "&").replace("&lt;", "<").replace("&gt;", ">")
        .replace("&quot;", "\"").replace("&apos;", "'").replace("&#39;", "'")
        .replace(Regex("\\s+"), " ").trim()

    private fun resolve(base: String, raw: String): String {
        if (raw.isBlank()) return ""
        return runCatching { URI(base).resolve(raw.trim()).toString() }.getOrDefault(raw.trim())
    }
}

package app.xylune.chat.agent

import org.junit.Assert.assertEquals
import org.junit.Test

class FeedParserTest {
    @Test fun normalizesRssAndResolvesRelativeLinks() {
        val feed = FeedParser.parse(
            """<rss><channel><title>Release feed</title><item><guid>1</guid><title>v1</title><link>/releases/1</link><pubDate>today</pubDate><description><![CDATA[<b>Ready</b>]]></description></item></channel></rss>""",
            "https://example.com/feed.xml",
            10,
        )
        assertEquals("rss", feed.format)
        assertEquals("Release feed", feed.title)
        assertEquals("https://example.com/releases/1", feed.items.single().url)
        assertEquals("Ready", feed.items.single().summary)
    }

    @Test fun normalizesAtomAlternateLink() {
        val feed = FeedParser.parse(
            """<feed><title>News</title><entry><id>a</id><title>Hello</title><link rel="alternate" href="https://example.com/a"/><updated>now</updated><summary>Body</summary></entry></feed>""",
            "https://example.com/atom.xml",
            10,
        )
        assertEquals("atom", feed.format)
        assertEquals("https://example.com/a", feed.items.single().url)
    }
}

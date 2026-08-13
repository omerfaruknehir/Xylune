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

    @Test fun boundsUntrustedFeedFields() {
        val huge = "x".repeat(5_000)
        val feed = FeedParser.parse(
            "<rss><channel><title>$huge</title><item><guid>$huge</guid><title>$huge</title><link>https://example.com/a</link><description>$huge</description></item></channel></rss>",
            "https://example.com/feed.xml",
            10,
        )
        assertEquals(1_000, feed.title.length)
        assertEquals(1_000, feed.items.single().id.length)
        assertEquals(1_000, feed.items.single().title.length)
        assertEquals(2_000, feed.items.single().summary.length)
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

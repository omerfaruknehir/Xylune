package app.xylune.chat.ui

import org.junit.Assert.assertEquals
import org.junit.Test

class TurkishDynamicUiCopyTest {
    @Test
    fun `model picker counts and summaries are translated`() {
        assertEquals("Sohbet · 502", TurkishDynamicUiCopy.translate("Chat · 502"))
        assertEquals("Görseller · 67", TurkishDynamicUiCopy.translate("Images · 67"))
        assertEquals("502 sonuç", TurkishDynamicUiCopy.translate("502 results"))
        assertEquals("3 favori", TurkishDynamicUiCopy.translate("3 starred"))
        assertEquals("1M bağlam", TurkishDynamicUiCopy.translate("1M context"))
        assertEquals("131K çıktı", TurkishDynamicUiCopy.translate("131K output"))
        assertEquals(
            "1M bağlam · 131K çıktı · Düşünme · Araçlar",
            TurkishDynamicUiCopy.translate("1M context · 131K output · Thinking · Tools"),
        )
        assertEquals(
            "Qwen Cloud · glm-5.2",
            TurkishDynamicUiCopy.translate("Qwen Cloud · glm-5.2"),
        )
    }

    @Test
    fun `update status keeps repository values but translates surrounding copy`() {
        assertEquals(
            "Güncellemeler omerfaruknehir/Xylune üzerinden kontrol edilir.",
            TurkishDynamicUiCopy.translate("Updates are checked against omerfaruknehir/Xylune."),
        )
        assertEquals(
            "omerfaruknehir/Xylune kontrol ediliyor…",
            TurkishDynamicUiCopy.translate("Checking omerfaruknehir/Xylune…"),
        )
        assertEquals("Xylune 0.24.27 kullanılabilir", TurkishDynamicUiCopy.translate("Xylune 0.24.27 is available"))
        assertEquals("Kaynak: omerfaruknehir/Xylune", TurkishDynamicUiCopy.translate("Source: omerfaruknehir/Xylune"))
    }

    @Test
    fun `runtime and backup status preserve dynamic payloads`() {
        assertEquals("3 sırada", TurkishDynamicUiCopy.translate("3 queued"))
        assertEquals("Çalışıyor · 2 sırada", TurkishDynamicUiCopy.translate("Working · 2 queued"))
        assertEquals("PDF sayfası 4", TurkishDynamicUiCopy.translate("PDF page 4"))
        assertEquals("9 sayfanın 4. sayfası", TurkishDynamicUiCopy.translate("Page 4 of 9"))
        assertEquals(
            "Google Drive bağlantısı kuruldu ancak Xylune yedeği bulunamadı.",
            TurkishDynamicUiCopy.translate("Connected to Google Drive, but no Xylune backups were found."),
        )
        assertEquals(
            "Google Drive içinde 2 Xylune yedeği bulundu.",
            TurkishDynamicUiCopy.translate("Found 2 Xylune backups in Google Drive."),
        )
    }

    @Test
    fun `unknown content is never guessed or machine translated`() {
        val providerOrUserText = "Acme Experimental Model"
        assertEquals(providerOrUserText, TurkishDynamicUiCopy.translate(providerOrUserText))
    }
}

package app.xylune.chat.ui

import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text as MaterialText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit

/**
 * Localized Material Text facade for Xylune-owned UI copy.
 *
 * Files in app.xylune.chat.ui alias Material3's Text import so ordinary String
 * UI labels pass through here. User/model rich text uses AnnotatedString or
 * dedicated renderers and is deliberately not translated.
 */
@Composable
internal fun Text(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
    style: TextStyle = LocalTextStyle.current,
) {
    val language = LocalConfiguration.current.locales[0]?.language
    MaterialText(
        text = if (language == "tr") TurkishUiCopy.translate(text) else text,
        modifier = modifier,
        color = color,
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
        onTextLayout = onTextLayout,
        style = style,
    )
}

/** Rich/user content is never run through the UI-copy translator. */
@Composable
internal fun Text(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
    style: TextStyle = LocalTextStyle.current,
) {
    MaterialText(
        text = text,
        modifier = modifier,
        color = color,
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
        onTextLayout = onTextLayout,
        style = style,
    )
}

internal object TurkishUiCopy {
    private val exact = mapOf(
        "Back" to "Geri",
        "Settings" to "Ayarlar",
        "Setup" to "Kurulum",
        "Finish setup" to "Kurulumu tamamla",
        "Setup & connections" to "Kurulum ve bağlantılar",
        "Providers & models" to "Sağlayıcılar ve modeller",
        "Add your first API provider" to "İlk API sağlayıcınızı ekleyin",
        "Backup & transfer" to "Yedekleme ve aktarım",
        "Cloud backups, local archives, and restore" to "Bulut yedekleri, yerel arşivler ve geri yükleme",
        "Chat behavior" to "Sohbet davranışı",
        "New chat defaults" to "Yeni sohbet varsayılanları",
        "Model, thinking, context, and output limits" to "Model, düşünme, bağlam ve çıktı sınırları",
        "Response style" to "Yanıt stili",
        "Emoji use and global answer presentation" to "Emoji kullanımı ve genel yanıt görünümü",
        "Custom instructions" to "Özel talimatlar",
        "Reusable tone and workflow profiles" to "Yeniden kullanılabilir üslup ve iş akışı profilleri",
        "Intelligence" to "Zekâ",
        "Memory" to "Hafıza",
        "Cross-chat facts and preferences stored locally" to "Sohbetler arası yerel olarak saklanan bilgiler ve tercihler",
        "Background tasks" to "Arka plan görevleri",
        "Chat naming and context compression models" to "Sohbet adlandırma ve bağlam sıkıştırma modelleri",
        "Tools & safety" to "Araçlar ve güvenlik",
        "Search & web" to "Arama ve web",
        "Native routing, search engines, credentials, and page fetching" to "Yerel yönlendirme, arama motorları, kimlik bilgileri ve sayfa getirme",
        "Local execution" to "Yerel çalıştırma",
        "Python, Linux, packages, and approval policy" to "Python, Linux, paketler ve onay ilkesi",
        "Privacy & safety" to "Gizlilik ve güvenlik",
        "Generated UI safety and local-data behavior" to "Oluşturulan arayüz güvenliği ve yerel veri davranışı",
        "Personalization" to "Kişiselleştirme",
        "Appearance" to "Görünüm",
        "Theme, palette, launcher icon, and AMOLED black" to "Tema, renk paleti, başlatıcı simgesi ve AMOLED siyahı",
        "About" to "Hakkında",
        "About Xylune" to "Xylune hakkında",
        "Version, architecture, and privacy model" to "Sürüm, mimari ve gizlilik modeli",
        "Assistant responses" to "Asistan yanıtları",
        "Less emoji" to "Daha az emoji",
        "Avoid decorative emoji and use them only when they add meaning" to "Dekoratif emojilerden kaçın ve yalnızca anlam kattıklarında kullan",
        "Enabled by default. Technical symbols and emoji requested by the user are not blocked." to "Varsayılan olarak etkindir. Teknik semboller ve kullanıcının istediği emojiler engellenmez.",
        "Background task models" to "Arka plan görevi modelleri",
        "Choose how Xylune names chats and compresses older context." to "Xylune'un sohbetleri nasıl adlandıracağını ve eski bağlamı nasıl sıkıştıracağını seçin.",
        "Configure a usable provider to enable model-based automation." to "Model tabanlı otomasyon için kullanılabilir bir sağlayıcı yapılandırın.",
        "Chat naming" to "Sohbet adlandırma",
        "Context compression" to "Bağlam sıkıştırma",
        "Use memory" to "Hafızayı kullan",
        "Automatic memory" to "Otomatik hafıza",
        "Add memory" to "Hafıza ekle",
        "Category" to "Kategori",
        "Save memory" to "Hafızayı kaydet",
        "Saved memories" to "Kaydedilmiş hafızalar",
        "Search memories" to "Hafızalarda ara",
        "All" to "Tümü",
        "Enabled" to "Etkin",
        "Disabled" to "Devre dışı",
        "All categories" to "Tüm kategoriler",
        "Enable" to "Etkinleştir",
        "Disable" to "Devre dışı bırak",
        "Delete" to "Sil",
        "Clear" to "Temizle",
        "Select shown" to "Gösterilenleri seç",
        "Delete disabled" to "Devre dışı olanları sil",
        "Enable all" to "Tümünü etkinleştir",
        "Disable all" to "Tümünü devre dışı bırak",
        "No memories saved yet." to "Henüz kaydedilmiş hafıza yok.",
        "No memories match the current filters." to "Geçerli filtrelerle eşleşen hafıza yok.",
        "Save" to "Kaydet",
        "Cancel" to "İptal",
        "Theme mode" to "Tema modu",
        "Color scheme" to "Renk şeması",
        "Follow device" to "Cihazı takip et",
        "Light" to "Açık",
        "Dark" to "Koyu",
        "Dynamic" to "Dinamik",
        "Graphite" to "Grafit",
        "Ocean" to "Okyanus",
        "Violet" to "Menekşe",
        "Sunset" to "Gün batımı",
        "Match launcher icon to palette" to "Başlatıcı simgesini paletle eşleştir",
        "AMOLED black" to "AMOLED siyahı",
        "AMOLED black only changes dark mode surfaces." to "AMOLED siyahı yalnızca koyu mod yüzeylerini değiştirir.",
        "Interface panels" to "Arayüz panelleri",
        "Blur" to "Bulanıklık",
        "Panel shape" to "Panel şekli",
        "Rounded" to "Yuvarlatılmış",
        "Flat" to "Düz",
        "Edge softness" to "Kenar yumuşaklığı",
        "Hard" to "Sert",
        "Tint opacity" to "Renk tonu opaklığı",
        "Generated content" to "Oluşturulan içerik",
        "Safe generated rendering" to "Güvenli oluşturulan içerik işleme",
        "Automatic repair attempts" to "Otomatik onarım denemeleri",
        "Third-party AI and services" to "Üçüncü taraf yapay zekâ ve hizmetler",
        "Privacy" to "Gizlilik",
        "Terms" to "Koşullar",
        "Data deletion" to "Veri silme",
        "Custom instruction profiles" to "Özel talimat profilleri",
        "New custom profile" to "Yeni özel profil",
        "No saved prompts yet." to "Henüz kaydedilmiş istem yok.",
        "Default" to "Varsayılan",
        "Use for new chats" to "Yeni sohbetlerde kullan",
        "Use Xylune default for new chats" to "Yeni sohbetlerde Xylune varsayılanını kullan",
        "Edit custom profile" to "Özel profili düzenle",
        "Name" to "Ad",
        "Prepend" to "Başa ekle",
        "Override" to "Geçersiz kıl",
        "Instructions" to "Talimatlar",
        "Availability in new chats" to "Yeni sohbetlerde kullanılabilirlik",
        "Tool defaults" to "Araç varsayılanları",
        "Linux commands" to "Linux komutları",
        "Runtime manager" to "Çalışma zamanı yöneticisi",
        "Open runtime manager" to "Çalışma zamanı yöneticisini aç",
        "Package approval" to "Paket onayı",
        "Developer settings" to "Geliştirici ayarları",
        "Enable developer settings" to "Geliştirici ayarlarını etkinleştir",
        "Tool diagnostics" to "Araç tanılamaları",
        "Show tool diagnostics" to "Araç tanılamalarını göster",
        "Performance counter" to "Performans sayacı",
        "Show performance overlay" to "Performans katmanını göster",
        "Cause profiler" to "Neden profilleyici",
        "Detailed metrics" to "Ayrıntılı ölçümler",
        "Panel opacity" to "Panel opaklığı",
        "Text opacity" to "Metin opaklığı",
        "Overlay scale" to "Katman ölçeği",
        "Update interval" to "Güncelleme aralığı",
        "Overlay position" to "Katman konumu",
        "Blur boundary diagnostics" to "Bulanıklık sınırı tanılamaları",
        "Show blur boundary guides" to "Bulanıklık sınırı kılavuzlarını göster",
        "Guide thickness" to "Kılavuz kalınlığı",
        "Project" to "Proje",
        "Build source" to "Derleme kaynağı",
        "Licenses & notices" to "Lisanslar ve bildirimler",
        "Report an issue" to "Sorun bildir",
        "Legal" to "Yasal",
        "Privacy policy" to "Gizlilik politikası",
        "Terms & disclaimer" to "Koşullar ve sorumluluk reddi",
        "Updates" to "Güncellemeler",
        "Check automatically" to "Otomatik kontrol et",
        "Check for updates" to "Güncellemeleri kontrol et",
        "Check again" to "Tekrar kontrol et",
        "Retry" to "Yeniden dene",
        "Build information" to "Derleme bilgileri",
        "Version" to "Sürüm",
        "Build" to "Derleme",
        "Package" to "Paket",
        "Source repository" to "Kaynak deposu",
        "Source commit" to "Kaynak commit'i",
        "Minimum Android" to "Minimum Android",
        "Target Android" to "Hedef Android",
        "Running on" to "Çalıştığı sürüm",
        "Device ABI" to "Cihaz ABI'si",
        "Unknown" to "Bilinmiyor",
        "Private by design" to "Tasarım gereği özel",
        "Developer options" to "Geliştirici seçenekleri",
        "Composer defaults" to "Yazma alanı varsayılanları",
        "Tools and modes" to "Araçlar ve modlar",
        "Web search" to "Web araması",
        "Deep Research" to "Derin Araştırma",
        "Token counting" to "Token sayımı",
        "Hybrid token counting" to "Hibrit token sayımı",
        "Context & output" to "Bağlam ve çıktı",
        "Last message pairs" to "Son mesaj çiftleri",
        "Context token ceiling" to "Bağlam token üst sınırı",
        "Working history token budget" to "Çalışma geçmişi token bütçesi",
        "Maximum output tokens" to "Maksimum çıktı token'ı",
        "Working display" to "Çalışma görünümü",
        "Xylune core prompt" to "Xylune çekirdek istemi",
        "Thinking" to "Düşünme",
        "Off" to "Kapalı",
        "Thinking effort" to "Düşünme düzeyi",
        "Minimal" to "Minimum",
        "Low" to "Düşük",
        "Medium" to "Orta",
        "High" to "Yüksek",
        "Extra high" to "Çok yüksek",
        "Max" to "Maksimum",
        "Expanded" to "Genişletilmiş",
        "While working" to "Çalışırken",
        "Collapsed" to "Daraltılmış",
        "Model" to "Model",
        "Choose a model" to "Model seç",
        "No provider selected" to "Sağlayıcı seçilmedi",
        "Providers" to "Sağlayıcılar",
        "No providers yet" to "Henüz sağlayıcı yok",
        "Add ChatGPT" to "ChatGPT ekle",
        "Add API" to "API ekle",
        "In use" to "Kullanımda",
        "Connected" to "Bağlı",
        "Disconnected" to "Bağlantı kesildi",
        "Edit" to "Düzenle",
        "Refreshing…" to "Yenileniyor…",
        "Refresh models" to "Modelleri yenile",
        "Edit connection" to "Bağlantıyı düzenle",
        "Remove provider" to "Sağlayıcıyı kaldır",
        "Add ChatGPT provider" to "ChatGPT sağlayıcısı ekle",
        "Provider name" to "Sağlayıcı adı",
        "Add" to "Ekle",
        "Rename ChatGPT provider" to "ChatGPT sağlayıcısını yeniden adlandır",
        "Usage & limits" to "Kullanım ve sınırlar",
        "Refresh usage" to "Kullanımı yenile",
        "Session" to "Oturum",
        "Weekly" to "Haftalık",
        "Require API key" to "API anahtarı gerektir",
        "Advanced headers" to "Gelişmiş başlıklar",
        "Usually unnecessary" to "Genellikle gerekli değildir",
        "Custom headers JSON" to "Özel başlıklar JSON",
        "Save connection" to "Bağlantıyı kaydet",
        "Add provider" to "Sağlayıcı ekle",
        "Custom provider" to "Özel sağlayıcı",
        "Base URL" to "Temel URL",
        "API key" to "API anahtarı",
        "API key (optional)" to "API anahtarı (isteğe bağlı)",
        "Models from provider" to "Sağlayıcıdaki modeller",
        "Select all" to "Tümünü seç",
        "Bundled suggestions" to "Dahili öneriler",
        "API model ID" to "API model kimliği",
        "Model display name" to "Model görünen adı",
        "Models" to "Modeller",
        "Search models" to "Modellerde ara",
        "No matching models." to "Eşleşen model yok.",
        "Add model" to "Model ekle",
        "Edit model" to "Modeli düzenle",
        "Display name" to "Görünen ad",
        "Context tokens" to "Bağlam token'ları",
        "Max output" to "Maksimum çıktı",
        "Request type" to "İstek türü",
        "Chat" to "Sohbet",
        "Image generation" to "Görsel oluşturma",
        "Advanced compatibility" to "Gelişmiş uyumluluk",
        "Tools" to "Araçlar",
        "Vision" to "Görüntü",
        "Files" to "Dosyalar",
        "Pricing" to "Fiyatlandırma",
        "Pricing configured" to "Fiyatlandırma yapılandırıldı",
        "Cached input" to "Önbelleğe alınmış girdi",
        "Input" to "Girdi",
        "Output" to "Çıktı",
        "Routing" to "Yönlendirme",
        "Fallback search engine" to "Yedek arama motoru",
        "Saved" to "Kaydedildi",
        "Save key" to "Anahtarı kaydet",
        "SearXNG endpoint" to "SearXNG uç noktası",
        "Public HTTPS base URL" to "Herkese açık HTTPS temel URL'si",
        "Tool behavior" to "Araç davranışı",
        "Maximum search results" to "Maksimum arama sonucu",
        "Allow page fetching" to "Sayfa getirmeye izin ver",
        "Close" to "Kapat",
        "Open" to "Aç",
        "Sources" to "Kaynaklar",
    )

    fun translate(text: String): String {
        exact[text]?.let { return it }

        Regex("""Continue from step (\d+) of 3""").matchEntire(text)?.let {
            return "3 adımın ${it.groupValues[1]}. adımından devam et"
        }
        Regex("""(\d+) providers configured""").matchEntire(text)?.let {
            return "${it.groupValues[1]} sağlayıcı yapılandırıldı"
        }
        Regex("""(\d+) provider configured""").matchEntire(text)?.let {
            return "${it.groupValues[1]} sağlayıcı yapılandırıldı"
        }
        Regex("""(\d+) selected""").matchEntire(text)?.let {
            return "${it.groupValues[1]} seçildi"
        }
        Regex("""Source (\d+)""").matchEntire(text)?.let {
            return "Kaynak ${it.groupValues[1]}"
        }
        Regex("""(\d+)% left""").matchEntire(text)?.let {
            return "%${it.groupValues[1]} kaldı"
        }
        Regex("""(\d+)% used""").matchEntire(text)?.let {
            return "%${it.groupValues[1]} kullanıldı"
        }
        Regex("""Delete (\d+) memories\?""").matchEntire(text)?.let {
            return "${it.groupValues[1]} hafıza silinsin mi?"
        }
        if (text == "Delete memory?") return "Hafıza silinsin mi?"
        if (text.startsWith("Managed by Xylune · revision ")) {
            return text.replace("Managed by Xylune · revision ", "Xylune tarafından yönetiliyor · revizyon ")
        }
        if (text.startsWith("Preset: ")) return text.replaceFirst("Preset: ", "Ön ayar: ")
        if (text.startsWith("Protocol: ")) return text.replaceFirst("Protocol: ", "Protokol: ")
        if (text.startsWith("Search ") && text.endsWith(" models")) {
            return text.removePrefix("Search ").removeSuffix(" models") + " modelde ara"
        }
        return text
    }
}

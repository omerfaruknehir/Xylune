package app.xylune.chat.ui

import java.util.Locale

enum class SettingsRoute(
    private val englishTitle: String,
) {
    HOME("Settings"),
    DEFAULTS("New chat defaults"),
    RESPONSE_STYLE("Response style"),
    SEARCH("Search & web"),
    AUTOMATION("Background tasks"),
    MEMORY("Memory"),
    APPEARANCE("Appearance"),
    PRIVACY("Privacy & safety"),
    BACKUP("Backup & transfer"),
    LOCAL_EXECUTION("Local execution"),
    DEVELOPER("Developer settings"),
    SYSTEM_PROMPTS("Custom instructions"),
    PROVIDERS("Providers & models"),
    ABOUT("About Xylune"),
    LICENSES("Licenses & notices"),
    ;

    private val turkishTitle: String
        get() = when (this) {
            HOME -> "Ayarlar"
            DEFAULTS -> "Yeni sohbet varsayılanları"
            RESPONSE_STYLE -> "Yanıt stili"
            SEARCH -> "Arama ve web"
            AUTOMATION -> "Arka plan görevleri"
            MEMORY -> "Hafıza"
            APPEARANCE -> "Görünüm"
            PRIVACY -> "Gizlilik ve güvenlik"
            BACKUP -> "Yedekleme ve aktarım"
            LOCAL_EXECUTION -> "Yerel çalıştırma"
            DEVELOPER -> "Geliştirici ayarları"
            SYSTEM_PROMPTS -> "Özel talimatlar"
            PROVIDERS -> "Sağlayıcılar ve modeller"
            ABOUT -> "Xylune hakkında"
            LICENSES -> "Lisanslar ve bildirimler"
        }

    val title: String
        get() = if (Locale.getDefault().language.equals("tr", ignoreCase = true)) {
            turkishTitle
        } else {
            englishTitle
        }
}

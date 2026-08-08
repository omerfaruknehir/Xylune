package app.xylune.chat.ui

import java.util.Locale

enum class SettingsRoute(
    private val englishTitle: String,
    private val turkishTitle: String,
) {
    HOME("Settings", "Ayarlar"),
    DEFAULTS("New chat defaults", "Yeni sohbet varsayılanları"),
    RESPONSE_STYLE("Response style", "Yanıt stili"),
    SEARCH("Search & web", "Arama ve web"),
    AUTOMATION("Background tasks", "Arka plan görevleri"),
    MEMORY("Memory", "Hafıza"),
    APPEARANCE("Appearance", "Görünüm"),
    PRIVACY("Privacy & safety", "Gizlilik ve güvenlik"),
    BACKUP("Backup & transfer", "Yedekleme ve aktarım"),
    LOCAL_EXECUTION("Local execution", "Yerel çalıştırma"),
    DEVELOPER("Developer settings", "Geliştirici ayarları"),
    SYSTEM_PROMPTS("Custom instructions", "Özel talimatlar"),
    PROVIDERS("Providers & models", "Sağlayıcılar ve modeller"),
    ABOUT("About Xylune", "Xylune hakkında"),
    LICENSES("Licenses & notices", "Lisanslar ve bildirimler"),
    ;

    val title: String
        get() = if (Locale.getDefault().language.equals("tr", ignoreCase = true)) {
            turkishTitle
        } else {
            englishTitle
        }
}

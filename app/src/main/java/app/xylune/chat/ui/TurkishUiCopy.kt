package app.xylune.chat.ui

/**
 * Turkish copy for Xylune-owned interface text.
 *
 * Provider/model names, URLs, file names, code, user text, and model output are
 * intentionally not translated. Dynamic rules below only match phrases that
 * Xylune itself formats.
 */
internal object TurkishUiCopy {
    private val exact = mapOf(
        // Common actions and navigation
        "Back" to "Geri",
        "Close" to "Kapat",
        "Open" to "Aç",
        "Save" to "Kaydet",
        "Cancel" to "İptal",
        "Delete" to "Sil",
        "Edit" to "Düzenle",
        "Add" to "Ekle",
        "Create" to "Oluştur",
        "Remove" to "Kaldır",
        "Rename" to "Yeniden adlandır",
        "Retry" to "Yeniden dene",
        "Refresh" to "Yenile",
        "Continue" to "Devam et",
        "Done" to "Bitti",
        "Finish" to "Bitir",
        "Next" to "İleri",
        "Previous" to "Önceki",
        "Search" to "Ara",
        "Clear" to "Temizle",
        "Select all" to "Tümünü seç",
        "Select shown" to "Gösterilenleri seç",
        "Enable" to "Etkinleştir",
        "Disable" to "Devre dışı bırak",
        "Enable all" to "Tümünü etkinleştir",
        "Disable all" to "Tümünü devre dışı bırak",
        "Enabled" to "Etkin",
        "Disabled" to "Devre dışı",
        "On" to "Açık",
        "Off" to "Kapalı",
        "Automatic" to "Otomatik",
        "Default" to "Varsayılan",
        "All" to "Tümü",
        "None" to "Hiçbiri",
        "Unknown" to "Bilinmiyor",
        "Ready" to "Hazır",
        "Loading…" to "Yükleniyor…",
        "Working…" to "Çalışıyor…",
        "Preparing…" to "Hazırlanıyor…",
        "Checking…" to "Kontrol ediliyor…",
        "Refreshing…" to "Yenileniyor…",
        "Connecting…" to "Bağlanıyor…",
        "Connected" to "Bağlı",
        "Disconnected" to "Bağlantı kesildi",
        "Copied" to "Kopyalandı",
        "Copy" to "Kopyala",
        "Download" to "İndir",
        "Share" to "Paylaş",
        "Import" to "İçe aktar",
        "Export" to "Dışa aktar",
        "Restore" to "Geri yükle",
        "Install" to "Yükle",
        "Uninstall" to "Kaldır",
        "Apply" to "Uygula",
        "Reset" to "Sıfırla",
        "Details" to "Ayrıntılar",
        "More" to "Daha fazla",
        "Less" to "Daha az",
        "Advanced" to "Gelişmiş",
        "Optional" to "İsteğe bağlı",
        "Required" to "Gerekli",
        "Selected" to "Seçili",
        "In use" to "Kullanımda",
        "Open settings" to "Ayarları aç",
        "Back to Settings" to "Ayarlara dön",
        "Back to setup" to "Kuruluma dön",
        "Back to runtime manager" to "Çalışma zamanı yöneticisine dön",

        // Setup and onboarding
        "Welcome" to "Hoş geldiniz",
        "Model access" to "Model erişimi",
        "Preparing Xylune…" to "Xylune hazırlanıyor…",
        "Start setup" to "Kuruluma başla",
        "Enter Xylune" to "Xylune'a gir",
        "Manage providers" to "Sağlayıcıları yönet",
        "Connect a provider" to "Sağlayıcı bağla",
        "Continue without one" to "Sağlayıcı olmadan devam et",
        "Skip for now" to "Şimdilik atla",
        "Set up Xylune" to "Xylune'u kur",
        "Restore a backup if you have one, then connect the model provider you actually want to use. Everything optional stays out of your way." to "Varsa bir yedeği geri yükleyin, ardından kullanmak istediğiniz model sağlayıcısını bağlayın. İsteğe bağlı her şey yolunuzdan çekilir.",
        "Private by design" to "Tasarım gereği özel",
        "Chats, credentials, and tool workspaces stay on this device." to "Sohbetler, kimlik bilgileri ve araç çalışma alanları bu cihazda kalır.",
        "Bring your own models" to "Kendi modellerinizi kullanın",
        "Use a ChatGPT account, API provider, or local server." to "Bir ChatGPT hesabı, API sağlayıcısı veya yerel sunucu kullanın.",
        "Optional tools stay optional" to "İsteğe bağlı araçlar isteğe bağlı kalır",
        "Python and Linux are managed later from Settings → Local execution." to "Python ve Linux daha sonra Ayarlar → Yerel çalıştırma bölümünden yönetilir.",
        "Starting fresh? Ignore the restore card and tap Continue." to "Sıfırdan mı başlıyorsunuz? Geri yükleme kartını atlayıp Devam et'e dokunun.",
        "Model access connected" to "Model erişimi bağlandı",
        "Connect a model provider" to "Model sağlayıcısı bağla",
        "A provider is needed only when you send a message. You may connect one now or continue and do it later." to "Bir sağlayıcı yalnızca mesaj gönderirken gerekir. Şimdi bağlayabilir veya devam edip daha sonra ekleyebilirsiniz.",
        "Credentials were found and this step is complete." to "Kimlik bilgileri bulundu ve bu adım tamamlandı.",
        "The built-in provider catalog is delayed. Setup remains usable and Xylune will keep retrying in the background." to "Yerleşik sağlayıcı kataloğu gecikiyor. Kurulum kullanılabilir kalacak ve Xylune arka planda yeniden denemeye devam edecek.",
        "Setup" to "Kurulum",
        "Finish setup" to "Kurulumu tamamla",
        "Setup & connections" to "Kurulum ve bağlantılar",
        "App settings and configuration" to "Uygulama ayarları ve yapılandırma",
        "Appearance, backups, memory, and advanced behavior are grouped for later." to "Görünüm, yedekler, hafıza ve gelişmiş davranışlar daha sonra düzenlenmek üzere gruplandırılmıştır.",

        // Sidebar / conversations / projects
        "New chat" to "Yeni sohbet",
        "Search chats and messages" to "Sohbetlerde ve mesajlarda ara",
        "All chats" to "Tüm sohbetler",
        "Archived" to "Arşivlenmiş",
        "PROJECTS" to "PROJELER",
        "RECENT CHATS" to "SON SOHBETLER",
        "ARCHIVED CHATS" to "ARŞİVLENMİŞ SOHBETLER",
        "PROJECT CHATS" to "PROJE SOHBETLERİ",
        "Archived chats will appear here." to "Arşivlenmiş sohbetler burada görünür.",
        "No chats in this project yet." to "Bu projede henüz sohbet yok.",
        "On-device history • BYOK" to "Cihazdaki geçmiş • BYOK",
        "No project" to "Proje yok",
        "Unpin" to "Sabitlemeyi kaldır",
        "Pin" to "Sabitle",
        "Move to project" to "Projeye taşı",
        "Share portable chat" to "Taşınabilir sohbeti paylaş",
        "Unarchive" to "Arşivden çıkar",
        "Archive" to "Arşivle",
        "Delete permanently" to "Kalıcı olarak sil",
        "Move chat" to "Sohbeti taşı",
        "Create new project" to "Yeni proje oluştur",
        "Project" to "Proje",
        "Rename project" to "Projeyi yeniden adlandır",
        "Delete project" to "Projeyi sil",
        "Rename chat" to "Sohbeti yeniden adlandır",
        "New project" to "Yeni proje",
        "Chats are kept and moved back to All chats." to "Sohbetler korunur ve Tüm sohbetler bölümüne geri taşınır.",
        "Its complete message history, attachments, and local code workspace records will be removed from Xylune." to "Tüm mesaj geçmişi, ekleri ve yerel kod çalışma alanı kayıtları Xylune'dan kaldırılır.",

        // Settings home and routes
        "Settings" to "Ayarlar",
        "Providers & models" to "Sağlayıcılar ve modeller",
        "Add your first API provider" to "İlk API sağlayıcınızı ekleyin",
        "Backup & transfer" to "Yedekleme ve aktarım",
        "Cloud backups, local archives, and restore" to "Bulut yedekleri, yerel arşivler ve geri yükleme",
        "Chat behavior" to "Sohbet davranışı",
        "New chat defaults" to "Yeni sohbet varsayılanları",
        "Model, thinking, context, and output limits" to "Model, düşünme, bağlam ve çıktı sınırları",
        "Response style" to "Yanıt stili",
        "Emoji use and global answer presentation" to "Emoji kullanımı ve genel yanıt sunumu",
        "Custom instructions" to "Özel talimatlar",
        "Reusable tone and workflow profiles" to "Yeniden kullanılabilir üslup ve iş akışı profilleri",
        "Intelligence" to "Zekâ",
        "Memory" to "Hafıza",
        "Cross-chat facts and preferences stored locally" to "Sohbetler arasında kullanılan, yerel saklanan bilgiler ve tercihler",
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
        "Theme, palette, launcher icon, and AMOLED black" to "Tema, palet, başlatıcı simgesi ve AMOLED siyahı",
        "About" to "Hakkında",
        "About Xylune" to "Xylune hakkında",
        "Version, architecture, and privacy model" to "Sürüm, mimari ve gizlilik modeli",

        // Response style / automation
        "Assistant responses" to "Asistan yanıtları",
        "Less emoji" to "Daha az emoji",
        "Avoid decorative emoji and use them only when they add meaning" to "Dekoratif emojilerden kaçın ve yalnızca anlam kattıklarında kullan",
        "Enabled by default. Technical symbols and emoji requested by the user are not blocked." to "Varsayılan olarak etkindir. Teknik semboller ve kullanıcının istediği emojiler engellenmez.",
        "Background task models" to "Arka plan görevi modelleri",
        "Choose how Xylune names chats and compresses older context." to "Xylune'un sohbetleri nasıl adlandıracağını ve eski bağlamı nasıl sıkıştıracağını seçin.",
        "Configure a usable provider to enable model-based automation." to "Model tabanlı otomasyon için kullanılabilir bir sağlayıcı yapılandırın.",
        "Chat naming" to "Sohbet adlandırma",
        "Model mode considers newer messages whenever a name is regenerated." to "Model modu, ad her yeniden oluşturulduğunda daha yeni mesajları dikkate alır.",
        "Context compression" to "Bağlam sıkıştırma",
        "Older messages outside the active context window are merged into saved compact context." to "Etkin bağlam penceresinin dışındaki eski mesajlar, kaydedilmiş sıkıştırılmış bağlamla birleştirilir.",
        "Local • no API call" to "Yerel • API çağrısı yok",
        "Use selected model" to "Seçili modeli kullan",
        "Choose automation model" to "Otomasyon modeli seç",

        // Memory
        "Use memory" to "Hafızayı kullan",
        "Expose selected enabled memories to chats and allow memory tools" to "Seçili etkin hafızaları sohbetlere sun ve hafıza araçlarına izin ver",
        "Automatic memory" to "Otomatik hafıza",
        "Allow models to save stable, non-sensitive details; duplicate items are merged" to "Modellerin kalıcı ve hassas olmayan ayrıntıları kaydetmesine izin ver; yinelenen öğeler birleştirilir",
        "Add memory" to "Hafıza ekle",
        "Manual memories are available immediately and use the same deduplication rules." to "Elle eklenen hafızalar hemen kullanılabilir ve aynı yinelenme önleme kurallarını kullanır.",
        "Category" to "Kategori",
        "Save memory" to "Hafızayı kaydet",
        "Saved memories" to "Kaydedilmiş hafızalar",
        "Nothing is stored yet." to "Henüz hiçbir şey kaydedilmedi.",
        "Search memories" to "Hafızalarda ara",
        "All categories" to "Tüm kategoriler",
        "Delete disabled" to "Devre dışı olanları sil",
        "No memories saved yet." to "Henüz kaydedilmiş hafıza yok.",
        "No memories match the current filters." to "Geçerli filtrelerle eşleşen hafıza yok.",
        "Manual" to "Elle",
        "From chat" to "Sohbetten",
        "Delete memory?" to "Hafıza silinsin mi?",
        "This permanently removes the selected memory data from Xylune." to "Bu işlem seçili hafıza verisini Xylune'dan kalıcı olarak kaldırır.",
        "Disabled memories are currently excluded from chats. This cleanup permanently removes them." to "Devre dışı hafızalar şu anda sohbetlere dahil edilmez. Bu temizlik onları kalıcı olarak kaldırır.",
        "Recently updated" to "Son güncellenen",
        "Recently created" to "Son oluşturulan",

        // Appearance
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
        "Choose a restrained built-in palette or Android dynamic colors. Every swatch is rendered from that palette, not the currently selected one." to "Sade bir yerleşik palet veya Android dinamik renklerini seçin. Her renk örneği mevcut seçimden değil, kendi paletinden oluşturulur.",
        "Match launcher icon to palette" to "Başlatıcı simgesini paletle eşleştir",
        "Keep the classic Xylune green icon regardless of the selected palette." to "Seçili paletten bağımsız olarak klasik yeşil Xylune simgesini koru.",
        "Android themed icons can recolor Xylune's monochrome layer. Dynamic uses the live wallpaper-derived Material You palette when themed icons are off." to "Android temalı simgeleri Xylune'un tek renkli katmanını yeniden renklendirebilir. Temalı simgeler kapalıyken Dinamik, duvar kâğıdından türetilen canlı Material You paletini kullanır.",
        "AMOLED black" to "AMOLED siyahı",
        "AMOLED black only changes dark mode surfaces." to "AMOLED siyahı yalnızca koyu mod yüzeylerini değiştirir.",
        "Interface panels" to "Arayüz panelleri",
        "Panel shape is a choice. Blur, softness, and tint remain continuous controls." to "Panel şekli ayrı bir seçimdir. Bulanıklık, yumuşaklık ve renk tonu kesintisiz kontroller olarak kalır.",
        "Blur" to "Bulanıklık",
        "Hidden by tint" to "Renk tonu tarafından gizleniyor",
        "0% disables blur. Higher values increase the panel-local blur radius." to "%0 bulanıklığı kapatır. Daha yüksek değerler panel içi bulanıklık yarıçapını artırır.",
        "Tint is fully opaque and covers the blurred background. Lower Tint opacity to reveal blur." to "Renk tonu tamamen opak ve bulanık arka planı kapatıyor. Bulanıklığı görmek için renk tonu opaklığını azaltın.",
        "Panel shape" to "Panel şekli",
        "Rounded" to "Yuvarlatılmış",
        "Flat" to "Düz",
        "Edge softness" to "Kenar yumuşaklığı",
        "Hard" to "Sert",
        "Softens the boundary where flat panels merge into the page." to "Düz panellerin sayfayla birleştiği sınırı yumuşatır.",
        "Rounded panels use a hard, rounded boundary. Choose Flat to adjust softness." to "Yuvarlatılmış paneller sert ve yuvarlak bir sınır kullanır. Yumuşaklığı ayarlamak için Düz'ü seçin.",
        "Tint opacity" to "Renk tonu opaklığı",
        "100% is fully opaque and hides background blur." to "%100 tamamen opaktır ve arka plan bulanıklığını gizler.",
        "0% is transparent. 100% is a fully opaque panel tint." to "%0 saydamdır. %100 tamamen opak panel renk tonudur.",

        // Privacy / safety / legal
        "Generated content" to "Oluşturulan içerik",
        "Controls how Xylune handles AI-generated interactive UI." to "Xylune'un yapay zekâ tarafından oluşturulan etkileşimli arayüzü nasıl işleyeceğini denetler.",
        "Safe generated rendering" to "Güvenli oluşturulan içerik işleme",
        "Generated widgets are paused and shown as safe fallback content." to "Oluşturulan widget'lar duraklatılır ve güvenli yedek içerik olarak gösterilir.",
        "Generated widgets may render, but Xylune still applies its capability checks and crash recovery." to "Oluşturulan widget'lar işlenebilir; Xylune yine de yetenek kontrollerini ve çökme kurtarmasını uygular.",
        "Automatic repair attempts" to "Otomatik onarım denemeleri",
        "Invalid completed widgets, charts, and diagrams are repaired in place up to this limit." to "Geçersiz tamamlanmış widget, grafik ve diyagramlar bu sınıra kadar yerinde onarılır.",
        "Third-party AI and services" to "Üçüncü taraf yapay zekâ ve hizmetler",
        "Xylune is a client, not an AI model host. Responses come from the provider or local server selected by the user." to "Xylune bir istemcidir, yapay zekâ modeli barındırmaz. Yanıtlar kullanıcının seçtiği sağlayıcıdan veya yerel sunucudan gelir.",
        "Privacy" to "Gizlilik",
        "Terms" to "Koşullar",
        "Data deletion" to "Veri silme",
        "No Xylune account, ads, analytics, or Xylune cloud. Chat history and API keys remain on this device; traffic goes to endpoints and web tools you explicitly enable." to "Xylune hesabı, reklam, analiz veya Xylune bulutu yoktur. Sohbet geçmişi ve API anahtarları bu cihazda kalır; trafik yalnızca açıkça etkinleştirdiğiniz uç noktalara ve web araçlarına gider.",
        "Privacy policy" to "Gizlilik politikası",
        "Terms & disclaimer" to "Koşullar ve sorumluluk reddi",
        "Legal" to "Yasal",

        // Custom instructions
        "Custom instruction profiles" to "Özel talimat profilleri",
        "New custom profile" to "Yeni özel profil",
        "No saved prompts yet." to "Henüz kaydedilmiş istem yok.",
        "Additional instructions" to "Ek talimatlar",
        "Override default tone/persona" to "Varsayılan üslup/personayı geçersiz kıl",
        "Use for new chats" to "Yeni sohbetlerde kullan",
        "Use Xylune default for new chats" to "Yeni sohbetlerde Xylune varsayılanını kullan",
        "Edit custom profile" to "Özel profili düzenle",
        "Name" to "Ad",
        "Prepend" to "Başa ekle",
        "Override" to "Geçersiz kıl",
        "Instructions" to "Talimatlar",

        // Local execution / packages
        "Availability in new chats" to "Yeni sohbetlerde kullanılabilirlik",
        "Local execution is opt-in for fresh installs. Existing chats keep their own tool choices." to "Yerel çalıştırma yeni kurulumlarda isteğe bağlıdır. Mevcut sohbetler kendi araç seçimlerini korur.",
        "Tool defaults" to "Araç varsayılanları",
        "Python" to "Python",
        "Bundled Python 3.12 · no Linux download required" to "Dahili Python 3.12 · Linux indirmesi gerekmez",
        "Linux commands" to "Linux komutları",
        "Requires a separate distribution download" to "Ayrı bir dağıtım indirmesi gerekir",
        "Runtime manager" to "Çalışma zamanı yöneticisi",
        "Inspect environments, install packages, run a test, or add/remove the optional Linux distribution." to "Ortamları inceleyin, paket yükleyin, test çalıştırın veya isteğe bağlı Linux dağıtımını ekleyip kaldırın.",
        "Open runtime manager" to "Çalışma zamanı yöneticisini aç",
        "Package approval" to "Paket onayı",
        "Choose when Xylune may install Python or Linux packages and which sources are trusted." to "Xylune'un Python veya Linux paketlerini ne zaman yükleyebileceğini ve hangi kaynakların güvenilir olduğunu seçin.",
        "Ask every time" to "Her seferinde sor",
        "Show the full plan and wait for you" to "Tam planı göster ve onayınızı bekle",
        "Trusted list" to "Güvenilir liste",
        "Auto-approve only package names you list" to "Yalnızca listelediğiniz paket adlarını otomatik onayla",
        "Approval model" to "Onay modeli",
        "A separately selected model allows or denies the preflight plan" to "Ayrı seçilen bir model ön kontrol planına izin verir veya reddeder",
        "Auto-approve" to "Otomatik onayla",
        "Install every valid preflight plan without asking" to "Geçerli her ön kontrol planını sormadan yükle",
        "Choose approval model" to "Onay modeli seç",
        "Trusted pip packages" to "Güvenilir pip paketleri",
        "Comma, space, or newline separated" to "Virgül, boşluk veya yeni satırla ayrılmış",
        "Trusted Linux packages (apt/apk)" to "Güvenilir Linux paketleri (apt/apk)",
        "Advanced package sources" to "Gelişmiş paket kaynakları",
        "Allow pip direct references and relaxed apt names; command-line options remain blocked" to "pip doğrudan referanslarına ve esnek apt adlarına izin ver; komut satırı seçenekleri engelli kalır",
        "Allow package changes?" to "Paket değişikliklerine izin verilsin mi?",
        "Allow package installation?" to "Paket yüklemeye izin verilsin mi?",
        "Allow and install" to "İzin ver ve yükle",
        "Before the first install" to "İlk yüklemeden önce",
        "Commands run as uid 0 inside the selected PRoot distribution." to "Komutlar seçili PRoot dağıtımında uid 0 olarak çalışır.",
        "Back up now" to "Şimdi yedekle",

        // Developer settings
        "Developer settings" to "Geliştirici ayarları",
        "Local diagnostics for measuring Xylune's rendering and process performance. No metrics are uploaded or stored in chat history." to "Xylune'un işleme ve süreç performansını ölçen yerel tanılamalar. Hiçbir ölçüm yüklenmez veya sohbet geçmişinde saklanmaz.",
        "Enable developer settings" to "Geliştirici ayarlarını etkinleştir",
        "Tool diagnostics" to "Araç tanılamaları",
        "Show tool diagnostics" to "Araç tanılamalarını göster",
        "Off by default. Normal chats show only a concise failure summary and Retry." to "Varsayılan olarak kapalıdır. Normal sohbetler yalnızca kısa bir hata özeti ve Yeniden dene seçeneğini gösterir.",
        "Performance counter" to "Performans sayacı",
        "Show performance overlay" to "Performans katmanını göster",
        "Cause profiler" to "Neden profilleyici",
        "Detailed metrics" to "Ayrıntılı ölçümler",
        "Panel opacity" to "Panel opaklığı",
        "Text opacity" to "Metin opaklığı",
        "Overlay scale" to "Katman ölçeği",
        "Update interval" to "Güncelleme aralığı",
        "Overlay position" to "Katman konumu",
        "Top left" to "Sol üst",
        "Top right" to "Sağ üst",
        "Bottom left" to "Sol alt",
        "Bottom right" to "Sağ alt",
        "Blur boundary diagnostics" to "Bulanıklık sınırı tanılamaları",
        "Show blur boundary guides" to "Bulanıklık sınırı kılavuzlarını göster",
        "Guide thickness" to "Kılavuz kalınlığı",
        "Allocation / blocking GC pressure" to "Ayırma / engelleyici GC baskısı",
        "Buffer swap" to "Arabellek takası",

        // About / updates
        "Project" to "Proje",
        "Created by @omerfaruknehir" to "@omerfaruknehir tarafından oluşturuldu",
        "Open the creator's GitHub profile" to "Geliştiricinin GitHub profilini aç",
        "Build source" to "Derleme kaynağı",
        "No GitHub source was embedded in this build" to "Bu derlemeye GitHub kaynağı eklenmemiş",
        "Licenses & notices" to "Lisanslar ve bildirimler",
        "Offline dependency catalog and full license texts" to "Çevrimdışı bağımlılık kataloğu ve tam lisans metinleri",
        "Report an issue" to "Sorun bildir",
        "Bugs, regressions, and feature requests" to "Hatalar, gerilemeler ve özellik istekleri",
        "Updates" to "Güncellemeler",
        "Check automatically" to "Otomatik kontrol et",
        "Check the source repository once per day when Xylune starts" to "Xylune başlatıldığında kaynak deposunu günde bir kez kontrol et",
        "Check for updates" to "Güncellemeleri kontrol et",
        "Check again" to "Tekrar kontrol et",
        "Xylune is up to date" to "Xylune güncel",
        "Update check failed" to "Güncelleme kontrolü başarısız",
        "Download update" to "Güncellemeyi indir",
        "Open release page" to "Sürüm sayfasını aç",
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
        "Private by design" to "Tasarım gereği özel",
        "Chats, credentials, and workspaces stay on your device. Xylune connects directly to providers you configure and has no application backend, ads, or telemetry." to "Sohbetler, kimlik bilgileri ve çalışma alanları cihazınızda kalır. Xylune yapılandırdığınız sağlayıcılara doğrudan bağlanır; uygulama arka ucu, reklam veya telemetri içermez.",
        "This is a debug-signed development build." to "Bu, hata ayıklama imzalı bir geliştirme derlemesidir.",
        "Developer options" to "Geliştirici seçenekleri",
        "Developer options · enabled" to "Geliştirici seçenekleri · etkin",

        // Chat defaults / composer
        "Composer defaults" to "Yazma alanı varsayılanları",
        "Starting state for the controls beside the message box." to "Mesaj kutusunun yanındaki kontrollerin başlangıç durumu.",
        "Tools and modes" to "Araçlar ve modlar",
        "Web search" to "Web araması",
        "Deep Research" to "Derin Araştırma",
        "Deep Research plans, searches iteratively, verifies sources, and produces a cited report. Enabling it also enables web search." to "Derin Araştırma plan yapar, yinelemeli arama yapar, kaynakları doğrular ve kaynaklı bir rapor üretir. Etkinleştirmek web aramasını da açar.",
        "Token counting" to "Token sayımı",
        "Hybrid token counting" to "Hibrit token sayımı",
        "Context & output" to "Bağlam ve çıktı",
        "A pair is one request plus its answer. Working history has its own budget inside the total context ceiling." to "Bir çift, bir istek ve yanıtıdır. Çalışma geçmişinin toplam bağlam üst sınırı içinde ayrı bir bütçesi vardır.",
        "Last message pairs" to "Son mesaj çiftleri",
        "Context token ceiling" to "Bağlam token üst sınırı",
        "Working history token budget" to "Çalışma geçmişi token bütçesi",
        "Maximum output tokens" to "Maksimum çıktı token'ı",
        "Working display" to "Çalışma görünümü",
        "Xylune core prompt" to "Xylune çekirdek istemi",
        "Thinking" to "Düşünme",
        "Not supported by this model" to "Bu model tarafından desteklenmiyor",
        "Thinking effort" to "Düşünme düzeyi",
        "Available levels follow the selected model. Some models cannot fully disable reasoning." to "Kullanılabilir düzeyler seçili modele bağlıdır. Bazı modeller akıl yürütmeyi tamamen kapatamaz.",
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
        "One searchable catalog is used everywhere in Xylune." to "Xylune'un her yerinde tek bir aranabilir katalog kullanılır.",
        "Choose a model" to "Model seç",
        "No provider selected" to "Sağlayıcı seçilmedi",
        "Add a usable provider in the Providers tab." to "Sağlayıcılar sekmesinden kullanılabilir bir sağlayıcı ekleyin.",

        // Providers and models
        "Providers" to "Sağlayıcılar",
        "Choose a provider, then manage its connection and models." to "Bir sağlayıcı seçin, ardından bağlantısını ve modellerini yönetin.",
        "No providers yet" to "Henüz sağlayıcı yok",
        "Add a ChatGPT account or configure an API-compatible provider." to "Bir ChatGPT hesabı ekleyin veya API uyumlu bir sağlayıcı yapılandırın.",
        "Add ChatGPT" to "ChatGPT ekle",
        "Add API" to "API ekle",
        "ChatGPT OAuth • Connected" to "ChatGPT OAuth • Bağlı",
        "ChatGPT OAuth • Signing in" to "ChatGPT OAuth • Oturum açılıyor",
        "ChatGPT OAuth • Needs attention" to "ChatGPT OAuth • İşlem gerekiyor",
        "ChatGPT OAuth • Disconnected" to "ChatGPT OAuth • Bağlantı kesildi",
        "Keyless endpoint" to "Anahtarsız uç nokta",
        "API key saved securely" to "API anahtarı güvenli şekilde kaydedildi",
        "API key missing" to "API anahtarı eksik",
        "Refresh models" to "Modelleri yenile",
        "Edit connection" to "Bağlantıyı düzenle",
        "Remove provider" to "Sağlayıcıyı kaldır",
        "Add ChatGPT provider" to "ChatGPT sağlayıcısı ekle",
        "Provider name" to "Sağlayıcı adı",
        "Rename ChatGPT provider" to "ChatGPT sağlayıcısını yeniden adlandır",
        "Use your ChatGPT plan without an API key" to "API anahtarı olmadan ChatGPT planınızı kullanın",
        "Complete sign-in in your browser…" to "Tarayıcınızda oturum açmayı tamamlayın…",
        "Sign in again" to "Tekrar oturum aç",
        "Sign in with ChatGPT" to "ChatGPT ile oturum aç",
        "Disconnect" to "Bağlantıyı kes",
        "Usage & limits" to "Kullanım ve sınırlar",
        "Current account quota windows" to "Mevcut hesap kota aralıkları",
        "Refresh usage" to "Kullanımı yenile",
        "Session" to "Oturum",
        "Weekly" to "Haftalık",
        "Additional credits available" to "Ek krediler kullanılabilir",
        "A ChatGPT usage limit has been reached." to "Bir ChatGPT kullanım sınırına ulaşıldı.",
        "Require API key" to "API anahtarı gerektir",
        "Disable only for a trusted local or keyless endpoint" to "Yalnızca güvenilir yerel veya anahtarsız bir uç nokta için kapatın",
        "Advanced headers" to "Gelişmiş başlıklar",
        "Usually unnecessary" to "Genellikle gerekli değildir",
        "Custom headers JSON" to "Özel başlıklar JSON",
        "Save connection" to "Bağlantıyı kaydet",
        "Add provider" to "Sağlayıcı ekle",
        "Custom provider" to "Özel sağlayıcı",
        "Provider name" to "Sağlayıcı adı",
        "API base URL" to "API temel URL'si",
        "Base URL" to "Temel URL",
        "API key" to "API anahtarı",
        "API key (optional)" to "API anahtarı (isteğe bağlı)",
        "API key is required" to "API anahtarı gerekli",
        "Connect & fetch models" to "Bağlan ve modelleri getir",
        "Fetch models again" to "Modelleri yeniden getir",
        "Models from provider" to "Sağlayıcıdaki modeller",
        "Clear" to "Temizle",
        "Provider has no model list? Enter manually" to "Sağlayıcının model listesi yok mu? Elle girin",
        "Hide manual model entry" to "Elle model girişini gizle",
        "Bundled suggestions" to "Dahili öneriler",
        "API model ID" to "API model kimliği",
        "Model display name" to "Model görünen adı",
        "Manual model will also be included" to "Elle girilen model de dahil edilecek",
        "Only the selected provider models will be saved." to "Yalnızca seçili sağlayıcı modelleri kaydedilecek.",
        "Models" to "Modeller",
        "Search models" to "Modellerde ara",
        "No matching models." to "Eşleşen model yok.",
        "Add model" to "Model ekle",
        "Edit model" to "Modeli düzenle",
        "Only the essentials are shown. Pricing is optional." to "Yalnızca temel alanlar gösterilir. Fiyatlandırma isteğe bağlıdır.",
        "Display name" to "Görünen ad",
        "Context tokens" to "Bağlam token'ları",
        "Max output" to "Maksimum çıktı",
        "Request type" to "İstek türü",
        "Chat" to "Sohbet",
        "Image generation" to "Görsel oluşturma",
        "Controls whether this custom endpoint uses chat/completions or images/generations." to "Bu özel uç noktanın chat/completions mı yoksa images/generations mı kullanacağını belirler.",
        "Advanced compatibility" to "Gelişmiş uyumluluk",
        "Tools" to "Araçlar",
        "Vision" to "Görüntü",
        "Files" to "Dosyalar",
        "Pricing" to "Fiyatlandırma",
        "Pricing configured" to "Fiyatlandırma yapılandırıldı",
        "Configured in USD per million tokens" to "Milyon token başına USD olarak yapılandırıldı",
        "Optional · cost will show as unavailable" to "İsteğe bağlı · maliyet kullanılamıyor olarak gösterilir",
        "Cached input" to "Önbelleğe alınmış girdi",
        "Input" to "Girdi",
        "Output" to "Çıktı",

        // Search settings / search screen
        "Routing" to "Yönlendirme",
        "Fallback search engine" to "Yedek arama motoru",
        "Saved" to "Kaydedildi",
        "Save key" to "Anahtarı kaydet",
        "SearXNG endpoint" to "SearXNG uç noktası",
        "Public HTTPS base URL" to "Herkese açık HTTPS temel URL'si",
        "The instance must enable JSON search output." to "Sunucu JSON arama çıktısını etkinleştirmiş olmalıdır.",
        "Tool behavior" to "Araç davranışı",
        "Maximum search results" to "Maksimum arama sonucu",
        "3–20 results per search call" to "Arama çağrısı başına 3–20 sonuç",
        "Allow page fetching" to "Sayfa getirmeye izin ver",
        "Expose web_fetch so the model can read public HTTPS pages after searching." to "Modelin aramadan sonra herkese açık HTTPS sayfalarını okuyabilmesi için web_fetch aracını sun.",
        "Search chats" to "Sohbetlerde ara",
        "Search messages" to "Mesajlarda ara",
        "Search chats and messages…" to "Sohbetlerde ve mesajlarda ara…",
        "No results" to "Sonuç yok",
        "No matching chats or messages." to "Eşleşen sohbet veya mesaj yok.",
        "Results" to "Sonuçlar",
        "All providers" to "Tüm sağlayıcılar",

        // Sources / links
        "Sources" to "Kaynaklar",
        "Referenced file" to "Başvurulan dosya",
        "External link" to "Harici bağlantı",
        "A file referenced by this answer." to "Bu yanıtta başvurulan bir dosya.",
        "A source used to support the surrounding claim." to "İlgili ifadeyi desteklemek için kullanılan bir kaynak.",
        "An external page linked from this answer." to "Bu yanıttan bağlantı verilen harici bir sayfa.",

        // Image generation / editing
        "Image generation" to "Görsel oluşturma",
        "Generate image" to "Görsel oluştur",
        "Edit image" to "Görseli düzenle",
        "Image editing" to "Görsel düzenleme",
        "Add image" to "Görsel ekle",
        "Add another" to "Bir tane daha ekle",
        "Add reference image" to "Referans görsel ekle",
        "Add to chat" to "Sohbete ekle",
        "Remove image" to "Görseli kaldır",
        "Reference images" to "Referans görseller",
        "Attachments are unavailable in image generation mode" to "Görsel oluşturma modunda ekler kullanılamaz",
        "Add an image, then describe the edit…" to "Bir görsel ekleyin, ardından düzenlemeyi açıklayın…",
        "Describe the image you want…" to "İstediğiniz görseli açıklayın…",
        "Generating image…" to "Görsel oluşturuluyor…",
        "Editing image…" to "Görsel düzenleniyor…",
        "Image request" to "Görsel isteği",
        "Pending image requests" to "Bekleyen görsel istekleri",
        "No image models available" to "Kullanılabilir görsel modeli yok",
        "Choose image model" to "Görsel modeli seç",
        "Image model" to "Görsel modeli",
        "Add at least one reference image." to "En az bir referans görsel ekleyin.",
        "Download image" to "Görseli indir",
        "Share image" to "Görseli paylaş",

        // Generated UI repair / working cards
        "Ask AI to retry" to "Yapay zekâdan yeniden denemesini iste",
        "Automatic repair unavailable" to "Otomatik onarım kullanılamıyor",
        "Repairing…" to "Onarılıyor…",
        "Working" to "Çalışma",
        "Tool call" to "Araç çağrısı",
        "Tool result" to "Araç sonucu",
        "Tool failed" to "Araç başarısız",
        "Run again" to "Tekrar çalıştır",
        "Stop" to "Durdur",
        "Stopped" to "Durduruldu",
        "Queued" to "Sırada",
        "Running" to "Çalışıyor",
        "Completed" to "Tamamlandı",
        "Failed" to "Başarısız",
        "Timed out" to "Zaman aşımına uğradı",
        "Approval required" to "Onay gerekli",
        "Allow" to "İzin ver",
        "Deny" to "Reddet",

        // Backup / transfer / cloud
        "Backup" to "Yedek",
        "Backups" to "Yedekler",
        "Local backup" to "Yerel yedek",
        "Cloud backup" to "Bulut yedeği",
        "Restore backup" to "Yedeği geri yükle",
        "Import backup" to "Yedeği içe aktar",
        "Export backup" to "Yedeği dışa aktar",
        "Backup saved" to "Yedek kaydedildi",
        "Backup failed" to "Yedekleme başarısız",
        "Backup downloaded. Opening preview…" to "Yedek indirildi. Önizleme açılıyor…",
        "Backup restored. Setup was paused; finish provider access later from Settings." to "Yedek geri yüklendi. Kurulum duraklatıldı; sağlayıcı erişimini daha sonra Ayarlar'dan tamamlayın.",
        "Choose folder" to "Klasör seç",
        "Change folder" to "Klasörü değiştir",
        "Select folder" to "Klasör seç",
        "Google Drive" to "Google Drive",
        "OneDrive" to "OneDrive",
        "Dropbox" to "Dropbox",
        "Nextcloud" to "Nextcloud",
        "Amazon S3, MinIO, Backblaze B2, or another compatible bucket" to "Amazon S3, MinIO, Backblaze B2 veya başka bir uyumlu bucket",
        "Account connected" to "Hesap bağlandı",
        "Connect account" to "Hesap bağla",
        "Disconnect account" to "Hesap bağlantısını kes",
        "Sign in" to "Oturum aç",
        "Sign out" to "Oturumu kapat",
        "Restore from cloud" to "Buluttan geri yükle",
        "Restore from file" to "Dosyadan geri yükle",
        "Export to file" to "Dosyaya dışa aktar",
        "Archive password" to "Arşiv parolası",
        "Access key ID" to "Erişim anahtarı kimliği",
        "Secret access key" to "Gizli erişim anahtarı",
        "Bucket" to "Bucket",
        "Endpoint" to "Uç nokta",
        "Region" to "Bölge",
        "Android grants Xylune persistent access only to the folder you select. Create or choose a dedicated Xylune folder; no account-wide permission is requested." to "Android, Xylune'a yalnızca seçtiğiniz klasöre kalıcı erişim verir. Xylune için özel bir klasör oluşturun veya seçin; hesap genelinde izin istenmez.",

        // Sandbox / Linux runtime
        "Runtime" to "Çalışma zamanı",
        "Runtime status" to "Çalışma zamanı durumu",
        "Python environment" to "Python ortamı",
        "Linux environment" to "Linux ortamı",
        "Linux distribution" to "Linux dağıtımı",
        "Install Linux" to "Linux yükle",
        "Remove Linux" to "Linux'u kaldır",
        "Reinstall" to "Yeniden yükle",
        "Packages" to "Paketler",
        "Installed packages" to "Yüklü paketler",
        "Install package" to "Paket yükle",
        "Remove package" to "Paketi kaldır",
        "Run test" to "Test çalıştır",
        "Terminal" to "Terminal",
        "Open terminal" to "Terminali aç",
        "Root terminal" to "Root terminali",
        "Command" to "Komut",
        "Run command" to "Komutu çalıştır",
        "Clear terminal" to "Terminali temizle",
        "Copy output" to "Çıktıyı kopyala",
        "Bundled runtime · loading this chat's environment…" to "Dahili çalışma zamanı · bu sohbetin ortamı yükleniyor…",
        "1–600 seconds. Pure Python is interrupted at the deadline; a blocking native extension may return later." to "1–600 saniye. Saf Python süre sonunda durdurulur; engelleyici bir yerel eklenti daha sonra dönebilir.",

        // Licenses
        "Licenses" to "Lisanslar",
        "Notices" to "Bildirimler",
        "Open-source licenses" to "Açık kaynak lisansları",
        "Search licenses" to "Lisanslarda ara",
        "License text" to "Lisans metni",
        "Dependency" to "Bağımlılık",
        "Dependencies" to "Bağımlılıklar",
        "Changed files" to "Değişen dosyalar",

        // Usage / generic model metadata
        "Cost unavailable" to "Maliyet kullanılamıyor",
        "Thinking always on" to "Düşünme her zaman açık",
        "Tools" to "Araçlar",
        "Vision" to "Görüntü",
        "Image generation" to "Görsel oluşturma",
        "Free" to "Ücretsiz",
        "Favorites" to "Favoriler",
        "Recent" to "Son kullanılanlar",
        "Add favorite" to "Favorilere ekle",
        "Remove favorite" to "Favorilerden kaldır",
        "Usage" to "Kullanım",
        "Cost" to "Maliyet",
        "Input tokens" to "Girdi token'ları",
        "Output tokens" to "Çıktı token'ları",
        "Total tokens" to "Toplam token",
        "Context" to "Bağlam",
        "Output limit" to "Çıktı sınırı",
    )

    fun translate(text: String): String {
        exact[text]?.let { return it }

        Regex("""Step (\d+) of (\d+)""").matchEntire(text)?.let {
            return "${it.groupValues[2]} adımın ${it.groupValues[1]}. adımı"
        }
        Regex("""Continue from step (\d+) of 3""").matchEntire(text)?.let {
            return "3 adımın ${it.groupValues[1]}. adımından devam et"
        }
        Regex("""(\d+) providers? configured""").matchEntire(text)?.let {
            return "${it.groupValues[1]} sağlayıcı yapılandırıldı"
        }
        Regex("""(\d+) configured providers?""").matchEntire(text)?.let {
            return "${it.groupValues[1]} yapılandırılmış sağlayıcı"
        }
        Regex("""(\d+) selected""").matchEntire(text)?.let {
            return "${it.groupValues[1]} seçildi"
        }
        Regex("""(\d+) of (\d+) selected""").matchEntire(text)?.let {
            return "${it.groupValues[2]} öğenin ${it.groupValues[1]} tanesi seçili"
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
        Regex("""(\d+) results?""").matchEntire(text)?.let {
            return "${it.groupValues[1]} sonuç"
        }
        Regex("""(\d+) steps?""").matchEntire(text)?.let {
            return "${it.groupValues[1]} adım"
        }
        Regex("""(\d+) queued""").matchEntire(text)?.let {
            return "${it.groupValues[1]} sırada"
        }
        Regex("""(\d+) starred""").matchEntire(text)?.let {
            return "${it.groupValues[1]} favori"
        }
        Regex("""Delete (\d+) memories\?""").matchEntire(text)?.let {
            return "${it.groupValues[1]} hafıza silinsin mi?"
        }
        Regex("""Delete (\d+) disabled memor(?:y|ies)\?""").matchEntire(text)?.let {
            return "${it.groupValues[1]} devre dışı hafıza silinsin mi?"
        }
        Regex("""(\d+) saved items?; (\d+) enabled\.""").matchEntire(text)?.let {
            return "${it.groupValues[1]} kayıtlı öğe; ${it.groupValues[2]} etkin."
        }
        Regex("""(\d+)-hour limit""").matchEntire(text)?.let {
            return "${it.groupValues[1]} saatlik sınır"
        }
        Regex("""(\d+)-day limit""").matchEntire(text)?.let {
            return "${it.groupValues[1]} günlük sınır"
        }
        Regex("""(\d+)K context""").matchEntire(text)?.let {
            return "${it.groupValues[1]}K bağlam"
        }
        Regex("""(\d+)K output""").matchEntire(text)?.let {
            return "${it.groupValues[1]}K çıktı"
        }
        Regex("""Delete “(.+)”\?""").matchEntire(text)?.let {
            return "“${it.groupValues[1]}” silinsin mi?"
        }
        Regex("""Delete project “(.+)”\?""").matchEntire(text)?.let {
            return "“${it.groupValues[1]}” projesi silinsin mi?"
        }
        Regex("""Search (\d+) models""").matchEntire(text)?.let {
            return "${it.groupValues[1]} modelde ara"
        }
        Regex("""Showing (\d+) of (\d+)\. Search or filter to narrow the catalog\.""").matchEntire(text)?.let {
            return "${it.groupValues[2]} modelin ${it.groupValues[1]} tanesi gösteriliyor. Kataloğu daraltmak için arayın veya filtreleyin."
        }
        Regex("""(\d+) available for (.+) · metadata refreshes with the catalog""").matchEntire(text)?.let {
            return "${it.groupValues[2]} için ${it.groupValues[1]} model kullanılabilir · meta veriler katalogla yenilenir"
        }
        Regex("""Updated (\d+) models""").matchEntire(text)?.let {
            return "${it.groupValues[1]} model güncellendi"
        }
        Regex("""Automatically updated metadata for (\d+) models""").matchEntire(text)?.let {
            return "${it.groupValues[1]} modelin meta verileri otomatik güncellendi"
        }
        Regex("""Latest release: (.+) · checked (.+)""").matchEntire(text)?.let {
            return "En son sürüm: ${it.groupValues[1]} · kontrol: ${it.groupValues[2]}"
        }
        Regex("""Xylune (.+) is available""").matchEntire(text)?.let {
            return "Xylune ${it.groupValues[1]} kullanılabilir"
        }
        Regex("""Credits balance: (.+)""").matchEntire(text)?.let {
            return "Kredi bakiyesi: ${it.groupValues[1]}"
        }
        Regex("""Credits: (.+)""").matchEntire(text)?.let {
            return "Krediler: ${it.groupValues[1]}"
        }
        Regex("""Limit reached: (.+)""").matchEntire(text)?.let {
            return "Sınıra ulaşıldı: ${it.groupValues[1]}"
        }
        Regex("""(.+) plan • reported by ChatGPT""").matchEntire(text)?.let {
            return "${it.groupValues[1]} planı • ChatGPT tarafından bildirildi"
        }
        Regex("""(.+) installed""").matchEntire(text)?.let {
            return "${it.groupValues[1]} yüklü"
        }
        Regex("""(.+) install failed""").matchEntire(text)?.let {
            return "${it.groupValues[1]} yüklemesi başarısız"
        }
        Regex("""(.+) requires at least one reference image\.""").matchEntire(text)?.let {
            return "${it.groupValues[1]} en az bir referans görsel gerektiriyor."
        }
        Regex("""(.+) accepts at most (\d+) reference images\.""").matchEntire(text)?.let {
            return "${it.groupValues[1]} en fazla ${it.groupValues[2]} referans görsel kabul ediyor."
        }
        Regex("""(.+) accepts up to (\d+) reference images\.""").matchEntire(text)?.let {
            return "${it.groupValues[1]} en fazla ${it.groupValues[2]} referans görsel kabul ediyor."
        }
        Regex("""(.+) does not accept reference images\.""").matchEntire(text)?.let {
            return "${it.groupValues[1]} referans görselleri kabul etmiyor."
        }
        Regex("""Add at least one reference image for (.+)\.""").matchEntire(text)?.let {
            return "${it.groupValues[1]} için en az bir referans görsel ekleyin."
        }
        Regex("""(.+) connection""").matchEntire(text)?.let {
            return "${it.groupValues[1]} bağlantısı"
        }
        Regex("""(.+) models""").matchEntire(text)?.let {
            return "${it.groupValues[1]} modelleri"
        }
        Regex("""(.+) ready""").matchEntire(text)?.let {
            return "${it.groupValues[1]} hazır"
        }
        Regex("""Xylune detected (.+)\. Continue or open the provider manager to make changes\.""").matchEntire(text)?.let {
            return "Xylune ${it.groupValues[1]} algıladı. Devam edin veya değişiklik yapmak için sağlayıcı yöneticisini açın."
        }
        Regex("""(.+) is running in the background • (\d+)s deadline""").matchEntire(text)?.let {
            return "${it.groupValues[1]} arka planda çalışıyor • ${it.groupValues[2]} sn süre sınırı"
        }
        Regex("""(.+) paused for crash recovery""").matchEntire(text)?.let {
            return "${it.groupValues[1]} çökme kurtarması için duraklatıldı"
        }
        Regex("""(.+) included""").matchEntire(text)?.let {
            return "${it.groupValues[1]} dahil"
        }
        Regex("""(.+) excluded""").matchEntire(text)?.let {
            return "${it.groupValues[1]} hariç"
        }

        if (text.startsWith("Managed by Xylune · revision ")) {
            return text.replaceFirst("Managed by Xylune · revision ", "Xylune tarafından yönetiliyor · revizyon ")
        }
        if (text.startsWith("Preset: ")) return text.replaceFirst("Preset: ", "Ön ayar: ")
        if (text.startsWith("Protocol: ")) return text.replaceFirst("Protocol: ", "Protokol: ")
        if (text.startsWith("Backup saved to ")) return text.replaceFirst("Backup saved to ", "Yedek şuraya kaydedildi: ")
        if (text.startsWith("Checking ") && text.endsWith("…")) {
            return "Kontrol ediliyor: ${text.removePrefix("Checking ").removeSuffix("…")}…"
        }
        if (text.startsWith("Source: ")) return text.replaceFirst("Source: ", "Kaynak: ")
        if (text.startsWith("Remove ") && text.endsWith("?")) {
            return "${text.removePrefix("Remove ").removeSuffix("?")} kaldırılsın mı?"
        }
        return text
    }
}

package app.xylune.chat.ui

/** Less-common Turkish UI strings: errors, backup flows, runtime states and previews. */
internal object TurkishUiCopyExtra {
    private fun exactLookup(text: String): String? =
        exact1(text) ?:
            exact2(text) ?:
            exact3(text) ?:
            exact4(text) ?:
            exact5(text) ?:
            exact6(text) ?:
            exact7(text)

    private fun exact1(text: String): String? = when (text) {
        "Confirm password" -> "Parolayı doğrula"
        "Connect & fetch models" -> "Bağlan ve modelleri getir"
        "Connect folder" -> "Klasörü bağla"
        "Connect Google Drive" -> "Google Drive'ı bağla"
        "Connect to Ollama, llama.cpp, or LM Studio." -> "Ollama, llama.cpp veya LM Studio'ya bağlanın."
        "Connection details stay out of the way until you need them." -> "Bağlantı ayrıntıları gerekene kadar gizli kalır."
        "Connection failed • tap to retry" -> "Bağlantı başarısız • yeniden denemek için dokunun"
        "Controls whether reasoning and tool cards expand automatically; they remain saved either way." -> "Akıl yürütme ve araç kartlarının otomatik açılıp açılmayacağını belirler; her durumda kaydedilmeye devam ederler."
        "Conversation deleted" -> "Sohbet silindi"
        "Cool teal and cyan accents" -> "Soğuk turkuaz ve camgöbeği vurgular"
        "Copy errors" -> "Hataları kopyala"
        "Copy setup details" -> "Kurulum ayrıntılarını kopyala"
        "Copy source" -> "Kaynağı kopyala"
        "cost unavailable" -> "maliyet kullanılamıyor"
        "Could not connect the cloud folder" -> "Bulut klasörüne bağlanılamadı"
        "Could not create the chat file" -> "Sohbet dosyası oluşturulamadı"
        "Could not delete backup" -> "Yedek silinemedi"
        "Could not download and inspect the cloud backup" -> "Bulut yedeği indirilemedi veya incelenemedi"
        "Could not download backup" -> "Yedek indirilemedi"
        "Could not fetch the model list" -> "Model listesi getirilemedi"
        "Could not inspect archive" -> "Arşiv incelenemedi"
        "Could not list backups" -> "Yedekler listelenemedi"
        "Could not open backup" -> "Yedek açılamadı"
        "Could not open cloud sign-in" -> "Bulut oturum açma sayfası açılamadı"
        "Could not open the update link" -> "Güncelleme bağlantısı açılamadı"
        "Could not read Google Drive app storage" -> "Google Drive uygulama depolaması okunamadı"
        "Could not read the cloud folder" -> "Bulut klasörü okunamadı"
        "Could not read the connected cloud folder" -> "Bağlı bulut klasörü okunamadı"
        "Could not read the selected cloud folder" -> "Seçili bulut klasörü okunamadı"
        "Could not unlock archive" -> "Arşidin kilidi açılamadı"
        "Create image" -> "Görsel oluştur"
        "Creating and uploading…" -> "Oluşturuluyor ve yükleniyor…"
        "Credentials are encrypted with Android Keystore and are never included in Xylune backups." -> "Kimlik bilgileri Android Keystore ile şifrelenir ve Xylune yedeklerine hiçbir zaman dahil edilmez."
        "Credentials are encrypted with Android Keystore and sent only to the provider you choose." -> "Kimlik bilgileri Android Keystore ile şifrelenir ve yalnızca seçtiğiniz sağlayıcıya gönderilir."
        "Credits: unlimited" -> "Krediler: sınırsız"
        "Custom system prompt" -> "Özel sistem istemi"
        "Deep purple with soft rose accents" -> "Yumuşak gül vurgulu koyu mor"
        "Delete backup" -> "Yedeği sil"
        "Delete cloud backup?" -> "Bulut yedeği silinsin mi?"
        "Delete local data and provider-held copies" -> "Yerel verileri ve sağlayıcıdaki kopyaları sil"
        "Delete memory" -> "Hafızayı sil"
        "Deleted system prompt" -> "Sistem istemi silindi"
        "Describe an image to generate…" -> "Oluşturulacak görseli açıklayın…"
        "Describe an image…" -> "Bir görsel açıklayın…"
        "Describe the changes…" -> "Değişiklikleri açıklayın…"
        "Describe the image you want to create." -> "Oluşturmak istediğiniz görseli açıklayın."
        "Describe the image you want. This model generates new images without reference-image editing." -> "İstediğiniz görseli açıklayın. Bu model referans görsel düzenlemeden yeni görseller oluşturur."
        "Development disclosure & disclaimer" -> "Geliştirme açıklaması ve sorumluluk reddi"
        else -> null
    }

    private fun exact2(text: String): String? = when (text) {
        "Diagram renderer found no supported nodes" -> "Diyagram işleyici desteklenen düğüm bulamadı"
        "DIAGRAM • native" -> "DİYAGRAM • yerel"
        "Direct app storage" -> "Doğrudan uygulama depolaması"
        "Direct HTTPS WebDAV support for Nextcloud, ownCloud, NAS servers, and compatible hosts. Use an app password when available." -> "Nextcloud, ownCloud, NAS sunucuları ve uyumlu ana makineler için doğrudan HTTPS WebDAV desteği. Mümkünse uygulama parolası kullanın."
        "Disable only for a trusted local or keyless endpoint" -> "Yalnızca güvenilir yerel veya anahtarsız bir uç nokta için kapatın"
        "Disable only for your own local/keyless endpoint" -> "Yalnızca kendi yerel/anahtarsız uç noktanız için kapatın"
        "Dismiss error" -> "Hatayı kapat"
        "Display-only Markdown cannot edit generated content" -> "Salt görüntüleme Markdown oluşturulan içeriği düzenleyemez"
        "Display-only Markdown cannot execute Linux tools" -> "Salt görüntüleme Markdown Linux araçlarını çalıştıramaz"
        "Display-only Markdown cannot execute Python" -> "Salt görüntüleme Markdown Python çalıştıramaz"
        "Display-only Markdown cannot install packages" -> "Salt görüntüleme Markdown paket yükleyemez"
        "Display-only Markdown cannot repair generated content" -> "Salt görüntüleme Markdown oluşturulan içeriği onaramaz"
        "Display-only Markdown cannot review packages" -> "Salt görüntüleme Markdown paketleri inceleyemez"
        "Display-only Markdown cannot run widgets" -> "Salt görüntüleme Markdown widget çalıştıramaz"
        "Distribution selection, installation, packages, and removal are managed in one place." -> "Dağıtım seçimi, kurulum, paketler ve kaldırma tek bir yerden yönetilir."
        "Documents, archives, code, audio, and other supported files" -> "Belgeler, arşivler, kod, ses ve desteklenen diğer dosyalar"
        "Double-tap, pinch, or drag" -> "Çift dokunun, sıkıştırın veya sürükleyin"
        "Draws explicit debug guides at the top and bottom panel boundaries. Normal UI no longer draws a boundary highlight." -> "Üst ve alt panel sınırlarında belirgin hata ayıklama kılavuzları çizer. Normal arayüz artık sınır vurgusu çizmez."
        "Dropbox Xylune App folder" -> "Dropbox Xylune uygulama klasörü"
        "Duplicate content recording for blur" -> "Bulanıklık için yinelenen içerik kaydı"
        "Each chat has a persistent bundled-Python session and isolated .packages directory. It works without installing Linux and remains confined by Android's app sandbox." -> "Her sohbetin kalıcı bir dahili Python oturumu ve yalıtılmış .packages dizini vardır. Linux yüklemeden çalışır ve Android uygulama sanal alanı içinde kalır."
        "Each provider keeps its OAuth session, models, usage limits, and refresh state separate. Xylune requests a fresh sign-in page so you can add a different ChatGPT account." -> "Her sağlayıcı OAuth oturumunu, modellerini, kullanım sınırlarını ve yenileme durumunu ayrı tutar. Farklı bir ChatGPT hesabı ekleyebilmeniz için Xylune yeni bir oturum açma sayfası ister."
        "Edit image" -> "Görseli düzenle"
        "Edit images" -> "Görselleri düzenle"
        "Edit memory" -> "Hafızayı düzenle"
        "Edit message" -> "Mesajı düzenle"
        "Edit source" -> "Kaynağı düzenle"
        "Encryption password (optional)" -> "Şifreleme parolası (isteğe bağlı)"
        "Enter a server folder and credentials" -> "Sunucu klasörü ve kimlik bilgilerini girin"
        "Enter the exact HTTPS URL of a dedicated Xylune folder. For Nextcloud this normally ends with /remote.php/dav/files/USERNAME/Xylune/." -> "Özel bir Xylune klasörünün tam HTTPS URL'sini girin. Nextcloud için bu genellikle /remote.php/dav/files/KULLANICI_ADI/Xylune/ ile biter."
        "Enter Xylune now. You can return to Providers & models from Settings at any time." -> "Şimdi Xylune'a girin. İstediğiniz zaman Ayarlar'dan Sağlayıcılar ve modeller bölümüne dönebilirsiniz."
        "Everything listed here is embedded in this build and available without a network connection." -> "Burada listelenen her şey bu derlemeye gömülüdür ve ağ bağlantısı olmadan kullanılabilir."
        "Everything requested is already installed." -> "İstenen her şey zaten yüklü."
        "Exact provider counters where available, then local family and generic fallbacks." -> "Varsa sağlayıcının kesin sayaçları, ardından yerel aile ve genel yedek değerler kullanılır."
        "Execution completed" -> "Çalıştırma tamamlandı"
        "Execution deadline (seconds)" -> "Çalıştırma süre sınırı (saniye)"
        "Execution exceeded its configured deadline." -> "Çalıştırma yapılandırılmış süre sınırını aştı."
        "Execution failed" -> "Çalıştırma başarısız"
        "Expand step" -> "Adımı genişlet"
        "Expand work details" -> "Çalışma ayrıntılarını genişlet"
        "Extended reasoning" -> "Genişletilmiş akıl yürütme"
        "Fastest, light reasoning" -> "En hızlı, hafif akıl yürütme"
        "Fetched source" -> "Kaynak getirildi"
        "Fetching OpenRouter capabilities, reasoning modes, limits, and pricing…" -> "OpenRouter yetenekleri, akıl yürütme modları, sınırlar ve fiyatlandırma getiriliyor…"
        "File no longer exists" -> "Dosya artık mevcut değil"
        "Finished with an error" -> "Hatayla tamamlandı"
        "Fit image" -> "Görseli sığdır"
        "Focused defaults" -> "Odaklanmış varsayılanlar"
        else -> null
    }

    private fun exact3(text: String): String? = when (text) {
        "Folder selection was cancelled." -> "Klasör seçimi iptal edildi."
        "Frame pacing / scheduling stalls" -> "Kare zamanlama / planlama takılmaları"
        "Generate + edit" -> "Oluştur + düzenle"
        "Generate image" -> "Görsel oluştur"
        "Generate images" -> "Görseller oluştur"
        "Generating image" -> "Görsel oluşturuluyor"
        "GitHub release workflows embed their own owner/repository. Fork builds therefore follow the fork they came from." -> "GitHub sürüm iş akışları kendi sahip/depo bilgisini içerir. Bu nedenle fork derlemeleri geldikleri fork'u takip eder."
        "Global answer preferences. Changes apply to existing chats and new chats on their next response." -> "Genel yanıt tercihleri. Değişiklikler mevcut ve yeni sohbetlere bir sonraki yanıtlarında uygulanır."
        "Go to latest message" -> "En son mesaja git"
        "Google account selected" -> "Google hesabı seçildi"
        "Google Drive app storage" -> "Google Drive uygulama depolaması"
        "Google Drive authorization could not be opened" -> "Google Drive yetkilendirmesi açılamadı"
        "Google Drive authorization could not be opened." -> "Google Drive yetkilendirmesi açılamadı."
        "Google Drive authorization expired" -> "Google Drive yetkilendirmesinin süresi doldu"
        "Google Drive authorization failed" -> "Google Drive yetkilendirmesi başarısız"
        "Google Drive authorization returned no access token" -> "Google Drive yetkilendirmesi erişim token'ı döndürmedi"
        "Google Drive backup failed" -> "Google Drive yedeklemesi başarısız"
        "Google Drive connection was canceled." -> "Google Drive bağlantısı iptal edildi."
        "Google Drive could not be disconnected" -> "Google Drive bağlantısı kesilemedi"
        "Google Drive error" -> "Google Drive hatası"
        "Google Drive registration details copied" -> "Google Drive kayıt ayrıntıları kopyalandı"
        "Google Drive sign-in was cancelled." -> "Google Drive oturum açma işlemi iptal edildi."
        "Google Drive, OneDrive, Nextcloud, USB, or local storage through Android" -> "Android üzerinden Google Drive, OneDrive, Nextcloud, USB veya yerel depolama"
        "Google returned no access token. Check that this app's package name and signing certificate are registered for its OAuth client." -> "Google erişim token'ı döndürmedi. Bu uygulamanın paket adının ve imzalama sertifikasının OAuth istemcisine kayıtlı olduğunu kontrol edin."
        "GPU rendering" -> "GPU işleme"
        "GPU rendering (blur active)" -> "GPU işleme (bulanıklık etkin)"
        "Guides are bright red and diagnostic-only. They are never shown unless both Developer settings and this toggle are enabled." -> "Kılavuzlar parlak kırmızıdır ve yalnızca tanılama içindir. Geliştirici ayarları ve bu anahtar birlikte etkin değilse hiçbir zaman gösterilmezler."
        "Hide attempts" -> "Denemeleri gizle"
        "Hide manual model entry" -> "Elle model girişini gizle"
        "Hide OCR overlay" -> "OCR katmanını gizle"
        "HTTPS endpoint" -> "HTTPS uç noktası"
        "Hybrid preflight counting" -> "Hibrit ön kontrol sayımı"
        "Idle / no rendered frames" -> "Boşta / işlenmiş kare yok"
        "Image billing" -> "Görsel faturalandırması"
        "Image generation and editing models are kept separate from chat models" -> "Görsel oluşturma ve düzenleme modelleri sohbet modellerinden ayrı tutulur"
        "Image generation · describe the image you want to create" -> "Görsel oluşturma · oluşturmak istediğiniz görseli açıklayın"
        "Import and continue" -> "İçe aktar ve devam et"
        "Import backup" -> "Yedeği içe aktar"
        "Import creates separate local copies. It never replaces an existing chat and does not import API keys or OAuth sessions." -> "İçe aktarma ayrı yerel kopyalar oluşturur. Mevcut bir sohbeti asla değiştirmez ve API anahtarlarını veya OAuth oturumlarını içe aktarmaz."
        "Import failed" -> "İçe aktarma başarısız"
        "Import verification passed." -> "İçe aktarma doğrulaması başarılı."
        "Import verification warning:" -> "İçe aktarma doğrulama uyarısı:"
        "Import Xylune backup" -> "Xylune yedeğini içe aktar"
        "Imports are non-destructive: every chat is created as a separate copy. Existing chats are never overwritten. Provider credentials are not imported, so reconnect the required provider before continuing an imported chat." -> "İçe aktarma tahribatsızdır: her sohbet ayrı bir kopya olarak oluşturulur. Mevcut sohbetlerin üzerine yazılmaz. Sağlayıcı kimlik bilgileri içe aktarılmaz; içe aktarılan bir sohbete devam etmeden önce gerekli sağlayıcıyı yeniden bağlayın."
        "Include app settings and configuration" -> "Uygulama ayarlarını ve yapılandırmayı dahil et"
        "Include attachments" -> "Ekleri dahil et"
        "Include custom system prompts" -> "Özel sistem istemlerini dahil et"
        "Include installed Linux environments" -> "Yüklü Linux ortamlarını dahil et"
        else -> null
    }

    private fun exact4(text: String): String? = when (text) {
        "Include reasoning, tool traces, and request metadata" -> "Akıl yürütme, araç izleri ve istek meta verilerini dahil et"
        "Included content" -> "Dahil edilen içerik"
        "Included modules" -> "Dahil edilen modüller"
        "Includes theme, UI behavior, new-chat defaults, provider endpoints/models, projects, prompt profiles, and automation settings. Credentials, OAuth sessions, provider authorization headers, cloud grants, drafts, and navigation state stay excluded." -> "Tema, arayüz davranışı, yeni sohbet varsayılanları, sağlayıcı uç noktaları/modelleri, projeler, istem profilleri ve otomasyon ayarlarını içerir. Kimlik bilgileri, OAuth oturumları, sağlayıcı yetkilendirme başlıkları, bulut izinleri, taslaklar ve gezinme durumu hariç tutulur."
        "Input handling" -> "Girdi işleme"
        "Input tokens" -> "Girdi token'ları"
        "Install a Linux workspace before enabling" -> "Etkinleştirmeden önce bir Linux çalışma alanı yükleyin"
        "Install blocked" -> "Yükleme engellendi"
        "Install failed" -> "Yükleme başarısız"
        "Install packages" -> "Paketleri yükle"
        "Install Ubuntu, Debian, or Alpine before Xylune can use Linux tools." -> "Xylune Linux araçlarını kullanmadan önce Ubuntu, Debian veya Alpine yükleyin."
        "Installation and import verification completed." -> "Yükleme ve içe aktarma doğrulaması tamamlandı."
        "Installation completed successfully." -> "Yükleme başarıyla tamamlandı."
        "Installation failed" -> "Yükleme başarısız"
        "Installation failed. No success was recorded." -> "Yükleme başarısız. Başarılı sonuç kaydedilmedi."
        "Installation or import verification failed." -> "Yükleme veya içe aktarma doğrulaması başarısız."
        "Installation started" -> "Yükleme başladı"
        "Installed Linux environments" -> "Yüklü Linux ortamları"
        "Installed, but import verification found a problem" -> "Yüklendi ancak içe aktarma doğrulaması bir sorun buldu"
        "Installing approved changes…" -> "Onaylanan değişiklikler yükleniyor…"
        "Installing…" -> "Yükleniyor…"
        "Interrupted response" -> "Kesintiye uğrayan yanıt"
        "Its encrypted OAuth session and models will be disconnected. Chats and usage history are kept." -> "Şifreli OAuth oturumu ve modellerin bağlantısı kesilir. Sohbetler ve kullanım geçmişi korunur."
        "Its saved API key will be erased and it will disappear from model selectors. Chats and usage history are kept." -> "Kaydedilmiş API anahtarı silinir ve sağlayıcı model seçicilerden kaybolur. Sohbetler ve kullanım geçmişi korunur."
        "Keep in background" -> "Arka planda tut"
        "Keep the partial answer" -> "Kısmi yanıtı koru"
        "Keep Xylune open" -> "Xylune'u açık tut"
        "Known calculated cost" -> "Bilinen hesaplanmış maliyet"
        "Last request/answer pairs" -> "Son istek/yanıt çiftleri"
        "Layout / measure" -> "Yerleşim / ölçüm"
        "License catalog unavailable" -> "Lisans kataloğu kullanılamıyor"
        "Linux command" -> "Linux komutu"
        "Linux execution" -> "Linux çalıştırma"
        "Linux package request" -> "Linux paket isteği"
        "Linux terminal" -> "Linux terminali"
        "Linux workspace" -> "Linux çalışma alanı"
        "Linux workspace not installed" -> "Linux çalışma alanı yüklü değil"
        "Linux workspace not ready" -> "Linux çalışma alanı hazır değil"
        "Linux · Not installed" -> "Linux · Yüklü değil"
        "Live output updates as the process prints." -> "Süreç çıktı verdikçe canlı çıktı güncellenir."
        "Live previews" -> "Canlı önizlemeler"
        "Loading preview…" -> "Önizleme yükleniyor…"
        "Loading usage…" -> "Kullanım yükleniyor…"
        "Local Code Execution" -> "Yerel Kod Çalıştırma"
        "Local code execution" -> "Yerel kod çalıştırma"
        "Local description" -> "Yerel açıklama"
        "Local execution starts off; enable Python or Linux only when a chat needs it." -> "Yerel çalıştırma kapalı başlar; Python veya Linux'u yalnızca bir sohbet gerektiğinde etkinleştirin."
        "Local image description" -> "Yerel görsel açıklaması"
        else -> null
    }

    private fun exact5(text: String): String? = when (text) {
        "Local server" -> "Yerel sunucu"
        "Manage Linux workspace" -> "Linux çalışma alanını yönet"
        "Maximum supported reasoning" -> "Desteklenen maksimum akıl yürütme"
        "Message actions" -> "Mesaj işlemleri"
        "Message Xylune…" -> "Xylune'a mesaj yaz…"
        "Messages, code, and reasoning are searched locally as you type." -> "Yazdıkça mesajlar, kod ve akıl yürütme yerel olarak aranır."
        "Mixed / unattributed frame work" -> "Karma / kaynağı belirlenemeyen kare işi"
        "Model capabilities and request transport are selected automatically by this provider preset." -> "Model yetenekleri ve istek aktarımı bu sağlayıcı ön ayarı tarafından otomatik seçilir."
        "Model provider" -> "Model sağlayıcısı"
        "Model receives OCR fallback • original preview is unchanged" -> "Model OCR yedeğini alır • özgün önizleme değişmez"
        "Model refresh failed" -> "Model yenileme başarısız"
        "Model review is advisory and can be wrong. Xylune records the selected model's allow/deny reason, but this is not malware analysis or a security guarantee." -> "Model incelemesi yalnızca tavsiye niteliğindedir ve yanlış olabilir. Xylune seçili modelin izin/verme nedenini kaydeder; bu bir kötü amaçlı yazılım analizi veya güvenlik garantisi değildir."
        "Models discovered for this ChatGPT account only." -> "Yalnızca bu ChatGPT hesabı için bulunan modeller."
        "More thorough reasoning" -> "Daha kapsamlı akıl yürütme"
        "My DeepSeek account" -> "DeepSeek hesabım"
        "Native Android BYOK model workspace." -> "Yerel Android BYOK model çalışma alanı."
        "Native compatibility warning:" -> "Yerel uyumluluk uyarısı:"
        "Next branch" -> "Sonraki dal"
        "Next extracted-text page" -> "Sonraki çıkarılan metin sayfası"
        "Next page" -> "Sonraki sayfa"
        "Next text page" -> "Sonraki metin sayfası"
        "Nextcloud / WebDAV" -> "Nextcloud / WebDAV"
        "No additional diagnostic text was returned by the provider." -> "Sağlayıcı ek tanılama metni döndürmedi."
        "No backup file selected." -> "Yedek dosyası seçilmedi."
        "No chat workspace is available" -> "Kullanılabilir sohbet çalışma alanı yok"
        "No context was compressed" -> "Hiçbir bağlam sıkıştırılmadı"
        "No conversation" -> "Sohbet yok"
        "No inline preview for this file type" -> "Bu dosya türü için satır içi önizleme yok"
        "No matches" -> "Eşleşme yok"
        "No matching chat models" -> "Eşleşen sohbet modeli yok"
        "No matching components" -> "Eşleşen bileşen yok"
        "No matching image models" -> "Eşleşen görsel modeli yok"
        "No output." -> "Çıktı yok."
        "No package transaction running." -> "Çalışan paket işlemi yok."
        "No password: backup remains readable to anyone who gets the file. This is allowed; choose a trusted destination." -> "Parola yok: dosyayı alan herkes yedeği okuyabilir. Buna izin verilir; güvenilir bir hedef seçin."
        "No password: the recipient can open the file immediately. This is allowed, but anyone with the file can read the included content." -> "Parola yok: alıcı dosyayı hemen açabilir. Buna izin verilir; ancak dosyaya sahip olan herkes içerdiği verileri okuyabilir."
        "No per-call usage rows were recorded for this response. The aggregate values above are still available." -> "Bu yanıt için çağrı bazında kullanım satırı kaydedilmedi. Yukarıdaki toplam değerler yine kullanılabilir."
        "No result details" -> "Sonuç ayrıntısı yok"
        "No search results were returned." -> "Arama sonucu döndürülmedi."
        "Non-cached input" -> "Önbelleğe alınmamış girdi"
        "Not connected yet" -> "Henüz bağlı değil"
        "Not embedded" -> "Gömülü değil"
        "not found" -> "bulunamadı"
        "OCR fallback enabled" -> "OCR yedeği etkin"
        "OCR on" -> "OCR açık"
        "OCR on send" -> "Gönderimde OCR"
        "OCR ready" -> "OCR hazır"
        "Offline dependency catalog and full license texts" -> "Çevrimdışı bağımlılık kataloğu ve tam lisans metinleri"
        else -> null
    }

    private fun exact6(text: String): String? = when (text) {
        "One package requirement per line. Xylune resolves Android-compatible Python 3.12 wheels before applying your approval policy." -> "Satır başına bir paket gereksinimi. Xylune onay politikanızı uygulamadan önce Android uyumlu Python 3.12 wheel paketlerini çözümler."
        "One-tap native OAuth. Xylune opens the system browser, receives the localhost callback itself, encrypts the session on this device, and refreshes it automatically. No extension or local proxy is required." -> "Tek dokunuşla yerel OAuth. Xylune sistem tarayıcısını açar, localhost geri çağrısını kendisi alır, oturumu bu cihazda şifreler ve otomatik yeniler. Eklenti veya yerel proxy gerekmez."
        "OneDrive Apps/Xylune" -> "OneDrive Apps/Xylune"
        "Only missing or outdated packages will be changed. Packages and install scripts run with Xylune's app permissions." -> "Yalnızca eksik veya eski paketler değiştirilir. Paketler ve kurulum betikleri Xylune'un uygulama izinleriyle çalışır."
        "Only missing or outdated packages will change. Install scripts run with Xylune's app permissions." -> "Yalnızca eksik veya eski paketler değişir. Kurulum betikleri Xylune'un uygulama izinleriyle çalışır."
        "Only this page is held in memory. Save or share the file for full-file processing." -> "Yalnızca bu sayfa bellekte tutulur. Dosyanın tamamını işlemek için kaydedin veya paylaşın."
        "Open conversations" -> "Sohbetleri aç"
        "Open full-screen preview" -> "Tam ekran önizlemeyi aç"
        "Open navigation drawer" -> "Gezinme çekmecesini aç"
        "Open OCR view" -> "OCR görünümünü aç"
        "Output tokens" -> "Çıktı token'ları"
        "Package changes" -> "Paket değişiklikleri"
        "Package installation" -> "Paket yükleme"
        "Package manager" -> "Paket yöneticisi"
        "Package review" -> "Paket incelemesi"
        "Password" -> "Parola"
        "Password (optional)" -> "Parola (isteğe bağlı)"
        "Pause" -> "Duraklat"
        "Paused" -> "Duraklatıldı"
        "Preview" -> "Önizleme"
        "Previous branch" -> "Önceki dal"
        "Previous page" -> "Önceki sayfa"
        "Provider credentials" -> "Sağlayıcı kimlik bilgileri"
        "Provider settings" -> "Sağlayıcı ayarları"
        "Python execution" -> "Python çalıştırma"
        "Python package request" -> "Python paket isteği"
        "Reasoning" -> "Akıl yürütme"
        "Reasoning details" -> "Akıl yürütma ayrıntıları"
        "Reconnect" -> "Yeniden bağlan"
        "Remove provider?" -> "Sağlayıcı kaldırılsın mı?"
        "Repair" -> "Onar"
        "Repair failed" -> "Onarım başarısız"
        "Restore complete" -> "Geri yükleme tamamlandı"
        "Restore failed" -> "Geri yükleme başarısız"
        "Restore preview" -> "Geri yükleme önizlemesi"
        "Run" -> "Çalıştır"
        "Run Python" -> "Python çalıştır"
        "Safe mode" -> "Güvenli mod"
        "Search results" -> "Arama sonuçları"
        "Send" -> "Gönder"
        "Sending…" -> "Gönderiliyor…"
        "Server folder" -> "Sunucu klasörü"
        "Setup details" -> "Kurulum ayrıntıları"
        "Show attempts" -> "Denemeleri göster"
        "Show OCR overlay" -> "OCR katmanını göster"
        "Source" -> "Kaynak"
        "System prompt" -> "Sistem istemi"
        "Try again" -> "Tekrar dene"
        else -> null
    }

    private fun exact7(text: String): String? = when (text) {
        "Usage details" -> "Kullanım ayrıntıları"
        "Verify" -> "Doğrula"
        "Web page" -> "Web sayfası"
        "Workspace" -> "Çalışma alanı"
        else -> null
    }

    fun translate(text: String): String {
        exactLookup(text)?.let { return it }

        Regex("""Connect (.+)""").matchEntire(text)?.let { return "${it.groupValues[1]} bağlantısını kur" }
        Regex("""Connected (.+) • (\d+) models available""").matchEntire(text)?.let {
            return "${it.groupValues[1]} bağlandı • ${it.groupValues[2]} model kullanılabilir"
        }
        Regex("""Connected to (.+), but no Xylune backups were found\.""").matchEntire(text)?.let {
            return "${it.groupValues[1]} bağlantısı kuruldu ancak Xylune yedeği bulunamadı."
        }
        Regex("""Could not open (.+) sign-in""").matchEntire(text)?.let { return "${it.groupValues[1]} oturum açma sayfası açılamadı" }
        Regex("""Could not read (.+) backups""").matchEntire(text)?.let { return "${it.groupValues[1]} yedekleri okunamadı" }
        Regex("""Could not send: (.+)""").matchEntire(text)?.let { return "Gönderilemedi: ${it.groupValues[1]}" }
        Regex("""Could not compile this (.+) after (\d+) repair attempts\.""").matchEntire(text)?.let {
            return "Bu ${it.groupValues[1]}, ${it.groupValues[2]} onarım denemesinden sonra derlenemedi."
        }
        Regex("""Describe what to create, or add up to (\d+) reference images to edit\.""").matchEntire(text)?.let {
            return "Oluşturmak istediğinizi açıklayın veya düzenlemek için en fazla ${it.groupValues[1]} referans görsel ekleyin."
        }
        Regex("""Image requests cannot include ordinary files\. Remove the non-image attachments?""").matchEntire(text)?.let {
            return "Görsel istekleri normal dosya içeremez. Görsel olmayan ekleri kaldırın."
        }
        Regex("""Found (\d+) Xylune backups?""").matchEntire(text)?.let { return "${it.groupValues[1]} Xylune yedeği bulundu" }
        Regex("""Installed (.+)""").matchEntire(text)?.let { return "Yüklendi: ${it.groupValues[1]}" }
        Regex("""Installed and import-verified:? (.+)""").matchEntire(text)?.let { return "Yüklendi ve içe aktarma doğrulandı: ${it.groupValues[1]}" }
        Regex("""Downloading (.+)…""").matchEntire(text)?.let { return "${it.groupValues[1]} indiriliyor…" }
        Regex("""Installation blocked • (.+)""").matchEntire(text)?.let { return "Yükleme engellendi • ${it.groupValues[1]}" }
        Regex("""Installation denied • (.+)""").matchEntire(text)?.let { return "Yükleme reddedildi • ${it.groupValues[1]}" }
        Regex("""Linux data on disk: (.+)""").matchEntire(text)?.let { return "Diskteki Linux verisi: ${it.groupValues[1]}" }
        Regex("""OCR could not read (.+); the original file is still attached""").matchEntire(text)?.let {
            return "OCR ${it.groupValues[1]} dosyasını okuyamadı; özgün dosya hâlâ ekli"
        }
        Regex("""OCR fallback is ready for (.+)""").matchEntire(text)?.let { return "${it.groupValues[1]} için OCR yedeği hazır" }
        Regex("""Environment (.+)""").matchEntire(text)?.let { return "Ortam ${it.groupValues[1]}" }
        Regex("""Exit (\d+)""").matchEntire(text)?.let { return "Çıkış ${it.groupValues[1]}" }
        Regex("""Elapsed (.+)""").matchEntire(text)?.let { return "Geçen süre ${it.groupValues[1]}" }
        Regex("""in (\d+)d""").matchEntire(text)?.let { return "${it.groupValues[1]} gün sonra" }
        Regex("""in (\d+)h""").matchEntire(text)?.let { return "${it.groupValues[1]} saat sonra" }
        Regex("""in (\d+)m""").matchEntire(text)?.let { return "${it.groupValues[1]} dakika sonra" }
        Regex("""in (\d+)s""").matchEntire(text)?.let { return "${it.groupValues[1]} saniye sonra" }
        Regex("""in (\d+)d (\d+)h""").matchEntire(text)?.let { return "${it.groupValues[1]} gün ${it.groupValues[2]} saat sonra" }
        Regex("""in (\d+)h (\d+)m""").matchEntire(text)?.let { return "${it.groupValues[1]} saat ${it.groupValues[2]} dakika sonra" }
        Regex("""in (\d+)m (\d+)s""").matchEntire(text)?.let { return "${it.groupValues[1]} dakika ${it.groupValues[2]} saniye sonra" }
        Regex("""Open (.+) terminal""").matchEntire(text)?.let { return "${it.groupValues[1]} terminalini aç" }
        Regex("""Install (.+) packages""").matchEntire(text)?.let { return "${it.groupValues[1]} paketlerini yükle" }
        Regex("""Install (.+)""").matchEntire(text)?.let { return "${it.groupValues[1]} yükle" }
        Regex("""Delete (.+)""").matchEntire(text)?.let { return "${it.groupValues[1]} sil" }
        Regex("""Edit (.+)""").matchEntire(text)?.let { return "${it.groupValues[1]} düzenle" }
        Regex("""Downloaded: (.+)""").matchEntire(text)?.let { return "İndirildi: ${it.groupValues[1]}" }
        Regex("""Download: (.+)""").matchEntire(text)?.let { return "İndirme: ${it.groupValues[1]}" }
        Regex("""Disk: (.+)""").matchEntire(text)?.let { return "Disk: ${it.groupValues[1]}" }
        return text
    }
}

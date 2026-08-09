package app.xylune.chat.ui

/**
 * Turkish formatting for Xylune-owned UI strings that contain runtime values.
 * Variable payloads such as provider/model names, file names, URLs and errors are preserved verbatim.
 */
internal object TurkishDynamicUiCopy {
    fun translate(text: String): String {
        fun repoLabel(value: String): String = if (value == "the build repository") "derleme deposu" else value

        Regex("""Chat · (\d+)""").matchEntire(text)?.let { return "Sohbet · ${it.groupValues[1]}" }
        Regex("""Images · (\d+)""").matchEntire(text)?.let { return "Görseller · ${it.groupValues[1]}" }
        Regex("""(\d+) starred""").matchEntire(text)?.let { return "${it.groupValues[1]} favori" }
        Regex("""(\d+) configured providers?""").matchEntire(text)?.let { return "${it.groupValues[1]} yapılandırılmış sağlayıcı" }
        Regex("""(\d+) results?(?: · (\d+) filters?)?""").matchEntire(text)?.let {
            val filters = it.groupValues[2].takeIf(String::isNotBlank)?.let { count -> " · $count filtre" }.orEmpty()
            return "${it.groupValues[1]} sonuç$filters"
        }
        Regex("""up to (\d+) reference images?""").matchEntire(text)?.let { return "en fazla ${it.groupValues[1]} referans görsel" }
        Regex("""([0-9]+(?:\.[0-9]+)?[KM]?) context""").matchEntire(text)?.let { return "${it.groupValues[1]} bağlam" }
        Regex("""([0-9]+(?:\.[0-9]+)?[KM]?) output""").matchEntire(text)?.let { return "${it.groupValues[1]} çıktı" }

        Regex("""Updates are checked against (.+)\.""").matchEntire(text)?.let {
            return "Güncellemeler ${repoLabel(it.groupValues[1])} üzerinden kontrol edilir."
        }
        Regex("""Checking (.+)…""").matchEntire(text)?.let { return "${repoLabel(it.groupValues[1])} kontrol ediliyor…" }
        Regex("""Latest release: (.+) · checked (.+)""").matchEntire(text)?.let {
            return "En son sürüm: ${it.groupValues[1]} · kontrol: ${it.groupValues[2]}"
        }
        Regex("""Xylune (.+) is available""").matchEntire(text)?.let { return "Xylune ${it.groupValues[1]} kullanılabilir" }
        Regex("""Source: (.+)""").matchEntire(text)?.let { return "Kaynak: ${it.groupValues[1]}" }

        Regex("""PDF preview unavailable: (.+)""").matchEntire(text)?.let { return "PDF önizlemesi kullanılamıyor: ${it.groupValues[1]}" }
        Regex("""PDF page (\d+)""").matchEntire(text)?.let { return "PDF sayfası ${it.groupValues[1]}" }
        Regex("""Page (\d+) of (\d+)""").matchEntire(text)?.let { return "${it.groupValues[2]} sayfanın ${it.groupValues[1]}. sayfası" }
        Regex("""Preview unavailable: (.+)""").matchEntire(text)?.let { return "Önizleme kullanılamıyor: ${it.groupValues[1]}" }
        Regex("""Bytes (.+) of (.+)""").matchEntire(text)?.let { return "${it.groupValues[2]} baytın ${it.groupValues[1]} aralığı" }

        Regex("""(.+) core prompt only""").matchEntire(text)?.let { return "Yalnızca ${it.groupValues[1]} çekirdek istemi" }
        Regex("""(.+)'s built-in core prompt is versioned with the app and cannot be edited\. Create reusable custom profiles in Settings\.""").matchEntire(text)?.let {
            return "${it.groupValues[1]} uygulamasının yerleşik çekirdek istemi uygulamayla birlikte sürümlenir ve düzenlenemez. Ayarlar'da yeniden kullanılabilir özel profiller oluşturabilirsiniz."
        }
        Regex("""(\d+) older messages • about (\d+) tokens""").matchEntire(text)?.let {
            return "${it.groupValues[1]} eski mesaj • yaklaşık ${it.groupValues[2]} token"
        }
        Regex("""Completed in (.+)""").matchEntire(text)?.let { return "${it.groupValues[1]} içinde tamamlandı" }
        Regex("""Run failed with exit code (\d+)""").matchEntire(text)?.let { return "Çalıştırma ${it.groupValues[1]} çıkış koduyla başarısız oldu" }
        Regex("""Working · (\d+) queued""").matchEntire(text)?.let { return "Çalışıyor · ${it.groupValues[1]} sırada" }
        Regex("""(\d+) queued""").matchEntire(text)?.let { return "${it.groupValues[1]} sırada" }
        Regex("""Use the (.+) tooling workspace""").matchEntire(text)?.let { return "${it.groupValues[1]} araç çalışma alanını kullan" }
        Regex("""FILE • (.+)""").matchEntire(text)?.let { return "DOSYA • ${it.groupValues[1]}" }
        Regex("""(.+) · attempt (\d+) · revision (.+)""").matchEntire(text)?.let {
            return "${it.groupValues[1]} · deneme ${it.groupValues[2]} · revizyon ${it.groupValues[3]}"
        }
        Regex("""(.+) • lines (\d+)–(\d+)""").matchEntire(text)?.let {
            return "${it.groupValues[1]} • satırlar ${it.groupValues[2]}–${it.groupValues[3]}"
        }
        Regex("""Chat renamed to “(.+)”""").matchEntire(text)?.let { return "Sohbet “${it.groupValues[1]}” olarak yeniden adlandırıldı" }

        Regex("""Connected: (.+)""").matchEntire(text)?.let { return "Bağlı: ${it.groupValues[1]}" }
        Regex("""(.+) will be permanently removed from its cloud provider\.""").matchEntire(text)?.let {
            return "${it.groupValues[1]} bulut sağlayıcısından kalıcı olarak kaldırılacak."
        }
        Regex("""(.+) will be permanently deleted from (.+)\.""").matchEntire(text)?.let {
            return "${it.groupValues[1]}, ${it.groupValues[2]} üzerinden kalıcı olarak silinecek."
        }
        Regex("""Build variable: (.+)""").matchEntire(text)?.let { return "Derleme değişkeni: ${it.groupValues[1]}" }
        Regex("""Redirect URI: (.+)""").matchEntire(text)?.let { return "Yönlendirme URI'si: ${it.groupValues[1]}" }
        Regex("""Connect (.+)""").matchEntire(text)?.let { return "${it.groupValues[1]} sağlayıcısına bağlan" }
        Regex("""Waiting for (.+) authorization…""").matchEntire(text)?.let { return "${it.groupValues[1]} yetkilendirmesi bekleniyor…" }
        Regex("""Configure (.+)""").matchEntire(text)?.let { return "${it.groupValues[1]} yapılandır" }

        Regex("""Compiled and tested (.+)""").matchEntire(text)?.let { return "${it.groupValues[1]} derlendi ve test edildi" }
        Regex("""AI rebuilt and compiled (.+) · (\d+) attempts?""").matchEntire(text)?.let {
            return "Yapay zekâ ${it.groupValues[1]} öğesini yeniden oluşturup derledi · ${it.groupValues[2]} deneme"
        }
        Regex("""Edit (.+) source""").matchEntire(text)?.let { return "${it.groupValues[1]} kaynağını düzenle" }
        Regex("""Compiling and testing (.+)…""").matchEntire(text)?.let { return "${it.groupValues[1]} derleniyor ve test ediliyor…" }
        Regex("""Rebuilding (.+) from compiler feedback… attempt (\d+) of (\d+)""").matchEntire(text)?.let {
            return "${it.groupValues[1]} derleyici geri bildiriminden yeniden oluşturuluyor… ${it.groupValues[3]} denemenin ${it.groupValues[2]}. denemesi"
        }
        Regex("""Preparing another (.+) build…""").matchEntire(text)?.let { return "Yeni bir ${it.groupValues[1]} derlemesi hazırlanıyor…" }
        Regex("""Could not compile this (.+) after (\d+) repair attempts\.""").matchEntire(text)?.let {
            return "Bu ${it.groupValues[1]} ${it.groupValues[2]} onarım denemesinden sonra derlenemedi."
        }
        Regex("""Attempt (\d+) · (.*?)(\d+) error\(s\)""").matchEntire(text)?.let {
            return "Deneme ${it.groupValues[1]} · ${it.groupValues[2]}${it.groupValues[3]} hata"
        }

        Regex("""(\d+) image requests? queued""").matchEntire(text)?.let { return "${it.groupValues[1]} görsel isteği sırada" }
        Regex("""(.+) accepts up to (\d+) reference images\.""").matchEntire(text)?.let {
            return "${it.groupValues[1]} en fazla ${it.groupValues[2]} referans görsel kabul eder."
        }
        Regex("""(.+) requires at least one reference image\.""").matchEntire(text)?.let { return "${it.groupValues[1]} en az bir referans görsel gerektirir." }
        Regex("""(\d+) reference images? · describe the changes you want\.""").matchEntire(text)?.let {
            return "${it.groupValues[1]} referans görsel · istediğiniz değişiklikleri açıklayın."
        }
        Regex("""Image requests cannot include ordinary files\. Remove the non-image attachments? first\.""").matchEntire(text)?.let {
            return "Görsel istekleri normal dosyalar içeremez. Önce görsel olmayan eki kaldırın."
        }
        Regex("""(\d+) of (\d+) components""").matchEntire(text)?.let { return "${it.groupValues[2]} bileşenden ${it.groupValues[1]}" }

        Regex("""Root shell • (.+) • /workspace""").matchEntire(text)?.let { return "Root kabuğu • ${it.groupValues[1]} • /workspace" }
        Regex("""exit (\d+) • (\d+) ms""").matchEntire(text)?.let { return "çıkış ${it.groupValues[1]} • ${it.groupValues[2]} ms" }
        Regex("""(.+) root terminal\nCommands run as uid 0 inside the selected PRoot distribution\.""").matchEntire(text)?.let {
            return "${it.groupValues[1]} root terminali\nKomutlar seçili PRoot dağıtımında uid 0 olarak çalışır."
        }

        Regex("""(.+) paused for crash recovery""").matchEntire(text)?.let { return "${it.groupValues[1]} çökme kurtarması için duraklatıldı" }
        Regex("""(\d+) packages resolved • (\d+) changes(?: • (\d+) dependencies)?""").matchEntire(text)?.let {
            val deps = it.groupValues[3].takeIf(String::isNotBlank)?.let { d -> " • $d bağımlılık" }.orEmpty()
            return "${it.groupValues[1]} paket çözümlendi • ${it.groupValues[2]} değişiklik$deps"
        }
        Regex("""Download: (.+)""").matchEntire(text)?.let { return "İndirme: ${it.groupValues[1]}" }
        Regex("""Disk: (.+)""").matchEntire(text)?.let { return "Disk: ${it.groupValues[1]}" }

        Regex("""Python (.+) · (\d+) packages · (.+)""").matchEntire(text)?.let {
            return "Python ${it.groupValues[1]} · ${it.groupValues[2]} paket · ${it.groupValues[3]}"
        }
        Regex("""Python (.+) • (\d+) packages • (.+)""").matchEntire(text)?.let {
            return "Python ${it.groupValues[1]} • ${it.groupValues[2]} paket • ${it.groupValues[3]}"
        }
        Regex("""(.+) (.+) · Ready""").matchEntire(text)?.let { return "${it.groupValues[1]} ${it.groupValues[2]} · Hazır" }
        Regex("""Environment (.+)""").matchEntire(text)?.let { return "Ortam ${it.groupValues[1]}" }
        Regex("""Remove (.+)\?""").matchEntire(text)?.let { return "${it.groupValues[1]} kaldırılsın mı?" }
        Regex("""Running in the background • (.+)s • you can browse other chats""").matchEntire(text)?.let {
            return "Arka planda çalışıyor • ${it.groupValues[1]} sn • diğer sohbetlere göz atabilirsiniz"
        }
        Regex("""Linux data on disk: (.+)""").matchEntire(text)?.let { return "Diskteki Linux verisi: ${it.groupValues[1]}" }
        Regex("""Step (\d+) of (\d+)""").matchEntire(text)?.let { return "${it.groupValues[2]} adımın ${it.groupValues[1]}. adımı" }
        Regex("""Elapsed (.+)""").matchEntire(text)?.let { return "Geçen süre: ${it.groupValues[1]}" }
        Regex("""Install (.+) terminal""").matchEntire(text)?.let { return "${it.groupValues[1]} terminalini yükle" }
        Regex("""Open (.+) terminal""").matchEntire(text)?.let { return "${it.groupValues[1]} terminalini aç" }
        Regex("""Install (.+) packages""").matchEntire(text)?.let { return "${it.groupValues[1]} paketlerini yükle" }
        Regex("""Simulating (.+) transaction…""").matchEntire(text)?.let { return "${it.groupValues[1]} işlemi simüle ediliyor…" }
        Regex("""(.+) install failed""").matchEntire(text)?.let { return "${it.groupValues[1]} yüklemesi başarısız oldu" }
        Regex("""Installed (.+)""").matchEntire(text)?.let { return "Yüklendi: ${it.groupValues[1]}" }
        Regex("""Allow (.+) package changes\?""").matchEntire(text)?.let { return "${it.groupValues[1]} paket değişikliklerine izin verilsin mi?" }
        Regex("""This is the complete (.+) simulation, including dependencies:""").matchEntire(text)?.let {
            return "Bağımlılıklar dahil tam ${it.groupValues[1]} simülasyonu:"
        }
        Regex("""(.+) will remove this Python package from the current chat environment\. Other packages and the optional Linux runtime are kept\.""").matchEntire(text)?.let {
            return "${it.groupValues[1]} bu Python paketini geçerli sohbet ortamından kaldıracak. Diğer paketler ve isteğe bağlı Linux çalışma zamanı korunur."
        }
        Regex("""This has taken (\d+) seconds\. It will keep running while you browse Xylune, up to its hard deadline\. You can leave it in the background or stop it now\. A blocking native Python extension may take a moment to return after Stop\.""").matchEntire(text)?.let {
            return "Bu işlem ${it.groupValues[1]} saniyedir sürüyor. Kesin süre sınırına kadar Xylune'da gezinirken çalışmaya devam eder. Arka planda bırakabilir veya şimdi durdurabilirsiniz. Engelleyici bir yerel Python uzantısının Durdur'dan sonra dönmesi kısa bir süre alabilir."
        }

        Regex("""Delete (\d+) disabled memories?""").matchEntire(text)?.let { return "${it.groupValues[1]} devre dışı hafızayı sil" }
        Regex("""Delete (\d+) memories\?""").matchEntire(text)?.let { return "${it.groupValues[1]} hafıza silinsin mi?" }
        Regex("""Choose whether (.+) follows Android or stays light or dark\.""").matchEntire(text)?.let {
            return "${it.groupValues[1]} Android'i izlesin mi yoksa açık ya da koyu modda mı kalsın seçin."
        }
        Regex("""(.+)'s green Material palette""").matchEntire(text)?.let { return "${it.groupValues[1]} uygulamasının yeşil Material paleti" }
        Regex("""Edit (.+)""").matchEntire(text)?.let { return "Düzenle: ${it.groupValues[1]}" }
        Regex("""Delete (.+)""").matchEntire(text)?.let { return "Sil: ${it.groupValues[1]}" }
        Regex("""(.+ \d[^ ]*) installed""").matchEntire(text)?.let { return "${it.groupValues[1]} yüklü" }
        Regex("""Managed by Xylune · revision (.+)""").matchEntire(text)?.let { return "Xylune tarafından yönetiliyor · revizyon ${it.groupValues[1]}" }
        Regex("""Automatically updated metadata for (\d+) models""").matchEntire(text)?.let { return "${it.groupValues[1]} modelin üst verileri otomatik güncellendi" }
        Regex("""Automatic metadata refresh failed: (.+)""").matchEntire(text)?.let { return "Otomatik üst veri yenileme başarısız: ${it.groupValues[1]}" }
        Regex("""Updated (\d+) models""").matchEntire(text)?.let { return "${it.groupValues[1]} model güncellendi" }
        Regex("""(.+) connection""").matchEntire(text)?.let { return "${it.groupValues[1]} bağlantısı" }
        Regex("""ChatGPT account (\d+)""").matchEntire(text)?.let { return "ChatGPT hesabı ${it.groupValues[1]}" }
        Regex("""Connected • (.+)""").matchEntire(text)?.let { return "Bağlı • ${it.groupValues[1]}" }
        Regex("""(.+) plan • reported by ChatGPT""").matchEntire(text)?.let { return "${it.groupValues[1]} planı • ChatGPT tarafından bildirildi" }
        Regex("""(.+) • secondary""").matchEntire(text)?.let { return "${it.groupValues[1]} • ikincil" }
        Regex("""Credits balance: (.+)""").matchEntire(text)?.let { return "Kredi bakiyesi: ${it.groupValues[1]}" }
        Regex("""Limit reached: (.+)""").matchEntire(text)?.let { return "Sınıra ulaşıldı: ${it.groupValues[1]}" }
        Regex("""Refresh failed • (.+)""").matchEntire(text)?.let { return "Yenileme başarısız • ${it.groupValues[1]}" }
        Regex("""(\d+)-day limit""").matchEntire(text)?.let { return "${it.groupValues[1]} günlük sınır" }
        Regex("""(\d+)-hour limit""").matchEntire(text)?.let { return "${it.groupValues[1]} saatlik sınır" }
        Regex("""in (\d+)d(?: (\d+)h)?""").matchEntire(text)?.let {
            val hours = it.groupValues[2].takeIf(String::isNotBlank)?.let { h -> " $h saat" }.orEmpty()
            return "${it.groupValues[1]} gün$hours sonra"
        }
        Regex("""in (\d+)h(?: (\d+)m)?""").matchEntire(text)?.let {
            val minutes = it.groupValues[2].takeIf(String::isNotBlank)?.let { m -> " $m dk" }.orEmpty()
            return "${it.groupValues[1]} saat$minutes sonra"
        }
        Regex("""in (\d+)m(?: (\d+)s)?""").matchEntire(text)?.let {
            val seconds = it.groupValues[2].takeIf(String::isNotBlank)?.let { s -> " $s sn" }.orEmpty()
            return "${it.groupValues[1]} dk$seconds sonra"
        }
        Regex("""in (\d+)s""").matchEntire(text)?.let { return "${it.groupValues[1]} sn sonra" }
        Regex("""resets (.+) • (.+)""").matchEntire(text)?.let { return "${translate(it.groupValues[1])} sıfırlanır • ${it.groupValues[2]}" }
        Regex("""Preset: (.+)""").matchEntire(text)?.let { return "Ön ayar: ${it.groupValues[1]}" }
        Regex("""Protocol: (.+)""").matchEntire(text)?.let { return "Protokol: ${it.groupValues[1]}" }
        Regex("""Search (\d+) models""").matchEntire(text)?.let { return "${it.groupValues[1]} modelde ara" }
        Regex("""(.+) models""").matchEntire(text)?.let {
            val prefix = it.groupValues[1]
            if (prefix != "Search" && prefix.toIntOrNull() == null) return "$prefix modelleri"
        }
        Regex("""(\d+) of (\d+) selected""").matchEntire(text)?.let { return "${it.groupValues[2]} modelden ${it.groupValues[1]} seçili" }
        Regex("""Showing the first (\d+) matches\. Search to find a specific model; all selected models will still be saved\.""").matchEntire(text)?.let {
            return "İlk ${it.groupValues[1]} eşleşme gösteriliyor. Belirli bir modeli bulmak için arayın; seçilen tüm modeller yine kaydedilecektir."
        }
        Regex("""(\d+) available for (.+) · metadata refreshes with the catalog""").matchEntire(text)?.let {
            return "${it.groupValues[2]} için ${it.groupValues[1]} model kullanılabilir · üst veriler katalogla yenilenir"
        }
        Regex("""Showing (\d+) of (\d+)\. Search or filter to narrow the catalog\.""").matchEntire(text)?.let {
            return "${it.groupValues[2]} modelden ${it.groupValues[1]} tanesi gösteriliyor. Kataloğu daraltmak için arayın veya filtreleyin."
        }
        Regex("""(.+) metadata""").matchEntire(text)?.let { return "${it.groupValues[1]} üst verisi" }
        Regex("""(.+) credential""").matchEntire(text)?.let { return "${it.groupValues[1]} kimlik bilgisi" }
        Regex("""Continue from step (\d+) of (\d+)""").matchEntire(text)?.let { return "${it.groupValues[2]} adımın ${it.groupValues[1]}. adımından devam et" }
        Regex("""(\d+) providers? configured""").matchEntire(text)?.let { return "${it.groupValues[1]} sağlayıcı yapılandırıldı" }
        Regex("""(\d+) saved items?; (\d+) enabled\.""").matchEntire(text)?.let { return "${it.groupValues[1]} kayıtlı öğe; ${it.groupValues[2]} etkin." }
        Regex("""(\d+) selected""").matchEntire(text)?.let { return "${it.groupValues[1]} seçili" }

        Regex("""Connected to (.+), but no Xylune backups were found\.""").matchEntire(text)?.let {
            return "${it.groupValues[1]} bağlantısı kuruldu ancak Xylune yedeği bulunamadı."
        }
        Regex("""Found (\d+) Xylune backups? in (.+)\.""").matchEntire(text)?.let {
            return "${it.groupValues[2]} içinde ${it.groupValues[1]} Xylune yedeği bulundu."
        }

        Regex("""(.+) ms of (.+) ms frame budget""").matchEntire(text)?.let { return "${it.groupValues[1]} ms / ${it.groupValues[2]} ms kare bütçesi" }
        Regex("""Render (.+) fps  (.+) ms  J (.+)%""").matchEntire(text)?.let { return "İşleme ${it.groupValues[1]} fps  ${it.groupValues[2]} ms  Takılma ${it.groupValues[3]}%" }
        Regex("""Display (.+) Hz  Callback (.+)/s  Present (.+)""").matchEntire(text)?.let { return "Ekran ${it.groupValues[1]} Hz  Geri çağrı ${it.groupValues[2]}/sn  Sunum ${it.groupValues[3]}" }
        Regex("""FM avg (.+) ms  p95 (.+)  p99 (.+)""").matchEntire(text)?.let { return "FM ort. ${it.groupValues[1]} ms  p95 ${it.groupValues[2]}  p99 ${it.groupValues[3]}" }
        Regex("""Cause (\d+)% · (.+): (.+)""").matchEntire(text)?.let { return "Neden %${it.groupValues[1]} · ${it.groupValues[2]}: ${it.groupValues[3]}" }
        Regex("""Evidence: (.+)""").matchEntire(text)?.let { return "Kanıt: ${it.groupValues[1]}" }
        Regex("""Secondary: (.+)""").matchEntire(text)?.let { return "İkincil: ${it.groupValues[1]}" }
        Regex("""Recomp/s app (.+) chat (.+)""").matchEntire(text)?.let { return "Yeniden oluşturma/sn uygulama ${it.groupValues[1]} sohbet ${it.groupValues[2]}" }
        Regex("""Alloc (.+) MB/s  bGC (.+)  Screen (.+)""").matchEntire(text)?.let { return "Ayırma ${it.groupValues[1]} MB/sn  bGC ${it.groupValues[2]}  Ekran ${it.groupValues[3]}" }

        Regex("""Describe what to create, or add up to (\d+) reference images to edit\.""").matchEntire(text)?.let {
            return "Ne oluşturmak istediğinizi açıklayın veya düzenlemek için en fazla ${it.groupValues[1]} referans görsel ekleyin."
        }
        Regex("""Describe a new image, or add (\d+) reference images? and describe the edit\.""").matchEntire(text)?.let {
            return "Yeni bir görseli açıklayın veya ${it.groupValues[1]} referans görsel ekleyip düzenlemeyi tarif edin."
        }
        Regex("""Provider preview (\d+)(?: of (\d+))? · the final image may still change\.""").matchEntire(text)?.let {
            val total = it.groupValues[2].takeIf(String::isNotBlank)?.let { count -> " / $count" }.orEmpty()
            return "Sağlayıcı önizlemesi ${it.groupValues[1]}$total · son görsel hâlâ değişebilir."
        }

        when (text) {
            "Close model picker" -> return "Model seçiciyi kapat"
            "Stop image generation" -> return "Görsel oluşturmayı durdur"
            "Inspect repair" -> return "Onarımı incele"
            "Clear search" -> return "Aramayı temizle"
            "• cost unavailable" -> return "• maliyet kullanılamıyor"
            "• partial cost" -> return "• kısmi maliyet"
            "2 on" -> return "2 etkin"
            "Thinking" -> return "Düşünme"
            "Tools" -> return "Araçlar"
            "Vision" -> return "Görsel"
            "Files" -> return "Dosyalar"
            "Free" -> return "Ücretsiz"
            "Generate images" -> return "Görsel oluştur"
            "Generate + edit" -> return "Oluştur + düzenle"
            "Edit images" -> return "Görselleri düzenle"
        }

        Regex("""Configured as (.+) • tap to check backups""").matchEntire(text)?.let {
  return "${it.groupValues[1]} olarak yapılandırıldı • yedekleri kontrol etmek için dokunun"
        }
        Regex("""(\d+) chats? • (\d+) messages • (\d+) attachments?(?: • (\d+) Linux environments?)?""").matchEntire(text)?.let {
  val linux = it.groupValues[4].takeIf(String::isNotBlank)?.let { count -> " • $count Linux ortamı" }.orEmpty()
  return "${it.groupValues[1]} sohbet • ${it.groupValues[2]} mesaj • ${it.groupValues[3]} ek$linux"
        }
        Regex("""Created by Xylune (.+) • (encrypted|not encrypted)""").matchEntire(text)?.let {
  val state = if (it.groupValues[2] == "encrypted") "şifreli" else "şifrelenmemiş"
  return "Xylune ${it.groupValues[1]} tarafından oluşturuldu • $state"
        }
        Regex("""Call (\d+) · round (\d+)""").matchEntire(text)?.let { return "Çağrı ${it.groupValues[1]} · tur ${it.groupValues[2]}" }
        Regex("""input (\d+) · cached (\d+) · output (\d+)""").matchEntire(text)?.let {
  return "girdi ${it.groupValues[1]} · önbellek ${it.groupValues[2]} · çıktı ${it.groupValues[3]}"
        }
        Regex("""non-cached (\d+) · total (\d+)""").matchEntire(text)?.let { return "önbelleksiz ${it.groupValues[1]} · toplam ${it.groupValues[2]}" }
        Regex("""(.+) is running in the background • (\d+)s deadline""").matchEntire(text)?.let {
  return "${it.groupValues[1]} arka planda çalışıyor • ${it.groupValues[2]} sn süre sınırı"
        }
        Regex("""(.+) simulates the complete transaction first and checks what is already installed before approval\.""").matchEntire(text)?.let {
  return "${it.groupValues[1]} önce işlemin tamamını simüle eder ve onaydan önce nelerin zaten yüklü olduğunu kontrol eder."
        }
        Regex("""Installed and import-verified (.+)""").matchEntire(text)?.let { return "Yüklendi ve içe aktarım doğrulandı: ${it.groupValues[1]}" }
        Regex("""(.+) → import (.+)""").matchEntire(text)?.let { return "${it.groupValues[1]} → içe aktarım ${it.groupValues[2]}" }
        Regex("""GPU (.+) ms  Miss/s (.+)  Reports (.+)""").matchEntire(text)?.let {
  return "GPU ${it.groupValues[1]} ms  Kaçırma/sn ${it.groupValues[2]}  Rapor ${it.groupValues[3]}"
        }
        Regex("""FM (.+)  L (.+)  D (.+)  Cmd (.+)  Sw (.+)""").matchEntire(text)?.let {
  return "FM ${it.groupValues[1]}  Yerleşim ${it.groupValues[2]}  Çizim ${it.groupValues[3]}  Komut ${it.groupValues[4]}  Takas ${it.groupValues[5]}"
        }
        Regex("""BlurCPU (.+)  (.+) MP/s  srcTrav×(.+) replay×(.+)""").matchEntire(text)?.let {
  return "BulanıklıkCPU ${it.groupValues[1]}  ${it.groupValues[2]} MP/sn  kaynakGez×${it.groupValues[3]} tekrar×${it.groupValues[4]}"
        }
        Regex("""cap/s (.+) fx/s (.+)  levels D(.+)/U(.+)""").matchEntire(text)?.let {
  return "yakalama/sn ${it.groupValues[1]} efekt/sn ${it.groupValues[2]}  düzeyler A${it.groupValues[3]}/Y${it.groupValues[4]}"
        }
        // Technical summaries are assembled from independently localizable UI segments.
        // Split only Xylune's middle-dot separator; provider/model names remain untouched
        // because unknown segments are returned verbatim.
        if (" · " in text) {
            val parts = text.split(" · ")
            val translated = parts.map(::translate)
            if (translated != parts) return translated.joinToString(" · ")
        }

        return text
    }
}

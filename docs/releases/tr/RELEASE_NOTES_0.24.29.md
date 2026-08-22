# Turp 0.24.29

## Turp kimliğinin tamamlanması

- Android paket ve namespace'i, Kotlin/kaynak/sınıf yolları, yerel kütüphane adları, depolama ve yedekleme tanımlayıcıları, derin bağlantılar, MIME türleri, CI değişkenleri, belgeler, Pages yolları ve depo bağlantıları dahil olmak üzere Turp kesin yeniden markalamasını tamamlar.
- Android uygulama kimliğini `app.turp.chat` olarak değiştirir; Turp artık önceki uyumluluk paketini korumak yerine ayrı bir uygulama kimliği kullanır.
- Eski kurulum, tercih, veritabanı, yedekleme, derin bağlantı veya önceki uygulamaya ait diğer tanımlayıcıları bilerek taşımaz ya da korumaz. Eski kurulumlar ve yedekler Turp verisi olarak kabul edilmez.
- Kaynak yollarında veya yerel ikili dosyalar dahil ham dosya baytlarında eski ürün adlarını reddeden depo çapında kimlik regresyon testi ve CI doğrulaması ekler.
- Sürüm/derleme dosyalarını ve kaynak meta verilerini Turp adıyla tutarlı hâle getirir; yeni paketi birim testleri, lint, release derlemesi, imza kontrolleri ve emülatör smoke testleriyle doğrular.

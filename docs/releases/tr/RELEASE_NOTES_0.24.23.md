# Turp 0.24.23

- Turp'un yanlış uygulama sürümünü göstermesi veya buna göre akıl yürütmesi düzeltildi; sürüm adı ve kodu artık öncelikle Android'de gerçekten yüklü olan paket meta verilerinden okunuyor, oluşturulan yapı sabitleri yalnızca yedek olarak kullanılıyor.
- Hakkında/yapı bilgileri, depo güncelleme karşılaştırmaları, güncelleme denetimi User-Agent bilgisi ve taşınabilir arşiv meta verileri artık yüklü paket sürümünü kullanıyor.
- Yapay zekâ çalışma zamanı bağlamına artık gerçekten yüklü Turp uygulama sürümü açıkça aktarılıyor ve bu değer ayrı çekirdek istem revizyonundan özellikle ayrıştırılıyor; böylece istem revizyonunun uygulama sürümü sanılması engelleniyor.
- Sağlayıcı Ekle akışı, büyük bir uyarı iletişim kutusu yerine uyarlanabilir bir Material alt sayfası olarak yeniden düzenlendi.
- Sağlayıcı formu artık bağımsız olarak kaydırılabiliyor; Ekle ve İptal eylemleri erişilebilir kalıyor, klavye ve gezinme çubuğu boşluklarına uyuyor ve çok dar ekranlarda düğmeler dikey olarak yerleşiyor.
- Çalışma zamanı sürümü bağlantıları ve küçük ekran sağlayıcı kurulumu davranışı için regresyon testleri eklendi.

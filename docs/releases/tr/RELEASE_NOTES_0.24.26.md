# Xylune 0.24.26

## Yapısal arayüz temizliği

- Yinelenen Ayarlar ana sayfasını ve 300 ms / 90 ms zamanlama geçici çözümünü kaldırır; Uygulama dili artık mevcut gezinme yapısının yönettiği normal bir Ayarlar rotasıdır.
- Sağlayıcı kataloğundaki 8 saniyelik kaçış zamanlayıcısını, uygulama başlangıcının sahip olduğu açık yükleniyor/hazır/başarısız durumuyla değiştirir.
- Gezinme çekmecesinin görünmez sabit genişlikli bir Ayarlar şeridi ayırmak yerine Android'in gerçek sistem Geri hareketi alanlarına uymasını sağlar.
- Bağlantı önizlemelerini sabit süre beklemek yerine çıkış geçişi gerçekten tamamlandığında kapatır.
- One UI başlatıcı diğer adı kurtarma yolunu korurken başlatıcının sabit 110 ms aktarımını ilk çizim yaşam döngüsü geri çağrısıyla değiştirir.

## Yerelleştirme ve regresyon kapsamı

- Sabit Türkçe arayüz metinlerini Android yerel ayar kaynaklarına taşır; uyumluluk biçimlendiricisi artık yalnızca dinamik/değişken metinler için kullanılır.
- Uygulama dili dahil Ayarlar sayfa başlıklarını kaynak kimlikleri üzerinden yerelleştirir ve yinelenen Türkçe çeviri dallarını kaldırır.
- Eski zamanlama geçici çözümlerini veya tam kaynak kodu yazımını zorunlu tutan regresyon testlerini davranış ve mimari değişmez kapsamıyla değiştirir.

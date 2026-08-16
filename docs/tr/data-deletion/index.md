---
layout: default
lang: tr
alternate_en: /data-deletion/
alternate_tr: /tr/data-deletion/
title: Veri Silme
heading: Veri silme
browser_title: Turp Verilerini Silme — Android BYOK Yapay Zekâ Sohbet Uygulaması
description: Turp'daki yerel verileri, bulut yedeklerini, OAuth erişimini ve sağlayıcılarda tutulan verileri silme adımları.
---

# Turp veri silme

Turp merkezi bir kullanıcı hesabı işletmez. Verilerin çoğu Android cihazda veya kullanıcının seçtiği sağlayıcıda tutulur.

## Yerel verileri silme

- Tekil sohbet, anı, sağlayıcı, taslak veya diğer kayıtları ilgili Turp ekranından silin.
- Tüm yerel verileri kaldırmak için Android'de **Ayarlar → Uygulamalar → Turp → Depolama → Veriyi temizle** yolunu kullanın veya uygulamayı kaldırın.
- Verileri temizlemek veya uygulamayı kaldırmak, yerelde saklanan şifreli OAuth oturumlarını ve bulut kimlik bilgilerini de kaldırır.

## Bulut yedeklerini silme

**Turp → Ayarlar → Yedekleme ve aktarım** bölümünde bağlı hedefi açın, yedekleri görüntüleyin ve **Sil** komutunu kullanın. Yedek; Google Drive uygulama verisi denetimlerinden, OneDrive Apps/Turp, Dropbox Apps/Turp, yapılandırılmış WebDAV/Nextcloud klasöründen veya yapılandırılmış S3 bucket/prefix konumundan da doğrudan silinebilir.

Sağlayıcı bağlantısını kesmek yalnızca cihazdaki oturumu veya kimlik bilgisini siler; sağlayıcıdaki mevcut yedekleri otomatik olarak silmez. Google, Microsoft veya Dropbox hesap güvenliği sayfasından Turp erişimini iptal etmek gelecekteki erişimi durdurur, ancak mevcut dosyaları ayrıca silmek gerekebilir.

## Geliştiricinin silebildiği ve silemediği bilgiler

Geliştiricinin Turp hesap veritabanı veya uzaktan yönetim erişimi yoktur ve cihazda, yedek hedefinde, yapay zekâ sağlayıcısında veya sağlayıcı hesabında tutulan verilere uzaktan erişip bunları silemez. Yukarıdaki kontrolleri kullanın veya ilgili sağlayıcıya başvurun.

Depo hesaplarını, barındırmayı, kayıtları ve herkese açık Issues'ı GitHub işletir. GitHub'ın kontrol ettiği veriler için GitHub hesap/içerik ve [gizlilik kontrollerini](https://docs.github.com/site-policy/privacy-policies/github-privacy-statement) kullanın. **Herkese açık Turp issue'suna gizlilik başvurusu, kişisel veri, kimlik bilgisi, özel sohbet veya kimlik belgesi yazmayın.**

Başvuru, özel bir OAuth kanalından bilerek gönderilmiş ve geliştirici tarafından fiilen tutulmuş belirli bilgiye ilişkinse ilgili OAuth onay ekranındaki özel iletişim yöntemini kullanın. Geliştirici yalnızca tanımlanan gönderi üzerinde işlem yapabilir; hiç almadığı veri üzerinde işlem yapamaz.

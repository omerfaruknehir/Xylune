# Turp 0.24.24

- Uygulama dili, sağ üstte yüzen bir dünya simgesi olarak gösterilmek yerine normal Ayarlar hiyerarşisine taşındı.
- **Ayarlar → Kişiselleştirme → Uygulama dili** satırı eklendi; seçili dil doğrudan Ayarlar listesinde gösteriliyor.
- **Sistem varsayılanı**, **İngilizce** ve **Türkçe** seçeneklerini içeren, normal öngörülü geri gezinmesini kullanan ayrı bir dil sayfası eklendi.
- `MainActivity` içindeki eski yalnızca Ayarlar ekranında görünen dil katmanı kaldırıldı.
- Ayarlar sayfa başlıklarının işlem/sistem yerel ayarı yerine Turp'da seçilen uygulama dilini izlemesi sağlandı.
- Arama ve web yönlendirme, sağlayıcının yerel araması, Turp arama motorları, kimlik bilgileri ve ilgili açıklamalar için Türkçe kapsamı genişletildi.
- Yüzen dil katmanının geri gelmesini engellemek ve doğru Ayarlar hedefinin bağlı kalmasını doğrulamak için regresyon testleri eklendi.

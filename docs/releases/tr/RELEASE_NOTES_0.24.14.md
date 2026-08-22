# Turp 0.24.14

## Güvenilir hata kurtarma

İlk belirteç üretilmeden duran başarısız istekler artık sağlayıcı hatası ve Yeniden Dene eylemiyle birlikte konuşmada görünür kalıyor. Yarıda kesilen yanıtlar da Devam Et eylemiyle korunuyor; böylece başarısız bir üretim artık boş bir asistan iletisi gibi kaybolamıyor.

Kurtarma bildirimi artık yalnızca etkin konuşma dalını izliyor, sohbet değiştirirken kapatılma durumunu hatırlıyor ve ilgisiz bir akış satırı tarafından gizlenmiyor. Böylece bir sohbet yeniden açıldığında eski hata bildirimlerinin kısa süreliğine görünmesi engelleniyor.

## Kaydırılabilir sağlayıcı çağrısı kullanımı

İleti başına Kullanım ayrıntıları popup'ında özet ve eylemler sabit kalırken sağlayıcı çağrısı dökümü sınırlandırılmış bir alan içinde kaydırılıyor. Uzun yeniden deneme ve araç çağrısı zincirleri artık iletişim kutusunu ekran dışına itmiyor.

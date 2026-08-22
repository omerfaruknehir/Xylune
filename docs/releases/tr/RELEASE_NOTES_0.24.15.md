# Turp 0.24.15

## Güvenilir hata kurtarma

İlk belirteç üretilmeden duran başarısız istekler artık sağlayıcı hatası ve Yeniden Dene eylemiyle birlikte konuşmada görünür kalıyor. Yarıda kesilen yanıtlar da Devam Et eylemiyle korunuyor; böylece başarısız bir üretim boş bir asistan iletisi gibi kaybolamıyor.

Kurtarma bildirimleri yalnızca etkin konuşma dalıyla sınırlandırılıyor ve kapatılma durumları sohbetler arasında ayrı ayrı izleniyor. Bu sayede bir sohbet yeniden açıldığında daha önce kapatılmış eski bir hata kısa süreliğine parlamıyor; gerçekten yeni bir hata sürümü ise normal şekilde gösterilebiliyor.

## Kaydırılabilir sağlayıcı çağrısı kullanımı

İleti başına Kullanım ayrıntıları popup'ında özet ve eylemler sabit kalırken sağlayıcı çağrısı dökümü sınırlandırılmış bir alan içinde kaydırılıyor. Uzun yeniden deneme ve araç çağrısı zincirleri artık iletişim kutusunu ekran dışına itmiyor.

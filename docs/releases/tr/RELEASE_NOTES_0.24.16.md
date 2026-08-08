# Xylune 0.24.16

## Görsel oluşturucu bulanıklığı

Görseller çalışma alanındaki alt bulanıklık artık sabit, aşırı büyük 240 dp alanı veya normal sohbet oluşturucusunun daha yüksek minimumunu kullanmıyor. Görsel üretimi için kompakt bir 88 dp bulanıklık tabanı var; referans görselleri, doğrulama metni, kuyruk durumu veya çok satırlı giriş alanı yükselttiğinde yalnızca gerçek ölçülen giriş yüksekliğine kadar genişliyor.

Böylece normal sohbetin araç ve mod satırlarına sahip olmayan daha sade görsel oluşturucunun çevresindeki yarı saydam alt krom daha sıkı kalıyor.

## Daha temiz oluşturucu denetimleri

Düşünme, arama ve yürütme denetimleri artık istemin üzerinde sürekli bir satır tüketmek yerine + menüsünde bulunuyor. Görünür etiketleri de daha kompakt: düşünme yalnızca güncel çabayı, arama Arama veya Araştırma durumunu, araçlar ise gereksiz önekler ya da sağlayıcı geri dönüş ayrıntıları olmadan etkin araç durumunu gösteriyor.

## Güvenilir popup kapatma

Düşünme/arama/araç menüleri, Xylune iletişim kutuları ve bağlı bağlantı/kaynak önizlemeleri artık odaklanabilir yerel dışarı dokunma ile kapatma davranışını kullanıyor. Dışarı dokunulduğunda popup ekranda takılı kalmak yerine kapanıyor; iletişim kutularında öngörülü Geri davranışı klavyeyi dikkate almaya devam ediyor.

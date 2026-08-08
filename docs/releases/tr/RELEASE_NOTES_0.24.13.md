# Xylune 0.24.13

## Geliştirilmiş Görseller çalışma alanı

Özel Görseller çalışma alanı artık daralan konuşma başlığı ve yarı saydam oluşturucu dâhil olmak üzere Xylune'un normal konuşma düzenine daha yakın çalışıyor. Referans görselleri Fotoğraflar ve Kamera eylemleriyle kompakt önizlemeler kullanırken Oluştur/Düzenle, Kuyruk ve Durdur denetimleri normal sohbete özgü Düşünme, Arama veya Araçlar seçeneklerini göstermeden görsel iş akışına odaklanıyor.

## Gemini görsel üretimi

Xylune artık Gemini model kataloğundaki güncel görsel üretim ailelerini; `*-image`, `*-image-*` ve Imagen tarzı model kimlikleri dâhil olmak üzere algılıyor. Gemini görsel istekleri metin ve görsel çıktısını açıkça talep ediyor; dönen gömülü görsel verileri çözümlenip oluşturulan ekler olarak kaydediliyor.

Gemini görsel üretimindeki ilerleme göstergesi gerçeğe uygun kalıyor: sağlayıcı gerçekten aşamalı kareler sunmadıkça Xylune yalnızca son görsel çıktısını gösteriyor.

## Daha iyi kullanım hesaplaması

Gemini aday belirteçleri ve düşünme belirteçleri artık faturalandırılabilir çıktı toplamında birlikte sayılıyor; böylece düşünme etkin isteklerin kullanımı eksik hesaplanmıyor.

Sohbet yapılandırması artık konuşma düzeyinde sağlayıcı çağrısı toplamlarını gösteriyor: giriş, önbelleğe alınmış giriş, önbelleğe alınmamış giriş, çıktı/faturalandırılan belirteçler, toplam belirteçler, bilinen maliyet ve fiyatlandırılmamış çağrılar.

Asistan iletileri de toplam ve çağrı başına belirteç, maliyet, durum ve bitiş nedeni bilgilerini içeren Kullanım ayrıntıları görünümünü sunuyor.

## İleti eylemleri

Kullanıcı ve asistan iletilerinde artık kompakt bir taşma menüsü var. Yerel ekler de dâhil olmak üzere iletiler Android paylaşım sayfası üzerinden paylaşılabiliyor.

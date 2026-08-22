# Turp 0.24.12

## Görseller çalışma alanı

Görsel üretim modelleri artık normal sohbet modelleriyle karışmak yerine ayrı bir **Görseller** kataloğu ve çalışma alanında yer alıyor. Bir görsel modeli seçildiğinde görsele özel denetimler açılıyor; sohbet modeline dönüldüğünde geçmiş kaybolmadan normal konuşma arayüzüne geri dönülüyor.

Görseller çalışma alanı **Oluştur**, **Oluştur + düzenle** ve **Düzenle** modellerini birbirinden ayırıyor, modele özel referans görseli sınırlarını gösteriyor, fotoğraf kitaplığı ve kamera referanslarını destekliyor, desteklenmeyen ekleri gönderilmeden önce doğruluyor ve net Oluştur/Düzenle, Kuyruk ve Durdur eylemleri sunuyor.

## Görsel düzenleme

Qwen Image 2.x üretim ve düzenleme modelleri desteklendiği durumlarda en fazla üç referans görseli kabul ediyor; yalnızca düzenleme veya yalnızca üretim yapan Qwen varyantları ise genel görsel/görme denetimleri yerine doğru iş akışını gösteriyor.

OpenAI GPT Image modelleri yerel Images üretim ve düzenleme uç noktalarını kullanıyor. GPT Image 2 güncel yerleşik OpenAI görsel modeli olarak eklendi; OpenAI ile düzenleme sırasında en fazla on altı referans görseli kullanılabiliyor.

## Canlı üretim önizlemeleri

Gerçek ara görsel kareleri sunan sağlayıcılar artık bunları üretim sırasında gösterebiliyor. OpenAI GPT Image üretim ve düzenleme istekleri en fazla üç yerel kısmi görsel talep ediyor; daha yeni sağlayıcı kareleri geldikçe Turp devam eden önizlemeyi değiştirip aralarında geçiş yapıyor ve yalnızca son görseli kaydediyor.

Mevcut Qwen Image API'si gibi aşamalı kare sunmayan sağlayıcılarda ise sahte ara ayrıntılar üretmek yerine son görsel gelene kadar açık bir üretim yer tutucusu gösteriliyor.

## Sürüm notu güvenilirliği

Sürüm yayımlama artık tüm `CHANGELOG.md` dosyasına geri dönemiyor. Turp, varsa tam sürüme ait sürüm notu dosyasını kullanıyor; yoksa yalnızca o sürümün değişiklik günlüğü bölümünü çıkarıyor ve ikisi de bulunamazsa yayını reddediyor. Aynı sürümün daha önce yayımlanmış GitHub sürümü de etiket veya varlıklar yeniden oluşturulmadan açıklamasıyla eşitlenebiliyor.

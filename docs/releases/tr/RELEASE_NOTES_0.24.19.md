# Turp 0.24.19

- Kaynak alıntıları artık çiplerini yerinde tutuyor ve çipin kendisini esnetmek yerine dokunulan kaynaktan büyüyüp soluklaşarak açılan ayrı bir bağlı önizleme kartı gösteriyor.
- Öngörülü Geri hareketinin ekran kenarına temas etmesi artık popup ve menüler için dışarı dokunma sayılmıyor; kapatma parmağın bırakılmasına erteleniyor ve sistem Geri kenarında başlayan hareketler dışarı dokunma işleyicisi tarafından yok sayılıyor.
- Büyük modal iletişim kutuları artık yerel dışarı dokunma ile kapatma davranışını kullanmıyor; böylece sağlayıcı/düzenleyici pencereleri Geri hareketi başlar başlamaz kaybolmuyor.
- Bir iletişim kutusunda klavye açıksa tamamlanan ilk Geri hareketi klavyeye ait oluyor ve iletişim kutusunu açık tutuyor; sonraki Geri hareketi pencereyi normal şekilde kapatabiliyor.

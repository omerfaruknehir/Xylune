# Turp 0.24.21

- Bir kaynak çipine dokunulduğunda görünmez ve odaklanabilir bir katmanın açılıp uygulamanın geri kalanını kullanılamaz hâle getirebildiği gerileme düzeltildi.
- Kaynak önizlemeleri artık katman boyutunu popup içindeki `fillMaxSize()` ölçümüne dayanmak yerine gerçek ana pencerenin boyutlarından hesaplıyor.
- Geçersiz çapa/pencere durumları için korumalar ve önizleme kartı ölçülemezse devreye giren güvenli kapatma mekanizması eklendi.
- Dışarı dokunarak kapatma hâlâ parmağın bırakılmasını bekliyor ve öngörülü Geri kenar hareketlerini yok sayıyor; ölçülemeyen bir katman artık girişi kilitleyemiyor.

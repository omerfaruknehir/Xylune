# Turp data deletion

Turp has no central user account. Most data is stored on the Android device or in a provider selected by the user.

## Delete local data

- Delete individual chats, memories, providers, drafts, or other records from the relevant Turp screen.
- To remove all local Turp data, use Android **Settings → Apps → Turp → Storage → Clear data**, or uninstall Turp.
- Clearing data or uninstalling also removes locally stored encrypted OAuth sessions and cloud credentials.

## Delete cloud backups

Open **Turp → Settings → Backup & transfer**, select the connected destination, browse backups, and choose **Delete**. A backup can also be deleted directly through Google Drive app data controls, OneDrive Apps/Turp, Dropbox Apps/Turp, the configured WebDAV/Nextcloud folder, or the configured S3 bucket/prefix.

Disconnecting a provider removes the local session or credentials but does **not** automatically delete backups already stored there. Revoking Turp in the Google, Microsoft, or Dropbox account security page stops future access but likewise does not necessarily delete stored files.

## What the maintainer can and cannot delete

The maintainer has no Turp account database or remote administration access and cannot delete data held only on a device, in a backup destination, by an AI provider, or in a provider account. Use the controls described above or contact the relevant provider.

GitHub operates repository accounts, hosting, logs, and public Issues. Use GitHub's account, content, and [privacy controls](https://docs.github.com/site-policy/privacy-policies/github-privacy-statement) for data controlled by GitHub. **Do not put a privacy request, personal data, credentials, private chats, or identity documents in a public Turp issue.**

If a request concerns specific information deliberately sent through a private OAuth channel and actually retained by the maintainer, use the private contact method shown on that OAuth consent screen. The maintainer can act only on that identified submission, not on data the maintainer never received.

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

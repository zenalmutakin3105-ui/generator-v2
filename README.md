# Generator PO Android — Google Apps Script Web App (Beta)

## Yang dibuat
Aplikasi Android yang membuka **Generator PO Google Apps Script Web App** melalui **Chrome Custom Tabs**.
Semua master, logika alokasi, pembulatan, stok BPA, dashboard, dan fitur rollback tetap dijalankan di Web App Google Apps Script, **bukan diduplikasi di APK**. Perubahan pada Apps Script cukup di-deploy ulang; APK tidak perlu di-build ulang selama URL Web App tidak berubah.

Ini adalah **project Android siap dibuild**, bukan APK yang sudah selesai dikompilasi. Pada lingkungan pembuatan file tidak tersedia Android SDK/Gradle, sehingga APK harus dibuild melalui GitHub Actions atau Android Studio.

## Kenapa bukan WebView biasa?
Karena akses Web App Google Apps Script bisa memerlukan login Google (`Who has access: Only myself`), Custom Tabs menggunakan sesi browser pengguna yang sudah login. WebView embedded berisiko tidak menerima proses login Google. Custom Tabs memungkinkan fitur cetak dan download berjalan melalui browser.

**Implikasi:** aplikasi membuka panel Chrome yang sedikit menampilkan kontrol browser; memerlukan Chrome/browser kompatibel dan internet. Tidak bekerja offline. Untuk seluruh UX benar-benar full-screen dan offline perlu arsitektur lain.

## Build dari GitHub

**Langkah paling aman jika repository Anda sebelumnya berisi Beta APK lama:**
1. Simpan branch/tag dari repo lama sebelum mengganti source. Backup data PO dengan JSON terlebih dahulu.
2. Ekstrak ZIP ini ke komputer. Struktur ZIP sudah ditata sesuai root repo.
3. Masuk ke repository GitHub. Upload/copy struktur `app/`, `settings.gradle`, `build.gradle`, `gradle.properties`, serta `.github/workflows/build-generator-po-webapp-apk.yml` di root. **Jangan upload ZIP mentah**—GitHub tidak mengekstrak ZIP otomatis.
4. Di tab Actions pilih **Build Generator PO Web App APK → Run workflow**. Jika workflow lama `Build Generator PO Beta APK` juga berjalan, gunakan hasil workflow **Web App APK** yang baru.
5. Setelah job hijau, klik run → Artifacts → **Generator-PO-WebApp-Android-Beta-APK**.
6. Unduh ZIP artifact, ekstrak, lalu instal `app-debug.apk` pada Android.

### Opsi menjaga aplikasi lama
Project menggunakan package baru `com.rekaciptaselera.generatorpo.webapp` sehingga dapat terpasang berdampingan dengan APK Beta 0.2. Tidak perlu uninstall APK lama saat testing.

## Pemakaian APK
1. Buka APK Generator PO pada Android.
2. Dari Apps Script pilih **Deploy > Manage deployments**, copy URL `Web app` dengan akhiran `/exec`.
3. Paste URL ke layar Generator PO di Android, klik **Simpan URL**.
4. Tekan **Buka Generator PO**.
5. Bila diminta login, gunakan akun Google yang memiliki izin membuka Web App. Jika Web App hanya untuk pemilik, gunakan akun pemilik yang sama.
6. Test daftar tanggal, preview Qty, rollback, cetak PDF, backup JSON dari tampilan Web App.

Jika isi Apps Script baru di-update, deploy **New version** pada deployment yang sama sehingga URL tetap. APK akan otomatis memuat versi Web App terbaru saat dibuka ulang.

## Keamanan & keterbatasan
- Tidak ada token API atau password yang ditanam dalam APK.
- URL disimpan lokal pada perangkat melalui SharedPreferences, hanya untuk shortcut; URL bukan kredensial.
- Web App mengikuti hak akses Google Apps Script—**jangan ubah ke `Anyone` hanya demi login dari APK**.
- Aplikasi menggunakan AndroidX Browser 1.8.0 untuk kompatibilitas compileSDK 35/AGP 8.9.2, sehingga mudah dibuild dengan workflow Gradle yang sebelumnya sukses.
- Browser mungkin mengunduh JSON ke folder Downloads perangkat, bukan direktori app.
- Anda tetap harus menguji print/PDF per supplier pada perangkat.
- Aplikasi hanya launcher Web App, bukan pemindahan Apps Script menjadi Android native atau sinkronisasi offline.

## Testing minimal
- [ ] APK terpasang tanpa menimpa aplikasi Beta sebelumnya.
- [ ] URL Web App bisa disimpan dan dibuka kembali.
- [ ] Akun Google yang berwenang dapat membuka Generator PO.
- [ ] Semua master dan stok tampil sesuai database Apps Script.
- [ ] Daftar tanggal kirim RO–PPIC muncul dan preview benar.
- [ ] Impor 87 qty dan rollback bekerja.
- [ ] Hasil tabel supplier A4 landscape dan warna SKU benar.
- [ ] Backup JSON bisa diunduh dan dipulihkan.

## Catatan tentang APK final
GitHub menghasilkan `app-debug.apk` yang cocok untuk uji internal. Untuk produksi dibutuhkan release signing key yang disimpan aman dan build release terpisah.

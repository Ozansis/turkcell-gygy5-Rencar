# PROGRESS.md

> Her onaylanmış AI batch'inden SONRA buraya 3-5 satır ekle. Yeni bir AI oturumuna
> başlarken ilk mesajın bu dosyanın içeriği olsun — AI'nın projeyi baştan keşfetmesine
> gerek kalmaz, kaldığın yerden devam eder.

## Şablon (her girdi için kopyala)

### [Tarih] — [Kısa başlık]
- **Ne yapıldı:** (1-2 cümle)
- **Değişen dosyalar:** (liste)
- **Neden bu şekilde yapıldı:** (kısa gerekçe — AI'nın verdiği açıklamanın SENİN
  özetin, kopyala-yapıştır değil)
- **Kendi kontrolüm:** (derledim mi? çalıştırdım mı? ne test ettim?)
- **Sıradaki adım:** (bir sonraki oturumun ilk cümlesi bu olacak)

---

## Girdiler

### 2026-07-13 — Proje analizi + yol haritası
- **Ne yapıldı:** Mevcut kod, güncel openapi.json ve hedef tasarım (26 ekran)
  karşılaştırıldı. Kritik bulgular: network katmanı yok, birden fazla "dead-end
  effect" (Route'ta karşılığı olmayan Effect), Maps ekranında segment/fiyat
  etiketi uyumsuzlukları.
- **Değişen dosyalar:** Yok (sadece analiz)
- **Neden bu şekilde yapıldı:** Koda dokunmadan önce tam resmi görmek gerekiyordu.
- **Kendi kontrolüm:** N/A
- **Sıradaki adım:** FAZ 0 — hijyen düzeltmeleri (MainScaffold import: MANUEL
  DÜZELTİLDİ ✅). Sırada: dead-end effect'ler.

### 2026-07-13 — PROGRESS.md / NOTLARIM.md kuruldu
- **Ne yapıldı:** Oturumlar arası bağlam takibi için PROGRESS.md ve NOTLARIM.md
  eklendi. Agent.md'ye §2.4 "Bağlam Dosyası" kuralı eklenecek (manuel yapılacak).
- **Değişen dosyalar:** PROGRESS.md, NOTLARIM.md (yeni), Agent.md (eklenecek)
- **Neden bu şekilde yapıldı:** AI ile çalışırken oturum başına bağlamı sıfırdan
  anlatmamak, projeye gerçek hakimiyeti korumak için.
- **Kendi kontrolüm:** N/A (henüz kod değişikliği yok)
- **Sıradaki adım:** FAZ 0 — dead-end effect'ler (Profile: 5, Wallet: 2,
  VehicleDetail: 2). MainScaffold import hatası manuel düzeltildi ✅.

### 2026-07-13 Bilinen Sorunlar (henüz çözülmedi)
- Maps: mock araçlar haritada görünmüyor
- Maps: "En Yakın Aracı Bul" sonrası harita kayboluyor

### 2026-07-13 — Network altyapısı: Retrofit + Hilt kuruldu
- **Ne yapıldı:** Auth akışını gerçek API'ye bağlamak için minimum network
  altyapısı kuruldu: Retrofit, OkHttp + logging-interceptor, Hilt, KSP,
  hilt-navigation-compose bağımlılıkları eklendi; `RenCarApplication`
  (`@HiltAndroidApp`) oluşturuldu; `MainActivity`'e `@AndroidEntryPoint`
  eklendi; Manifest'te application adı güncellendi. `openapi.json` bu
  batch'te KULLANILMADI (yalnız referans amaçlı okundu) — endpoint/DTO
  entegrasyonu sonraki batch.
- **Değişen dosyalar:** `gradle/libs.versions.toml`, `build.gradle.kts`
  (root), `app/build.gradle.kts`, `app/src/main/java/com/turkcell/rencar_pair/RenCarApplication.kt`
  (yeni), `app/src/main/java/com/turkcell/rencar_pair/MainActivity.kt`,
  `app/src/main/AndroidManifest.xml`
- **Neden bu şekilde yapıldı:** decisions.md'de Hilt zaten seçilmişti;
  Retrofit 3.0.0 + OkHttp 4.12.0 kombinasyonu Retrofit'in resmi transitive
  bağımlılığıyla hizalı seçildi (kullanıcı düzeltmesiyle; ilk önerilen
  OkHttp 5.4.0 hatalıydı). Proje AGP 9.2.1 kullandığından Hilt 2.59.2
  (AGP 9 desteği 2.59+'da geldi) ve KSP 2.3.9 (AGP 9 built-in Kotlin
  desteği) seçildi — ilk denenen Hilt 2.57.2 ve KSP 2.2.10-2.0.2
  sürümleri AGP 9 ile derleme hatası verdi.
- **Kendi kontrolüm:** `./gradlew :app:assembleDebug` ile derlendi,
  BUILD SUCCESSFUL (Hilt component graph — hiltSyncDebug/hiltAggregateDepsDebug/
  hiltJavaCompileDebug — sorunsuz çalıştı). Runtime/UI testi yapıldı.
- **Sıradaki adım:** openapi.json'daki Auth endpoint'lerini (register/login/
  verify-otp/refresh) `ApiService` arayüzü + Hilt `NetworkModule` olarak
  ekleyip mevcut FakeRepository'nin yerine gerçek repository implementasyonunu
  bağlamak (decisions.md'deki Repository Stub Stratejisi'ne göre yalnız
  `di/<Feature>Module.kt` değişecek)
- ### Hatırlatma
- Register ekranı UI'da HİÇ YOK (sadece LoginRoute'ta boş bir TODO var).
  Network katmanı bittiğinde ilk ekran işi bu olmalı.

### 2026-07-14 — Auth Retrofit servis katmanı (DTO + API arayüzü + NetworkModule)
- **Ne yapıldı:** openapi.json'daki 6 auth endpoint'i (register/login/verify-otp/
  refresh/logout/me) için Retrofit sözleşmesi kuruldu: DTO'lar, `AuthApiService`
  arayüzü ve bunları sağlayan Hilt `NetworkModule`. Repository katmanı, token
  saklama ve ViewModel entegrasyonu bilinçli olarak kapsam dışı bırakıldı.
- **Değişen dosyalar (yeni):** `data/network/dto/AuthDtos.kt`,
  `data/network/AuthApiService.kt`, `di/NetworkModule.kt`
- **Neden bu şekilde yapıldı:** `data/`, `network/`, `di/` paketleri projede
  hiç yoktu, sıfırdan oluşturuldu. `role` alanı bilinçli olarak enum değil
  `String` (API yeni bir rol dönerse Gson deserialization'ının patlamasını
  önlemek için). `HttpLoggingInterceptor` seviyesi debug'da BODY, release'de
  BASIC yapıldı — aksi halde parola ve access/refresh token'lar release
  Logcat'ine düz metin yazılırdı (kullanıcı onayıyla karar verildi).
  `di/NetworkModule.kt`, decisions.md'nin öngördüğü `di/AuthModule.kt`
  (fake/real repository seçimi) ile ÇAKIŞMIYOR — tamamlayıcı, altyapı
  katmanı; repository binding'i hâlâ gelecek `AuthModule.kt`'ye ait.
- **Kendi kontrolüm:** `./gradlew :app:assembleDebug` ile derlendi, BUILD
  SUCCESSFUL. Derleme sırasında proje dışı bir ortam sorunu çıktı (Gradle
  daemon yanlış bir JRE ile başlamıştı, jlink yoktu) — daemon durdurulup tam
  JDK ile yeniden başlatılarak çözüldü, proje dosyalarına dokunulmadı. Yeni
  dosyalar henüz hiçbir yerden çağrılmadığı için runtime/network testi yok.
- **Sıradaki adım:** `AuthRepository` arayüzü + gerçek implementasyonu (bu
  `AuthApiService`'i saran) ve token saklama (DataStore/EncryptedPrefs)
  eklenmesi; ardından `di/AuthModule.kt` ile fake/real seçimi ve auth
  header ekleyen bir OkHttp interceptor.

### 2026-07-14 — API v2'ye geçiş + token saklama + auth interceptor + AuthRepository
- **Ne yapıldı:** Gerçek backend'in `https://rencarv2.halitkalayci.com/api/docs`
  adresindeki güncel (v2) OpenAPI şeması canlı olarak çekilip
  `docs/api/openapi.json` bu şemayla değiştirildi (v1 bayattı — `segment`,
  `includeBusy`, `/vehicles/{id}/quote` gibi yeni alanlar/uçlar eksikti).
  `NetworkModule.kt`'deki `BASE_URL` v1'den v2'ye güncellendi. Ardından token
  saklama (DataStore Preferences tabanlı `TokenStore`), bir OkHttp
  `AuthInterceptor` (auth uçları hariç tüm isteklere `Authorization: Bearer`
  ekler) ve bunları saran `AuthRepository` (requestOtp/verifyOtp/refresh/logout)
  eklendi.
- **Değişen dosyalar:** `docs/api/openapi.json`, `di/NetworkModule.kt`,
  `gradle/libs.versions.toml`, `app/build.gradle.kts`,
  `data/local/TokenStore.kt` (yeni), `data/network/AuthInterceptor.kt` (yeni),
  `data/repository/AuthRepository.kt` (yeni)
- **Neden bu şekilde yapıldı:** `di/AuthModule.kt` (decisions.md'nin öngördüğü
  fake/real seçim modülü) bilinçli olarak eklenmedi — Auth için artık bir
  "fake" varyant planlanmadığından (login zaten gerçek API'ye bağlanacak),
  `AuthRepository`'nin `@Inject constructor` ile doğrudan Hilt tarafından
  sağlanması yeterli; gereksiz bir modül dosyası eklemek aşırı mühendislik
  olurdu. `AuthInterceptor` senkron çalıştığından (`OkHttp Interceptor`),
  `TokenStore` erişim token'ını DataStore'a ek olarak bellekte
  (`AtomicReference`) de tutuyor; ilk değer uygulama başlarken `runBlocking`
  ile diskten okunuyor (tek seferlik, küçük bir okuma olduğundan kabul
  edilebilir bulundu).
- **Kendi kontrolüm:** `./gradlew :app:assembleDebug` ile derlendi, BUILD
  SUCCESSFUL (Hilt/KSP DI grafiği `AuthInterceptor`/`TokenStore`/
  `AuthRepository` enjeksiyonlarıyla birlikte sorunsuz üretildi). Yeni
  dosyalar henüz hiçbir ViewModel'den çağrılmadığı için runtime/network
  testi yapılmadı (bu bilinçli olarak sıradaki adıma bırakıldı).
- **Sıradaki adım:** `LoginViewModel.handleSendCode()` ve
  `OtpViewModel.handleVerify()`'ı `AuthRepository.requestOtp`/`verifyOtp`'a
  bağlamak (Faz 3); ardından v2 `VehicleResponseDto` şemasına göre
  `VehiclesApiService` + DTO'lar + repository eklenip `MapsViewModel`'in
  `MapsMockSource` yerine gerçek veriyi kullanması (Faz 4-5).
### 2026-07-14 — Register ekranı (Contract/ViewModel/Screen/Route) + AuthRepository.register()
- **Ne yapıldı:** `feature/auth/register/` paketi sıfırdan oluşturuldu
  (RegisterContract/ViewModel/Screen/Route) ve `AuthRepository`'ye `verifyOtp()`
  ile aynı desende bir `register()` fonksiyonu eklendi (başarılıysa
  `tokenStore.saveTokens(...)` çağrılır). `AuthApiService.register` ve
  `RegisterDto` zaten mevcuttu (önceki batch'te eklenmişti) ve
  `docs/api/openapi.json`'daki `POST /auth/register` şemasıyla birebir
  eşleşiyordu — bu batch'te değiştirilmedi. `RenCarNavHost.kt` bilinçli
  olarak KAPSAM DIŞI bırakıldı (Agent.md §2.1 dosya limiti gereği ayrı
  onay bekleyen 6. dosya); Login'deki `onNavigateToRegister` TODO'su hâlâ
  boş, `RegisterRoute` henüz hiçbir NavHost'a bağlı değil.
- **Değişen dosyalar (yeni):** `feature/auth/register/RegisterContract.kt`,
  `RegisterViewModel.kt`, `RegisterScreen.kt`, `RegisterRoute.kt`.
  **Değişen dosya:** `data/repository/AuthRepository.kt` (register() eklendi).
- **Neden bu şekilde yapıldı:** Kullanıcı onayıyla üç ürün kararı netleşti:
  (1) kayıt başarılı olduğunda License Verification akışına yönlendirme
  (OTP doğrulama sonrasıyla tutarlı, her iki giriş yolu da PENDING rollü
  kullanıcıyı aynı kimlik doğrulama akışına sokuyor); (2) formda client-side
  şifre tekrarı (confirm password) alanı var, API'ye yalnız `password`
  gönderiliyor; (3) telefon numarası Login ile aynı desende 10 haneli yerel
  numara olarak toplanıyor, ViewModel API'ye göndermeden önce başına "+90"
  ekliyor. `RegisterViewModel`, projede `@HiltViewModel` + repository inject
  eden İLK ViewModel oldu (Login/Otp henüz Hilt'e taşınmadı) — kullanıcının
  açık talimatıyla bilinçli bir öncülük, Login/Otp'nin Hilt'e taşınması ayrı
  bir migration batch'i olarak bekliyor. Şifre alanları kullanıcı talebiyle
  `PasswordVisualTransformation()` + `KeyboardType.Password` ile maskelendi.
  Form 6 alan içerdiğinden (Login/Otp'nin tek alanına kıyasla) Column'a
  `verticalScroll` eklendi — küçük ekranlarda taşmayı önlemek için.
- **Kendi kontrolüm:** `./gradlew :app:assembleDebug` ile derlendi, BUILD
  SUCCESSFUL. Tek uyarı: `hiltViewModel` (androidx.hilt.navigation.compose
  1.4.0) yeni bir pakete taşınmak üzere deprecated işaretli — derlemeyi
  bloklamıyor, yeni bağımlılık eklemeden şimdilik göz ardı edildi. NavHost'a
  bağlı olmadığından runtime/UI testi yapılamadı (bilinçli olarak sonraki
  onaylı adıma bırakıldı).
- **Sıradaki adım:** (6. dosya, ayrı onay bekliyor) `RenCarNavHost.kt`'ye
  `REGISTER` route sabiti eklenip Login'deki `onNavigateToRegister` TODO'su
  gerçek navigasyona bağlanmalı; ardından uçtan uca happy-path testi
  (Login → Kayıt ol → form → Kayıt Ol → License Verification + token
  kaydı) yapılmalı. Sonrasında `LoginViewModel.handleSendCode()` ve
  `OtpViewModel.handleVerify()`'ı `AuthRepository.requestOtp`/`verifyOtp`'a
  bağlamak (Faz 3) hâlâ bekliyor.

### 2026-07-14 — RenCarNavHost.kt: Register ekranı bağlandı
- **Ne yapıldı:** Bir önceki batch'te "6. dosya, ayrı onay bekliyor" olarak
  bırakılan `RenCarNavHost.kt` değişikliği yapıldı. `RenCarDestinations`'a
  parametresiz `REGISTER = "register"` sabiti eklendi; Login'deki boş
  `onNavigateToRegister` TODO'su `navController.navigate(REGISTER)`'a
  bağlandı; yeni `REGISTER` composable bloğu `RegisterRoute`'un 3
  callback'ini (`onNavigateToLicenseVerification`, `onNavigateToLogin`,
  `onNavigateBack`) NavHost'a kabloladı.
- **Değişen dosyalar:** `navigation/RenCarNavHost.kt`
- **Neden bu şekilde yapıldı:** `onNavigateToLicenseVerification`,
  OTP doğrulama sonrası izlenen mevcut yolla (`OtpRoute.onNavigateToHome`
  → `LICENSE_VERIFICATION`) birebir aynı hedefe (`navigate(LICENSE_VERIFICATION)`)
  bağlandı — ehliyet doğrulama akışının ilk ekranı zaten bu route.
  `onNavigateToLogin` VE `onNavigateBack` ikisi de kasıtlı olarak
  `navigate(LOGIN)` değil `popBackStack()` kullanıyor: Register'a ulaşmanın
  tek yolu Login'in `GoToRegister`'ı olduğundan Register her zaman Login'in
  üstünde push edilmiş durumda; `navigate(LOGIN)` backstack'e ikinci bir
  Login örneği daha ekleyip geri tuşuna iki kez basma sorunu yaratırdı.
  Bu, projede zaten LICENSE_VERIFICATION/SELFIE/OTP/VEHICLE_DETAIL'in
  `onNavigateBack`'i için kullandığı desenle tutarlı.
- **Kendi kontrolüm:** `./gradlew :app:assembleDebug` ile derlendi, BUILD
  SUCCESSFUL. Emülatör/cihazda runtime UI testi yapılmadı (önceki
  batch'lerle tutarlı olarak yalnızca derleme doğrulaması yapıldı) —
  istenirse ayrı bir adımda çalıştırılıp Login → Kayıt ol → form doldur →
  Kayıt Ol → License Verification akışı elle doğrulanabilir.
- **Sıradaki adım:** Register ekranının uçtan uca happy-path testi
  (gerçek cihaz/emülatörde); ardından `LoginViewModel.handleSendCode()` ve
  `OtpViewModel.handleVerify()`'ı `AuthRepository.requestOtp`/`verifyOtp`'a
  bağlamak (Faz 3) hâlâ bekliyor.

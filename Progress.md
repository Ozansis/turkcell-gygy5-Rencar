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

### 2026-07-14 — Faz 3: Login/Otp ekranları gerçek Auth API'ye bağlandı
- **Ne yapıldı:** `LoginViewModel` ve `OtpViewModel`, `AuthRepository`
  üzerinden gerçek `/auth/login` ve `/auth/verify-otp` uçlarına bağlandı.
  `LoginViewModel` `@HiltViewModel` + `@Inject constructor` oldu;
  `OtpViewModel` telefon numarasını runtime parametresi olarak almaya
  devam ettiği için `@HiltViewModel(assistedFactory = ...)` +
  `@AssistedInject`/`@AssistedFactory` deseniyle Hilt'e bağlandı (eski
  manuel `ViewModelProvider.Factory` kaldırıldı). Her iki Contract'a
  `ShowError(message: String)` effect'i eklendi, Route'larda Toast ile
  gösteriliyor. "Kod Yeniden Gönder" artık gerçekten `requestOtp`'u tekrar
  çağırıyor (öncesinde sadece sayaç sıfırlıyordu).
- **Değişen dosyalar:** `feature/auth/login/LoginContract.kt`,
  `LoginViewModel.kt`, `LoginRoute.kt`, `feature/auth/otp/OtpContract.kt`,
  `OtpViewModel.kt`, `OtpRoute.kt`
- **Neden bu şekilde yapıldı:** UI'nin telefon numarasını 10 haneli
  (ülke kodsuz) tutması nedeniyle API çağrılarında `"+90"` öneki
  ViewModel içinde ekleniyor (iki dosyada küçük tekrar — ayrı bir yardımcı
  dosya açmak bu ölçekte gereksiz soyutlama olurdu). `OtpViewModel` için
  assisted injection'a geçiş, decisions.md'nin "Hilt sonrası
  @AssistedInject'e geçilecek" planına uygun; ancak bu geçiş yalnızca
  bu iki ekranla sınırlı tutuldu, projedeki diğer parametre alan
  ViewModel'lere (örn. `VehicleDetailViewModel`) dokunulmadı (kapsam dışı).
- **Kendi kontrolüm:** `./gradlew :app:assembleDebug` ile derlendi, BUILD
  SUCCESSFUL (Hilt/KSP, AssistedFactory üretimi dahil sorunsuz). Gerçek
  bir telefon numarasıyla uçtan uca çalıştırma (login → OTP → home) henüz
  yapılmadı.
- **Sıradaki adım:** Uçtan uca runtime testi (gerçek/test telefon
  numarasıyla, varsayılan OTP kodu 123456) yapmak; ardından Faz 4 —
  v2 `VehicleResponseDto` şemasına göre `VehiclesApiService` + DTO'lar +
  repository eklenmesi.
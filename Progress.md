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

### 2026-07-13 — PROGRESS.md / NOTLARIM.md kuruldu

- **Ne yapıldı:** Oturumlar arası bağlam takibi için PROGRESS.md ve NOTLARIM.md
  eklendi. Agent.md'ye §2.4 "Bağlam Dosyası" kuralı eklenecek (manuel yapılacak).
- **Değişen dosyalar:** PROGRESS.md, NOTLARIM.md (yeni), Agent.md (eklenecek)
- **Neden bu şekilde yapıldı:** AI ile çalışırken oturum başına bağlamı sıfırdan
  anlatmamak, projeye gerçek hakimiyeti korumak için.
- **Kendi kontrolüm:** N/A (henüz kod değişikliği yok)

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

### 2026-07-14 — Faz 4: Vehicles API katmanı eklendi

- **Ne yapıldı:** v2 `VehicleResponseDto` şemasına (fuelPercent, rangeKm,
  transmission, seats, segment dahil) göre `VehiclesApiService` (GET
  /vehicles ve /vehicles/{id}, type/segment/page/limit/includeBusy query
  parametreleriyle) ve bunu saran `VehiclesRepository` eklendi.
  `NetworkModule.kt`'ye `VehiclesApiService` sağlayıcısı eklendi.
- **Değişen dosyalar:** `data/network/dto/VehicleDtos.kt` (yeni),
  `data/network/VehiclesApiService.kt` (yeni),
  `data/repository/VehiclesRepository.kt` (yeni), `di/NetworkModule.kt`
- **Neden bu şekilde yapıldı:** `type`/`segment`/`status`/`transmission`
  alanları, backend'in `role` alanı için zaten benimsediği desene uyarak
  Kotlin enum değil `String` tutuldu (API yeni bir değer dönerse Gson
  deserialization'ının patlamasını önlemek için) — eşleme (domain
  modele çevirme) Faz 5'te yapılacak. `VehiclesRepository`, ayrı bir
  sonuç sarmalayıcı (result wrapper) yazmak yerine `AuthRepository.kt`
  içindeki genel amaçlı `AuthResult<T>`'yi yeniden kullandı — isim
  "Auth" ile sınırlıymış gibi görünse de yapısal olarak jenerik;
  yeniden adlandırmak `LoginViewModel`/`OtpViewModel` dahil 4 dosyaya
  daha dokunmayı gerektirdiğinden bu batch'te kapsam dışı bırakıldı
  (bkz. öneriler).
- **Kendi kontrolüm:** `./gradlew :app:assembleDebug` ile derlendi, BUILD
  SUCCESSFUL. Henüz hiçbir ViewModel bu repository'yi çağırmadığından
  runtime testi yok.

- **Öneri:** `AuthResult<T>` adı, artık Auth dışı repository'lerde de
  kullanıldığından `ApiResult<T>` gibi daha nötr bir isme taşınabilir —
  ileride tüm kullanım yerlerini (Auth + Vehicles ViewModel'leri) tek
  bir batch'te güncellemek gerekir.

### 2026-07-14 — Faz 5: MapsViewModel gerçek Vehicles API'sine bağlandı

- **Ne yapıldı:** `MapsViewModel`, `@HiltViewModel` + `@Inject constructor(VehiclesRepository)`
  olacak şekilde Hilt'e bağlandı. `loadVehicles()` artık `/vehicles?includeBusy=true`
  ucunu çağırıyor (Login/Otp'teki `AuthResult` pattern matching deseniyle aynı);
  hata durumunda yeni `MapsContract.Effect.ShowError` ile Toast gösteriliyor.
  DTO→`NearbyVehicle` eşlemesi eklendi: `distanceMeters` mevcut haversine
  fonksiyonuyla `myLocation`'a göre hesaplanıyor (konum yokken 0, konum her
  değiştiğinde `handleLocationChanged` tüm listeyi yeniden hesaplıyor);
  `tankLabel`, `fuelPercent`'ten türetiliyor. `MapsRoute.kt` `viewModel()` yerine
  `hiltViewModel()` kullanıyor. `VehicleStatus` enum'ına `RESERVED` eklendi
  (API `includeBusy=true` ile RESERVED/RENTED araçları da döndürüyor).
- **Değişen dosyalar:** `feature/maps/MapsContract.kt`, `feature/maps/MapsViewModel.kt`,
  `feature/maps/MapsRoute.kt`
- **Neden bu şekilde yapıldı:** `tankLabel` eşiği API'de tanımlı olmadığından
  mevcut mock veriden geriye doğru çıkarıldı (`>=70` Dolu, `>=30` Yarı dolu,
  altı Az yakıt) — kullanıcı onayıyla kabul edildi. `type`/`status` DTO
  string'i enum'a çevrilirken bilinmeyen bir değer gelirse (`runCatching` ile)
  o araç listeden atlanıyor, çökme yerine sessizce dışlanıyor (dış API sınırı).
  `MapsMockSource.kt` BİLİNÇLİ OLARAK SİLİNMEDİ: `VehicleDetailViewModel` hâlâ
  ona bağımlı (`MapsMockSource.vehicles.find { it.id == vehicleId }`); bu
  ekranın kendi konum akışı olmadan mock'u kaldırmak derlemeyi bozardı.
  Kaldırma kullanıcı onayıyla Faz 6'ya ertelendi.
- **Kendi kontrolüm:** `./gradlew :app:compileDebugKotlin` ile derlendi,
  BUILD SUCCESSFUL (yalnızca `hiltViewModel()`'in deprecated olduğuna dair,
  projede zaten var olan bir uyarı — LoginRoute.kt'de de aynı uyarı mevcut).
  Runtime/UI testi (haritada gerçek araçların görünmesi) henüz yapılmadı.
- **Sıradaki adım:** Faz 6 — `VehicleDetailViewModel`'i `MapsMockSource`
  yerine `VehiclesRepository.getVehicle(id)`'e bağlamak; bu ekranın kendi
  konum akışı olmadığından `distanceMeters`/`canUnlock` hesaplaması için bir
  çözüm (kendi konum akışı mı, yoksa Maps'ten devralınan bir değer mi)
  netleştirilmeli. Ardından `MapsMockSource.kt` kaldırılabilir.

### 2026-07-14 — Runtime debug: harita gerçek verisi + Faz 6 (VehicleDetailViewModel gerçek API'ye bağlandı)

- **Ne yapıldı:** Faz 5 sonrası "haritada araç görünmüyor" şikayeti canlı
  backend'e karşı runtime debug ile araştırıldı. Kök neden: kullanıcının test
  ettiği `5550000000` numarası backend'de ADMIN rolünde kayıtlıydı; `/vehicles`
  ucu yalnızca CUSTOMER rolüne açık olduğundan istekler 401/403 dönüyor, liste
  boş kalıyordu (kod hatası değil, hesap rolü). Doğrulama için yeni bir test
  kullanıcısı (`+905550000102`) register edilip sahte ehliyet foto'suyla
  ADMIN token'ı üzerinden onaylandı (PENDING → CUSTOMER); bu hesapla
  `/vehicles` 20 araç döndürdüğü doğrulandı. Ayrıca gerçek araçların (20 adet)
  neredeyse tamamının İstanbul civarında olduğu, emülatörün varsayılan
  konumunun oraya uzak olduğu tespit edildi; emülatör konumu `adb emu geo fix`
  ile araç kümesine (40.98, 28.87) sabitlendi.
  Bu sırada araç detay ekranının hâlâ sıfır veri gösterdiği fark edildi
  (bkz. Faz 5 notu) — bu, planlanan Faz 6 olarak hemen uygulandı:
  `VehicleDetailViewModel` artık `@HiltViewModel(assistedFactory=...)` +
  `VehiclesRepository.getVehicle(id)` ile gerçek veri çekiyor. `distanceMeters`
  bu ekranda canlı konum akışı kurmak yerine, kullanıcı haritada marker'a
  tıkladığı andaki (Maps'te zaten hesaplanmış) değer navigasyon parametresi
  olarak taşınıyor (`vehicle-detail/{vehicleId}/{distanceMeters}`).
  `VehicleDetailContract.Effect.ShowError` eklendi (Toast). `MapsMockSource.kt`
  artık hiçbir tüketicisi kalmadığından silindi.
- **Değişen dosyalar:** `feature/maps/MapsContract.kt` (Effect'e distanceMeters
  eklendi), `feature/maps/MapsViewModel.kt` (marker tıklama/en yakın araç artık
  mesafeyi de effect'e taşıyor), `feature/maps/MapsRoute.kt`,
  `navigation/MainScaffold.kt`, `navigation/RenCarNavHost.kt` (rota
  `{distanceMeters}` argümanı aldı), `feature/maps/detail/VehicleDetailContract.kt`,
  `feature/maps/detail/VehicleDetailViewModel.kt`,
  `feature/maps/detail/VehicleDetailRoute.kt`,
  `data/repository/VehiclesRepository.kt` (plandışı küçük ekleme: `getVehicle(id)`
  sarmalayıcısı — `VehiclesApiService.getVehicle` zaten vardı ama repository
  seviyesinde sarılmamıştı, VehicleDetailViewModel için gerekliydi),
  `feature/maps/MapsMockSource.kt` (silindi).
- **Neden bu şekilde yapıldı:** Kendi konum akışını (izin diyaloğu +
  FusedLocationProviderClient) detay ekranında tekrar kurmak hem daha büyük
  bir değişiklik hem de gereksiz bir ikinci konum aboneliği olurdu; mesafe
  zaten Maps'te tıklama anında hesaplanmış olduğundan tek seferlik bir
  navigasyon parametresi olarak taşımak yeterli ve mimari kurala (path segment
  olarak basit tip) uygun. `tankLabel` türetme fonksiyonu MapsViewModel'dekiyle
  aynı ama ayrı bir dosyada tutuldu (Login/Otp'teki ülke kodu tekrarı
  emsaliyle tutarlı — bu ölçekte paylaşımlı bir yardımcı dosya açmak aşırı
  soyutlama olurdu).
- **Kendi kontrolüm:** `./gradlew :app:compileDebugKotlin` ile derlendi, BUILD
  SUCCESSFUL. Gerçek backend'e karşı curl/PowerShell ile uçtan uca doğrulandı
  (register → license upload → admin approve → /vehicles 20 kayıt döndü).
  Emülatörde gerçek CUSTOMER hesabıyla (`5550000102`, kod `123456`) haritada
  araçların göründüğü henüz uygulama içinden TEKRAR doğrulanmadı (bu oturumda
  hesap az önce hazırlandı) — sıradaki oturumda ilk iş bu olmalı.
- **Sıradaki adım:** Uygulamada `5550000102` ile giriş yapıp Haritalar
  sekmesinde araçların göründüğünü ve bir araca tıklayınca detay ekranının artık
  gerçek veriyle (sıfır değil) dolduğunu doğrulamak.
- **Hatırlatma:** Test hesapları — ADMIN: `5550000000`; CUSTOMER: `5550000102`
  (ikisi de OTP kodu `123456`). Emülatör konumu İstanbul'a sabitlendi
  (`adb emu geo fix 28.87 40.98`), gerekirse tekrar ayarlanmalı.

### 2026-07-15 — Kök neden bulundu: harita kamerası araç kümesine hiç gitmiyordu

- **Ne yapıldı:** "Haritada araç markerları görünmüyor" şikayeti uçtan uca
  araştırıldı. `RencarMap.kt`'de commit edilmemiş, yarım kalmış bir önceki
  oturumdan kalma "TEŞHİS" bloğu bulundu (araç ikonu/fiyat `SymbolLayer`'ları
  devre dışı bırakılmış, fazladan `Log.d` çağrıları eklenmiş) — bu, aynı
  şikayetin daha önce de araştırıldığını ama sonuca bağlanmadığını gösteriyor.
  Backend'e CUSTOMER token'ıyla doğrudan `curl` ile `/vehicles?includeBusy=true`
  çağrıldı: 20 araç, doğru `type`/`status` enum değerleriyle dönüyor (veri
  katmanında sorun yok). Ancak araçların gerçek konumu enlem 39.93–41.18,
  boylam 28.72–32.86 aralığında (İstanbul-Kocaeli-Ankara koridoru), oysa
  `RencarMap.kt`'deki `DEFAULT_CENTER` sabiti İzmir'e ayarlıydı (38.517,
  27.162) ve kamera SADECE cihazın gerçek GPS konumu geldiğinde bir kereliğine
  oraya zoom yapıyordu. Konum izni reddedilirse veya GPS fix hiç gelmezse
  (emülatörde `dumpsys location` ile tüm sağlayıcılarda `last location=null`
  olduğu doğrulandı — önceki oturumdaki `adb emu geo fix` ayarı emülatör
  yeniden başlatıldığında sıfırlanmış) kamera hep İzmir'de asılı kalıyor,
  araçlar ekranın 250+ km dışında kaldığından hiç görünmüyordu. Düzeltme:
  `RencarMap.kt`'de kullanıcı konumu + tüm araç konumlarını kapsayan tek
  seferlik bir `LatLngBounds` fit eklendi (`hasFramedInitialView`); konum
  yoksa/gecikirse bile kamera araç kümesine odaklanıyor. TEŞHİS kodu
  temizlendi, ikon/fiyat `SymbolLayer`'ları geri getirildi, kullanılmayan
  `Log`/`TAG` kaldırıldı.
- **Değişen dosyalar:** `feature/maps/RencarMap.kt`
- **Neden bu şekilde yapıldı:** `DEFAULT_CENTER`'ı değiştirmek yerine
  bounding-box fit tercih edildi — çünkü araçların gerçek konumu backend
  verisine göre değişebilir (sabit bir şehre kilitlemek kırılgan olurdu);
  bounding-box çözümü hem konum izni verilen hem verilmeyen kullanıcılarda
  çalışıyor. Kullanıcı konumu sonradan gelirse ayrıca yeniden zoom
  yapılmıyor (`hasFramedInitialView` tek seferlik) — kullanıcı o noktadan
  sonra haritada serbestçe gezinebilsin diye kamera kontrolü geri
  bırakılıyor (kullanıcı onayıyla kabul edildi).
- **Kendi kontrolüm:** `./gradlew :app:compileDebugKotlin` ile derlendi,
  BUILD SUCCESSFUL. Emülatör, test sırasında (login ekranındaki ayrı bir
  UI sorunu nedeniyle yapılan manuel dokunma denemeleri sırasında oluşan
  bir "System UI isn't responding" sonrası) kapandığından cihaz üzerinde
  görsel doğrulama YAPILAMADI — kullanıcı kendisi doğrulayacak.
- **Sıradaki adım:** Emülatörü/cihazı açıp `5550000102` ile giriş yapıp
  Haritalar sekmesinde araçların artık göründüğünü doğrulamak.
- **Hatırlatma (kapsam dışı, ayrı görev):** Login ekranındaki telefon
  numarası alanına rakam girilirken (hem sistem klavyesi hem uygulama içi
  tuş takımıyla, hıza bakmaksızın, tekrarlanabilir şekilde) son haneler
  yanlış sırada yerleşiyor (örn. `...0102` yazılınca `...0210` görünüyor).
  Muhtemelen özel bir `VisualTransformation`'ın `OffsetMapping`'inde
  off-by-one. Kullanıcı isteğiyle bu oturumun kapsamı dışında bırakıldı.
  Not: bu görünüşteki bug'ın etkisi sadece ekranda değil — API'ye giden
  gerçek `phone` alanı da bozuk gidiyor (`+905550000210` gibi), yani
  sadece bir `VisualTransformation` render sorunu değil, alttaki text
  state de etkileniyor.

### 2026-07-15 — Gerçek kök neden bulundu ve düzeltildi: harita stilinde eksik `glyphs` URL'si tüm araç katmanının render'ını bozuyordu

- **Ne yapıldı:** Bir önceki oturumun "kamera araç kümesine gitmiyordu"
  düzeltmesi kamerayı doğru konuma getirmiş olsa da, kullanıcı haritada
  hâlâ hiçbir araç marker'ı göremediğini bildirdi. Bağlı emülatörde
  (`emulator-5554`) canlı debug yapıldı: `/vehicles` API'si ve
  `MapsViewModel` filtrelemesi doğru çalıştığı (bottom sheet'teki araç
  sayısı her zaman doğruydu), kamera doğru konuma geldiği ve konum
  noktasının (mavi nokta) sorunsuz render edildiği doğrulandı — yani sorun
  veri katmanında değil, `RencarMap.kt`'nin çizim katmanındaydı. Geçici
  `Log.d` satırlarıyla doğrulandı: `GeoJsonSource`'a araçlar başarıyla
  yazılıyor (`features set=20`), tüm katmanlar (`circle`/`halo`/`icon`/
  `price`) stile sorunsuz ekleniyor — ama ekranda hiçbir turuncu marker
  çıkmıyor. Katmanları tek tek devre dışı bırakarak izole edildi: sadece
  `VEHICLE_PRICE_LAYER_ID` (fiyat etiketi, `SymbolLayer` + `textField`)
  kaldırıldığında circle/icon katmanları HEMEN render olmaya başladı.
  Kök neden: `OSM_STYLE_JSON`'da (`MapsStyle.kt`) hiç `glyphs` URL'si
  tanımlı değildi; metin içeren bir `SymbolLayer` glyph kaynağı olmadan
  render edilmeye çalışılınca MapLibre native render thread'inde
  `queryRenderedFeatures`/tile bucket'ı bozan bir hataya yol açıyor ve bu,
  AYNI source'a bağlı circle/icon katmanlarını da (bazen, race'e bağlı
  olarak — bu yüzden ilk denemede tutarsız görünüyordu) görünmez kılıyordu.
  Önce `glyphs` alanına bir CDN (`fonts.openmaptiles.org`) eklenip fiyat
  katmanı `textFont` ile geri açıldı, ama bu CDN'in PBF formatı MapLibre'nin
  parser'ıyla uyuşmadığından `Mbgl: Failed to load glyph range ...:
unknown pbf field type exception` hatası verdi ve sorunu ÇÖZMEDİ (yine
  tutarsız render'a yol açtı). Bunun yerine haritadaki fiyat etiketi
  tamamen kaldırıldı (fiyat zaten alt listede/araç detayında gösteriliyor)
  — harici bir glyph/font servisine bağımlılık, kırılgan ve gereksiz
  olduğundan tercih edilmedi.
- **Değişen dosyalar:** `feature/maps/RencarMap.kt` (`VEHICLE_PRICE_LAYER_ID
`SymbolLayer`'ı ve ilgili `Expression`import'u kaldırıldı),`feature/maps/MapsStyle.kt`(net değişiklik yok — denenen`glyphs`
  eklentisi işe yaramayınca geri alındı).
- **Neden bu şekilde yapıldı:** Fiyatı harita üzerinde bir metin etiketi
  olarak göstermek, tüm marker render'ının güvenilirliğini bir harici
  glyph CDN'ine bağımlı kılıyordu (hem ağ gecikmesi hem format uyumsuzluğu
  riskiyle); bu güvenilmezlik, uygulamanın ANA özelliği olan "haritada
  araç görme"yi tehlikeye atıyordu. Fiyat bilgisi zaten alt araç
  listesinde ve araç detay ekranında gösterildiğinden, haritadaki metin
  etiketi kozmetik bir ekstraydı — kaldırılması özellik kaybı yaratmadı.
- **Kendi kontrolüm:** `./gradlew :app:installDebug` ile birden fazla kez
  kuruldu; bağlı emülatörde gerçek CUSTOMER hesabıyla (bozuk ama kayıtlı
  numara `+905550000210` — bkz. yukarıdaki telefon input hatırlatması)
  uçtan uca test edildi: temiz bir uygulama açılışında (fresh process)
  turuncu araç marker'ları (halo+daire+araç ikonu) güvenilir şekilde
  görüntülendi ve bir marker'a tıklayınca doğru `VehicleDetail` ekranına
  (plaka/marka eşleşti) gidildiği doğrulandı.
- **Yeni bulunan, kapsam dışı bırakılan sorun:** Haritadan bir araç
  detayına gidip GERİ dönüldüğünde marker'lar bazen tekrar kayboluyor
  (veri/tıklama hâlâ doğru — `queryRenderedFeatures` çalışıyor, sadece
  paint olmuyor). Bu, PROGRESS.md'nin 2026-07-13 tarihli "'En Yakın Aracı
  Bul' sonrası harita kayboluyor" notuyla aynı aileden, muhtemelen
  `RencarMap`'in `mapView`'i her `MapsRoute` yeniden kompoze olduğunda
  (geri navigasyonda) sıfırdan yaratmasıyla ilgili bir MapLibre native
  render/invalidate zamanlama sorunu. Kullanıcının bu oturumdaki asıl
  şikayeti (ilk açılışta hiç marker görünmemesi) çözüldüğünden ve bu ayrı
  bir regresyon olduğundan, kapsam dışında bırakıldı.
- **Sıradaki adım:** Geri navigasyondan sonra marker'ların kaybolması
  sorununu araştırmak — muhtemelen `RencarMap`'te `MapView`'in
  `rememberSaveable`/tek instance olacak şekilde nav-safe hale getirilmesi
  veya geri dönüşte native render'ı tetikleyen bir `invalidate()` çağrısı
  gerekebilir.

### 2026-07-15 — Araç detay ekranına mini harita eklendi (seçilen araç + kendi konumum)

- **Ne yapıldı:** `VehicleDetailScreen`'deki düz renkli arka plan `Box`,
  Maps ekranındaki `RencarMap` composable'ı yeniden kullanılarak gerçek bir
  mini haritayla değiştirildi. Harita, seçilen aracın konumuna ortalanıyor
  (turuncu marker) ve — izin verilmişse — kullanıcının bilinen son konumunu
  da (mavi nokta) gösteriyor. `VehicleDetailContract.State`'e `type`,
  `pricePerDay`, `vehicleLocation`, `myLocation` alanları ve
  `Intent.LocationChanged` eklendi; `VehicleDetailViewModel.loadVehicle()`
  bu alanları `VehicleResponseDto.latitude/longitude/type/pricePerDay`'den
  dolduruyor.
- **Değişen dosyalar:** `feature/maps/detail/VehicleDetailContract.kt`,
  `feature/maps/detail/VehicleDetailViewModel.kt`,
  `feature/maps/detail/VehicleDetailRoute.kt`,
  `feature/maps/detail/VehicleDetailScreen.kt`
- **Neden bu şekilde yapıldı:** Faz 6'daki (2026-07-14) "bu ekrana ikinci bir
  konum aboneliği kurulmasın" kararı kullanıcı isteğiyle bilinçli olarak
  gözden geçirildi — ancak Maps ekranındaki gibi TAM bir izin isteme +
  sürekli `LocationRequest` akışı kurmak yerine "sessiz kontrol" tercih
  edildi: `VehicleDetailRoute` izin diyaloğu AÇMIYOR, yalnızca mevcut izin
  durumunu okuyor; izin varsa `fusedClient.lastLocation` ile TEK SEFERLİK
  bir okuma yapılıyor (abonelik yok). İzin yoksa harita yalnızca aracı
  gösteriyor, kullanıcı noktası basılmıyor. Haritadaki tek araç marker'ı
  için yeni bir veri modeli yazmak yerine mevcut `NearbyVehicle` tipi
  yeniden kullanıldı (`VehicleDetailContract.State`'ten `toMapVehicle()`
  ile eşleniyor) — `RencarMap`'e dokunulmadı, Maps akışında regresyon
  riski oluşturulmadı.
- **Kendi kontrolüm:** `./gradlew :app:compileDebugKotlin` ile derlendi,
  BUILD SUCCESSFUL (yalnızca projede zaten var olan `hiltViewModel()`
  deprecation uyarısı). Cihazda görsel/runtime testi (mini haritada aracın
  ve mavi konum noktasının göründüğü) henüz yapılmadı.
- **Sıradaki adım:** Emülatörde/cihazda bir araca tıklayıp detay ekranında
  mini haritanın aracı doğru konumda gösterdiğini, konum izni verilmişse
  mavi noktanın da göründüğünü doğrulamak.

### 2026-07-15 — Araç detay ekranına canlı konum (Socket.IO `/ws/locations`, `my-vehicle`) eklendi

- **Ne yapıldı:** Bir PR yorumunda (`halitkalayci`) paylaşılan sözleşme
  (`/ws/locations` namespace, handshake'te `auth.token`, yalnız müşterinin
  aktif kiralamasındaki aracı bildiren `my-vehicle` event'i, aktif kiralama
  yoksa event hiç gelmez) referans alınarak Araç Detay ekranındaki mini
  haritanın araç konumu artık canlı güncelleniyor. Yeni `VehicleLocationSocketClient`
  (`io.socket:socket.io-client:2.1.1`), `/ws/locations`'a bağlanıp `my-vehicle`
  event'ini `Flow<VehicleLocationUpdate>` (vehicleId + GeoPoint) olarak
  sunuyor. `VehicleDetailViewModel` bu akışı dinliyor, gelen `vehicleId`
  ekranın kendi `vehicleId`'siyle eşleşirse `state.vehicleLocation`
  güncelleniyor (eşleşmezse — kullanıcı başka bir aracı incelerken kendi
  aktif kiralaması başka bir araçtaysa — yok sayılıyor).
- **Değişen/yeni dosyalar:** `gradle/libs.versions.toml`, `app/build.gradle.kts`
  (yeni bağımlılık: `io.socket:socket.io-client:2.1.1`),
  `data/network/VehicleLocationSocketClient.kt` (yeni),
  `feature/maps/detail/VehicleDetailViewModel.kt`
  (`VehicleDetailContract.kt`'ye dokunulmadı — `vehicleLocation: GeoPoint?`
  alanı zaten mevcuttu).
- **Neden bu şekilde yapıldı:** Paylaşılan referans kod (`RideLocationClient.kt`)
  bu depoyla uyuşmayan bir paket (`com.rencar.app.*`), var olmayan bir
  `SessionManager` sınıfı ve var olmayan bir `VehiclePoint` modeli
  kullanıyordu — Agent.md §2.2 (uydurma yasağı) gereği olduğu gibi
  kopyalanmadı; sözleşme (event adı/payload şekli) korunarak proje mevcut
  `GeoPoint`, `TokenStore.accessToken` (senkron property) ve
  `AuthRepository.refresh()` (var olan token yenileme) üzerine kuruldu.
  Token süresi dolduğunda tek seferlik yenileme + yeniden bağlanma denemesi
  var; o da başarısız olursa akış sessizce kapanıyor (Effect/Toast YOK —
  bu ekranda REST'ten gelen başlangıç konumu zaten gösterildiğinden,
  canlı katmanın sessiz başarısız olması UX'i bozmuyor). Socket.IO istemci
  versiyonu (2.1.1, Engine.IO v4 uyumlu) backend'in gerçek Socket.IO sunucu
  sürümü teyit edilemediğinden varsayım olarak kullanıcı onayıyla seçildi;
  bağlantı gerçek cihazda/backend'e karşı henüz doğrulanmadı.
- **Kendi kontrolüm:** `./gradlew :app:compileDebugKotlin` ile derlendi,
  BUILD SUCCESSFUL (yalnızca projede zaten var olan `hiltViewModel()`
  deprecation uyarıları). Runtime/canlı bağlantı testi (gerçek bir aktif
  kiralamayla `my-vehicle` event'inin gelip haritanın hareket ettiği)
  HENÜZ YAPILMADI — bunun için müsait bir araçta gerçek bir kiralama
  başlatmak gerekiyor.
- **Sıradaki adım:** Bir CUSTOMER hesabıyla gerçek bir kiralama başlatıp
  (rezervasyon → foto akışı → start) Araç Detay ekranını açık tutarak
  backend'in `my-vehicle` event'i gönderdiğini ve mini haritadaki marker'ın
  buna göre hareket ettiğini doğrulamak. Ayrıca socket.io-client 2.1.1'in
  backend'in gerçek Socket.IO sürümüyle el sıkışabildiği runtime'da teyit
  edilmeli.

### 2026-07-15 — hiltViewModel() deprecation uyarısı tamamen giderildi

- **Ne yapıldı:** Bir önceki girdide "zaten var olan uyarı" olarak not
  edilen `hiltViewModel()` deprecation'ı araştırıldı: fonksiyon
  `androidx.hilt:hilt-navigation-compose` paketinden yeni bir artifact'a
  (`androidx.hilt:hilt-lifecycle-viewmodel-compose`, paket
  `androidx.hilt.lifecycle.viewmodel.compose`) taşınmış — hem düz
  `hiltViewModel()` hem de assisted-injection `creationCallback` overload'ı
  yeni artifact'ta mevcut. Projede eski paket yalnızca `hiltViewModel()`
  çağırmak için kullanıldığından (nav-graph scoping gibi başka bir
  kullanım yoktu), eski bağımlılık kaldırılıp yenisiyle TAM değiştirildi;
  4 Route dosyasındaki import güncellendi.
- **Değişen dosyalar:** `gradle/libs.versions.toml` (`hiltNavigationCompose`
  versiyon anahtarı `hiltLifecycleViewmodelCompose` olarak yeniden
  adlandırıldı, kütüphane girdisi yeni artifact'a çevrildi),
  `app/build.gradle.kts`, `feature/auth/login/LoginRoute.kt`,
  `feature/auth/otp/OtpRoute.kt`, `feature/maps/MapsRoute.kt`,
  `feature/maps/detail/VehicleDetailRoute.kt` (yalnızca import satırı).
  6 dosya olduğundan Agent.md §2.1'deki 5 dosya limiti aşıldı; kullanıcıdan
  ek onay alınarak tek batch'te uygulandı (değişiklikler birbirine sıkı
  bağlı olduğundan bölünmesi yarım kalmış bir derleme riski doğururdu).
- **Neden bu şekilde yapıldı:** Yeni versiyon numarası (1.4.0) mevcut
  `hiltNavigationCompose` versiyon değeriyle aynı olduğundan (aynı
  androidx.hilt release train) yeni bir versiyon anahtarı eklemek yerine
  mevcut anahtar yeniden adlandırıldı — iki ayrı versiyon sabiti tutmak
  gereksiz olurdu. Eski `hilt-navigation-compose` bağımlılığı projede
  başka hiçbir yerde kullanılmadığından tamamen kaldırıldı (yanında
  bırakmak kullanılmayan bir bağımlılık biriktirirdi).
- **Kendi kontrolüm:** `./gradlew :app:compileDebugKotlin` ile derlendi,
  BUILD SUCCESSFUL. `hiltViewModel()` deprecation uyarıları tamamen
  kayboldu; geriye yalnızca bu görevle ilgisiz, projede önceden var olan
  `Icons.Filled.List` deprecation uyarısı kaldı.
- **Sıradaki adım:** Yok — bu görev kapandı. Bir sonraki oturum, bir
  önceki girdideki "canlı konum runtime testi" ile devam edebilir.

### 2026-07-16 — Splash ekranı + başlangıç yönlendirme mantığı (5 dosya)

- **Ne yapıldı:** Uygulama açılışında Home/Onboarding/Login kararını veren bir
  Splash özelliği sıfırdan eklendi: `data/local/OnboardingPreferences.kt`
  (DataStore tabanlı `hasSeenOnboarding` okuma/yazma), `feature/splash/`
  altında `SplashContract.kt` (State/boş Intent/3 Effect), `SplashViewModel.kt`
  (`TokenStore` + `OnboardingPreferences` inject edilen `@HiltViewModel`),
  `SplashScreen.kt` (statik logo + uygulama adı, animasyonsuz) ve
  `SplashRoute.kt` (`hiltViewModel()` ile 3 effect'i 3 nav callback'ine
  bağlıyor). Karar mantığı: `async`/`await` ile 800ms minimum gecikme ve
  gerçek token/onboarding kontrolü paralel çalışıyor, ikisi de bitmeden effect
  gönderilmiyor. NavHost'a bağlama ve `OnboardingViewModel`'in
  `hasSeenOnboarding` yazma mantığı bilinçli olarak KAPSAM DIŞI bırakıldı
  (kullanıcı onayıyla, ayrı bir sonraki batch).
- **Değişen dosyalar (yeni):** `data/local/OnboardingPreferences.kt`,
  `feature/splash/SplashContract.kt`, `feature/splash/SplashViewModel.kt`,
  `feature/splash/SplashScreen.kt`, `feature/splash/SplashRoute.kt`.
- **Neden bu şekilde yapıldı:** `OnboardingPreferences`, `TokenStore.kt`'nin
  izlediği desenin (private top-level `Context.xDataStore` delegate +
  `@Singleton @Inject constructor(@ApplicationContext)`) birebir aynısı, ama
  `TokenStore`'daki `AtomicReference` bellek önbelleklemesi BİLİNÇLİ OLARAK
  eklenmedi — o önbellekleme yalnızca senkron çalışan `AuthInterceptor`
  yüzünden gerekliydi, `OnboardingPreferences`'ın böyle bir senkron
  tüketicisi yok. `SplashContract.Intent` boş sealed interface bırakıldı
  (Splash kullanıcıdan hiç girdi almıyor) ama mvi-contracts.md'nin
  Route/Screen imza kuralına uymak için tip olarak korundu. Token geçerliliği
  `tokenStore.accessToken != null && tokenStore.readRefreshToken() != null`
  ile kontrol ediliyor — bu, token'ın VAR olduğunu gösterir, SÜRESİNİN
  DOLMADIĞINI değil; bu bilinçli bir sınırlama, `AuthInterceptor`'ın 401
  sonrası refresh mekanizmasının süresi dolmuş durumu telafi edeceği
  varsayılıyor (kullanıcı onayıyla kabul edildi — bkz. aşağıdaki bilinen
  sınırlama). `TokenStore.kt`'ye DOKUNULMADI (bu batch'in 5 dosya limitine
  dahil değildi, mevcut `accessToken`/`readRefreshToken()` yeterliydi).
  Logo için `R.mipmap.ic_launcher_foreground` denendi ama bu bir
  `drawable` kaynağı olduğundan derleme hatası verdi (`Unresolved
reference`); `R.mipmap.ic_launcher` ile değiştirildi.
- **Kendi kontrolüm:** `./gradlew :app:compileDebugKotlin` ile derlendi,
  BUILD SUCCESSFUL (Hilt/KSP grafiği `SplashViewModel` → `TokenStore` +
  `OnboardingPreferences` enjeksiyonuyla sorunsuz üretildi). NavHost'a
  bağlanmadığından runtime/UI testi bu oturumda YAPILAMADI (bilinçli
  olarak sıradaki NavHost-bağlama batch'ine bırakıldı).
- **Bilinen sınırlama (sıradaki NavHost batch'inde not edilmeli):** Süresi
  dolmuş ama hâlâ diskte/bellekte duran bir `accessToken` ile açılışta
  yanlışlıkla Home'a yönlendirme riski var — `resolveDestination()` token'ın
  sadece VARLIĞINA bakıyor, JWT süresine bakmıyor.

### 2026-07-16 — Splash NavHost'a bağlandı + Onboarding "bir daha gösterme" kaydı (3 dosya)

- **Ne yapıldı:** `RenCarNavHost.kt`'de `startDestination` `ONBOARDING`'den
  `SPLASH`'e değiştirildi; yeni `composable(SPLASH)` bloğu `SplashRoute`'un
  3 effect'ini (`onNavigateToHome`/`onNavigateToOnboarding`/`onNavigateToLogin`)
  ilgili hedeflere bağladı, üçü de `popUpTo(SPLASH){inclusive=true}` kullanıyor
  (geri tuşuyla Splash'e dönüp döngüye girme riskini önlemek için).
  `OnboardingViewModel`, `OnboardingPreferences` inject eden bir
  `@HiltViewModel`'e çevrildi; `handlePrimaryAction()` artık son sayfada
  `NavigateToLogin` effect'ini göndermeden ÖNCE
  `onboardingPreferences.setHasSeenOnboarding(true)` çağırıyor (aynı
  `viewModelScope.launch` bloğu içinde, sıralama garantili).
- **Değişen dosyalar:** `navigation/RenCarNavHost.kt`,
  `feature/onboarding/OnboardingViewModel.kt`,
  `feature/onboarding/OnboardingRoute.kt`.
- **Neden bu şekilde yapıldı:** Kullanıcı bu batch'i 2 dosyayla talep etmişti
  (NavHost + OnboardingViewModel), ama `OnboardingViewModel`'i Hilt'e
  taşımak (constructor'a `OnboardingPreferences` enjekte edebilmek için)
  `OnboardingRoute.kt`'nin de `androidx.lifecycle.viewmodel.compose.viewModel()`
  (no-arg reflection factory) yerine `hiltViewModel()` kullanmasını teknik
  olarak ZORUNLU kılıyordu — aksi halde runtime'da constructor bulunamadığı
  için çökerdi. Bu, uygulamaya geçmeden önce kullanıcıya açıkça sunuldu ve 3. dosya olarak onaylandı. `Onboarding`'in kullanılmayan `NavigateToHome`
  effect'ine (talimat gereği) dokunulmadı. Splash'tan çıkan üç yönün de
  `popUpTo(SPLASH)` kullanması, kullanıcının önceki batch'te onayladığı
  planın "ÖNEMLİ" notuyla birebir örtüşüyor.
- **Kendi kontrolüm:** `./gradlew :app:compileDebugKotlin` ile derlendi,
  BUILD SUCCESSFUL (Hilt/KSP grafiği `OnboardingViewModel` →
  `OnboardingPreferences` enjeksiyonuyla sorunsuz üretildi). Emülatör/cihazda
  uçtan uca runtime testi (Splash → Onboarding/Login/Home akışının gerçekten
  çalıştığı, "bir daha gösterme" bayrağının kalıcı olduğu) bu oturumda
  YAPILMADI.
- **Hatırlatma (önceki batch'ten taşınan, hâlâ geçerli bilinen sınırlama):**
  `SplashViewModel.resolveDestination()` token'ın yalnızca VARLIĞINA bakıyor,
  JWT süresinin dolup dolmadığına bakmıyor; süresi dolmuş bir token'la
  açılışta yanlışlıkla Home'a düşme riski hâlâ mevcut, bu batch'te
  çözülmedi.

### 2026-07-16 — Ehliyet + Selfie doğrulama: network/repository katmanı (4 dosya)

- **Ne yapıldı:** `docs/api/openapi.json`'daki `POST /license/upload` (front+back+selfie,
  üçü de zorunlu, tek istekte multipart) ve `GET /license/status` uçları için
  Retrofit sözleşmesi kuruldu: `LicenseResponseDto`/`LicenseStatusResponseDto`,
  `LicenseApiService` (projenin ilk `@Multipart` kullanımı), bunu sağlayan
  `NetworkModule.provideLicenseApiService`, ve `AuthRepository`'deki
  `AuthResult<T>` desenini yeniden kullanan `LicenseRepository`
  (`upload(front: Uri, back: Uri, selfie: Uri)`, `getStatus()`). UI/ekran
  değişiklikleri, kamera/galeri seçici entegrasyonu ve ConfirmationScreen
  bilinçli olarak KAPSAM DIŞI bırakıldı.
- **Değişen dosyalar (yeni):** `data/network/dto/LicenseDtos.kt`,
  `data/network/LicenseApiService.kt`, `data/repository/LicenseRepository.kt`.
  **Değişen dosya:** `di/NetworkModule.kt` (`provideLicenseApiService` eklendi).
- **Neden bu şekilde yapıldı:** `status` alanı, kullanıcı onayıyla projenin
  `role`/`type`/`transmission`/`status` konvansiyonuyla tutarlı olarak Kotlin
  enum değil `String` tutuldu (backend yeni bir status değeri döndürürse Gson
  deserialization'ının çökmesini önlemek için). `LicenseStatusResponseDto`'da
  openapi şemasına uygun olarak `selfieImageUrl` YOK (yalnız `LicenseResponseDto`'da
  var). `Uri`→`MultipartBody.Part` dönüşümü için `LicenseRepository`'ye
  `@ApplicationContext Context` inject edildi (`TokenStore`'un zaten kullandığı
  desenle tutarlı); `ContentResolver.openInputStream` `Dispatchers.IO` üzerinde
  okunuyor (ana thread'i bloklamamak için). Görsellerin üç ekranda toplanıp son
  ekranda birleştirilmesi için gereken state paylaşım mekanizması bu batch'te
  UYGULANMADI; kullanıcıyla nav-graph-scoped paylaşılan bir ViewModel
  (`getBackStackEntry` + `hiltViewModel(viewModelStoreOwner=...)`) üzerinde
  anlaşıldı — `Uri`'lerin nav argümanı olarak taşınması (URI izin kaybı/encode
  kırılganlığı riski nedeniyle) tercih edilmedi; somutlaştırma sonraki
  UI-bağlama batch'ine bırakıldı.
- **Kendi kontrolüm:** `./gradlew :app:compileDebugKotlin` ile derlendi, BUILD
  SUCCESSFUL (tek uyarı: `@ApplicationContext` ile ilgili, projede `TokenStore.kt`
  gibi diğer dosyalarda da zaten var olan genel bir Kotlin gelecek-uyumluluk
  uyarısı, bu değişiklikle ilgisiz). Yeni dosyalar henüz hiçbir ViewModel'den
  çağrılmadığından runtime/network testi bu batch'te YAPILAMADI.

### 2026-07-16 — Ehliyet+Selfie akışı için nav-graph-scoped paylaşılan ViewModel altyapısı (2 dosya)

- **Ne yapıldı:** Bir önceki batch'te (network/repository katmanı) kullanıcıyla
  üzerinde anlaşılan mimari karar uygulandı: `feature/auth/LicenseFlowViewModel.kt`
  (yeni) — `frontUri`/`backUri`/`selfieUri` (üçü de `Uri?`) tutan bir
  `@HiltViewModel`, üçü de doluyken `LicenseRepository.upload()`'ı çağıran
  `uploadIfReady()` fonksiyonu içeriyor. `RenCarNavHost.kt`'de `LICENSE_VERIFICATION`
  ve `SELFIE_VERIFICATION` route'ları `navigation(startDestination=..., route="license-flow")`
  ile bir nav-graph altında gruplandı; her iki composable bloğu içinde
  `navController.getBackStackEntry("license-flow")` + `hiltViewModel(viewModelStoreOwner=parentEntry)`
  deseniyle paylaşılan `LicenseFlowViewModel` örneği alınıyor. `ConfirmationRoute`
  bilinçli olarak bu nav-graph'ın DIŞINDA bırakıldı (kendi başına `GET /license/status`
  çağıracağı için paylaşılan state'e bağımlı değil). `LicenseViewModel`/`SelfieViewModel`
  ve bunların Route'ları bu batch'te KASITLI OLARAK DEĞİŞTİRİLMEDİ — paylaşılan
  ViewModel şu an her iki composable bloğunda alınıyor ama Route'lara parametre
  olarak geçilmiyor (yalnızca scoping mekanizmasının derlendiği/çalıştığı kanıtlandı).
- **Değişen dosyalar (yeni):** `feature/auth/LicenseFlowViewModel.kt`.
  **Değişen dosya:** `navigation/RenCarNavHost.kt`.
- **Neden bu şekilde yapıldı:** `LicenseFlowViewModel`, `feature/auth/license/` veya
  `feature/auth/selfie/` yerine domain kökü `feature/auth/`'a konuldu — çünkü tek bir
  feature'a değil, `auth` domain'i içindeki iki feature'a birden ait paylaşılan bir
  "veri havuzu". Kullanıcının bu batch'ten önce `docs/decisions.md`'ye eklediği
  "Nav-Graph-Scoped Paylaşılan ViewModel'ler MVI Contract Kullanmaz" kararı gereği bu
  ViewModel'de Intent/Effect YOK, doğrudan fonksiyon çağrılarıyla (`setFrontUri`,
  `setBackUri`, `setSelfieUri`, `uploadIfReady`) kontrol ediliyor — yalnızca State
  (`_state`/`state` MutableStateFlow/StateFlow çifti, projenin genel deseniyle
  tutarlı). `uploadIfReady()`, `LoginViewModel.handleSendCode()`'daki mevcut
  `AuthResult.Success`/`AuthResult.Error` tüketim desenini birebir tekrar ediyor
  (`isUploading`/`isUploaded`/`uploadError` state alanlarını güncelliyor). `Uri`'lerin
  nav argümanı olarak taşınması (önceki batch'te reddedilen alternatif) yine
  kullanılmadı. `CONFIRMATION` composable'ı nav-graph dışında bırakıldığından
  Register/OTP'nin mevcut `navigate(LICENSE_VERIFICATION)` çağrıları DEĞİŞMEDİ —
  Compose Navigation iç içe bir grafiğin child route'una doğrudan navigasyonu
  native destekliyor.
- **Kendi kontrolüm:** `./gradlew :app:compileDebugKotlin` ile derlendi, BUILD
  SUCCESSFUL (Hilt/KSP grafiği `LicenseFlowViewModel` → `LicenseRepository`
  enjeksiyonuyla sorunsuz üretildi; `hiltViewModel(viewModelStoreOwner=parentEntry)`
  çağrısı derleme hatası vermeden `NavBackStackEntry`'yi kabul etti). Görülen tek iki
  uyarı bu değişiklikle ilgisiz, projede önceden var olan uyarılar (`LicenseRepository.kt`
  `@ApplicationContext` hedef uyarısı, `BottomNavItem.kt` `Icons.Filled.List`
  deprecation). Beklenen bir "unused variable" (`licenseFlowViewModel`) uyarısı
  derleyici çıktısında GÖRÜNMEDİ ama kod hâlâ kullanılmıyor — bu bilinçli ve geçici,
  sonraki batch Route imzalarına parametre eklediğinde ortadan kalkacak. Emülatör/cihazda
  runtime testi (License→Selfie→Confirmation akışının önceki davranışla birebir aynı
  çalıştığı, geri/ileri navigasyonda aynı `LicenseFlowViewModel` örneğinin korunduğu)
  bu batch'te YAPILAMADI.

### 2026-07-16 — Ehliyet görsel seçici altyapısı: Coil + FileProvider (Batch 1, 4 dosya)

- **Ne yapıldı:** LicenseScreen'i gerçek kamera/galeri seçiciye bağlamanın ön koşulu
  olan altyapı kuruldu: Coil 3 (`coil-compose`) bağımlılığı eklendi (projenin ilk
  görsel önizleme kütüphanesi) ve kamera çekimi için `androidx.core.content.FileProvider`
  tanımlandı (`AndroidManifest.xml`'e `<provider>` + `res/xml/file_paths.xml`'e
  `cache-path`). İlk denemede Coil 3.5.0 seçilmişti ama bu sürüm Kotlin 2.4.0 ile
  derlenmiş `kotlin-stdlib`'i transitive olarak çekiyor; projenin Kotlin derleyicisi
  (2.2.10) bu metadata sürümünü okuyamadığından `compileDebugKotlin` "incompatible
  metadata version" hatasıyla çöktü. Coil'in resmi değişiklik geçmişi kontrol edilerek
  Kotlin 2.2.0 ile derlenmiş Coil 3.3.0'a düşürüldü, derleme sorunsuz geçti.
- **Değişen dosyalar:** `gradle/libs.versions.toml` (coil versiyonu + `coil-compose`
  kütüphane girdisi), `app/build.gradle.kts` (`implementation(libs.coil.compose)`),
  `app/src/main/AndroidManifest.xml` (`FileProvider` provider tanımı,
  `${applicationId}.fileprovider` authority). **Yeni dosya:** `app/src/main/res/xml/file_paths.xml`
  (`cache-path name="license_images" path="license_images/"`).
- **Neden bu şekilde yapıldı:** FileProvider'ın kamera fotoğraflarını `cacheDir/license_images/`
  altına yazması tercih edildi (kalıcı depolama gerekmiyor, `Selfie`/upload batch'i
  bu dosyaları yükledikten sonra ihtiyaç kalmıyor). Coil'in `coil3.compose.AsyncImage`
  API'si (paket adı Coil 2'den farklı olarak `coil3` oldu) yerel `content://`/`file://`
  Uri'lerini ek bir network modülüne (`coil-network-*`) gerek kalmadan çözebildiğinden
  bu batch'te sadece `coil-compose` eklendi. Kamera için `CAMERA` izni eklenmedi:
  `ActivityResultContracts.TakePicture()` örtük intent ile sistem kamera uygulamasını
  açıyor, izni o uygulama kendi yönetiyor. Galeri için de `READ_EXTERNAL_STORAGE`
  eklenmedi: `PickVisualMedia` sistem foto seçici olduğundan scoped-storage'a tabi değil.
- **Kendi kontrolüm:** `./gradlew :app:compileDebugKotlin` ve ardından tam
  `./gradlew :app:assembleDebug` ile derlendi, ikisi de BUILD SUCCESSFUL. Birleştirilmiş
  manifest dosyası (`app/build/intermediates/merged_manifest/debug/.../AndroidManifest.xml`)
  okunarak `<provider>` girdisinin `authorities="com.turkcell.rencar_pair.fileprovider"`
  olarak doğru birleştiği doğrulandı. Runtime/UI testi (gerçek kamera/galeri açma) bu
  batch'te kapsam dışı — henüz hiçbir Route bu altyapıyı çağırmıyor (Batch 2'nin işi).

### 2026-07-16 — LicenseScreen gerçek kamera/galeri seçiciye bağlandı (Batch 2, 5 dosya)

- **Ne yapıldı:** `LicenseContract.State`'teki `isFrontUploaded`/`isBackUploaded` artık
  ayrı boolean alanlar değil, `frontUri`/`backUri: Uri?` alanlarından türetilen computed
  property (varsayılan `true` hatası bu şekilde kökten düzeldi — `Uri` varsayılan `null`
  olduğundan `isFrontUploaded` varsayılan `false`). Yeni `isContinueEnabled` computed
  property eklendi (ikisi de dolu değilse `false`); "Devam Et" butonu artık buna göre
  `enabled` oluyor ve `handleContinue()`'a guard clause eklendi. Yeni Intent'ler
  (`PickFrontImage`/`PickBackImage` → dialog tetikler, `FrontImageSelected`/
  `BackImageSelected(uri)` → seçim sonucu State'e yazar) ve Effect'ler
  (`ShowFrontImageSourceDialog`/`ShowBackImageSourceDialog`) eklendi. `LicenseScreen`'de
  önceden salt-okunur olan ön yüz satırı, arka yüzle aynı tıklanabilir dropzone'a
  çevrildi; ikisi de dolu olduğunda Coil `AsyncImage` ile gerçek küçük resim önizlemesi
  gösteriyor (üstte "Yüklendi" rozeti overlay). `LicenseRoute`, artık zorunlu
  `licenseFlowViewModel: LicenseFlowViewModel` parametresi alıyor;
  `ActivityResultContracts.TakePicture`/`PickVisualMedia` launcher'ları, basit bir
  `AlertDialog` ("Kameradan Çek" / "Galeriden Seç") ve seçilen `Uri`'yi hem
  `licenseFlowViewModel.setFrontUri()/setBackUri()`'ye (Selfie batch'inin de okuyacağı
  paylaşılan state) hem `viewModel.onIntent(...ImageSelected(uri))`'ye (bu ekranın kendi
  önizleme state'i) yazan köprü mantığı eklendi. `RenCarNavHost.kt`'de daha önce alınıp
  hiç geçilmeyen `licenseFlowViewModel` artık `LicenseRoute(...)`'a parametre olarak
  veriliyor.
- **Değişen dosyalar:** `feature/auth/license/LicenseContract.kt`,
  `feature/auth/license/LicenseViewModel.kt`, `feature/auth/license/LicenseScreen.kt`,
  `feature/auth/license/LicenseRoute.kt`, `navigation/RenCarNavHost.kt`.
- **Neden bu şekilde yapıldı:** Kamera/galeri seçimi platform (Activity) düzeyinde bir
  kaygı olduğundan (`registerForActivityResult`, `FileProvider`) bu mantık ViewModel'e
  değil Route'a kondu — ViewModel yalnızca "hangi taraf için dialog gösterilsin" kararını
  Effect ile iletiyor, dialogdan sonra gerçek launcher çağrısı Route'ta kalıyor; bu,
  mvi-overview.md'nin Route'a verdiği "ViewModel ile köprü" rolüyle tutarlı. Seçilen
  `Uri` bilinçli olarak İKİ yere birden yazılıyor: `LicenseFlowViewModel` (üç ekran
  arasında paylaşılan, upload'da kullanılacak kalıcı kaynak — önceki batch'te kurulan
  mimari karar) ve `LicenseContract.State` (bu ekranın kendi Coil önizlemesi için) —
  tekrar gibi görünse de ikisinin sorumluluğu farklı ve `LicenseViewModel`'in
  `LicenseFlowViewModel`'e bağımlı olması (farklı ViewModelStore scope'ları nedeniyle)
  teknik olarak pratik değildi. Kullanıcıyla netleştirilen karar gereği "Devam Et"
  butonunun ön/arka yüz olmadan aktif kalması bu batch'te kapatıldı; selfie'nin kendi
  butonu Selfie batch'inin kapsamında kalacak. `AlertDialog`'da "Galeriden Seç"
  `dismissButton` slotuna kondu (iptal değil, ikinci gerçek seçenek) — ayrı bir custom
  dialog yazmak bu ölçekte gereksiz olurdu.
- **Kendi kontrolüm:** `./gradlew :app:compileDebugKotlin` ve tam `./gradlew :app:assembleDebug`
  ile derlendi, ikisi de BUILD SUCCESSFUL, yeni uyarı yok. Emülatör/cihazda runtime/UI
  testi (kamera açma, galeri seçme, önizlemenin gerçekten göründüğü, "Devam Et"in
  disabled/enabled geçişi) bu oturumda YAPILAMADI — sadece derleme ve manifest birleştirme
  doğrulandı.

### 2026-07-16 — SelfieScreen gerçek kameraya + LicenseFlowViewModel'e bağlandı, gerçek upload tetikleniyor (Batch 1, 5 dosya)

- **Ne yapıldı:** `SelfieContract.State`'teki sahte `isSelfieUploaded: Boolean`
  (tıklamayla anında `true` olan) kaldırıldı; yerine `selfieUri: Uri?`,
  `isUploading: Boolean`, `uploadError: String?` ve `isContinueEnabled`
  computed property'si eklendi. Yeni Intent'ler: `CaptureSelfie` (kamerayı
  tetikler — galeri seçeneği YOK, selfie için sadece kamera anlamlı),
  `SelfieImageSelected(uri)`, `UploadStateChanged(isUploading, isUploaded,
uploadError)` (bu sonuncusu `VehicleDetailContract.Intent.LocationChanged`
  ile aynı desende: Route'un başka bir ViewModel'den — burada
  `LicenseFlowViewModel` — okuduğu state'i kendi ViewModel'ine ilettiği
  köprü Intent'i). `SelfieRoute`, artık zorunlu `licenseFlowViewModel`
  parametresi alıyor, License batch'indeki desenle birebir aynı
  `TakePicture` + `FileProvider` kamera akışını kuruyor (dialog YOK, doğrudan
  kamera açılıyor), çekilen `Uri`'yi hem `licenseFlowViewModel.setSelfieUri()`'ye
  hem kendi `SelfieViewModel`'ine yazıyor, `licenseFlowViewModel.state`'i
  `LaunchedEffect` ile izleyip `UploadStateChanged` Intent'iyle senkronize
  ediyor. "Devam Et" tıklanınca `TriggerUpload` effect'i Route'ta
  `licenseFlowViewModel.uploadIfReady()`'i çağırıyor; `isUploading` true iken
  buton disabled + `CircularProgressIndicator`, `isUploaded` true olunca
  otomatik Confirmation'a navigate, `uploadError` doluysa mevcut
  Login/Otp/Maps/VehicleDetail deseniyle aynı Toast gösteriliyor (dead-end
  effect bırakılmadı). `SelfieScreen`'deki dropzone artık Coil `AsyncImage`
  ile gerçek selfie önizlemesi gösteriyor. `RenCarNavHost.kt`'de daha önce
  alınıp hiç geçilmeyen `licenseFlowViewModel`, `SelfieRoute`'a parametre
  olarak verildi.
- **Değişen dosyalar:** `feature/auth/selfie/SelfieContract.kt`,
  `feature/auth/selfie/SelfieViewModel.kt`, `feature/auth/selfie/SelfieRoute.kt`,
  `feature/auth/selfie/SelfieScreen.kt`, `navigation/RenCarNavHost.kt`.
- **Neden bu şekilde yapıldı:** Kullanıcıyla üzerinde anlaşıldığı üzere ortak
  bir `ui/components/ImageSourcePicker.kt` soyutlaması BİLİNÇLİ OLARAK bu
  batch'e alınmadı — çıkarılacak olsa bile `LicenseRoute.kt`'nin de o ortak
  dosyayı kullanacak şekilde refactor edilmesi gerekiyordu (aksi halde tek
  tüketicili bir soyutlama olurdu), bu da Batch 1'i 5 dosya sınırının üstüne
  çıkarırdı (Agent.md §2.1). Bunun yerine kamera/`FileProvider` mantığı bu
  batch'te `SelfieRoute.kt` içinde License'daki desenin geçici bir kopyası
  olarak yazıldı; ortak dosyaya çıkarma + `LicenseRoute.kt` refactor'ü ayrı,
  kullanıcının bu batch derlenip test edildikten sonra ayrıca onaylayacağı
  bir Batch 2 olarak planlandı (henüz UYGULANMADI). Selfie'nin çekilen
  fotoğrafı, yeni bir `selfie_images` FileProvider cache-path'i eklemek
  yerine (bu da `file_paths.xml`'e dokunup dosya sayısını 6'ya çıkarırdı)
  bilinçli olarak mevcut `cacheDir/license_images/` dizinine `selfie_` dosya
  adı önekiyle yazıldı — bu dizin adı yalnızca bir önbellek klasörü ismi,
  kullanıcıya hiçbir şekilde görünmüyor. 413 (dosya çok büyük) gibi ayırt
  edici bir hata mesajı için `LicenseRepository.kt`'ye dokunma seçeneği
  kullanıcıya soruldu; kullanıcı 5 dosyada kalmayı ve mevcut jenerik "Sunucu
  hatası (kod: N)." mesaj desenini korumayı tercih etti — `LicenseRepository.kt`
  bu batch'te DEĞİŞMEDİ. Hata gösterimi için Snackbar yerine mevcut Toast
  deseni (Login/Otp/Maps/VehicleDetail'deki `ShowError` ile aynı) kullanıldı
  — Snackbar, projede hiçbir ekranda kurulu olmayan bir `SnackbarHostState`/
  `Scaffold` altyapısı gerektirdiğinden büyük bir sapma olurdu.
  `SelfieViewModel`, `LicenseFlowViewModel`'e constructor'dan inject
  EDİLMEDİ — nav-graph scoping'i olmayan bir ViewModel'in nav-graph-scoped
  bir ViewModel'e doğrudan bağımlı olması pratik değil (License batch'inde
  `LicenseViewModel`/`LicenseFlowViewModel` için verilen kararla aynı
  gerekçe); köprüleme tamamen `SelfieRoute.kt`'de kaldı.
- **Kendi kontrolüm:** `./gradlew :app:compileDebugKotlin` ve tam
  `./gradlew :app:assembleDebug` ile derlendi, ikisi de BUILD SUCCESSFUL, yeni
  uyarı yok. Emülatör/cihazda runtime/UI testi (gerçek kamera açma, selfie
  önizlemesinin göründüğü, "Devam Et"in loading/disabled geçişi, gerçek
  upload'ın Confirmation'a yönlendirdiği, hata Toast'ının göründüğü) bu
  oturumda YAPILAMADI — sadece derleme doğrulandı.

### 2026-07-16 — Rezervasyon Onayı + Aktif Kiralama akışı: Reservations network/repository katmanı (Batch 1, 4 dosya)

- **Ne yapıldı:** Araç Detay ekranındaki "Kilidi Aç" → Rezervasyon Onayı →
  Aktif Kiralama zincirinin ilk adımı olarak `POST /reservations` için
  Retrofit sözleşmesi kuruldu — DTO'lar, `ReservationsApiService`,
  `ReservationsRepository`. Alanlar `docs/api/openapi.json`'daki
  `CreateReservationDto`/`ReservationVehicleSummaryDto`/`ReservationResponseDto`
  şemalarıyla birebir.
- **Değişen dosyalar:** `data/network/dto/ReservationDtos.kt` (yeni),
  `data/network/ReservationsApiService.kt` (yeni),
  `data/repository/ReservationsRepository.kt` (yeni), `di/NetworkModule.kt`
  (`provideReservationsApiService` eklendi).
- **Neden bu şekilde yapıldı:** `ReservationsRepository`, `VehiclesRepository.kt`'deki
  inline `try/catch IOException` + `AuthResult<T>` deseni birebir izlenerek
  yazıldı (projede repository'ler arası ortak bir `safeCall` soyutlaması yok,
  `AuthRepository.kt`'deki `safeCall` yalnızca kendi içinde private — bu yüzden
  yeni repository'ler de aynı inline deseni tekrar ediyor, mevcut konvansiyona
  uyum için). Bu, kullanıcıyla onaylanan 6 batch'lik plandan (bkz. plan dosyası
  `agent-md-docs-api-openapi-json-bu-ekran-starry-finch.md`) Batch 1'dir; Rentals
  katmanı, Quote endpoint'i ve ekranlar ayrı batch'lerde gelecek.
- **Kendi kontrolüm:** `./gradlew :app:compileDebugKotlin` BUILD SUCCESSFUL.
  Bu katman henüz hiçbir ekrandan çağrılmadığı için runtime/UI testi bu
  batch'te YAPILAMADI (henüz bir tüketicisi yok).

### 2026-07-16 — Rezervasyon Onayı + Aktif Kiralama akışı: Rentals network/repository katmanı (Batch 2, 4 dosya)

- **Ne yapıldı:** `POST /rentals`, `GET /rentals/active`, `POST /rentals/{id}/finish`
  için Retrofit sözleşmesi kuruldu — DTO'lar, `RentalsApiService`,
  `RentalsRepository`. `docs/api/openapi.json`'daki `FinishRentalResponseDto`
  şeması `RentalResponseDto` ile alan alan birebir aynı olduğu için ayrı bir
  DTO sınıfı açılmadı, `finishRental()` da `RentalResponseDto` döndürüyor.
- **Değişen dosyalar:** `data/network/dto/RentalDtos.kt` (yeni —
  `CreateRentalDto`, `RentalVehicleSummaryDto`, `RentalResponseDto`,
  `ActiveRentalResponseDto`), `data/network/RentalsApiService.kt` (yeni),
  `data/repository/RentalsRepository.kt` (yeni), `di/NetworkModule.kt`
  (`provideRentalsApiService` eklendi).
- **Neden bu şekilde yapıldı:** Yine `VehiclesRepository.kt`'deki inline
  `try/catch IOException` + `AuthResult<T>` deseni izlendi (bkz. Batch 1
  gerekçesi). Deprecated `startDate` alanı (backend'in "KULLANMAYIN, eski
  istemci uyumluluğu için tutuluyor" dediği alan) DTO'lara bilinçli olarak
  eklenmedi — Gson yanıttaki fazladan JSON alanlarını sessizce yok sayar, bu
  yüzden eksik bırakmak güvenli ve gereksiz alan eklemeden kaçınıyor. Bu,
  onaylanan 6 batch'lik planın (`agent-md-docs-api-openapi-json-bu-ekran-starry-finch.md`)
  Batch 2'sidir.
- **Kendi kontrolüm:** `./gradlew :app:compileDebugKotlin` BUILD SUCCESSFUL.
  Bu katman de henüz hiçbir ekrandan çağrılmıyor, runtime testi bu batch'te
  YAPILAMADI.

### 2026-07-16 — Rezervasyon Onayı + Aktif Kiralama akışı: Vehicles katmanına quote endpoint'i eklendi (Batch 3, 3 dosya)

- **Ne yapıldı:** `GET /vehicles/{id}/quote` (fiyat önizleme) mevcut
  `VehiclesApiService`/`VehiclesRepository`'ye eklendi. `QuoteResponseDto`
  (`vehicleId, plan, minutes, usageFee, startFee, serviceFee, estimatedTotal`)
  `openapi.json`'daki şemayla birebir.
- **Değişen dosyalar:** `data/network/dto/VehicleDtos.kt` (`QuoteResponseDto`
  eklendi), `data/network/VehiclesApiService.kt` (`getQuote` eklendi),
  `data/repository/VehiclesRepository.kt` (`getQuote(id, plan, minutes)`
  eklendi).
- **Neden bu şekilde yapıldı:** Quote, ayrı bir "Quotes" kaynağı değil,
  backend'de zaten `/vehicles/{id}/quote` altında yaşıyor; bu yüzden yeni bir
  `QuoteApiService`/`QuoteRepository` açmak yerine mevcut Vehicles
  katmanına eklendi — bu, ilgili tek endpoint için gereksiz bir dosya çifti
  açmaktan kaçınıyor. Onaylanan planın (`agent-md-docs-api-openapi-json-bu-ekran-starry-finch.md`)
  Batch 3'üdür.
- **Kendi kontrolüm:** `./gradlew :app:compileDebugKotlin` BUILD SUCCESSFUL.
  Henüz bir tüketicisi olmadığı için runtime testi YAPILAMADI.

### 2026-07-16 — Rezervasyon Onayı ekranı (MVI, Batch 4, 4 dosya)

- **Ne yapıldı:** Verilen tasarıma (araç özeti + Yakıt% rozeti, Dakikalık/
  Saatlik/Günlük plan seçici, ücret dökümü, şartlar checkbox'ı, alt sabit
  "Rezervasyonu Tamamla" butonu) birebir yeni `feature/rental/reservation/`
  paketi eklendi. `ReservationConfirmationViewModel`, açılışta aracı
  (`vehiclesRepository.getVehicle`) ve ilk quote'u (`getQuote(..., PER_MINUTE,
30)`) yüklüyor; plan değişince quote'u tekrar çekiyor. "Rezervasyonu
  Tamamla" → `reservationsRepository.createReservation` başarılıysa, seçili
  plan DAILY ise otomatik `rentalsRepository.createRental(..., endDate = şu
an + 1 gün)` çağırıp `NavigateToActiveRental` efektini gönderiyor; DAILY
  değilse (Dakikalık/Saatlik — foto akışı bu görevin kapsamı dışında)
  `NavigateBackWithMessage` ile bilgilendirip geri dönüyor.
- **Değişen dosyalar:** `feature/rental/reservation/ReservationConfirmationContract.kt`,
  `ReservationConfirmationViewModel.kt`, `ReservationConfirmationRoute.kt`,
  `ReservationConfirmationScreen.kt` (hepsi yeni).
- **Neden bu şekilde yapıldı:** `ViewModel`, `VehicleDetailViewModel.kt`'deki
  `@AssistedInject`/`@AssistedFactory` (vehicleId parametreli) deseniyle
  birebir kuruldu — `docs/decisions.md`'deki güncel param-taking ViewModel
  stratejisiyle uyumlu. Bu ekran, License/Selfie akışındaki gibi nav-graph-
  scoped paylaşılan bir ViewModel KULLANMIYOR — çünkü ekranlar arasında
  adım adım biriken ortak mutable state yok, her ekran kendi `vehicleId`/
  `rentalId` nav-arg'ından bağımsız veri çekiyor (`docs/decisions.md`'deki
  "Nav-Graph-Scoped Paylaşılan ViewModel" kuralı yalnızca çok adımlı, veri
  biriktiren akışlar için geçerli). Bu, kullanıcıyla netleştirilen kapsam
  kararlarına göre onaylanan 6 batch'lik planın Batch 4'üdür; DAILY dışı
  planlarda gerçek kiralama açma ve 4-yön foto akışı bilinçli olarak bu
  batch'e alınmadı.
- **Kendi kontrolüm:** `./gradlew :app:compileDebugKotlin` BUILD SUCCESSFUL —
  ilk denemede `ReservationConfirmationScreen.kt`'de yanlışlıkla eklenen
  `import androidx.compose.foundation.layout.weight` satırı derleme hatasına
  yol açtı (`weight`, Compose'da `RowScope`/`ColumnScope`'un gerçek üye
  fonksiyonu olduğu için ayrı import gerektirmiyor, yanlış import internal
  bir sembole çarpıyordu); import kaldırılınca derleme geçti. Bu ekran henüz
  navigasyona bağlanmadığı (Batch 6'da bağlanacak) için emülatörde açılış/
  akış testi bu batch'te YAPILAMADI.

### 2026-07-16 — Aktif Kiralama ekranı (MVI, Batch 5, 4 dosya)

- **Ne yapıldı:** Verilen 3. tasarıma (üstte yeşil noktalı "Kiralama aktif ·
  {marka} {model}" pili, harita, alt sheet'te "Geçen süre" büyük saat +
  "Anlık ücret"/"Mesafe" kartları + "Kilitle / Aç"/"Kiralamayı Bitir"
  butonları) birebir yeni `feature/rental/active/` paketi eklendi.
  `ActiveRentalViewModel`, `GET /rentals/active`'i 5 saniyede bir polling
  yapıyor (`mvi-viewmodel-rules.md`'deki `Job?` timer deseni, `onCleared()`'da
  iptal ediliyor), 404 gelirse (kiralama bitmiş) polling'i sessizce durduruyor.
  "Kiralamayı Bitir" → `POST /rentals/{id}/finish`, başarılıysa `NavigateToHome`
  efekti; "Kilitle / Aç" → kullanıcıyla netleşen kararla şimdilik yalnızca
  "Bu özellik yakında eklenecek." bilgi Toast'ı (placeholder).
- **Değişen dosyalar:** `feature/rental/active/ActiveRentalContract.kt`,
  `ActiveRentalViewModel.kt`, `ActiveRentalRoute.kt`, `ActiveRentalScreen.kt`
  (hepsi yeni).
- **Neden bu şekilde yapıldı:** Plandaki ilk varsayımın aksine,
  `ActiveRentalResponseDto`/`RentalVehicleSummaryDto` şemasında araç
  konumu (latitude/longitude) YOK — bu, `openapi.json`'da doğrulandı,
  uydurulmadı. Bunun yerine `VehicleDetailViewModel.kt`'deki mevcut desen
  aynen tekrarlandı: ilk konum `vehiclesRepository.getVehicle(vehicleId)`
  ile tek seferlik çekiliyor, sonrası `VehicleLocationSocketClient`
  (proje genelinde zaten kullanılan canlı konum soketi) ile güncelleniyor.
  Aynı `getVehicle` çağrısından `pricePerDay` de alınıp haritadaki marker
  etiketine (`RencarMap`'in `updateVehicles()`'ı `NearbyVehicle.pricePerDay`'i
  okuyor) yansıtıldı — aksi halde marker "₺0" gösterirdi. Haritadaki noktalı
  "rota/iz" çizgisi, backend'de bir konum geçmişi endpoint'i olmadığı için
  BİLİNÇLİ OLARAK eklenmedi (kullanıcıya plan aşamasında bildirilen sınırlama).
- **Kendi kontrolüm:** `./gradlew :app:compileDebugKotlin` BUILD SUCCESSFUL.
  Bu ekran henüz navigasyona bağlanmadığı (Batch 6'da bağlanacak) için
  emülatörde polling/finish akışı testi bu batch'te YAPILAMADI.

### 2026-07-16 — Rezervasyon Onayı + Aktif Kiralama akışı: Navigasyon + Araç Detay entegrasyonu (Batch 6, 3 dosya)

- **Ne yapıldı:** `RenCarNavHost.kt`'ye `reservation-confirmation/{vehicleId}`
  ve `rental-active/{rentalId}` route'ları eklendi. Araç Detay ekranındaki
  "Kilidi Aç" butonu artık gerçek bir yere gidiyor: `VehicleDetailRoute.kt`'de
  `Effect.ShowUnlockConfirmed -> onNavigateToReservationConfirmation(vehicleId)`
  olarak bağlandı (Contract/ViewModel'e dokunulmadı — mevcut `canUnlock`
  mesafe/durum koşulu aynen korunuyor). Rezervasyon Onayı → Aktif Kiralama
  geçişinde `popUpTo(HOME)` ile Araç Detay + Rezervasyon Onayı ekranları
  yığından temizleniyor (geri tuşu doğrudan Ana ekrana dönüyor). "Kiralamayı
  Bitir" sonrası `NavigateToHome` efekti de `popUpTo(HOME){inclusive=true}`
  ile yığını sıfırlayıp Ana ekrana dönüyor. `VehicleDetailScreen.kt`'de
  "Rezerve Et" butonu artık koşulsuz `enabled = false` — bu görevde her zaman
  pasif.
- **Değişen dosyalar:** `navigation/RenCarNavHost.kt`,
  `feature/maps/detail/VehicleDetailRoute.kt`,
  `feature/maps/detail/VehicleDetailScreen.kt`.
- **Neden bu şekilde yapıldı:** Kullanıcıyla netleşen karara göre "Rezerve Et"
  bu görevde tamamen devre dışı bırakıldı (ayrı bir görevde ele alınacak);
  Contract/ViewModel değiştirilmedi çünkü `canReserve` computed alanı başka
  yerde (`StatusBadge`) hâlâ kullanılıyor, sadece buton görünümü sabitlendi.
  Reservation/Rental ekranları arasında License/Selfie'deki gibi nav-graph-
  scoped paylaşılan bir ViewModel KURULMADI çünkü her ekran kendi
  `vehicleId`/`rentalId` nav-arg'ından bağımsız veri çekiyor — aralarında
  adım adım biriktirilen ortak state yok (bkz. Batch 4 gerekçesi).
- **Kendi kontrolüm:** `./gradlew :app:compileDebugKotlin` BUILD SUCCESSFUL.
  Bu, onaylanan 6 batch'lik planın son batch'idir; tüm zincir artık
  navigasyona bağlı. Emülatörde uçtan uca happy-path testi (Harita → Araç
  Detay → Kilidi Aç → Rezervasyon Onayı → Günlük plan → Rezervasyonu Tamamla
  → Aktif Kiralama → Kiralamayı Bitir → Ana ekran) bu oturumda henüz
  YAPILMADI — ayrıca doğrulanacak.

### 2026-07-16 — Kilidi Aç/Rezerve Et etkileşim akışı emülatör testi sonrası revize edildi (2 dosya)

- **Ne yapıldı:** Emülatörde gerçek backend'e karşı test edilirken kullanıcı
  akışı netleştirdi: "Kilidi Aç" artık bir sayfaya GEÇMİYOR — sadece aracı
  yerel olarak "açık" (`isUnlocked`) durumuna getirip bir onay Toast'ı
  gösteriyor. Bu durumda "Rezerve Et" butonu aktifleşiyor; Rezervasyon
  Onayı ekranına geçiş artık "Rezerve Et"e basınca oluyor. Ayrıca kullanıcı
  isteğiyle "Kilidi Aç"ın 100m mesafe şartı tamamen kaldırıldı — buton artık
  yalnızca araç MÜSAİT olduğunda aktif (mesafeden bağımsız).
- **Değişen dosyalar:** `feature/maps/detail/VehicleDetailContract.kt`
  (`State.isUnlocked` eklendi, `canUnlock` mesafe şartı kaldırıldı,
  `UNLOCK_MAX_DISTANCE_METERS` kullanılmadığı için silindi),
  `feature/maps/detail/VehicleDetailViewModel.kt` (`handleUnlockClicked`
  artık `isUnlocked=true` set ediyor; `handleReserveClicked` artık
  `canReserve` yerine `isUnlocked`'a bakıyor), `feature/maps/detail/VehicleDetailRoute.kt`
  (`ShowReservationConfirmed` efekti Rezervasyon Onayı'na navigate ediyor,
  `ShowUnlockConfirmed` artık sadece bilgi Toast'ı gösteriyor — bir önceki
  batch'teki kablolama tam tersine çevrildi), `feature/maps/detail/VehicleDetailScreen.kt`
  ("Rezerve Et" butonu artık `enabled = state.isUnlocked`, sabit `false`
  değil).
- **Neden bu şekilde yapıldı:** Kullanıcı, gerçek cihazda test ederken
  orijinal kararın (Kilidi Aç → doğrudan Rezervasyon Onayı, Rezerve Et hep
  pasif) istediği ürün akışıyla uyuşmadığını fark etti ve düzeltti. Mesafe
  şartının kaldırılması da kullanıcının açık talebi — artık "Kilidi Aç"
  gerçek bir donanım kilidi açmıyor (backend'de böyle bir endpoint yok,
  tamamen yerel/simüle bir durum), bu yüzden fiziksel yakınlık şartının bir
  güvenlik anlamı kalmadı.
- **Kendi kontrolüm:** `./gradlew :app:compileDebugKotlin` ve
  `./gradlew :app:installDebug` ile derlenip emülatöre kuruldu, BUILD
  SUCCESSFUL. Kullanıcı emülatörde canlı backend'e karşı akışı bizzat test
  etti (giriş, harita, araç listesi, Kilidi Aç/Rezerve Et etkileşimi).

### 2026-07-17 — Araç Teslim Fotoğrafı (4 yön) ekranı + Dakikalık/Saatlik akışının tamamlanması (11 dosya, 3 batch)

- **Ne yapıldı:** Daha önce kapsam dışı bırakılan foto akışı eklendi. Verilen
  tasarıma (Ön/Arka/Sol/Sağ 2x2 kare, "X / 4 çekildi" sayacı, hasar uyarı
  banner'ı, alt "Kiralamayı Başlat · N foto kaldı" butonu) birebir yeni
  `feature/rental/photos/` paketi eklendi. `RentalsApiService`/`RentalsRepository`'ye
  `POST /rentals/{id}/photos` (multipart: `side` + `file`), `GET /rentals/{id}/photos`
  (yarım kalan akışı devralmak için) ve `POST /rentals/{id}/start` eklendi.
  `ReservationConfirmationViewModel`, Dakikalık/Saatlik planlarda artık
  sadece rezervasyon oluşturup geri dönmüyor — rezervasyon sonrası
  `POST /rentals` ile kiralamayı PREPARING durumunda açıp yeni foto ekranına
  yönlendiriyor. 4 fotoğrafın tamamı yüklenince "Kiralamayı Başlat" aktifleşip
  `POST /rentals/{id}/start` çağırıyor ve mevcut Aktif Kiralama ekranına
  geçiyor — böylece Dakikalık/Saatlik planlar da artık DAILY gibi uçtan uca
  çalışıyor.
- **Değişen dosyalar:** Batch A — `data/network/dto/RentalDtos.kt`,
  `data/network/RentalsApiService.kt`, `data/repository/RentalsRepository.kt`
  (hepsi düzenlendi: `RentalPhotoDto`/`RentalPhotosStateDto` + 3 yeni endpoint).
  Batch B (yeni) — `feature/rental/photos/VehiclePhotosContract.kt`,
  `VehiclePhotosViewModel.kt`, `VehiclePhotosRoute.kt`, `VehiclePhotosScreen.kt`.
  Batch C — `navigation/RenCarNavHost.kt` (`vehicle-photos/{rentalId}/{vehicleId}`
  route'u eklendi), `feature/rental/reservation/ReservationConfirmationContract.kt`
  (`NavigateBackWithMessage` kaldırıldı, `NavigateToVehiclePhotos` eklendi),
  `ReservationConfirmationViewModel.kt` (`createUsageRental` eklendi),
  `ReservationConfirmationRoute.kt`.
- **Neden bu şekilde yapıldı:** Kamera/`FileProvider` akışı, License/Selfie
  batch'lerindeki AYNI desen tekrar kullanılarak yazıldı (`TakePicture` +
  mevcut `cacheDir/license_images/` önbellek klasörüne `delivery_<yön>_`
  dosya adı önekiyle yazma) — yeni bir FileProvider `cache-path` eklemek
  `AndroidManifest.xml`/`file_paths.xml`'e dokunup dosya sayısını artırırdı,
  Selfie batch'inde de aynı gerekçeyle bu yol tercih edilmişti. `@AssistedInject`'te
  iki `String` tipli assisted parametre (`rentalId`, `vehicleId`) olduğu için
  KSP derleme hatası verdi ("duplicate @Assisted type"); `@Assisted("rentalId")`/
  `@Assisted("vehicleId")` kimlikleriyle çözüldü. Zaten yüklenmiş fotoğraflar
  `GET /rentals/{id}/photos`'un döndürdüğü uzak `imageUrl`'ler `Uri.parse()`
  ile doğrudan `AsyncImage`'e veriliyor (Coil hem yerel hem uzak URI'leri aynı
  şekilde yükleyebiliyor) — böylece uygulama yeniden açılırsa yarım kalan akış
  devralınabiliyor, ayrı bir "resume" mantığı yazmaya gerek kalmadı.
- **Kendi kontrolüm:** `./gradlew :app:compileDebugKotlin` (her 3 batch
  sonunda) ve `./gradlew :app:installDebug` BUILD SUCCESSFUL, emülatöre
  kuruldu. Emülatörde gerçek kamera + gerçek `POST /rentals/{id}/photos`
  yükleme akışı bu oturumda henüz TEST EDİLMEDİ — kullanıcı tarafından
  ayrıca doğrulanacak.

### 2026-07-17 — Aktif Yolculuk ekranı yeniden tasarlandı: harita rota izi + verilen tasarıma göre yeniden düzen (4 dosya, 3 batch)

- **Ne yapıldı:** Verilen tasarıma göre Aktif Kiralama ekranı baştan
  düzenlendi: sabit üst bölüm ("Aktif Yolculuk" / "Süre ve ücret canlı
  işliyor" + araç bilgi kartı: marka/model, plaka · plan etiketi), ortada
  harita, alt sheet'te "Geçen süre" + "Başlangıç: dd.MM.yyyy HH:mm" +
  "Anlık ücret"/"Mesafe" kartları + açılış ücreti notu + mevcut butonlar.
  `RencarMap` bileşenine yeni opsiyonel `routePoints: List<GeoPoint>`
  parametresi ve noktalı (dashed) `LineLayer` eklendi; `ActiveRentalViewModel`
  artık `VehicleLocationSocketClient`'tan (`/ws/locations` — zaten mevcuttu)
  gelen HER konum güncellemesini `routePoints` listesine biriktirip
  (aynı nokta tekrarını atlayarak, son 300 nokta ile sınırlı) haritaya
  aktarıyor; böylece aracın ekran açıkken izlediği yol noktalı çizgi olarak
  görünüyor.
- **Değişen dosyalar:** Batch 1 — `feature/maps/RencarMap.kt` (route
  source/layer eklendi, geriye dönük uyumlu — diğer ekranlar parametre
  vermediği için etkilenmedi). Batch 2 — `feature/rental/active/ActiveRentalContract.kt`
  (`plate`, `plan`, `startFee`, `startedAtIso`, `routePoints` alanları +
  `planLabel`/`formattedStartedAt`/`formattedStartFeeNote` computed'ları),
  `ActiveRentalViewModel.kt` (poll'da bu alanlar dolduruluyor, soket
  callback'i rota biriktiriyor). Batch 3 — `feature/rental/active/ActiveRentalScreen.kt`
  (tam yeniden yazıldı).
- **Neden bu şekilde yapıldı:** Backend'de araç konum GEÇMİŞİ döndüren bir
  endpoint yok (`docs/api/openapi.json`'da yalnızca anlık konum: `/ws/locations`
  soketi ve müşteriye kapalı `/admin/locations` anlık snapshot'ı) — bu
  yüzden noktalı iz UYDURULMADI, yalnızca ekran açıkken soketten gerçekten
  alınan noktalar biriktirilerek çiziliyor (bilinen sınırlama: ekran
  açılmadan önceki geçmiş gösterilemez). Tasarımdaki geri (`<`) ok ikonu
  BİLİNÇLİ OLARAK eklenmedi — onaylanan 4 dosyalık batch listesi
  `ActiveRentalRoute.kt` ve `RenCarNavHost.kt`'yi içermiyordu; geri
  navigasyonu için yeni bir Effect eklemek bu iki dosyaya da dokunmayı
  gerektirip onaylanan kapsamı aşardı. Kullanıcı isterse ayrı bir küçük
  batch olarak eklenebilir.
- **Kendi kontrolüm:** `./gradlew :app:compileDebugKotlin` (her 3 batch
  sonunda) ve `./gradlew :app:installDebug` BUILD SUCCESSFUL, emülatöre
  kuruldu. Gerçek bir aktif kiralama üzerinde haritada rota iziminin
  gerçekten çizildiği bu oturumda TEST EDİLMEDİ — kullanıcı tarafından
  doğrulanacak.

### 2026-07-17 — AuthInterceptor'a otomatik token yenileme (401 -> /auth/refresh -> tekrar dene) eklendi (1 dosya)

- **Ne yapıldı:** Erişim token'ı 15 dakikada bir dolduğunda uygulamanın
  test oturumları sürekli kesiliyordu (kullanıcı bunu üç kez ayrı ayrı
  fark etti — "sunucu hatası", "araçlar gözükmüyor" şikayetleri aslında
  hep bu kökten geliyordu). `AuthInterceptor.kt`'ye şu mantık eklendi: bir
  istek 401 dönerse (ve bu isteğin kendisi zaten `/auth/refresh` değilse),
  interceptor `AuthRepository.refresh()`'i senkron çağırıp (`runBlocking`)
  yeni access token alır ve ORİJİNAL isteği bu token'la BİR KEZ tekrar
  dener; yenileme başarısızsa orijinal 401 yanıtı olduğu gibi döner (sonsuz
  döngü riski yok — tekrar denenen isteğin sonucu tekrar kontrol edilmiyor).
- **Değişen dosyalar:** `data/network/AuthInterceptor.kt` (düzenlendi).
- **Neden bu şekilde yapıldı:** `AuthInterceptor`, `OkHttpClient` kurulumunun
  bir parçası; `AuthRepository` ise `AuthApiService` üzerinden AYNI
  `Retrofit`/`OkHttpClient`'a bağımlı — bu yüzden `AuthRepository`'yi
  doğrudan `@Inject constructor`'a almak dairesel bağımlılık (`OkHttpClient
-> AuthInterceptor -> AuthRepository -> AuthApiService -> Retrofit ->
OkHttpClient`) oluştururdu. Bunu kırmak için `dagger.Lazy<AuthRepository>`
  kullanıldı (Dagger/Hilt'in bu tür döngüleri kırmak için standart deseni;
  `Lazy<T>` sarmalayıcı, `OkHttpClient` inşa edilirken `AuthRepository`'yi
  gerçekten OLUŞTURMAZ, yalnızca ilk `.get()` çağrısında — yani gerçekten
  bir 401 geldiğinde — inşa eder). Eşzamanlı birden fazla isteğin aynı anda
  401 alıp YARIŞ HALİNDE birbirini geçersiz kılan (backend'in refresh token
  ROTATION + reuse-detection kuralı: aynı refresh token iki kez kullanılırsa
  TÜM oturum zinciri iptal edilir) iki ayrı yenileme tetiklemesini önlemek
  için `refreshAccessToken` fonksiyonu `@Synchronized` yapıldı ve kilide
  girince önce `tokenStore.accessToken`'ın hâlâ "bayat" (401'e sebep olan)
  token olup olmadığı kontrol ediliyor — başka bir istek zaten yenilemişse
  gereksiz ikinci bir `/auth/refresh` çağrısı YAPILMIYOR, doğrudan güncel
  token kullanılıyor.
- **Kendi kontrolüm:** `./gradlew :app:compileDebugKotlin` BUILD SUCCESSFUL
  (Hilt/KSP dairesel bağımlılık hatası vermedi, `Lazy<AuthRepository>`
  çözümü doğrulandı). `./gradlew :app:installDebug` ile emülatöre kuruldu
  ve GERÇEK backend'e karşı canlı doğrulandı: süresi dolmuş bir token'la
  `GET /vehicles` isteği atıldı, logcat'te tam beklenen zincir gözlendi —
  `<-- 401` → otomatik `--> POST /auth/refresh` → `<-- 200` (yeni token
  çifti) → orijinal istek yeni token'la tekrar denendi → `<-- 200` (20
  araç döndü). Kullanıcı müdahalesi olmadan (yeniden giriş yapmadan) akış
  kendiliğinden düzeldi.

### 2026-07-17 — Rota izi hatası + Kilitle/Aç akışı + fotoğraf ekranında Vazgeç + yükleme göstergeleri (10 dosya, 4 batch)

- **Ne yapıldı:** Kullanıcının test sırasında bulduğu dört ayrı eksik/hata
  giderildi. **(A)** `RencarMap.kt`'deki noktalı rota çizgisi hiç
  görünmüyordu; sebebi `lineDasharray(arrayOf(0f, 1.6f))` — 0 uzunluklu
  tire segmenti MapLibre'de görünmez çizgi üretiyor; `arrayOf(1f, 1.6f)`
  olarak düzeltildi. **(B)** Aktif Yolculuk ekranında "Kiralamayı Bitir"
  artık `state.canFinish` (`isUnlocked && !isFinishing`) şartına bağlı —
  Araç Detay ekranındaki Kilidi Aç → Rezerve Et deseniyle birebir aynı
  mantıkla, önce "Kilitle / Aç"a basılması gerekiyor. **(C)** Araç Durumu
  (4 foto) ekranındaki geri butonu artık sadece geri gitmiyor —
  `DELETE /rentals/{id}` (`cancelRental`, yeni) ile arkadaki PREPARING
  kiralamayı gerçekten iptal edip öyle çıkıyor; bu, kullanıcının canlı
  testte yaşadığı "yarım bırakılmış kiralama tüm hesabı kilitliyor" (409
  "Aktif bir kiralamanız varken rezervasyon yapamazsınız") tuzağını kalıcı
  kapatıyor. **(F)** Rezervasyon Onayı ekranında araç bilgisi/ücret
  yüklenirken artık boş/sıfır değerler yerine dönen bir gösterge + durum
  metni gösteriliyor.
- **Değişen dosyalar:** Batch A — `feature/maps/RencarMap.kt`. Batch B —
  `feature/rental/active/ActiveRentalContract.kt` (`isUnlocked`, `canFinish`
  eklendi), `ActiveRentalViewModel.kt`, `ActiveRentalScreen.kt`. Batch C —
  `data/network/RentalsApiService.kt` (`DELETE rentals/{id}` eklendi),
  `data/repository/RentalsRepository.kt` (`cancelRental` eklendi),
  `feature/rental/photos/VehiclePhotosContract.kt` (`isCancelling`,
  `canCancel` eklendi), `VehiclePhotosViewModel.kt` (`NavigateBack` artık
  önce iptal ediyor), `VehiclePhotosScreen.kt`. Batch F —
  `feature/rental/reservation/ReservationConfirmationScreen.kt`.
- **Neden bu şekilde yapıldı:** (C) için "Vazgeç" ayrı bir buton/Intent
  olarak eklenmedi — zaten var olan geri oku ve `NavigateBack`
  Intent/Effect'i yeniden kullanıldı, çünkü bu ekrandan fotoğraf
  tamamlanmadan çıkmanın TEK anlamlı sonucu kiralamayı iptal etmektir
  (yarım kalan bir PREPARING kaydını sessizce arkada bırakmanın hiçbir
  kullanım senaryosu yok, sadece hesabı kilitliyor). Ödeme adımı (kiralama
  bitince cüzdan/kart ile ödeme) kullanıcıyla netleşen kararla bu kapsama
  DAHİL EDİLMEDİ — ayrı, büyük bir özellik olarak bilinçli şekilde
  ertelendi.
- **Kendi kontrolüm:** `./gradlew :app:compileDebugKotlin` (her batch
  sonunda) ve `./gradlew :app:installDebug` BUILD SUCCESSFUL, emülatöre
  kuruldu. Rota izinin gerçekten göründüğü, Kiralamayı Bitir'in Kilitle/
  Aç'a kadar pasif kaldığı ve fotoğraf ekranından "Vazgeç" ile çıkınca
  hesabın gerçekten temizlendiği bu oturumda kullanıcı tarafından henüz
  TEST EDİLMEDİ.

### 2026-07-17 — Ana ekrana "Aktif yolculuğunuz var" banner'ı eklendi (6 dosya, 2 batch)

- **Ne yapıldı:** Kullanıcı canlı testte gerçek bir sorun buldu: Aktif
  Yolculuk ekranından bir kez ayrılınca (örn. Ana ekrana dönünce) geri
  dönmenin HİÇBİR yolu yoktu — sadece rezervasyon akışını yeni
  tamamladığınızda oraya düşülüyordu. Bunu kapatmak için Ana ekran (Harita
  sekmesi) artık açılışta `GET /rentals/active`'i kontrol ediyor; aktif bir
  kiralama varsa arama çubuğunun altında "Aktif yolculuğunuz var — {marka}
  {model} · Devam Et" banner'ı çıkıyor, tıklanınca doğrudan Aktif Yolculuk
  ekranına gidiyor.
- **Değişen dosyalar:** Batch 1 — `feature/maps/MapsContract.kt`
  (`activeRentalId`, `activeRentalVehicleLabel` state alanları,
  `ActiveRentalBannerClicked` Intent, `NavigateToActiveRental` Effect),
  `MapsViewModel.kt` (`RentalsRepository` inject edildi, `loadActiveRental()`
  eklendi), `MapsScreen.kt` (`ActiveRentalBanner` composable'ı eklendi),
  `MapsRoute.kt` (`onNavigateToActiveRental` parametresi eklendi). Batch 2 —
  `navigation/MainScaffold.kt`, `navigation/RenCarNavHost.kt` (parametre
  zincirleme ile gerçek navigasyona bağlandı).
- **Neden bu şekilde yapıldı:** Aktif kiralama kontrolü Splash'e değil Ana
  ekrana eklendi — Splash zaten sadece token geçerliliğine bakıp Ana ekrana
  yönlendiriyor (`docs/decisions.md`'deki mevcut sorumluluk ayrımını
  bozmamak için); ayrıca kullanıcı bilinçli olarak "Ana ekrana banner
  ekle" seçeneğini onayladı. `GET /rentals/active` 404 dönerse (aktif
  kiralama yoksa, beklenen/normal durum) hata gösterilmeden sessizce yok
  sayılıyor — bu her Ana ekran açılışında olağan bir durum, kullanıcıyı
  gereksiz hata mesajıyla rahatsız etmemek için.
- **Kendi kontrolüm:** `./gradlew :app:compileDebugKotlin` (her iki batch
  sonunda) ve `./gradlew :app:installDebug` BUILD SUCCESSFUL, emülatöre
  kuruldu. Banner'ın gerçekten göründüğü ve tıklanınca doğru kiralamaya
  götürdüğü bu oturumda kullanıcı tarafından henüz TEST EDİLMEDİ.

### 2026-07-17 — Ortak post-auth yönlendirme mekanizması: PostAuthNavigationResolver + AuthRepository.getMe() (Batch 1, 2 dosya)

- **Ne yapıldı:** Splash/OTP/Register'ın kullanıcının güncel role/ehliyet durumuna göre doğru
  ekrana (Home / ehliyet yükleme / ehliyet inceleme bekleme) yönlendirilmesi için tek bir ortak
  karar mekanizması kuruldu: `AuthRepository`'ye `GET /auth/me`'yi sarmalayan `getMe()`
  eklendi (uç zaten `AuthApiService`'te tanımlıydı ama hiçbir yerden çağrılmıyordu); yeni
  `domain/PostAuthNavigationResolver.kt` (yeni `domain/` paketi) — `AuthRepository.getMe()` ve
  `LicenseRepository.getStatus()`'u orkestre edip `PostAuthDestination` (Home/LicenseUpload/
  LicensePending) döndüren `@Singleton` bir sınıf. Bu batch, resolver'ı henüz hiçbir ekrana
  bağlamıyor — sadece altyapı.
- **Değişen dosyalar:** `data/repository/AuthRepository.kt` (`getMe()` eklendi).
  **Yeni dosya:** `domain/PostAuthNavigationResolver.kt`.
- **Neden bu şekilde yapıldı:** Kullanıcıyla netleşen karar gereği bu mantık `AuthRepository`'ye
  eklenen bir fonksiyon DEĞİL, ayrı bir sınıf oldu — repository'lerin tek sorumluluğu (kendi
  API'sini sarmalamak) korunsun, "hangi role/status hangi ekrana gider" kararı iki
  repository'nin ÜSTÜNDE bir orkestrasyon katmanına ait olsun diye. Proje ilk kez bir
  `domain/` paketi açtı (data/feature dışında üçüncü bir katman: ekrana bağlı olmayan,
  birden fazla repository'yi orkestre eden mantık). `role == "PENDING"` DIŞINDAKİ tüm
  roller (`CUSTOMER`, teknik olarak `ADMIN`) `Home`'a düşüyor — `ADMIN` için mobil
  istemcide tanımlı ayrı bir ekran olmadığından güvenli varsayılan. `NOT_SUBMITTED` ve
  `REJECTED` bilinçli olarak AYNI hedefe (`LicenseUpload`) gidiyor — ikisinde de yapılması
  gereken aynı (ehliyet yükleme ekranına gitmek); REJECTED'in `rejectReason`'ının
  gösterilmesi yalnızca Confirmation ekranının kendisini ilgilendiriyor (Batch 5). `getMe()`
  veya `getStatus()` ağ hatasıyla başarısız olursa kullanıcı onayıyla `Home` fallback'i
  seçildi (mevcut/önceki davranışla aynı risk seviyesi — token varsa zaten Home'a
  gidiliyordu). `role != "PENDING"` durumunda `getStatus()` HİÇ çağrılmıyor — gereksiz
  network isteği önlendi.
- **Kendi kontrolüm:** `./gradlew :app:compileDebugKotlin` BUILD SUCCESSFUL (Hilt/KSP grafiği
  `AuthRepository.getMe()` ve `PostAuthNavigationResolver`'ın `AuthRepository`+
  `LicenseRepository` enjeksiyonuyla sorunsuz üretildi). Bu iki dosya henüz hiçbir
  ViewModel'den çağrılmadığından runtime/network testi bu batch'te YAPILAMADI — Splash'in bu
  resolver'ı kullanacağı Batch 2'de gerçek backend'e karşı doğrulanacak.

### 2026-07-17 — Splash artık PostAuthNavigationResolver'ı kullanıyor + gerçek backend'e karşı 3 senaryo doğrulandı (Batch 2, 4 dosya)

- **Ne yapıldı:** `SplashViewModel.resolveDestination()` artık token varsa (önceki gibi)
  doğrudan `NavigateToHome` göndermek yerine `PostAuthNavigationResolver.resolve()`'u çağırıp
  `PostAuthDestination`'ı yeni Effect'lere çeviriyor: `SplashContract.Effect`'e
  `NavigateToLicenseVerification` ve `NavigateToConfirmation` eklendi; `SplashRoute.kt`'ye
  bu ikisi için 2 yeni nav callback parametresi eklendi; `RenCarNavHost.kt`'nin `SPLASH`
  composable bloğu bu 2 yeni callback'i sırasıyla `LICENSE_VERIFICATION` ve `CONFIRMATION`
  route'larına, mevcut 3 callback'le AYNI `popUpTo(SPLASH){inclusive=true}` deseniyle bağladı.
  Token yoksa (onboarding/login dalı) HİÇ değişmedi.
- **Değişen dosyalar:** `feature/splash/SplashContract.kt`, `feature/splash/SplashViewModel.kt`,
  `feature/splash/SplashRoute.kt`, `navigation/RenCarNavHost.kt`.
- **Neden bu şekilde yapıldı:** Batch 1'de kurulan `PostAuthNavigationResolver` aynen
  kullanıldı, yeni bir karar mantığı yazılmadı. Diğer 3 Splash Effect'i (`NavigateToHome`/
  `NavigateToOnboarding`/`NavigateToLogin`) ve onların `popUpTo` deseni hiç değiştirilmedi —
  yeni 2 Effect aynı deseni birebir tekrarlıyor (tutarlılık, ayrı bir istisna dalı yok).
- **Kendi kontrolüm:** `./gradlew :app:compileDebugKotlin` BUILD SUCCESSFUL. Kullanıcının
  açık talebiyle emülatörde (Pixel_6a) gerçek backend'e (`rencarv2.halitkalayci.com`) karşı
  3 senaryo UÇTAN UCA doğrulandı (Claude tarafından `adb`/`uiautomator` ile sürülerek —
  telefon input alanındaki bilinen scramble bug'ı `KEYCODE_MOVE_END` + digit-by-digit
  yazma ile aşıldı, OTP kutusu ve buton koordinatları `uiautomator dump` ile bulundu):
  - `+905550000401` (curl ile register edilen taze hesap, PENDING + NOT_SUBMITTED) →
    Login→OTP→force-stop→relaunch sonrası Splash doğru şekilde **Ehliyet Doğrulama**
    ekranına yönlendirdi.
  - `+905550000302` (curl ile lisans yüklenip UNDER_REVIEW yapılan hesap) → aynı akış
    sonrası Splash doğru şekilde **Onay (Confirmation)** ekranına yönlendirdi.
  - `+905550000102` (bilinen CUSTOMER test hesabı) → aynı akış sonrası Splash doğru şekilde
    **Ana ekrana (Home/Harita)** yönlendirdi.
    Üçü de beklenen `PostAuthDestination` dallanmasıyla birebir eşleşti.
- **Önemli bulgu (kapsam dışı, ayrı görev):** İlk denemede `+905550000301` adlı bir test
  hesabı NOT_SUBMITTED olarak kuruldu ama birkaç dakika sonra tekrar kontrol edildiğinde
  (kodumuza hiç dokunulmadan) durumu UNDER_REVIEW'e dönüşmüş bulundu — gerçek `front`/`back`
  görsel URL'leriyle birlikte. Bu, uygulama kodundan kaynaklanmıyor: `rencarv2.halitkalayci.com`
  paylaşımlı bir staging backend'i, muhtemelen başka bir oturum/süreç aynı öngörülebilir
  test numarası aralığını (`555000030x`) kullanıyor. **Hatırlatma:** bu backend'de sıralı/
  tahmin edilebilir telefon numaralarıyla (`55500003xx` gibi) test hesabı açmak, paralel
  kullanımda veriyi kirletebiliyor — ileride test hesabı açarken rastgele/geniş bir numara
  aralığı tercih edilmeli.

### 2026-07-17 — OTP'nin koşulsuz LICENSE_VERIFICATION bug'ı düzeltildi: OTP artık PostAuthNavigationResolver'ı kullanıyor (Batch 3, 4 dosya)

- **Ne yapıldı:** Görevin bildirdiği asıl bug düzeltildi. `OtpContract.kt`'ye
  `NavigateToLicenseVerification`/`NavigateToConfirmation` Effect'leri eklendi (mevcut
  `NavigateToHome`'un yanına). `OtpViewModel.kt`, başarılı `verifyOtp()` sonrası artık
  koşulsuz `NavigateToHome` göndermek yerine `PostAuthNavigationResolver.resolve()`'u çağırıp
  sonucu Effect'e çeviriyor (Splash'teki `resolvePostAuthEffect()` ile birebir aynı desen).
  `OtpRoute.kt`'ye 2 yeni nav callback parametresi eklendi. `RenCarNavHost.kt`'nin `OTP`
  composable bloğu artık 3 hedefe kablolu: `onNavigateToHome` artık gerçekten
  `RenCarDestinations.HOME`'a gidiyor (önceden LICENSE_VERIFICATION'a gidiyordu) ve YENİ
  olarak `popUpTo(LOGIN){inclusive=true}` kullanıyor (Login+Otp'yi backstack'ten temizler —
  Splash/Confirmation'ın Home'a geçişte zaten kullandığı hijyen deseniyle tutarlı);
  `onNavigateToLicenseVerification`/`onNavigateToConfirmation` ise Register'ın
  `onNavigateToLicenseVerification`'ıyla aynı şekilde `popUpTo` KULLANMIYOR (geri tuşuyla
  OTP ekranına dönülebilsin diye, mevcut davranışla tutarlı).
- **Değişen dosyalar:** `feature/auth/otp/OtpContract.kt`, `feature/auth/otp/OtpViewModel.kt`,
  `feature/auth/otp/OtpRoute.kt`, `navigation/RenCarNavHost.kt`.
- **Neden bu şekilde yapıldı:** Batch 1'de kurulan `PostAuthNavigationResolver` aynen yeniden
  kullanıldı — OTP ve Splash artık AYNI karar mantığını çağırıyor, kod tekrarı yok. `Home`
  dalına `popUpTo(LOGIN)` eklenmesi bilinçli bir ek düzeltme: kullanıcı onayıyla netleşen
  plana göre (bkz. plan dosyası), token zaten geçerli bir CUSTOMER oturumu kurulduğunda geri
  tuşuyla Login/Otp ekranlarına dönülmesinin hiçbir anlamı yok — bu, Splash/Confirmation'ın
  Home'a geçişte zaten uyguladığı deseni OTP'ye de getiriyor.
- **Kendi kontrolüm:** `./gradlew :app:compileDebugKotlin` ve `./gradlew :app:installDebug`
  BUILD SUCCESSFUL. Kullanıcının açık talebiyle emülatörde gerçek backend'e karşı ASIL BUG
  SENARYOSU doğrulandı: `+905550000102` (CUSTOMER) ile Login→OTP akışı SIFIRDAN (önbellekte
  token olmadan, `pm clear` sonrası) çalıştırıldı — OTP doğrulandığında uygulama artık
  doğrudan **Ana ekrana (Home/Harita)** düştü, önceki (düzeltilmeden önceki) davranışın
  aksine Ehliyet Doğrulama ekranına DÜŞMEDİ. Diğer iki dal (`LicenseUpload`/`LicensePending`)
  Batch 2'de aynı resolver ile zaten doğrulanmıştı, kod birebir aynı olduğundan bu batch'te
  tekrar test edilmedi.

### 2026-07-17 — Register de PostAuthNavigationResolver'ı kullanıyor: tutarlılık (Batch 4, 4 dosya)

- **Ne yapıldı:** `RegisterContract.kt`'ye `NavigateToConfirmation`/`NavigateToHome`
  Effect'leri eklendi (mevcut `NavigateToLicenseVerification`'ın yanına).
  `RegisterViewModel.kt`, başarılı `register()` sonrası artık koşulsuz
  `NavigateToLicenseVerification` göndermek yerine Splash/OTP'deki BİREBİR AYNI
  `resolvePostAuthEffect()` desenini kullanıp `PostAuthNavigationResolver.resolve()`'u
  çağırıyor. `RegisterRoute.kt`'ye 2 yeni nav callback parametresi eklendi.
  `RenCarNavHost.kt`'nin `REGISTER` composable bloğu artık 3 hedefe kablolu:
  `onNavigateToLicenseVerification` (değişmedi, popUpTo yok), YENİ
  `onNavigateToConfirmation` (popUpTo yok, aynı desen), YENİ `onNavigateToHome` —
  kullanıcının açıkça istediği gibi OTP'nin Home dalıyla TUTARLI olarak
  `popUpTo(LOGIN){inclusive=true}` kullanıyor (REGISTER, LOGIN'in üstünde push edildiğinden
  aynı mantık burada da geçerli: token zaten geçerli bir CUSTOMER oturumu kurulduysa geri
  tuşuyla Login/Register'a dönmenin anlamı yok).
- **Değişen dosyalar:** `feature/auth/register/RegisterContract.kt`,
  `feature/auth/register/RegisterViewModel.kt`, `feature/auth/register/RegisterRoute.kt`,
  `navigation/RenCarNavHost.kt`.
- **Neden bu şekilde yapıldı:** Bu batch bir bug düzeltmiyor — taze bir kayıt her zaman
  PENDING+NOT_SUBMITTED sonucunu ürettiğinden (`Home`/`Confirmation` dallarına gerçek kayıt
  akışıyla ulaşmak mümkün değil, backend zaten aynı email/telefonla ikinci kaydı
  reddediyor) — amaç saf tutarlılık: Splash/OTP/Register'ın hepsi artık AYNI karar
  mekanizmasını çağırıyor, "hangi role/status hangi ekrana gider" kararı tek bir yerde
  (`PostAuthNavigationResolver`) yaşıyor.
- **Kendi kontrolüm:** `./gradlew :app:compileDebugKotlin` ve `./gradlew :app:installDebug`
  BUILD SUCCESSFUL. Emülatörde gerçek backend'e karşı uçtan uca test edildi: taze bir
  hesap (`+905550000501`, `test.register.20260717@rencar.test`) Register formu üzerinden
  gerçekten oluşturuldu — kayıt sonrası uygulama beklenen şekilde **Ehliyet Doğrulama**
  ekranına düştü (resolver `PENDING`+`NOT_SUBMITTED` için doğru dala düşüyor,
  regresyon yok). `Home`/`Confirmation` dalları gerçek kayıt akışıyla ulaşılamaz
  olduğundan ayrıca test edilmedi — kod `resolvePostAuthEffect()` fonksiyonu Splash/OTP'de
  zaten doğrulanmış olan AYNI fonksiyon.

### 2026-07-17 — ConfirmationViewModel gerçek GET /license/status'a bağlandı: polling + APPROVED/REJECTED dallanması + Yeniden Yükle (Batch 5, 5 dosya)

- **Ne yapıldı:** Görevin son maddesi tamamlandı. `ConfirmationContract.kt`'deki tamamen boş
  `State(val unit: Unit = Unit)` kaldırıldı; yerine `status: String?`, `rejectReason: String?`,
  `isLoading: Boolean` alanları ve `isContinueEnabled`/`isRejected`/`isTerminal` computed
  property'leri eklendi (mvi-contracts.md'nin computed-property kuralına uygun).
  `ConfirmationViewModel.kt` artık plain `ViewModel()` değil `@HiltViewModel` —
  `LicenseRepository` inject ediliyor. `ActiveRentalViewModel.kt`'deki (mvi-viewmodel-
  rules.md §8) BİREBİR AYNI polling deseni (`Job?` + `while (isActive) { ...; delay(5000) }`,
  `onCleared()`'da iptal) kullanılarak `GET /license/status` her 5 saniyede bir tekrar
  çağrılıyor; `status` `APPROVED` veya `REJECTED` (terminal) olduğunda döngü kendiliğinden
  duruyor, `UNDER_REVIEW` iken (veya bir ağ hatası sonrası — hata durumunda `status`
  değişmediğinden döngü otomatik devam ediyor) tekrar denemeye devam ediyor. Yeni
  `Intent.ReuploadClicked` ve `Effect.NavigateToLicenseVerification` eklendi.
  `ConfirmationScreen.kt` artık `state`'e göre 3 farklı görünüm çiziyor: UNDER_REVIEW
  (mevcut tasarım, "Devam Et" disabled), APPROVED (yeşil değil ama birincil renkte
  checkmark, "Ehliyetin onaylandı, kiralamaya başlayabilirsin", bilgi banner'ı kayboluyor,
  "Devam Et" enabled), REJECTED (kırmızı `ErrorOutline` ikonu, `rejectReason` banner'da
  gösteriliyor, alt buton "Devam Et" yerine "Yeniden Yükle" oluyor).
  `ConfirmationRoute.kt` `viewModel()` yerine `hiltViewModel()` kullanıyor, yeni
  `onNavigateToLicenseVerification` callback'ini `NavigateToLicenseVerification` Effect'ine
  bağlıyor. `RenCarNavHost.kt`'nin `CONFIRMATION` bloğuna bu callback eklendi —
  `LICENSE_VERIFICATION`'a `popUpTo(CONFIRMATION){inclusive=true}` ile navigate ediyor
  (Confirmation'ı backstack'ten temizleyip License'ı yerine koyuyor).
- **Değişen dosyalar:** `feature/auth/confirmation/ConfirmationContract.kt`,
  `ConfirmationViewModel.kt`, `ConfirmationRoute.kt`, `ConfirmationScreen.kt`,
  `navigation/RenCarNavHost.kt`.
- **Neden bu şekilde yapıldı:** `LICENSE_VERIFICATION`'a `popUpTo(CONFIRMATION)` ile
  navigate etmek, Compose Navigation'ın iç içe `license-flow` grafiğine (LicenseFlowViewModel
  scope'lu) DIŞARIDAN doğrudan girişi native desteklediği PROGRESS.md'de zaten doğrulanmış
  bir kalıp olduğundan (bkz. 2026-07-16 kaydı) — bu, otomatik olarak TAZE bir
  `LicenseFlowViewModel` örneği (boş front/back/selfie URI'leri) yaratıyor, REJECTED sonrası
  yeniden yükleme için doğru davranış (eski reddedilen görseller tekrar kullanılmamalı).
  `status`/`rejectReason` ham `String` tutuldu (projenin genel konvansiyonuyla tutarlı,
  `LicenseStatusResponseDto` zaten böyle). Polling hata durumunda DURMUYOR (yalnızca terminal
  durumlarda duruyor) — geçici bir ağ kesintisinin kullanıcıyı sonsuza kadar "kontrol
  edilemedi" durumunda bırakmaması için, `ActiveRentalViewModel`'in 404 dışında hata almasa
  bile pollinge devam etme mantığıyla ruh olarak tutarlı.
- **Kendi kontrolüm:** `./gradlew :app:compileDebugKotlin` ve `./gradlew :app:installDebug`
  BUILD SUCCESSFUL. Emülatörde gerçek backend'e karşı, admin onaylama/reddetme uçları
  (`PATCH /admin/licenses/{id}/approve`, `.../reject`) kullanılarak İKİ CANLI POLLING
  SENARYOSU uçtan uca doğrulandı:
  - UNDER_REVIEW hesabı (`+905550000302`) ile Confirmation ekranı açıkken (disabled "Devam
    Et" + bekleme mesajı doğrulandı), admin API'siyle lisans REDDEDİLDİ — ekran hiçbir
    kullanıcı etkileşimi olmadan ~5 saniye içinde kendiliğinden REJECTED görünümüne geçti
    (kırmızı ikon, "Ehliyetin reddedildi", rejectReason banner'da doğru metinle göründü,
    buton "Yeniden Yükle"ye döndü). "Yeniden Yükle"ye basıldığında uygulama gerçekten
    BOŞ (yeni) bir Ehliyet Doğrulama ekranına döndü.
  - Taze bir hesap (`+905550000603`) ile Confirmation ekranı açıkken (yine UNDER_REVIEW
    görünümü doğrulandı), admin API'siyle lisans ONAYLANDI — ekran ~5 saniye içinde
    kendiliğinden APPROVED görünümüne geçti ("Ehliyetin onaylandı, kiralamaya
    başlayabilirsin", bilgi banner'ı kayboldu, "Devam Et" enabled oldu). "Devam Et"e
    basıldığında uygulama gerçekten Ana ekrana (Home/Harita) geçti.
    Her iki senaryo da polling mekanizmasının, durum dallanmasının ve navigasyonun gerçek
    backend'e karşı uçtan uca çalıştığını kanıtladı.
- **Yan not (test altyapısı, kod değil):** İlk reject denemesinde Türkçe karakterli
  (`ğ`/`ı` içeren) bir `reason` metni Windows/Git Bash terminal kodlama sorunu yüzünden
  backend'e bozuk UTF-8 (U+FFFD) olarak gitti; bu bir uygulama hatası değil, test
  ortamındaki `curl` çağrısının terminal kod sayfası sorunuydu — ASCII-güvenli bir metinle
  tekrarlanarak aşıldı. Uygulamanın kendisi `rejectReason`'ı olduğu gibi gösteriyor,
  encoding sorunu yalnızca bu oturumun admin-API test aracına özgüydü.

Bu, kullanıcının başlangıçta bildirdiği görevin TÜM maddeleri (ortak post-auth yönlendirme
mekanizması + Confirmation'ın gerçek API'ye bağlanması) tamamlandı ve gerçek backend'e karşı
uçtan uca doğrulandı anlamına gelir.

### 2026-07-18 — History (Kiralamalarım) için eksik 3 Rentals API'si eklendi: listMine/getRental/getStats (3 dosya)

- **Ne yapıldı:** Kiralamalarım ekranını gerçek API'ye bağlamadan önceki altyapı adımı olarak,
  `openapi.json`'da tanımlı ama hiç eklenmemiş üç uç eklendi: `GET /rentals` (`listMine`,
  kullanıcının tüm kiralamaları), `GET /rentals/{id}` (`getRental`, tek kiralama detayı),
  `GET /rentals/stats` (`getStats`, opsiyonel `month` query parametresiyle aylık özet).
  `RentalDtos.kt`'ye şemayla birebir `RentalStatsResponseDto` (month/tripCount/totalSpent/
  totalMinutes/totalKm) eklendi. `RentalsApiService.kt`'ye üç yeni Retrofit fonksiyonu
  eklendi. `RentalsRepository.kt`'ye bunları saran, mevcut `AuthResult<T>` +
  `try/catch (IOException)` desenine birebir uyan üç fonksiyon eklendi. Bu batch'te
  HistoryViewModel/Screen/HistoryMockSource'a KASITLI olarak dokunulmadı (kullanıcının açık
  talebiyle yalnızca altyapı; ekranın gerçek API'ye bağlanması ayrı bir batch).
- **Değişen dosyalar:** `data/network/dto/RentalDtos.kt`, `data/network/RentalsApiService.kt`,
  `data/repository/RentalsRepository.kt`.
- **Neden bu şekilde yapıldı:** `tripCount` openapi'de `type: number` (integer değil)
  olduğundan, projenin `distanceKm`/`durationMinutes` gibi diğer sayısal alanlarda zaten
  kullandığı `Double` konvansiyonuyla tutarlı tutuldu — `Int`'e çevirmek şemadan sapma
  olurdu. `di/NetworkModule.kt`'ye dokunulmadı çünkü `RentalsApiService` zaten oradan
  sağlanıyor, yeni bir provider gerekmiyor.
- **Kendi kontrolüm:** `./gradlew :app:compileDebugKotlin` ile derlendi, BUILD SUCCESSFUL
  (yalnızca projeyle ilgisiz, mevcut bir Kotlin derleyici uyarısı — `@Inject`/parametre hedefi
  hakkında). Yeni fonksiyonlar henüz hiçbir ViewModel'den çağrılmadığından runtime testi
  yapılmadı (bilinçli olarak sonraki batch'e bırakıldı).

### 2026-07-18 — HistoryViewModel gerçek RentalsRepository.listMine()'a bağlandı (3 dosya)

- **Ne yapıldı:** `HistoryViewModel`, `class HistoryViewModel : ViewModel()`den `@HiltViewModel`
  - `@Inject constructor(RentalsRepository)`e taşındı. `loadRentals()`, `MapsViewModel.
loadVehicles()` ile BİREBİR AYNI `isLoading` + `AuthResult` `when` deseniyle `GET /rentals`
    (`listMine()`) çağırıyor; sonuç `status == "COMPLETED"` olan kayıtlarla filtrelenip yeni bir
    `RentalResponseDto.toRentalRecord()` private uzantı fonksiyonuyla mevcut `RentalRecord`e
    eşleniyor (`dateLabel`, `ActiveRentalContract.kt`'deki `dd.MM.yyyy HH:mm` formatlayıcısının
    aynısıyla `startedAt`'ten türetiliyor). `HistoryContract.State`e `errorMessage: String? = null`
    eklendi — hata durumunda ekran sessizce boş kalmak yerine bu mesajı boş-liste alanında
    gösteriyor (dead-end bırakılmadı). `HistoryScreen.kt`e üç durumlu (`isLoading` → spinner,
    `rentals.isEmpty()` → `errorMessage` veya "Henüz bir kiralaman yok." metni, aksi halde mevcut
    `LazyColumn`) bir `when` dallanması eklendi — mock veri hep dolu geldiğinden bu iki durum daha
    önce hiç ele alınmamıştı.
- **Değişen dosyalar:** `feature/history/HistoryContract.kt`, `feature/history/HistoryViewModel.kt`,
  `feature/history/HistoryScreen.kt`.
- **Neden bu şekilde yapıldı:** Kullanıcı onayıyla netleşen karar: `GET /rentals` kullanıcının TÜM
  kiralamalarını (PREPARING/ACTIVE/COMPLETED/CANCELLED) döndürdüğünden, History ekranı yalnızca
  `COMPLETED` olanları gösteriyor — PREPARING/ACTIVE zaten ayrı bir "aktif kiralama" akışında
  (Home banner) gösteriliyor, CANCELLED'ı geçmiş listesine karıştırmak kafa karıştırır ve
  `totalPrice` bu durumlarda `null` gelebileceğinden yanlış (₺0) bir tutar üretirdi. Hata için
  Effect/Toast yerine kalıcı bir State alanı (`errorMessage`) tercih edildi — tek seferlik bir
  Toast mesajı kaçırılırsa ekran açıklamasız boş kalırdı; State alanı kullanıcı ekrana her
  baktığında görünür kalıyor. `MainScaffold.kt`e KASITLI olarak dokunulmadı — `HistoryRoute()`
  hâlâ parametresiz çağrılıyor (`onNavigateToDetail` varsayılan no-op `{}`), gerçek detay
  navigasyonu ayrı bir batch'e (Batch 1c) bırakıldı.
- **Kendi kontrolüm:** `./gradlew :app:compileDebugKotlin` ile derlendi, BUILD SUCCESSFUL (uyarı
  yok). Yeni navigasyon/hata kod yollarının emülatörde gerçek backend'e karşı uçtan uca testi
  (COMPLETED kiralaması olan/olmayan hesap, hata senaryosu) henüz yapılmadı.
- **Kapsam dışı, ayrı bir temizlik adımı olarak not:** `HistoryMockSource.kt` artık hiçbir yerden
  çağrılmıyor (bu batch'te teyit edildi) ama dosya limiti gereği silinmedi; ileride kaldırılabilir.

### 2026-07-18 — Geçmiş Detay ekranı (feature/history/detail/) sıfırdan kuruldu ve navigasyona bağlandı (7 dosya, 2 alt-batch)

- **Ne yapıldı:** `HistoryContract.Effect.NavigateToDetail(rentalId)`'in gittiği yeni bir MVI
  dörtlüsü eklendi: `HistoryDetailContract.kt` (`ActiveRentalContract.kt`'deki dosya-seviyesi
  `DateTimeFormatter` deseniyle `formattedStartedAt`/`formattedEndedAt`/ücret alanları computed
  property olarak tanımlandı), `HistoryDetailViewModel.kt` (`VehicleDetailViewModel.kt`'deki
  BİREBİR AYNI `@HiltViewModel(assistedFactory=...)` + `@AssistedInject @Assisted rentalId: String`
  deseni; `loadRental()` `RentalsRepository.getRental(id)`'i `isLoading`/`AuthResult` deseniyle
  çağırıp `RentalResponseDto`'nun TÜM alanlarını (araç, plan, tarihler, mesafe/süre, ücret dökümü:
  startFee/serviceFee/discountAmount/totalPrice, durum/ödeme durumu) State'e kopyalıyor),
  `HistoryDetailScreen.kt` (geri butonlu üst bar + History listesindeki gibi üç durumlu
  `isLoading`/`errorMessage`/içerik dallanması + araç başlığı, tarih/süre/mesafe kartı, ücret
  dökümü kartı), `HistoryDetailRoute.kt` (`VehicleDetailRoute.kt` iskeletiyle aynı,
  konum/izin mantığı YOK — bu ekranın buna ihtiyacı yok).
  Ardından navigasyon bağlandı: `HistoryRoute.kt`'de bir önceki batch'ten kalma bir hata
  düzeltildi (`HistoryViewModel` iki batch önce `@HiltViewModel`e taşınmıştı ama Route hâlâ Hilt
  DIŞI `viewModel()` çağırıyordu — bu haliyle History sekmesi açılır açılmaz çökerdi; `hiltViewModel()`e
  geçildi). `MainScaffold.kt`'ye `onNavigateToHistoryDetail: (String) -> Unit = {}` parametresi
  eklendi, `HistoryRoute`'a aktarıldı. `RenCarNavHost.kt`'ye `HISTORY_DETAIL =
"history-detail/{rentalId}"` sabiti + `historyDetailRoute()` yardımcı fonksiyonu eklendi;
  `HOME` bloğu `onNavigateToHistoryDetail`i üst seviye `navController.navigate(...)`e kablolayacak
  şekilde güncellendi; yeni bir `composable(HISTORY_DETAIL)` bloğu `HistoryDetailRoute`'u
  `onNavigateBack = { navController.popBackStack() }` ile çağırıyor.
- **Değişen/yeni dosyalar:** `feature/history/detail/HistoryDetailContract.kt` (yeni),
  `HistoryDetailViewModel.kt` (yeni), `HistoryDetailScreen.kt` (yeni), `HistoryDetailRoute.kt`
  (yeni), `feature/history/HistoryRoute.kt`, `navigation/MainScaffold.kt`,
  `navigation/RenCarNavHost.kt`. 7 dosya olduğundan Agent.md §2.1'deki 5 dosya limitini aşmamak
  için TEK onay altında İKİ alt-batch olarak uygulandı: önce yeni 4 dosyalık MVI dörtlüsü (izole,
  henüz hiçbir yerden çağrılmıyor, ayrı derlendi), ardından 3 dosyalık navigasyon bağlama +
  bug fix (ayrı derlendi).
- **Neden bu şekilde yapıldı:** Detay ekranına navigasyon köprüsü, `VehicleDetail`'in zaten
  kanıtlanmış deseniyle BİREBİR AYNI kuruldu: `MainScaffold`'un kendi iç NavHost'u üst seviye
  navigasyon graph'ına asla doğrudan erişmiyor, bunun yerine üst seviye `RenCarNavHost`'tan aldığı
  bir callback'i (`onNavigateToHistoryDetail`) alt Route'a (`HistoryRoute`) olduğu gibi aktarıyor;
  gerçek `navigate(...)` çağrısı yalnızca `RenCarNavHost`'un `HOME` composable bloğunda yapılıyor.
  Bu tutarlılık zaten `onNavigateToVehicleDetail`/`onNavigateToActiveRental` için kanıtlanmış bir
  desen olduğundan alternatif bir yaklaşım (örn. History'nin kendi alt-graph'ını açması)
  değerlendirilmedi. `HistoryRoute.kt`'deki `viewModel()` → `hiltViewModel()` düzeltmesi bu
  batch'in kapsamına dahil edildi çünkü Detay ekranına hiç ulaşılamazdı (liste ekranı zaten
  runtime'da çökerdi) — ayrı bir batch'e ertelemek anlamsız olurdu.
- **Kendi kontrolüm:** Her iki alt-batch ayrı ayrı `./gradlew :app:compileDebugKotlin` ile
  derlendi, ikisi de BUILD SUCCESSFUL (uyarı yok). Emülatörde gerçek backend'e karşı uçtan uca
  runtime testi (History listesinden bir karta tıklayıp Detay ekranının gerçek veriyle açılması,
  geri tuşu, hata senaryosu) HENÜZ YAPILMADI — bir önceki batch'te bulunan `HistoryRoute`
  çökme hatası da dahil olmak üzere bu batch'in gerçek cihazda ilk kez doğrulanması gerekiyor.
  ### Bilinen Hata: Bottom-nav konum izni engeli yanlış (ertelendi)
- MainScaffold.kt'de Harita dışındaki sekmelere (Kiralamalarım/Cüzdan/Profil)
  geçiş, gereksiz yere konum izni şartına bağlanmış.
- Düzeltme: bu kontrolü kaldırmak (tek satır, MainScaffold.kt).
- Durum: Ertelendi, bilinçli karar.

### 2026-07-18 — Profil ekranı "Çıkış Yap" akışı gerçek hale getirildi (5 dosya)

- **Ne yapıldı:** `AuthRepository.logout()` artık önce gerçek `POST /auth/logout`'u
  (`AuthApiService.logout()`) çağırıyor, ardından (API çağrısı başarılı olsun olmasın)
  `tokenStore.clear()` çalışıyor. `ProfileViewModel` `class : ViewModel()`den `@HiltViewModel`
  + `@Inject constructor(AuthRepository)`e taşındı; `SignOutClicked` artık yeni
  `handleSignOutClicked()` içinde `authRepository.logout()`u çağırıp `NavigateToLogin`
  effect'ini gönderiyor (`ProfileMockSource` kullanan `loadProfile()` ve diğer 5 buton
  DEĞİŞMEDİ, kapsam dışı). `ProfileRoute.kt` `viewModel()` yerine `hiltViewModel()` kullanıyor,
  yeni `onNavigateToLogin: () -> Unit = {}` parametresini `NavigateToLogin` effect'ine
  bağlıyor (diğer 5 effect kasıtlı olarak `-> Unit` kaldı, koda not düşüldü).
  `MainScaffold.kt`'ye aynı isimde bir parametre eklenip `ProfileRoute`'a aktarıldı.
  `RenCarNavHost.kt`'nin `HOME` bloğundaki `MainScaffold` çağrısına
  `onNavigateToLogin = { navController.navigate(LOGIN) { popUpTo(findStartDestination().id)
  { inclusive = true } } }` eklendi.
- **Değişen dosyalar:** `data/repository/AuthRepository.kt`, `feature/profile/
  ProfileViewModel.kt`, `feature/profile/ProfileRoute.kt`, `navigation/MainScaffold.kt`,
  `navigation/RenCarNavHost.kt`.
- **Neden bu şekilde yapıldı:** Kullanıcı onayıyla netleşen karar: backend `POST /auth/logout`
  çağrısı ağ hatası/timeout/401 nedeniyle başarısız olsa bile `tokenStore.clear()` HER DURUMDA
  çalışıyor (`try/catch` + `finally`) — kullanıcı bir ağ sorunu yüzünden "çıkış yapamama"
  durumuna düşmemeli, backend çağrısı best-effort kabul edildi; bu yüzden `ProfileViewModel`
  tarafında bir hata effect'i eklenmedi. Sıra önce API sonra `clear()` olacak şekilde
  korundu: `tokenStore.clear()` önce çalışsaydı `AuthInterceptor` artık `Authorization`
  header'ı eklemeyeceğinden backend isteği 401 ile reddedilebilirdi. `popUpTo` hedefi olarak
  `RenCarDestinations.HOME` değil `navController.graph.findStartDestination().id` (=
  `SPLASH`) kullanıldı — görevin "Splash/Onboarding dahil TÜM backstack temizlensin, geri
  tuşuyla Home'a dönülemesin" şartı için `HOME`'u hedeflemek yetersiz kalırdı (öncesindeki
  Login/Onboarding/Splash entry'lerini silmezdi).
- **Kendi kontrolüm:** `./gradlew :app:compileDebugKotlin` ile derlendi, BUILD SUCCESSFUL,
  uyarı yok. Emülatörde gerçek backend'e karşı uçtan uca runtime testi (Çıkış Yap → POST
  isteğinin gittiğinin doğrulanması → Login ekranı → geri tuşuyla Home'a dönülememesi →
  ağ kapalıyken çıkış senaryosu) HENÜZ YAPILMADI.

### 2026-07-18 — Bug fix: Çıkış Yap sonrası geri tuşu Profile'a dönüyordu (1 dosya)

- **Ne yapıldı:** Kullanıcı, Çıkış Yap → Login ekranından geri tuşuna basınca tekrar
  Profile'a döndüğünü bildirdi. Kök neden: bir önceki batch'teki `RenCarNavHost.kt`'nin
  `HOME` bloğundaki `onNavigateToLogin`, `popUpTo(navController.graph.
  findStartDestination().id)` (yani `SPLASH`) kullanıyordu — ama `SPLASH`, Splash'tan
  Home'a ilk geçişte zaten `popUpTo(SPLASH){inclusive=true}` ile backstack'ten
  kaldırılmış olduğundan, Home'a ulaşıldığında backstack'te artık YOK. Compose
  Navigation'da `popUpTo` hedef ID backstack'te bulunamazsa sessizce hiçbir şeyi
  popplamıyor; bu yüzden `LOGIN` sadece mevcut `[HOME]` backstack'inin üzerine
  ekleniyordu (`[HOME, LOGIN]`), geri tuşu da Home'a (Profile sekmesine) dönüyordu.
  Düzeltme: hedef ID'yi `SPLASH` yerine backstack'te olup olmadığına bakılmaksızın
  TÜM backstack'i temizleyen `popUpTo(0)` yapıldı — artık kullanıcı hangi ekrandan
  çıkış yaparsa yapsın backstack tamamen boşalıyor.
- **Değişen dosyalar:** `navigation/RenCarNavHost.kt` (`onNavigateToLogin` içindeki
  `popUpTo` hedefi + artık kullanılmayan `findStartDestination` importu kaldırıldı).
- **Neden bu şekilde yapıldı:** `findStartDestination().id` yaklaşımı yalnızca start
  destination backstack'te HÂLÂ mevcutsa çalışır — bu projede login/onboarding/splash
  akışlarının neredeyse tamamı kendi geçişlerinde zaten `inclusive=true` ile önceki
  entry'leri temizlediğinden, Home'a ulaşan hiçbir yol üzerinde Splash backstack'te
  kalmıyor. `popUpTo(0)` bu varsayıma bağımlı olmayan, Android Navigation'ın "tüm
  backstack'i temizle" için standart/idiomatik yolu.
- **Kendi kontrolüm:** `./gradlew :app:compileDebugKotlin` ile derlendi, BUILD
  SUCCESSFUL, uyarı yok. Emülatörde runtime testi (Profil → Çıkış Yap → Login →
  geri tuşu → uygulamadan çıkma, Profile'a dönmeme) HENÜZ YAPILMADI.

### 2026-07-18 — Profil ekranı kullanıcı bilgisi + ehliyet kartı gerçek API'ye bağlandı (3 dosya)

- **Ne yapıldı:** `ProfileViewModel.loadProfile()` artık `ProfileMockSource` yerine
  `AuthRepository.getMe()` çağırıyor (`HistoryViewModel.loadRentals()`'daki `AuthResult`
  `when` deseniyle birebir aynı); başarılıysa `userName`/`phoneNumber` `UserResponseDto.
  fullName`/`phone` alanlarından dolduruluyor (`phone` nullable olduğundan `?: ""`),
  hata durumunda yeni `ProfileContract.State.errorMessage` alanı dolduruluyor (dead-end
  yok, `ProfileScreen.kt`'de başlığın altında kırmızı bir satır olarak gösteriliyor).
  Kullanıcıyla netleşen ek karar üzerine ehliyet kartı da `LicenseRepository.getStatus()`'a
  bağlandı: `LicenseVerification`'dan API'de karşılığı olmayan `licenseClass` alanı
  kaldırıldı, yeni bir `LicenseStatusResponseDto.toLicenseVerification()` uzantısı
  `status`'u (`APPROVED`/`REJECTED`/`UNDER_REVIEW`/diğer) `isVerified`/`statusLabel`'e
  eşliyor; bu çağrı yalnızca `getMe()` başarılıysa yapılıyor, kendi hatası ayrı bir
  `errorMessage` üretmiyor (sessizce `license = null` kalıyor). Bu sırada `ProfileScreen.kt`'de
  önceden fark edilmemiş bir hata düzeltildi: `LicenseCard` mock veri hep `isVerified=true`
  döndürdüğünden şimdiye kadar `isVerified` durumuna HİÇ bakmadan her zaman yeşil ikon +
  "Ehliyet doğrulandı" gösteriyordu; artık gerçek `isVerified`'a göre yeşil/nötr renk ve
  "doğrulandı"/"doğrulanmadı" metni arasında dallanıyor. Diğer 5 menü butonu (Profili
  Düzenle/Ödeme Yöntemleri/Ayarlar/Yardım/Davet Et) kullanıcının açık talimatıyla KAPSAM
  DIŞI bırakıldı, dokunulmadı.
- **Değişen dosyalar:** `feature/profile/ProfileContract.kt`, `feature/profile/
  ProfileViewModel.kt`, `feature/profile/ProfileScreen.kt`. Ayrıca `feature/profile/
  ProfileMockSource.kt`'de mekanik bir derleme düzeltmesi yapıldı (`LicenseVerification`
  çağrısından kaldırılan `licenseClass` parametresi silindi) — dosya hâlâ hiçbir yerden
  çağrılmıyor, sadece `LicenseVerification`'ın yeni şekliyle derlenebilir kalması
  gerekiyordu (History'deki `HistoryMockSource` kararıyla tutarlı: dosya silinmiyor).
- **Neden bu şekilde yapıldı:** Kullanıcıya AskUserQuestion ile üç seçenek sunuldu
  (LicenseRepository'ye bağla / mock veriyi ViewModel'e taşı / null bırak); kullanıcı
  "LicenseRepository.getStatus() ile bağla" seçeneğini onayladı. `getStatus()` hatasının
  `errorMessage`'a karıştırılmaması bilinçli bir tercih: kimlik yükleme hatasıyla ehliyet
  yükleme hatası kullanıcı için ayırt edilemez hale gelmesin diye ayrı tutuldu. `LicenseCard`
  düzeltmesi kapsam genişletme değil, gerçek veri artık `isVerified=false` üretebildiğinden
  yanlış "doğrulandı" mesajının kullanıcıya gösterilmeye devam etmesini önlemek için zorunlu
  bir düzeltmeydi.
- **Kendi kontrolüm:** `./gradlew :app:compileDebugKotlin` ile derlendi, BUILD SUCCESSFUL
  (yalnızca projeyle ilgisiz, önceden var olan uyarılar — `@Inject`/parametre hedefi ve
  deprecated `Icons.Filled.List`). Bağlı bir emülatör/cihaz olmadığından (`adb devices`
  boş döndü) runtime testi (gerçek CUSTOMER hesabıyla Profil'de gerçek ad/telefonun ve
  doğru ehliyet durumunun görünmesi, hata senaryosu) HENÜZ YAPILMADI.

### 2026-07-18 — Profil dead-end butonları: Batch 1 — Ayarlar ekranı (tema tercihi, DataStore) izole kuruldu (5 dosya)

- **Ne yapıldı:** Profil ekranındaki 5 kalan dead-end butondan (Profili Düzenle/Ödeme
  Yöntemleri/Ayarlar/Yardım/Davet Et) "Ayarlar"ı gerçek hale getirmenin ilk alt-batch'i
  uygulandı. `OnboardingPreferences.kt` ile aynı DataStore Preferences deseninde yeni
  `ThemePreferences.kt` eklendi: `enum class ThemeMode { SYSTEM, LIGHT, DARK }` ve
  `Flow<ThemeMode>` (`OnboardingPreferences`'ın tek seferlik `suspend fun`
  okumasından farklı olarak reaktif — tema değişince tüm uygulamaya anında yansıması
  gerektiğinden). Ardından `feature/settings/` paketi sıfırdan MVI dörtlüsü olarak
  kuruldu: `SettingsContract.kt` (State: `themeMode`; Intent:
  `ThemeModeSelected`/`NavigateBack`; Effect: `NavigateBack`), `SettingsViewModel.kt`
  (`@HiltViewModel` + `ThemePreferences` inject, `init` içinde `themeMode` flow'unu
  dinleyip state'i günceller — DataStore tek doğruluk kaynağı, seçim yapıldığında
  state doğrudan değil DataStore'un yeni emit'i üzerinden güncellenir),
  `SettingsScreen.kt` (`HistoryDetailScreen.kt`'deki geri butonlu üst bar deseni +
  `ProfileScreen.kt`'deki `MenuRow` diline benzer, seçili satırda check ikonu gösteren
  3 satırlık tema kartı), `SettingsRoute.kt` (`HistoryDetailRoute.kt` iskeletiyle
  birebir aynı, parametresiz `hiltViewModel()`).
- **Değişen/yeni dosyalar:** `data/local/ThemePreferences.kt` (yeni),
  `feature/settings/SettingsContract.kt` (yeni), `SettingsViewModel.kt` (yeni),
  `SettingsScreen.kt` (yeni), `SettingsRoute.kt` (yeni).
- **Neden bu şekilde yapıldı:** Kullanıcı onayıyla netleşen karar: Ayarlar ekranı için
  dil seçimi değil tema tercihi (Sistem/Açık/Koyu) seçildi — dil desteği ayrı bir i18n
  altyapısı gerektirdiğinden kapsam dışı, tema tercihi tek bir DataStore anahtarıyla
  çözülebilen gerçek bir ayar. `ThemeMode` bilinçli olarak gerçek bir Kotlin `enum`
  (projenin API DTO'larında "role"/"status" gibi alanlar için benimsediği "enum yerine
  String" kuralının aksine) — çünkü bu backend'den gelen bir veri değil, tamamen yerel
  ve kapalı bir küme, deserialization riski yok. Bu batch bilinçli olarak İZOLE
  bırakıldı: `MainActivity.kt` (tema akışını `RenCarPairTheme`'e bağlama),
  `RenCarNavHost.kt`/`MainScaffold.kt`/`ProfileRoute.kt` (navigasyon bağlama) bu
  batch'te DEĞİŞTİRİLMEDİ — Register/HistoryDetail ekranlarındaki "önce izole MVI
  dörtlüsü, sonra ayrı bir alt-batch'te navigasyon bağlama" emsaline tutarlı.
- **Kendi kontrolüm:** `./gradlew :app:compileDebugKotlin` ile derlendi; ilk denemede
  `SettingsScreen.kt`'de `Spacer(Modifier.height(8.dp))` için eksik
  `androidx.compose.foundation.layout.height` import'u derleme hatası verdi, eklenip
  düzeltildi. Sonrasında BUILD SUCCESSFUL, uyarı yok. `SettingsRoute` henüz hiçbir
  yerden çağrılmadığından runtime testi bilinçli olarak sonraki (navigasyon bağlama)
  batch'ine bırakıldı.

### 2026-07-18 — Profil dead-end butonları: Batch 4-5 — Ayarlar/Yardım/Davet Et navigasyona bağlandı, tema uygulandı, "Yakında" Toast'ı eklendi (6 dosya, tek onayla iki alt-batch, uçtan uca doğrulandı)

- **Ne yapıldı:** Görevin son iki alt-batch'i tek onayla ardışık uygulandı ve gerçek
  emülatörde uçtan uca test edildi.
  **Batch 4 (Contract/ViewModel):** `ProfileContract.kt`'ye `referralCode: String?` State
  alanı ve `const val COMING_SOON_MESSAGE` eklendi; `NavigateToEditProfile`/
  `NavigateToPaymentMethods` Effect'leri tamamen kaldırıldı (artık hiçbir yere
  navigate etmiyorlar), `NavigateToInvite` `data object`'ten
  `data class NavigateToInvite(val referralCode: String)`'e çevrildi, yeni
  `ShowToast(val message: String)` Effect'i eklendi. `ProfileViewModel.kt`:
  `EditProfileClicked`/`PaymentMethodsClicked` artık ortak `handleComingSoonClicked()`
  ile `ShowToast`'a bağlanıyor; yeni `handleInviteClicked()` guard clause ile
  (`referralCode == null` ise sessizce no-op) `NavigateToInvite(referralCode)`
  gönderiyor; `loadProfile()` artık `result.data.referralCode`'u State'e yazıyor.
  **Batch 5 (navigasyon bağlama):** `ProfileRoute.kt` yeni effect'leri tüketiyor
  (`ShowToast` → `Toast.makeText`, `NavigateToInvite` → `onNavigateToInvite(code)`).
  `MainScaffold.kt`'ye üç yeni parametre (`onNavigateToSettings`/`onNavigateToHelp`/
  `onNavigateToInvite`) eklenip `ProfileRoute`'a aktarıldı. `RenCarNavHost.kt`'ye
  `SETTINGS`/`HELP`/`INVITE = "invite/{referralCode}"` rotaları + üç yeni `composable`
  bloğu eklendi (`HOME` bloğundaki `MainScaffold` çağrısına üç yeni callback bağlandı).
  `MainActivity.kt` artık `ThemePreferences`'ı Hilt field injection ile alıyor,
  `themeMode.collectAsState(initial = ThemeMode.SYSTEM)` ile dinleyip
  `RenCarPairTheme(darkTheme = ...)`'e besliyor (SYSTEM → `isSystemInDarkTheme()`,
  LIGHT → `false`, DARK → `true`).
- **Değişen dosyalar:** `feature/profile/ProfileContract.kt`, `ProfileViewModel.kt`,
  `ProfileRoute.kt`, `navigation/MainScaffold.kt`, `navigation/RenCarNavHost.kt`,
  `MainActivity.kt`.
- **Neden bu şekilde yapıldı:** `NavigateToEditProfile`/`NavigateToPaymentMethods`
  Effect'lerinin tamamen silinmesi (Route'ta `-> Unit` olarak bırakılmak yerine)
  bilinçli bir tercih: artık gerçekten hiçbir navigasyona karşılık gelmiyorlar, ölü
  Effect dalları bırakmak yerine kaynağında kaldırıldı. `handleInviteClicked()`'daki
  guard clause (mvi-viewmodel-rules.md §5 örneğiyle birebir aynı desen), kullanıcı
  Profil yüklenmeden/hata sonrası `referralCode` `null` iken Davet Et'e basarsa
  ekranın boş bir kodla açılmasını engelliyor — İzole batch'te (Batch 3) not edilen
  riskin kaynağında kapatılması. `MainActivity`'nin `ThemePreferences`'ı doğrudan field
  injection ile alması (ayrı bir ViewModel yerine) bilinçli: tema, herhangi bir tek
  ekranın Contract'ına ait değil, tüm `NavHost`'u saran kök bir uygulama durumu —
  decisions.md'nin "Nav-Graph-Scoped Paylaşılan State MVI Contract Kullanmaz" kararıyla
  ruh olarak aynı gerekçe (LicenseFlowViewModel emsali).
- **Kendi kontrolüm:** `./gradlew :app:compileDebugKotlin` — BUILD SUCCESSFUL, ilk
  denemede geçti (ara derleme yapılamadı çünkü Batch 4, zaten bağlı olan
  `ProfileRoute.kt`'nin exhaustive `when`'ini kırıyordu — Settings/Help/Invite'ın
  izole ilk alt-batch'lerinden farklı olarak Profile MVI dörtlüsü baştan beri
  navigasyona bağlıydı). `./gradlew :app:installDebug` ile bağlı emülatöre
  (`emulator-5554`) kuruldu ve CANLI BACKEND'E KARŞI UÇTAN UCA doğrulandı:
  Profil ekranı gerçek kullanıcı verisiyle açıldı → **Ayarlar**: "Koyu" seçilince
  tüm uygulama ANINDA koyu temaya geçti (ekran görüntüsüyle doğrulandı), geri
  dönüldüğünde tema kalıcı kaldı; → **Yardım & Destek**: 5 SSS + iletişim kartı
  doğru göründü, geri tuşu çalıştı; → **Davet Et**: gerçek referans kodu
  (`REN-6F9AWV`, `GET /auth/me`'den) görüntülendi, "Davet kodunu paylaş"a
  basılınca Android'in gerçek paylaşım penceresi doğru metinle
  (`"RenCar'a katıl, referans kodumla kaydolduğunda ikimiz de kazanıyoruz:
  REN-6F9AWV"`) açıldı; → **Profili Düzenle** (kalem ikonu) ve **Ödeme yöntemleri**
  ikisi de "Bu özellik yakında eklenecek" Toast'ını gösterdi, dead-end kalmadı.
  `adb logcat -d -t 500 *:E` ile crash/exception taraması yapıldı, uygulamayla
  ilgili hiçbir hata bulunmadı. Bu, görevin TÜM maddelerinin (Ayarlar/Yardım/
  Davet Et gerçek hale getirme + Profili Düzenle/Ödeme Yöntemleri "Yakında"
  bağlanması) tamamlandığı ve gerçek cihazda uçtan uca doğrulandığı anlamına gelir.

### 2026-07-18 — Profil dead-end butonları: Batch 2 — Yardım ekranı (statik SSS) ve Batch 3 — Davet Et ekranı (referans kodu paylaşımı) izole kuruldu (8 dosya, tek onayla iki alt-batch)

- **Ne yapıldı:** Kullanıcının "tek onayla ilerle" talimatıyla, bağımsız iki izole MVI
  dörtlüsü art arda uygulandı.
  **Yardım:** `feature/help/` paketi kuruldu — `HelpContract.kt` (dosya seviyesinde
  `data class FaqEntry(question, answer)` ve 5 adet sabit SSS girdisi + destek
  e-posta/telefonu içeren `State`; `Intent`/`Effect` yalnızca `NavigateBack`),
  `HelpViewModel.kt` (parametresiz `@HiltViewModel`, veri zaten `State` varsayılanında
  sabit olduğundan `init`/yükleme fonksiyonu yok), `HelpScreen.kt` (geri butonlu üst bar +
  SSS kartları + iletişim kartı, tamamen statik, expand/collapse gibi ekstra state
  eklenmedi), `HelpRoute.kt` (`HistoryDetailRoute.kt` iskeletiyle aynı).
  **Davet Et:** `feature/invite/` paketi kuruldu — `InviteContract.kt` (State:
  `referralCode`; Intent: `ShareClicked`/`NavigateBack`; Effect:
  `ShareReferralCode(referralCode)`/`NavigateBack`), `InviteViewModel.kt`
  (`HistoryDetailViewModel.kt`'deki BİREBİR AYNI `@HiltViewModel(assistedFactory=...)` +
  `@AssistedInject @Assisted referralCode: String` deseni — kendi API çağrısı YOK,
  referans kodu constructor parametresi olarak alınıp doğrudan `State`'e konuyor),
  `InviteScreen.kt` (kod kartı + "Davet kodunu paylaş" butonu, `referralCode` boşsa
  buton disabled), `InviteRoute.kt` (`ShareReferralCode` effect'i `LocalContext.current`
  ile gerçek bir `Intent.ACTION_SEND` + `Intent.createChooser` açıyor — Android'in
  yerleşik paylaşım penceresi).
- **Değişen/yeni dosyalar:** `feature/help/HelpContract.kt`, `HelpViewModel.kt`,
  `HelpScreen.kt`, `HelpRoute.kt` (hepsi yeni); `feature/invite/InviteContract.kt`,
  `InviteViewModel.kt`, `InviteScreen.kt`, `InviteRoute.kt` (hepsi yeni).
- **Neden bu şekilde yapıldı:** Davet Et ekranının referans kodunu kendi API çağrısıyla
  ÇEKMEMESİ bilinçli bir tasarım kararı: `GET /auth/me` zaten Profil ekranında bir önceki
  batch'te çekiliyor (`UserResponseDto.referralCode`), görev açıkça "yeni bir API çağrısı
  gerekmiyor" dediğinden, kod `VehicleDetail`'deki `distanceMeters` emsaliyle aynı şekilde
  (tek seferlik hesaplanmış bir değerin nav path segmenti olarak taşınması) Profil'den
  Invite'a bir rota parametresi olarak aktarılacak — bu bağlantı henüz kurulmadı (Batch 4-5).
  Paylaşım penceresinin (`Intent.ACTION_SEND`) Screen değil Route içinde açılması,
  `VehicleDetailRoute`'un konum izni kontrolünü Route'ta yapması emsaliyle tutarlı: Screen
  hiçbir zaman platforma özgü/Context gerektiren bir çağrı yapmaz, bunun yerine ViewModel
  bir Effect gönderir, Route bu Effect'i tüketip gerçek sistem çağrısını yapar. Yardım
  ekranında SSS içeriği genişletilebilir (accordion) yapılmadı — görevde "basit bir ekran
  yeterli" denildiğinden gereksiz bir UI-state eklemek kapsam dışı aşırı mühendislik olurdu.
- **Kendi kontrolüm:** `./gradlew :app:compileDebugKotlin` ile iki kez derlendi (8 dosya
  eklendikten sonra ve küçük bir temizlik sonrası — `InviteScreen.kt`'de `Icon`'a
  gereksiz boş `modifier = Modifier` parametresi verilmişti, kaldırıldı), ikisinde de
  BUILD SUCCESSFUL, uyarı yok. `HelpRoute`/`InviteRoute` henüz hiçbir yerden
  çağrılmadığından (izole) runtime testi bilinçli olarak navigasyon bağlama batch'ine
  bırakıldı.

### 2026-07-18 — Backend hata mesajları artık kullanıcıya gösteriliyor (repository katmanı, 5 dosya)

- **Ne yapıldı:** Kullanıcının "ehliyet onaylama 3. sayfa neden bekliyor" sorusu
  araştırılırken, tüm repository'lerdeki hata yakalamanın backend'in döndüğü
  `{"statusCode":...,"message":"...","error":"..."}` gövdesini hiç okumadığı,
  HTTP koduna bakmaksızın her zaman sabit "Sunucu hatası (kod: X)." metni
  ürettiği tespit edildi (`response.errorBody()` kod tabanında hiç okunmuyordu).
  `AuthRepository.kt`'ye `Response<*>.extractErrorMessage()` adlı paylaşılan bir
  extension fonksiyon eklendi: Gson `JsonParser` ile `errorBody()` içindeki
  `message` alanını okuyor (NestJS validation hatalarında dizi olabileceğinden
  bu durumda satırlarla birleştiriyor), ayrıştırma başarısız olursa eski jenerik
  metne düşüyor. 5 repository dosyasındaki toplam 17 tekrar bu fonksiyona
  yönlendirildi.
- **Değişen dosyalar:** `data/repository/AuthRepository.kt` (yeni extension
  fonksiyon + 1 çağrı), `data/repository/LicenseRepository.kt` (2 çağrı),
  `data/repository/VehiclesRepository.kt` (3 çağrı),
  `data/repository/ReservationsRepository.kt` (1 çağrı),
  `data/repository/RentalsRepository.kt` (10 çağrı).
- **Neden bu şekilde yapıldı:** Yardımcı fonksiyon yeni bir dosya açmak yerine
  `AuthRepository.kt`'ye eklendi — `AuthResult` sealed interface zaten orada
  tanımlıydı ve Agent.md §2.1'in 5 dosya limitini aşmadan (tam 5 dosyada kalarak)
  tüm repository'lerin ortak kullanabileceği bir yer sağladı. ViewModel/UI
  katmanına (Toast gösterimi) dokunulmadı — onlar zaten `AuthResult.Error.message`'ı
  olduğu gibi gösteriyordu, sadece repository'nin ürettiği mesaj değişti.
- **Kendi kontrolüm:** `./gradlew :app:compileDebugKotlin` ile derlendi, BUILD
  SUCCESSFUL (yalnızca projede önceden var olan, bu değişiklikle ilgisiz iki
  `@ApplicationContext` annotation-target uyarısı). Gerçek bir hatalı istekle
  (örn. kayıtsız telefonla login) uçtan uca runtime testi henüz yapılmadı.

### 2026-07-18 — Telefon numarası alanındaki imleç kayması düzeltildi (Login + Register)

- **Ne yapıldı:** 2026-07-15 tarihli girdide "kapsam dışı, ayrı görev" olarak not
  edilen imleç kayması hatası (`...0102` yazılınca `...0210` görünmesi) kök
  nedeniyle düzeltildi. Kök neden: `PhoneNumberField`'da `OutlinedTextField`'a
  `value` olarak her tuşta yeniden üretilen formatlı (boşluklu) bir String
  veriliyordu ("532 123 45 67"); Compose'un String-tabanlı overload'ı imleç
  konumunu eski/yeni metni diff ederek buluyor, formatlama boşluk ekleyip
  metnin uzunluğunu değiştirince bu diff yanlış hizalanıp imleci yanlış yere
  atlatıyordu. Düzeltme: `value` artık ham (boşluksuz) rakamlarla kalıyor,
  boşluklar yalnızca görüntüde `PhoneNumberVisualTransformation` (yeni,
  `OffsetMapping` ile ham<->görüntü imleç eşlemesi yapan bir `VisualTransformation`)
  ile ekleniyor — bu Compose'un maskeli/formatlı giriş için önerdiği standart
  desendir, imleç eşlemesini çerçeve doğru yapıyor.
- **Değişen dosyalar:** `feature/auth/login/LoginScreen.kt`,
  `feature/auth/register/RegisterScreen.kt` (ikisinde de birebir aynı kopya
  `PhoneNumberField`/`formatPhoneNumber` deseni vardı, aynı hata ikisinde de
  mevcuttu — kullanıcı onayıyla ikisi de aynı batch'te düzeltildi).
- **Neden bu şekilde yapıldı:** Paylaşımlı bir dosya/fonksiyon açmak yerine
  `PhoneNumberVisualTransformation` her iki dosyada da (mevcut
  `formatPhoneNumber` kopyası emsaliyle tutarlı olarak) ayrı ayrı tanımlandı —
  proje bu iki ekran arasında küçük telefon-alanı tekrarını zaten bilinçli
  olarak kabul etmişti (bkz. 2026-07-14 Login/Otp girdisi). `ViewModel`
  katmanına (Login/RegisterViewModel'deki `value.filter{it.isDigit()}.take(10)`
  güvenlik filtresi) dokunulmadı — hâlâ geçerli ve gerekli, artık her zaman
  zaten temiz rakam string'i gelse de savunma amaçlı korundu.
- **Kendi kontrolüm:** `./gradlew :app:compileDebugKotlin` ile derlendi, BUILD
  SUCCESSFUL, yeni uyarı yok. Şu an bağlı bir emülatör/cihaz olmadığından
  (`adb devices` boş döndü) cihazda görsel/runtime testi YAPILAMADI — bir
  sonraki oturumda emülatör açılıp Login ve Register ekranlarında telefon
  alanına hızlı/yavaş rakam girilerek imlecin artık sabit kaldığı elle
  doğrulanmalı.
- **Sıradaki adım:** Emülatörü açıp Login ekranında telefon alanına
  `5321234567` yazarak (hem baştan hem araya rakam ekleyerek) imlecin doğru
  konumda kaldığını, aynısını Register ekranında da doğrulamak.

### 2026-07-18 — Maps ekranına "Filtreler" eklendi: çalışan arama çubuğu + segment/vites/koltuk filtre paneli (3 dosya)

- **Ne yapıldı:** Kullanıcının "haritadaki arama çubuğu çalışmıyor" şikayeti araştırılırken
  `SearchBar()`'ın (`MapsScreen.kt`) tamamen dekoratif olduğu (parametre almıyor, `onIntent`'e
  hiç bağlı değil) ve `NearbyVehiclesSheet` içinde de aynı amaçla ikinci, boş (`onClick = {}`)
  bir Tune ikonu daha olduğu bulundu. Kullanıcı konum/adres aramayı (backend'de geocoding
  endpoint'i yok) kapsam dışı bırakıp yalnızca araç filtrelemeyi istedi. Eklenenler:
  (1) arama çubuğu artık gerçek bir `BasicTextField` — marka/model/plaka'ya göre canlı filtreliyor;
  (2) her iki Tune ikonu da aynı `FilterPanelToggled` intent'ine bağlandı (tekrar eden ölü kod
  birleştirildi); (3) `VehicleTypeFilterRow`'un altında segment (Ekonomik/Konfor/SUV — backend
  enum'ı), vites (backend'de sabit enum YOK, bu yüzden uydurulmadı — yüklenen araç listesinden
  `distinct()` ile dinamik çıkarılıyor) ve min. koltuk (2+/4+/5+/7+ sayısal eşik) chip satırlarından
  oluşan yeni bir `ExtraFiltersPanel` eklendi, + "Filtreleri temizle" butonu.
- **Değişen dosyalar:** `feature/maps/MapsContract.kt` (`VehicleSegment` enum; `NearbyVehicle.segment`;
  `State`'e `searchQuery`/`selectedSegment`/`selectedTransmission`/`selectedMinSeats`/
  `isFilterPanelExpanded`; `filteredVehicles` getter'ı tüm kriterlere göre genişletildi;
  `availableTransmissions`/`activeExtraFilterCount` computed property'leri), `feature/maps/MapsViewModel.kt`
  (6 yeni Intent dalı + `handle*` fonksiyonları; `toNearbyVehicle()` artık `segment`'i de parse ediyor),
  `feature/maps/MapsScreen.kt` (`SearchBar` stateful oldu; `ExtraFiltersPanel`/`SelectableChip`/
  `FilterSectionLabel` composable'ları eklendi; `VehicleTypeFilterRow` yeni `SelectableChip`
  helper'ını kullanacak şekilde sadeleştirildi).
- **Neden bu şekilde yapıldı:** `NearbyVehicle.segment`'e `= VehicleSegment.ECONOMY` varsayılan
  değeri verildi — `VehicleDetailScreen.kt` ve `ActiveRentalScreen.kt`'deki `toMapVehicle()` benzeri
  fonksiyonlar da aynı `NearbyVehicle` tipini tek-marker mini-harita için üretiyor ama segment'i hiç
  göstermiyor/filtrelemiyor; bu iki ekranın `Contract.State`'ine sadece derleme geçsin diye alakasız
  bir alan eklemek (7 dosyaya çıkıp onaylanan 5 dosya/3 dosya kapsamını aşardı) kapsam dışı olurdu —
  Maps ekranının kendi `toNearbyVehicle()` eşlemesi gerçek değeri zaten her zaman açıkça set ediyor.
  Vites filtresi için sabit bir Türkçe etiket seti (ör. "Manuel"/"Otomatik") YAZILMADI çünkü backend
  şemasında (`openapi.json`) `transmission` sabit bir enum değil serbest `String` — Agent.md §2.2
  (uydurma yasağı) gereği değerler tahmin edilmedi, bunun yerine `State.availableTransmissions`
  yüklenen gerçek veriden türetiliyor.
- **Kendi kontrolüm:** `./gradlew :app:compileDebugKotlin` ile derlendi. İlk denemede iki hata
  çıktı: (1) `import androidx.compose.foundation.layout.weight` yanlış/internal bir sembolü
  çekiyordu ("Cannot access ... it is internal in file") — kaldırıldı, `Modifier.weight()` zaten
  Row/Column scope'unda import gerektirmeden çözülüyor; (2) `NearbyVehicle`'a `segment` eklenince
  `VehicleDetailScreen.kt`/`ActiveRentalScreen.kt`'deki mevcut `NearbyVehicle(...)` çağrıları
  parametre eksikliğinden derlemeyi kırdı — yukarıdaki varsayılan değerle çözüldü. İkisi de
  düzeltildikten sonra BUILD SUCCESSFUL (yalnızca projede önceden var olan, ilgisiz uyarılar).
  Bağlı bir emülatör/cihaz olmadığından cihazda görsel/runtime testi (arama kutusuna yazınca
  listenin filtrelenmesi, Tune ikonlarının paneli aç/kapa yapması) YAPILAMADI.
- **Sıradaki adım:** Emülatörde Haritalar ekranını açıp arama kutusuna bir marka/model/plaka
  yazarak listenin filtrelendiğini, üstteki ve alttaki Tune ikonlarının aynı paneli aç/kapa
  yaptığını, segment/vites/min. koltuk chip'lerinin doğru filtrelediğini ve "Filtreleri temizle"nin
  tüm ek filtreleri sıfırladığını doğrulamak.

### 2026-07-18 — Filtre sonucu boşsa uyarı mesajı eklendi (Maps, 1 dosya)

- **Ne yapıldı:** Bir önceki batch'teki filtre panelinde, seçilen kriterlere (arama metni/tip/
  segment/vites/min. koltuk) uyan hiç araç kalmadığında ekranın sessizce "Yakınında 0 araç"
  yazıp boş bir liste göstermesi kullanıcı isteğiyle iyileştirildi. `NearbyVehiclesSheet` içine,
  `state.filteredVehicles` boşsa gösterilen yeni bir `EmptyFilterResult()` composable'ı (arama
  ikonu + "Bu özelliklerde araç yok, tekrar deneyin" metni) eklendi. Ayrıca aynı durumda
  "En Yakın Aracı Bul" butonu da devre dışı bırakıldı (`enabled` koşuluna
  `state.filteredVehicles.isNotEmpty()` eklendi) — boş sonuçla bu butona basmanın anlamı yoktu.
- **Değişen dosyalar:** `feature/maps/MapsScreen.kt`
- **Neden bu şekilde yapıldı:** Contract/ViewModel'e dokunulmadı — `filteredVehicles` zaten
  var olan bir computed property, ekranın kendisi boş durumu doğrudan bu değerden okuyabiliyor;
  yeni bir State alanı (ör. `isEmptyResult: Boolean`) eklemek gereksiz bir türetilmiş alan
  olurdu (mvi-contracts.md'nin "UI'da hesaplanan türetilmiş değerler State alanı değil,
  computed property olarak tanımlanır" kuralına aykırı düşerdi).
- **Kendi kontrolüm:** `./gradlew :app:compileDebugKotlin` ile derlendi, BUILD SUCCESSFUL,
  yeni uyarı yok. Bağlı emülatör olmadığından (tüm filtrelerin eşleşmediği bir kombinasyon
  seçilip mesajın göründüğü, "En Yakın Aracı Bul"un devre dışı kaldığı) runtime testi
  YAPILAMADI.

### 2026-07-18 — Konum izni akışındaki iki hata düzeltildi (Maps ekranı, 5 dosya)

- **Ne yapıldı:** Kullanıcının bildirdiği iki ayrı şikayet araştırıldı: (1) "izin verdim ama
  konumu bulamıyor, telefondan konumu açman gerekiyor" — kök neden, uygulamanın yalnızca
  runtime *iznini* kontrol edip cihazın konum servisinin (GPS/Ağ) fiilen açık olup olmadığını
  hiç sorgulamaması; kapalıyken `fusedClient` sessizce `null` dönüyor, kullanıcı ayarlara
  elle gitmek zorunda kalıyordu. Çözüm: `SettingsClient.checkLocationSettings()` ile durum
  sorgulanıyor, kapalıysa `ResolvableApiException` üzerinden sistemin "Konumu Aç" diyaloğu
  uygulama içinden (`StartIntentSenderForResult`) açtırılıyor; ayrıca `LocationManager
  .PROVIDERS_CHANGED_ACTION` yayını dinlenerek kullanıcı konumu hızlı ayarlardan açtığında
  ekran otomatik haberdar oluyor. (2) "izin versem de sayfalar arasında tekrar tekrar
  soruyor" — kök neden, `MainScaffold.kt`'deki `hasLocationPermission`'ın sabit `false` ile
  başlatılması; `HOME` tek bir üst düzey `composable` olduğundan (`RenCarNavHost.kt`)
  VehicleDetail/Settings/Help/Invite/HistoryDetail/RentalActive/VehiclePhotos gibi kardeş
  ekranlardan dönüşte `MainScaffold` komposizyonu sıfırdan kuruluyor ve izin zaten verilmiş
  olsa bile bir an için `false` görünüp alt sekmelerde yanlışlıkla "izin ver" snackbar'ı tekrar
  tetikleniyordu; artık gerçek `ContextCompat.checkSelfPermission` sonucuyla başlatılıyor.
  Ayrıca kullanıcının istediği şekilde, Haritalar ekranında izin/konum servisi onaylanana kadar
  ekranın üstünde kalan engelleyici bir `LocationRequiredOverlay` eklendi (`MapsScreen.kt`) —
  sistem diyaloğu reddedilse/kapatılsa da harita "bozuk" görünmüyor, yeniden deneme butonu
  gösteriliyor.
- **Değişen dosyalar:** `feature/maps/MapsContract.kt` (`State.isLocationServiceEnabled`;
  `Intent.LocationServicesEnabled/Disabled`, `PermissionRequestRetryClicked`,
  `EnableLocationServicesClicked`; `Effect.RequestLocationPermission`,
  `Effect.RequestEnableLocationServices`), `feature/maps/MapsViewModel.kt` (yeni intent
  dalları + `handleLocationServicesEnabled/Disabled`), `feature/maps/MapsRoute.kt`
  (`checkLocationSettings()` yardımcı fonksiyonu, `enableLocationServicesLauncher`,
  `PROVIDERS_CHANGED_ACTION` `BroadcastReceiver`'ı, yeni effect dalları),
  `feature/maps/MapsScreen.kt` (`LocationRequiredOverlay` composable'ı), `navigation/
  MainScaffold.kt` (`hasLocationPermission` başlangıç değeri gerçek izin kontrolünden okunuyor).
- **Neden bu şekilde yapıldı:** Yeni bağımlılık eklenmedi — `SettingsClient`/
  `LocationSettingsRequest`/`ResolvableApiException` zaten mevcut
  `play-services-location:21.4.0` içinde geliyor. Mevcut `hasLocationPermission`/
  `LocationPermissionGranted` deseniyle birebir tutarlı olması için konum servisi durumu da
  aynı State/Intent/Effect üçlüsüne (mvi-contracts.md) eklendi; ayrı bir servis/yardımcı sınıf
  açılmadı çünkü mantık zaten `MapsRoute.kt`'nin sorumluluğunda olan Android API çağrılarıyla
  (permission launcher, activity result) aynı katmanda.
- **Kendi kontrolüm:** `./gradlew :app:compileDebugKotlin` ile temiz derleme (`--rerun`) yapıldı,
  BUILD SUCCESSFUL, bu değişiklikle ilgili yeni uyarı yok (yalnızca projede önceden var olan,
  ilgisiz 3 uyarı). Bağlı emülatör/cihaz olmadığından (`adb` bu ortamda kurulu değil) izin
  reddetme/kabul etme, konum servisini kapatıp açma ve kardeş ekranlar arasında gidip gelme
  senaryolarının runtime testi YAPILAMADI — bir sonraki oturumda cihazda elle doğrulanmalı.

### 2026-07-18 — Harita ilk açılışta konuma değil araç kümesine odaklanabiliyordu, düzeltildi (1 dosya)

- **Ne yapıldı:** Kullanıcı "izin verir vermez harita konumuma odaklanıyor değil mi, ilk orayı
  görüyorum" diye sordu; kod incelenince `RencarMap.kt`'deki tek seferlik ilk kare mantığının
  (`hasFramedInitialView`) bunu garanti etmediği görüldü: `vehicles` listesi (`MapsViewModel
  .init`'te) konum izninden bağımsız hemen yüklendiğinden, GPS fix'i gelmeden önce vehicles
  değişirse kamera `myLocation + vehicles` sınır kutusuna (bounds) göre kilitleniyor ve konum
  birazdan gelse bile kamera bir daha oynamıyordu — kullanıcı ilk açılışta kendi konumu yerine
  araçların bulunduğu bölgeyi görebiliyordu. Düzeltme: ilk kare artık `myLocation` geldiği an
  SADECE ona zum yapıyor (zoom 15.0); araç sınır kutusuna düşme yalnızca 2 saniye içinde konum
  hiç gelmezse (izin reddedildi/GPS kapalı) devreye giren bir yedek haline getirildi.
- **Değişen dosyalar:** `feature/maps/RencarMap.kt` (ilk kare `LaunchedEffect`'i ikiye
  ayrıldı: konum-öncelikli anlık kare + 2 sn'lik gecikmeli araç-kümesi yedeği; `kotlinx.coroutines
  .delay` importu eklendi).
- **Neden bu şekilde yapıldı:** Kullanıcı açıkça "önce bana odaklan, sonra araçları göster"
  seçeneğini onayladı (araçlarla birlikte geniş görünüme yumuşak geçiş seçeneği yerine).
  Ayrı bir Contract/State alanı eklenmedi — bu tamamen `RencarMap`'in kendi iç kamera mantığı,
  MVI State'e yansıtılacak bir kullanıcı-görünür durum değil.
- **Kendi kontrolüm:** `./gradlew :app:compileDebugKotlin` ile derlendi, BUILD SUCCESSFUL, yeni
  uyarı yok. Bağlı emülatör/cihaz olmadığından (bu ortamda `adb` kurulu değil) konum gelişinin
  gerçekten önce/tek başına kareye alındığı, araçların artık kameranın odağını bozmadığı ve
  izin reddedilince 2 sn sonra araç kümesine düşüldüğü runtime'da GÖRSEL OLARAK DOĞRULANAMADI —
  bir sonraki oturumda cihazda: (a) konum + araçlar aynı anda yüklüyken haritanın kullanıcıya
  zum yaptığı, (b) izin reddedilince ~2 sn sonra araç kümesine düşüldüğü elle test edilmeli.

### 2026-07-18 — Araç-kümesi yedeği hâlâ konumdan önce tetikleniyordu, sabit 2sn yerine duruma göre bekleme eklendi (2 dosya)

- **Ne yapıldı:** Bir önceki batch'teki düzeltme (2sn'lik sabit gecikme) yeterli değildi:
  kullanıcı "kendi konumumu ilk göremiyorum, araçları çok çabuk getiriyor olabilir misin"
  diye bildirdi. Kök neden: gerçek bir GPS fix (özellikle soğuk başlangıçta/yüksek doğruluk
  modunda) 2 saniyeden uzun sürebiliyor; bu durumda 2sn'lik yedek zamanlayıcı `myLocation`
  gelmeden ateşleniyor, kamera araç sınır kutusuna kilitleniyor ve konum birazdan gelse bile
  (`hasFramedInitialView` artık true olduğundan) kamera bir daha oynamıyordu — yani konum her
  zaman "araçlara yenik düşüyordu". Düzeltme: `RencarMap`'e `canObtainLocation: Boolean` adında
  yeni bir parametre eklendi. Haritalar ekranı bunu `state.hasLocationPermission &&
  state.isLocationServiceEnabled` olarak geçiyor: konum gerçekten gelebilecek durumdaysa yedek
  zamanlayıcı artık sabit 2sn yerine 8sn bekliyor (gerçekçi bir GPS fix süresi) ve `myLocation`
  gelirse zaten iptal oluyor; konum hiç gelemeyecek durumdaysa (izin yok/servis kapalı) yedek
  hâlâ anında devreye giriyor. Parametrenin varsayılanı `false` — VehicleDetail/ActiveRental
  mini-haritaları (tek seferlik/sessiz konum okuyan, sürekli GPS akışı olmayan ekranlar)
  hiçbir gecikme olmadan doğrudan araç konumuna kareleniyor, eski davranışları korunuyor.
- **Değişen dosyalar:** `feature/maps/RencarMap.kt` (`canObtainLocation` parametresi; yedek
  `LaunchedEffect`'in `delay` süresi duruma göre 0/8000ms), `feature/maps/MapsScreen.kt`
  (`RencarMap` çağrısına `canObtainLocation = state.hasLocationPermission &&
  state.isLocationServiceEnabled` eklendi).
- **Neden bu şekilde yapıldı:** Sabit bir gecikme süresini büyütmek (ör. 2sn yerine 5sn) yine
  tahmini bir sayı olurdu ve GPS fix bazen ondan da uzun sürebilir; asıl doğru sinyal zaten
  ekranın kendisinde mevcuttu (`hasLocationPermission`/`isLocationServiceEnabled`) — bu yüzden
  zamanlayıcıyı büyütmek yerine "konum gelecek mi, gelmeyecek mi" bilgisini doğrudan `RencarMap`'e
  taşımak tercih edildi. `VehicleDetailScreen.kt`/`ActiveRentalScreen.kt` çağrılarına
  dokunulmadı — parametrenin varsayılan değeri onların mevcut (gecikmesiz) davranışını
  bozmadan koruyor.
- **Kendi kontrolüm:** `./gradlew :app:compileDebugKotlin` ile derlendi, BUILD SUCCESSFUL, yeni
  uyarı yok. Bağlı emülatör/cihaz olmadığından (`adb` kurulu değil) GPS fix'in gerçekten 8sn
  içinde gelip kamerayı konuma kilitlediği, izin reddedilince hâlâ anında araçlara düşüldüğü ve
  VehicleDetail/ActiveRental mini-haritalarının davranışının değişmediği runtime'da
  DOĞRULANAMADI — bir sonraki oturumda cihazda/emülatörde test edilmeli.

### 2026-07-18 — Aktif yolculukta geçen süre 5'er 5'er sıçrıyordu, her saniye artacak şekilde düzeltildi (1 dosya)

- **Ne yapıldı:** Kullanıcı "araç kiralama başladığında saniyeler 5 5 gidiyor, her saniye
  gözüksün" diye bildirdi. Kök neden: `ActiveRentalViewModel`'deki `elapsedSeconds` doğrudan
  `GET /rentals/active` yoklamasından geliyordu ve bu uç yalnızca `POLL_INTERVAL_MS = 5000`ms'de
  bir çağrılıyordu — ekrandaki süre bu yüzden 0, 5, 10, 15... şeklinde 5'er sıçrayarak
  ilerliyordu, her saniye artmıyordu. Düzeltme: 1 saniyelik ayrı bir `tickingJob`
  (`startTicking()`) eklendi; bu, `elapsedSeconds`'ı her saniye yerelde 1 artırıyor. 5 saniyelik
  sunucu yoklaması (`refreshActiveRental()`) hâlâ aynı sıklıkta çalışıyor ama artık yalnızca
  `elapsedSeconds`'ı gerçek sunucu değerine "senkronize/düzeltiyor" (sürüklenmeyi önlüyor);
  `currentCost`/`distanceKm` gibi sunucudan gelmesi gereken diğer alanlara dokunulmadı, onlar
  hâlâ 5sn'de bir güncelleniyor (kapsam dışı, kullanıcı yalnızca saniye göstergesinden bahsetti).
- **Değişen dosyalar:** `feature/rental/active/ActiveRentalViewModel.kt` (`TICK_INTERVAL_MS`
  sabiti, `tickingJob` + `startTicking()`, ortak `stopTimers()` yardımcı fonksiyonu — hem
  `pollingJob` hem `tickingJob`'ı birlikte durdurup yeniden başlatan yerler bu fonksiyona
  yönlendirildi: 404 durumu, `handleFinishRentalClicked`'daki başarı/hata dalları, `onCleared`).
- **Neden bu şekilde yapıldı:** Poll aralığını 1sn'ye düşürmek yerine (gereksiz sunucu yükü)
  yerel bir sayaç eklendi — klasik "smooth timer + periyodik sunucu senkronizasyonu" deseni.
  `Contract.State.elapsedSeconds` alanına dokunulmadı, yalnızca ViewModel'in onu güncelleme
  sıklığı değişti; mvi-viewmodel-rules.md'ye aykırı bir şey yok (state güncellemesi hâlâ yalnızca
  `_state.update { it.copy(...) }` ile).
- **Kendi kontrolüm:** `./gradlew :app:compileDebugKotlin` ile derlendi, BUILD SUCCESSFUL, yeni
  uyarı yok. Bağlı emülatör/cihaz olmadığından (`adb` kurulu değil) aktif bir kiralamada süre
  etiketinin gerçekten her saniye arttığı ve 5sn'lik sunucu senkronizasyonunun görünür bir
  sıçramaya/geri gitmeye yol açmadığı runtime'da DOĞRULANAMADI — bir sonraki oturumda cihazda
  test edilmeli.

### 2026-07-18 — Kiralama Geçmişi kartlarına rota önizleme ikonu eklendi (1 dosya)

- **Ne yapıldı:** Aktif yolculuktaki rotanın neden dümdüz göründüğü araştırılırken (bkz. bir
  önceki konu — kod tarafında bir hata bulunamadı, muhtemelen soketten az/tek nokta gelmesi)
  kullanıcı konuyu değiştirip Figma tasarımından bir ekran görüntüsü paylaştı: "Kiralama
  Geçmişi" listesindeki her kartın solundaki küçük görselin, o yolculuğun rotasını gösteren
  yeşil (başlangıç) / mavi (bitiş) noktalı bir çizgi ikonu olmasını istedi. `HistoryScreen.kt`'
  deki düz araba ikonlu `CarThumbnail()` composable'ı kaldırılıp yerine `RouteThumbnail()`
  eklendi: 64dp'lik kutuda hafif bir "sokak ızgarası" (2 ince çizgi) arka planı üzerine,
  yeşil başlangıç noktasından mavi bitiş noktasına giden yumuşak bir eğri (`Path.quadraticTo`)
  çiziliyor. Kiralama `id`'sinden türetilen bir varyant (`hashCode() % 4`) başlangıç/bitiş
  köşelerini ve eğrinin yönünü belirliyor — böylece kartlar tasarımdaki gibi birbirinden farklı
  görünüyor ama her kart kendi içinde her açılışta aynı kalıyor (kararlı/deterministik).
- **Değişen dosyalar:** `feature/history/HistoryScreen.kt`.
- **Neden bu şekilde yapıldı:** `RentalRecord`/`HistoryContract` ve openapi.json'daki geçmiş
  kiralama uçları yalnızca süre/mesafe/ücret özeti dönüyor, hiçbir GPS/rota koordinatı
  saklamıyor — bu yüzden gerçek bir rota YENİDEN ÇİZİLEMEZ (Agent.md §2.2 uydurma yasağı:
  var olmayan koordinat verisi üretilmedi). Bunun yerine tasarımdaki görseli birebir taklit eden,
  tamamen dekoratif/stilize bir Canvas çizimi tercih edildi; bu, kullanıcının ekran görüntüsüyle
  netleştirdiği talebe (gerçek harita değil, o görünümdeki gibi bir ikon) tam karşılık veriyor.
- **Kendi kontrolüm:** `./gradlew :app:compileDebugKotlin` ile derlendi, BUILD SUCCESSFUL, yeni
  uyarı yok (ilk denemede `quadraticBezierTo` deprecation uyarısı çıktı, `quadraticTo` ile
  değiştirilip giderildi). Bağlı emülatör/cihaz olmadığından (`adb` kurulu değil) 4 farklı
  varyantın gerçekten görsel olarak ayırt edilebilir ve tasarıma yakın göründüğü runtime'da
  GÖRSEL OLARAK DOĞRULANAMADI — bir sonraki oturumda Kiralama Geçmişi ekranı cihazda açılıp
  paylaşılan tasarımla karşılaştırılmalı.

### 2026-07-18 — 8 ekranda status bar / gesture nav bar üzerine taşma düzeltildi (2 batch, 8 dosya)

- **Ne yapıldı:** Kullanıcı "uygulama telefonun saat/bildirim kısmına ve alttaki oklara kayıyor,
  dinamik olsun" diye bildirdi. `MainActivity.kt`'de `enableEdgeToEdge()` açık olduğundan içerik
  varsayılan olarak tüm ekranın kenarına kadar çiziliyor; `Login/Register/Otp/Confirmation/
  Selfie/License/Onboarding` ekranlarında zaten `.systemBarsPadding()` vardı ve `MainScaffold`
  (Harita/Geçmiş/Cüzdan/Profil sekmeleri) Material3 `Scaffold`'un `innerPadding`'i sayesinde
  güvenliydi, ama `RenCarNavHost.kt`'de `HOME`'un kardeşi olan 8 üst düzey rota hiç inset koruması
  almıyordu. Explore agent ile tam liste çıkarıldı, ardından 2 batch'te (Agent.md §2.1 — tek
  seferde 5 dosya sınırı) düzeltildi: (Batch 1, düz `Column(fillMaxSize())` kökü olan 5 ekran)
  `SettingsScreen.kt`, `HistoryDetailScreen.kt`, `InviteScreen.kt`, `HelpScreen.kt`,
  `ReservationConfirmationScreen.kt` — kök `Modifier`'a `.systemBarsPadding()` eklendi (mevcut
  Login/Register deseniyle birebir aynı). (Batch 2) `VehiclePhotosScreen.kt` aynı basit desenle
  düzeltildi; `VehicleDetailScreen.kt` ve `ActiveRentalScreen.kt` farklı ele alındı çünkü arka
  planda tam ekran `RencarMap` var — kök'e `.systemBarsPadding()` eklemek haritayı da içeri
  çekip istenmeyen bir kenar boşluğu bırakırdı. Bunun yerine yalnızca dokunulabilir kontrollere
  (geri butonu -> `.statusBarsPadding()`, alttaki `VehicleDetailSheet`/`ActiveRentalSheet` ->
  `.navigationBarsPadding()`) inset eklendi; haritanın kendisi kenara kadar uzanmaya devam ediyor.
- **Değişen dosyalar:** `feature/settings/SettingsScreen.kt`, `feature/history/detail/
  HistoryDetailScreen.kt`, `feature/invite/InviteScreen.kt`, `feature/help/HelpScreen.kt`,
  `feature/rental/reservation/ReservationConfirmationScreen.kt`, `feature/rental/photos/
  VehiclePhotosScreen.kt`, `feature/maps/detail/VehicleDetailScreen.kt`, `feature/rental/active/
  ActiveRentalScreen.kt`.
- **Neden bu şekilde yapıldı:** Sabit dp değeri yerine `systemBarsPadding()`/`statusBarsPadding()`/
  `navigationBarsPadding()` kullanıldı çünkü bunlar Compose'un `WindowInsets` API'siyle cihazdan
  cihaza (3 tuşlu navigasyon/gesture çubuğu, çentik/delik kamera, klavye açılışı vb.) otomatik
  uyum sağlıyor — kullanıcının "dinamik olsun" talebi buydu. Harita arka planlı iki ekranda kök
  yerine yalnızca kontrollere padding eklenmesi bilinçli bir tercih: haritanın edge-to-edge
  görünümü tasarım gereği korunmalı, yalnızca üstündeki geri butonu ve alttaki eylem sheet'i
  sistem çubuklarının altında/arkasında kalmamalı.
- **Kendi kontrolüm:** Her iki batch'ten sonra `./gradlew :app:compileDebugKotlin` (2. batch'te
  `--rerun` ile temiz derleme) çalıştırıldı, ikisinde de BUILD SUCCESSFUL; yalnızca projede
  önceden var olan, bu değişikliklerle ilgisiz 3 uyarı. Bağlı emülatör/cihaz olmadığından
  (`adb` bu ortamda kurulu değil) 8 ekranın da gerçek bir cihazda/emülatörde (özellikle gesture
  navigasyonlu ve 3-tuşlu navigasyonlu cihazlarda, ayrıca çentikli ekranlarda) geri butonlarının
  ve alt butonların artık sistem çubuklarıyla çakışmadığı GÖRSEL OLARAK DOĞRULANAMADI — bir
  sonraki oturumda cihazda test edilmeli.

### 2026-07-18 — Cüzdan (Wallet) network/repository altyapısı kuruldu (4 dosya)

- **Ne yapıldı:** Cüzdan ekranı için (o ana kadar tamamen `WalletMockSource`
  ile beslenen mock bir ekrandı) `GET /wallet` ve `POST /wallet/topup`
  uçlarına bağlanacak Retrofit/Hilt altyapısı sıfırdan kuruldu:
  `WalletResponseDto`/`WalletTransactionDto`/`TopupDto` (openapi.json'daki
  `Wallet` tag'i şemasına birebir), `WalletApiService` (GET wallet, POST
  wallet/topup), ve `AuthRepository.kt`'deki paylaşılan `AuthResult<T>` +
  `extractErrorMessage()` desenini yeniden kullanan `WalletRepository`
  (`getWallet()`, `topup(amount)`). `NetworkModule.kt`'ye
  `provideWalletApiService` eklendi. `WalletViewModel`/`WalletRoute`/
  `WalletScreen`/`WalletMockSource`'a bilinçli olarak dokunulmadı — ekran
  hâlâ mock veriyle çalışıyor, bu batch sadece altyapı.
- **Değişen dosyalar (yeni):** `data/network/dto/WalletDtos.kt`,
  `data/network/WalletApiService.kt`, `data/repository/WalletRepository.kt`.
  **Değişen dosya:** `di/NetworkModule.kt` (`provideWalletApiService`
  eklendi).
- **Neden bu şekilde yapıldı:** `WalletTransactionDto.type` alanı (`TOPUP`/
  `RENTAL_PAYMENT`/`REFERRAL_BONUS`), projedeki yerleşik kararla tutarlı
  olarak Kotlin enum değil `String` tutuldu (bkz. `role`/`type`/`segment`/
  `status` için aynı gerekçe — backend yeni bir değer dönerse Gson
  deserialization'ının patlamasını önlemek için). `rentalId` alanı ham
  şemada `"type": "object", "nullable": true` olarak tanımlıydı (açıklaması
  "ilgili kiralamanın id'si" olmasına rağmen) — Agent.md §2.2 (uydurma
  yasağı) gereği bu belirsizlik kullanıcıya soruldu, kullanıcı onayıyla
  `String?` olarak tiplendi. `POST /wallet/topup` başarı kodu şemada 200
  değil 201 — `Response.isSuccessful` tüm 2xx'i kapsadığından
  `WalletRepository`'de ekstra bir ayrıma gerek duyulmadı. `topup()`
  fonksiyonu ayrıca bir `getWallet()` çağrısı yapmıyor çünkü backend zaten
  güncel `WalletResponseDto`'yu (bakiye + işlemler) topup cevabında
  döndürüyor. Kayıtlı kart (`savedCards`) alanı kapsam dışı bırakıldı —
  backend'de bu alanın karşılığı `Wallet` tag'i DEĞİL, ayrı bir `Cards`
  tag'i altında (`GET`/`POST /cards`, `PATCH /cards/{id}/default`,
  `DELETE /cards/{id}`) mevcut; bu batch'in konusu olmadığından sıradaki
  (Cards altyapı) batch'ine bırakıldı.

### 2026-07-18 — Kayıtlı Kartlar (Cards) network/repository altyapısı kuruldu (4 dosya)

- **Ne yapıldı:** Bir önceki Wallet altyapı batch'inde kapsam dışı
  bırakılan `savedCards` alanının backend karşılığı olan `Cards` tag'i
  (`GET /cards`, `POST /cards`, `PATCH /cards/{id}/default`,
  `DELETE /cards/{id}`) için Wallet batch'iyle aynı desende Retrofit/Hilt
  altyapısı kuruldu: `CardResponseDto`/`CreateCardDto`
  (openapi.json'daki `Cards` şemasına birebir), `CardsApiService` (4 uç),
  ve paylaşılan `AuthResult<T>` desenini kullanan `CardsRepository`
  (`listCards()`, `addCard(brand, last4, expMonth, expYear)`,
  `setDefaultCard(id)`, `deleteCard(id)`). `NetworkModule.kt`'ye
  `provideCardsApiService` eklendi. `WalletViewModel`/`WalletScreen`
  entegrasyonuna bilinçli olarak dokunulmadı — sonraki batch'in konusu.
- **Değişen dosyalar (yeni):** `data/network/dto/CardDtos.kt`,
  `data/network/CardsApiService.kt`, `data/repository/CardsRepository.kt`.
  **Değişen dosya:** `di/NetworkModule.kt` (`provideCardsApiService`
  eklendi).
- **Neden bu şekilde yapıldı:** `CardResponseDto.brand`/`CreateCardDto.brand`
  alanı, şemada kapalı bir enum (`VISA`/`MASTERCARD`) olmasına rağmen
  Kotlin enum değil `String` tutuldu — projedeki yerleşik konvansiyonla
  (`role`/`type`/`segment`/`status`/`transmission`/`WalletTransactionDto.type`)
  tutarlılık ve backend ileride yeni bir marka (ör. TROY/AMEX) döndürürse
  Gson deserialization'ının çökmesini önlemek için (kullanıcı onayıyla
  karar verildi, gerekçe planda sunuldu). `expMonth`/`expYear` şemada
  `"type": "number"` olsa da `VehicleResponseDto.seats` emsaliyle tutarlı
  olarak `Int` tutuldu (tam sayı anlamına gelen alanlar, parasal değil).
  `deleteCard()`, `RentalsRepository.cancelRental()` ile BİREBİR aynı
  desende `AuthResult<Unit>` döndürüyor — `DELETE /cards/{id}` 204 No
  Content döndüğünden gövde kontrolü yapılmadan yalnızca
  `response.isSuccessful` kontrol ediliyor (`CardsApiService.deleteCard`
  de aynı nedenle `Response<Unit>` imzasını kullanıyor, projede zaten
  `RentalsApiService.cancelRental`'da mevcut bir emsal). `CreateCardDto`'da
  kullanıcının açık talimatıyla tam kart numarası/CVV alanı KESİNLİKLE
  YOK — yalnızca `brand`/`last4`/`expMonth`/`expYear` (backend zaten bunu
  400 ile reddediyor, PCI uyumluluğu).
- **Kendi kontrolüm:** `./gradlew :app:compileDebugKotlin` ile derlendi,
  BUILD SUCCESSFUL. Yeni dosyalar henüz hiçbir ViewModel'den çağrılmadığından
  runtime/network testi yapılmadı (Wallet altyapı batch'iyle tutarlı olarak
  sonraki batch'e bırakıldı).

### 2026-07-18 — WalletViewModel/Route/Screen gerçek API'ye bağlandı (4 dosya)

- **Ne yapıldı:** Cüzdan ekranı, önceki iki batch'te kurulan ama hiçbir
  yerden çağrılmayan `WalletRepository`/`CardsRepository`'ye uçtan uca
  bağlandı. `WalletViewModel` artık `@HiltViewModel` +
  `@Inject constructor(WalletRepository, CardsRepository)`; açılışta
  `coroutineScope { async {...}; async {...} }` (SplashViewModel'deki
  paralel-çağrı deseni, min-delay'siz) ile `GET /wallet` +
  `GET /cards` paralel çağrılıyor. "Bakiye Yükle" artık gerçek bir
  `ModalBottomSheet` (10-5000 TL client-side doğrulama, `POST /wallet/topup`,
  dönen `WalletResponseDto` doğrudan state'e yazılıyor). "+ Ekle" artık
  gerçek bir `ModalBottomSheet` (VISA/MASTERCARD `FilterChip` seçimi +
  last4 + ay/yıl, `YearMonth` ile geçmiş SKT reddi, `POST /cards`). Bir
  karta tıklamak `PATCH /cards/{id}/default` çağırıyor; yeni eklenen bir
  silme ikonu `DELETE /cards/{id}` çağırıp SONRASINDA `listCards()` ile
  listeyi yeniden çekiyor (yerel "isDefault tahmini" YOK).
  `WalletRoute.kt` eski `viewModel()` yerine `hiltViewModel()` kullanıyor.
  `WalletMockSource.kt` artık hiçbir yerden çağrılmıyor ama
  `HistoryMockSource`/`ProfileMockSource` kararıyla tutarlı olarak
  SİLİNMEDİ.
- **Değişen dosyalar:** `feature/wallet/WalletContract.kt`,
  `feature/wallet/WalletViewModel.kt`, `feature/wallet/WalletRoute.kt`,
  `feature/wallet/WalletScreen.kt`.
- **Neden bu şekilde yapıldı:** `CardType` enum'ına `OTHER` eklendi;
  backend'in `brand` alanı `String` olduğundan hem `WalletViewModel
  .toSavedCard()`'daki hem de `WalletScreen.CardTypeIcon`'daki
  `when (brand)`/`when (type)` blokları artık exhaustive bir `else`/`OTHER`
  dalına sahip — kullanıcının işaretlediği "bilinmeyen marka çökmesin"
  riski böyle kapatıldı. `State`'e `errorMessage`/`isTopupSubmitting`/
  `isAddCardSubmitting` eklendi; hata gösterimi History/Profile'daki kalıcı
  `State.errorMessage` deseniyle tutarlı (ayrı bir `ShowError` Effect'i
  yok). Sheet'lerin başarı sonrası otomatik kapanması yeni bir `Close*`
  Effect'i ile YAPILMADI — `mvi-contracts.md` Effect isimlerinin yalnızca
  `Navigate`/`Show` prefixiyle başlamasını zorunlu kılıyor; bunun yerine
  Route'ta `isTopupSubmitting`/`isAddCardSubmitting`'in `true→false`
  geçişini (önceki değer `remember` ile izlenerek) `errorMessage == null`
  koşuluyla birlikte kontrol eden bir `LaunchedEffect` sheet'i kapatıyor.
  `AddBalanceSheet`/`AddCardSheet`, `WalletScreen.kt` içinde `private`
  DEĞİL tanımlandı çünkü `WalletRoute.kt` (aynı paket, farklı dosya) bunları
  çağırıyor — Kotlin'de `private` üst düzey bildirim dosya kapsamıyla
  sınırlı. `addCard`/`setDefaultCard` sonrası ekstra bir `GET /cards`
  YOK (dönen veri zaten kesin); yalnızca `deleteCard` sonrası tam liste
  yenileniyor çünkü backend'in yeni varsayılanı hangi karta atadığı
  istemci tarafından tahmin edilemez. Kart silme için onay diyaloğu
  eklenmedi (istenmedi).
- **Kendi kontrolüm:** `./gradlew :app:compileDebugKotlin` ile derlendi,
  BUILD SUCCESSFUL (tek denemede, yeni uyarı yok). `grep -r WalletMockSource
  app/src/main/java` ile dosyanın artık yalnızca kendi tanımında geçtiği
  (hiçbir çağıran kalmadığı) doğrulandı. Bağlı emülatör/cihaz olmadığından
  (`adb` kurulu değil) runtime testi (cüzdan açılışta gerçek bakiye/kart
  verisiyle dolması, "Bakiye Yükle"/"+ Ekle" sheet'lerinin doğrulama ve
  API çağrısı akışı, kart silme sonrası varsayılanın doğru yeniden
  çekilmesi) YAPILAMADI — bir sonraki oturumda cihazda elle doğrulanmalı.
- **Kendi kontrolüm:** `./gradlew :app:compileDebugKotlin` ile derlendi,
  BUILD SUCCESSFUL. Yeni dosyalar henüz hiçbir ViewModel'den çağrılmadığından
  runtime/network testi yapılmadı (Auth/Vehicles altyapı batch'leriyle
  tutarlı olarak sonraki batch'e bırakıldı).

### 2026-07-19 — Kiralama Ödemesi ekranı sıfırdan eklendi (3 batch, 12 dosya)

- **Ne yapıldı:** Yolculuk bittikten sonra açılan, o tek kiralamanın ödemesini
  alan yeni bir ekran (`feature/rental/payment/`) eklendi — "Kiralamalarım"
  listesinden (`feature/history`, dün tamamlanan iş) tamamen ayrı, o pakete
  hiç dokunulmadı. Araştırmada iki gerçek bulgu çıktı: (1) finish cevabındaki
  `usageFee` alanı `GET /rentals/{id}`'de bir daha hiç dönmüyor — bu yüzden
  Payment ekranı, projedeki her detay ekranıyla (VehicleDetail/
  ReservationConfirmation/HistoryDetail) aynı desende yalnızca `rentalId`
  taşıyor, kendi `GET /rentals/{id}` + `GET /cards` çağrısını yapıyor ve
  `usageFee`'yi `totalPrice - startFee - serviceFee` olarak hesaplıyor (finish
  endpoint'inin openapi açıklamasında birebir tanımlı formül); (2)
  `POST /rentals/{id}/pay` projede hiç yoktu, sıfırdan eklendi. Batch 1
  (veri katmanı): `PayRentalDto`/`PaidCardSummaryDto`/`PayRentalResponseDto`
  + `RentalsApiService.payRental` + `RentalsRepository.payRental`. Batch 2
  (yeni MVI dörtlüsü): `RentalPaymentContract/ViewModel/Screen/Route` —
  `VehicleDetailViewModel`'deki `@AssistedInject`/`@AssistedFactory`
  deseniyle. Batch 3 (bağlama): `ActiveRentalContract/ViewModel/Route`'taki
  `Effect.NavigateToHome` → `NavigateToPayment(rentalId)`; `RenCarNavHost.kt`'ye
  `rental-payment/{rentalId}` rotası ve "Kiralamalarım" sekmesine doğrudan
  inebilmek için `home-history` rotası (tekrarlanan 6 callback'i çoğaltmamak
  için ortak `HomeGraph` yardımcı composable'ına taşındı); `MainScaffold.kt`'ye
  `startTab: BottomNavItem = BottomNavItem.Map` parametresi eklendi.
- **Değişen dosyalar (yeni):** `feature/rental/payment/RentalPaymentContract.kt`,
  `RentalPaymentViewModel.kt`, `RentalPaymentScreen.kt`, `RentalPaymentRoute.kt`.
  **Değişen dosyalar:** `data/network/dto/RentalDtos.kt`,
  `data/network/RentalsApiService.kt`, `data/repository/RentalsRepository.kt`,
  `feature/rental/active/ActiveRentalContract.kt`, `ActiveRentalViewModel.kt`,
  `ActiveRentalRoute.kt`, `navigation/RenCarNavHost.kt`,
  `navigation/MainScaffold.kt`.
- **Neden bu şekilde yapıldı:** Kullanıcı onayıyla "sadece rentalId taşı,
  ekranda yeniden çek" seçeneği tercih edildi (alternatifi: finish cevabının
  tüm alanlarını ~9 path segmentiyle taşımak) — bu sayede
  `RentalsApiService.finishRental`'ın dönüş tipindeki mevcut uyumsuzluğuna
  (openapi'de `FinishRentalResponseDto` olması gerekirken hâlâ
  `RentalResponseDto` yazıyor — AYRI, bağımsız bir düzeltme konusu, bu
  batch'te dokunulmadı) hiç gerek kalmadı ve Payment ekranı ileride herhangi
  bir COMPLETED+UNPAID kiralama için bağımsız açılabilir hale geldi. Kart
  seçimi için `feature/wallet`'a özgü `WalletContract.SavedCard` bilinçli
  olarak reuse EDİLMEDİ (çapraz-feature Contract importu, paket izolasyonuna
  aykırı olurdu); bunun yerine zaten hem alanları karşılayan
  `data/network/dto/CardDtos.kt`'deki `CardResponseDto` doğrudan
  `State.cards`'ta kullanıldı — ne yeni bir model tekrarı, ne bağımlılık.
  IYZICO bu batch'te yalnızca UI'da seçilebilir; "Öde" tıklanınca
  `handlePayClicked` içinde herhangi bir repository/API çağrısından ÖNCE
  `ShowInfo("İyzico ile ödeme yakında eklenecek.")` Effect'i gönderilip
  `return` ediliyor — gerçek İyzico entegrasyonu ayrı, sıradaki bir batch'in
  konusu. Varsayılan ödeme yöntemi: `listCards()` sonucunda `isDefault`
  kartı varsa `CARD` + o kart, yoksa `WALLET`. Kalıcı `State.errorMessage`
  deseni (`WalletContract`/`HistoryDetailContract` ile tutarlı) 409/400 ödeme
  hatalarında ekranı açık tutuyor, kullanıcı yöntem değiştirip tekrar
  deneyebiliyor (dead-end yok). `PayRentalDto`'da `discountCode`/
  `iyzicoPaymentId` bilinçli olarak YOK — tasarımda indirim satırı istenmedi,
  gerçek İyzico akışı kapsam dışı. Toplam dosya sayısı (12) Agent.md §2.1'in
  5 dosya sınırını aştığından iş, birbirine bağlı 3 batch'e bölündü ve plan
  onaydan önce kullanıcıya sunuldu (§2.3); onay sürecinde kullanıcının
  belirttiği bir taslak hata (kart için ayrı, gereksiz bir `PaymentCard`
  modeli tanımlanmıştı) planı ExitPlanMode ile sunmadan hemen önce düzeltildi.
- **Kendi kontrolüm:** Her 3 batch'ten sonra ayrı ayrı
  `./gradlew :app:compileDebugKotlin` çalıştırıldı, üçünde de BUILD
  SUCCESSFUL, yeni uyarı yok. `grep -r onNavigateToHome` ile `ActiveRental*`
  dosyalarında eski effect/callback adının hiç kalmadığı, yalnızca ilgisiz
  Auth/Onboarding/Splash akışlarında (dokunulmayan dosyalar) geçtiği
  doğrulandı. Bağlı emülatör/cihaz olmadığından (`adb` kurulu değil) uçtan
  uca akış (Bitir → Ödeme ekranı → Cüzdan/Kart ile öde → Kiralamalarım'a
  düşme, İyzico "yakında" bildirimi, 409 bakiye yetersiz senaryosu) runtime'da
  DOĞRULANAMADI — bir sonraki oturumda cihazda test edilmeli.

### 2026-07-19 — İyzico Checkout Form network/repository altyapısı eklendi

- **Ne yapıldı:** `openapi.json`'daki "Iyzico" tag'i altındaki 8 endpoint'ten
  yalnızca Checkout Form akışının konusu olan ikisi (`POST
  /iyzico/checkout-form/initialize`, `GET /iyzico/checkout-form/result/{token}`)
  için Wallet/Cards altyapı batch'leriyle aynı desende (DTO + ApiService +
  Repository + NetworkModule) 4 dosyalık bir altyapı katmanı kuruldu. Diğer
  6 endpoint (doğrudan kart, 3DS, iptal, iade) kullanıcı onayıyla bilinçli
  olarak kapsam dışı bırakıldı.
- **Değişen dosyalar (yeni):** `data/network/dto/IyzicoDtos.kt`,
  `data/network/IyzicoApiService.kt`, `data/repository/IyzicoRepository.kt`.
  **Değişen dosya:** `di/NetworkModule.kt` (`provideIyzicoApiService` eklendi).
- **Neden bu şekilde yapıldı:** `InitializeCheckoutFormDto`'nun gerçek
  şemasındaki `enabledInstallments`/`buyer` alanları kullanıcının açık
  talimatıyla DTO'ya eklenmedi (Checkout Form akışında ikisi de opsiyonel,
  backend eksiklerinde otomatik varsayılan kullanıyor). `IyzicoRepository
  .initializeCheckoutForm`, `basketId` formatını (`rental-<rentalId>`)
  çağıran tarafın hatırlamasına gerek bırakmadan kendi içinde üretiyor —
  backend'in `POST /rentals/{id}/pay` doğrulaması bu formatı aradığından
  (openapi açıklamasında birebir belirtilmiş), bu formatı tek bir yerde
  garanti altına almak istendi. Şemada `"type": "number"` olan alanlardan
  parasal olanlar (`price`, `paidPrice`) projede yerleşik olan `Double`
  kararına (`RentalResponseDto.totalPrice` emsali), sayaç/durum
  niteliğindeki alanlar (`tokenExpireTime`, `installment`, `fraudStatus`)
  `CardDtos.kt`'deki `expMonth`/`expYear` kararına (tam sayı, parasal değil)
  uyacak şekilde `Int` tutuldu. `AuthResult`/`extractErrorMessage`,
  `AuthRepository.kt` içinde aynı pakette (`data.repository`) zaten
  tanımlı olduğundan yeniden tanımlanmadı, doğrudan kullanıldı.
- **Kendi kontrolüm:** `./gradlew :app:compileDebugKotlin` ile derlendi,
  BUILD SUCCESSFUL, yeni uyarı yok. Yeni dosyalar henüz hiçbir ViewModel'den
  çağrılmadığından (Wallet/Cards altyapı batch'leriyle tutarlı olarak)
  runtime/network testi yapılmadı — WebView bileşeni ve Ödeme ekranına
  bağlama ayrı, sonraki batch'lerin konusu.

### 2026-07-19 — Iyzico Checkout Form paylaşılan WebView ödeme bileşeni eklendi

- **Ne yapıldı:** `IyzicoRepository`'yi kullanan, bağımsız/tekrar
  kullanılabilir bir WebView ödeme bileşeni `ui/components/` altına (bir
  NavHost hedefi DEĞİL, koşullu gösterilen tam ekran `Dialog`) 4 dosya
  olarak eklendi: `IyzicoPaymentWebViewContract.kt` (State/Intent/Effect),
  `IyzicoPaymentWebViewViewModel.kt` (`@AssistedInject`,
  rentalId/price/description parametreli), `IyzicoPaymentWebViewScreen.kt`
  (`AndroidView` ile `android.webkit.WebView`), `IyzicoPaymentWebViewRoute.kt`
  (`Dialog` + Hilt `creationCallback`). Akış: initialize → paymentPageUrl
  WebView'da açılır → `WebViewClient.shouldOverrideUrlLoading` URL'in
  `iyzico/checkout-form/callback` path'ini içerip içermediğini POLLING
  OLMADAN dinler → yakalanınca WebView içerik göstermeyi durdurur (`return
  true`) ve elde tutulan token ile `getCheckoutFormResult` TEK SEFERLİK
  çağrılır → `paymentStatus` sonucuna göre `ShowPaymentSucceeded`/
  `ShowPaymentFailed` effect'i.
- **Değişen dosyalar (yeni):** `ui/components/IyzicoPaymentWebViewContract.kt`,
  `IyzicoPaymentWebViewViewModel.kt`, `IyzicoPaymentWebViewScreen.kt`,
  `IyzicoPaymentWebViewRoute.kt`.
- **Neden bu şekilde yapıldı:** `docs/api/openapi.json`'daki açıklamaya göre
  (`POST /iyzico/checkout-form/callback` — istemci tarafından gönderilen bir
  `callbackUrl` alanı yok) callback path'i backend'de sabit; bu yüzden
  token'ı callback URL'inden parse etmeye gerek kalmadan yalnızca "bu path'e
  ulaşıldı mı" kontrolü yeterli görüldü, token adım 1'den beri bellekte
  tutuluyor. Bileşen `feature/` değil `ui/components/` altına konuldu çünkü
  bir nav hedefi değil — `mvi-overview.md`'nin Route/Screen/Contract/
  ViewModel dörtlüsü yine de birebir uygulandı, yalnızca paket yerleşimi
  farklı; Effect isimlendirmesi (`ShowPaymentSucceeded`/`ShowPaymentFailed`/
  `ShowPaymentCancelled`) `mvi-contracts.md`'nin `Show` prefix kuralına
  esnetilmeden uyuyor. "İptal" (kullanıcı WebView'ı manuel kapattı) ile
  "hata" kasıtlı olarak ayrı effect'ler (`ShowPaymentCancelled` vs
  `ShowPaymentFailed`) — kullanıcının açık gereksinimiydi. Terminal
  effect'lerin yalnızca bir kez gönderilmesi için `hasResolved` guard'ı
  eklendi (örn. callback yakalanırken kullanıcı aynı anda geri tuşuna
  basarsa iki effect birden gitmesin diye); `sendEffect()` yine tek
  Channel-gönderim noktası olarak korundu. `paymentPageUrl` boş/null
  gelirse WebView hiç açılmadan `errorMessage` state'e yazılıp
  `ShowPaymentFailed` effect'i gönderiliyor (kullanıcı gereksinimiydi).
  `Dialog`'un varsayılan `dismissOnBackPress = true` davranışı geri tuşunu
  zaten `Dismissed` Intent'ine yönlendirdiğinden ayrı bir `BackHandler`
  eklenmedi; `dismissOnClickOutside = false` yapıldı (ödeme akışının
  yanlışlıkla dışarı tıklamayla iptali önlensin diye).
- **Bilinen sınırlama (çözülmedi, sonraki bağlama batch'ine not):**
  `hiltViewModel(creationCallback = ...)` en yakın `ViewModelStoreOwner`
  kapsamında sınıf+key'e göre önbelleğe alınıyor. Bu bileşen bir NavHost
  hedefi olmadığından, çağıran ekran aynı `ViewModelStoreOwner`'da kalırken
  kullanıcı ödemeyi iptal edip tekrar denerse Compose aynı ViewModel
  örneğini geri getirebilir ve `init` (`initializeCheckoutForm`) TEKRAR
  ÇALIŞMAYABİLİR. Bağlama olmadan test edilemeyecek bir varsayım üzerine
  kurmamak için bu batch'te bilinçli olarak çözülmedi; önerilen çözüm:
  sonraki bağlama batch'inde `hiltViewModel(key = ...)` ile dialog her
  açıldığında benzersiz bir key (örn. `remember { UUID.randomUUID()
  .toString() }`) verilmesi.
- **Kendi kontrolüm:** `./gradlew :app:compileDebugKotlin` ile derlendi,
  BUILD SUCCESSFUL. İlk denemede KSP hatası çıktı: `@AssistedInject`
  constructor'da `rentalId`/`description` ikisi de `String` tipinde olduğu
  için "duplicate @Assisted type" hatası verdi; `@Assisted("rentalId")`/
  `@Assisted("description")` identifier'larıyla (hem constructor hem
  `Factory.create` imzasında) düzeltildi, ikinci derlemede BUILD
  SUCCESSFUL. Bileşen hiçbir yerden çağrılmadığından (bağlama sonraki
  batch) gerçek WebView/İyzico akışı runtime'da DOĞRULANAMADI.

### 2026-07-19 — İyzico ödemesi Ödeme ekranına bağlandı (2 batch, 6 dosya)

- **Ne yapıldı:** `RentalPaymentViewModel.handlePayClicked`'daki İyzico
  dalı artık "yakında eklenecek" Toast'ı göstermiyor; gerçek Checkout Form
  WebView akışını tetikliyor. Batch 1 (veri katmanı düzeltmesi):
  `PayRentalDto`'ya eksik `iyzicoPaymentId: String? = null` alanı eklendi
  (openapi şemasında zaten vardı, Kotlin DTO'da eksikti — bu oturumun
  başında doğrulanmıştı) ve `RentalsRepository.payRental`'a aynı isimli
  parametre eklendi. Batch 2 (MVI bağlama):
  `RentalPaymentContract.State`'e `showIyzicoDialog: Boolean` eklendi;
  `Intent`'e `IyzicoPaymentSucceeded(paymentId)`/`IyzicoPaymentFailed(reason)`/
  `IyzicoPaymentCancelled` eklendi; `RentalPaymentViewModel` bu üç intent'i
  karşılayan `handle*` fonksiyonlarıyla genişletildi;
  `RentalPaymentRoute.kt`, `state.showIyzicoDialog` true iken
  `IyzicoPaymentWebViewRoute`'u koşullu render edip üç callback'ini yeni
  Intent'lere çeviriyor; `ui/components/IyzicoPaymentWebViewRoute.kt`'ye
  önceki batch'te not edilen "aynı ViewModel örneği geri gelebilir"
  sınırlamasını çözen `hiltViewModel(key = remember { UUID.randomUUID()
  .toString() }, ...)` eklendi.
- **Değişen dosyalar:** `data/network/dto/RentalDtos.kt`,
  `data/repository/RentalsRepository.kt`,
  `feature/rental/payment/RentalPaymentContract.kt`,
  `feature/rental/payment/RentalPaymentViewModel.kt`,
  `feature/rental/payment/RentalPaymentRoute.kt`,
  `ui/components/IyzicoPaymentWebViewRoute.kt`.
- **Neden bu şekilde yapıldı:** Toplam 6 dosya Agent.md §2.1'in 5 dosya
  sınırını aştığından iş, 2026-07-19'daki "Kiralama Ödemesi ekranı"
  emsaliyle tutarlı olarak 2 bağlı batch'e bölünüp TEK onayla sunuldu.
  `handlePayClicked`'ın İyzico dalı artık gerçek ödeme çağrısı yapmıyor —
  yalnızca `showIyzicoDialog = true` set edip WebView sonucunu bekliyor;
  gerçek `rentalsRepository.payRental(..., method = "IYZICO",
  iyzicoPaymentId = paymentId)` çağrısı yalnızca WebView
  `ShowPaymentSucceeded` effect'i döndükten SONRA yapılıyor (`cardId`
  gönderilmiyor — yalnız CARD yönteminde zorunlu). Başarı sonrası akış
  WALLET/CARD ile BİREBİR aynı (`NavigateToHistory` effect'i, Kiralamalarım'a
  düşme). `IyzicoPaymentFailed` mevcut kalıcı `State.errorMessage`
  deseniyle (Wallet/HistoryDetail emsali) gösteriliyor — dead-end yok,
  kullanıcı yöntem değiştirip tekrar deneyebiliyor.
  `IyzicoPaymentCancelled` KASITLI olarak `errorMessage`'a dokunmuyor,
  yalnızca dialog'u kapatıyor — iptal ile hata kullanıcının açık isteğiyle
  ayrı tutuldu. `Effect.ShowInfo` (yalnızca "İyzico yakında" mesajı için
  vardı) bu batch'te göndereni kalmadığından ölü kod olarak TAMAMEN
  kaldırıldı; `RentalPaymentRoute.kt`'deki karşılık gelen `Toast` dalı ve
  artık kullanılmayan `Toast`/`LocalContext` importları da temizlendi.
  `IyzicoPaymentWebViewRoute`'a eklenen `key` parametresinin varlığı,
  kaynak koda inilerek (`hilt-lifecycle-viewmodel-compose-1.4.0-sources.jar`
  içindeki `HiltViewModel.kt`) doğrulandı — uydurulmadı; kullanılan
  overload'da `key: String? = null` zaten mevcut. `state.showIyzicoDialog`
  `false → true` geçtiğinde `RentalPaymentRoute`'taki `if` bloğu
  `IyzicoPaymentWebViewRoute`'u SIFIRDAN oluşturduğundan, `remember { UUID
  .randomUUID().toString() }` her açılışta yeni bir key üretiyor ve
  `hiltViewModel` ViewModelStore'da her seferinde YENİ bir örnek yaratıyor
  (`init`/`initializeCheckoutForm` gerçekten tekrar çalışıyor) — eski
  key'ler altındaki örnekler ekran kapanana kadar bellekte kalıyor
  (bilinen, kabul edilebilir bir trade-off).
- **Kendi kontrolüm:** Her iki batch'ten sonra ayrı ayrı
  `./gradlew :app:compileDebugKotlin` çalıştırıldı, ikisinde de BUILD
  SUCCESSFUL (yalnızca `RentalsRepository.kt`'de projede zaten var olan,
  bu değişiklikle ilgisiz bir `@ApplicationContext` derleyici uyarısı).
  Bağlı emülatör/cihaz olmadığından gerçek İyzico akışı (Öde → İyzico seç
  → WebView açılır → test kartıyla öde → callback yakalanır →
  Kiralamalarım'a düşme; iptal senaryosu; hata senaryosu) runtime'da
  DOĞRULANAMADI — sonraki oturumda cihazda elle test edilmeli.

### 2026-07-19 — İyzico WebView callback yakalanamıyor: teşhis logu eklendi (kök neden BULUNAMADI)

- **Ne yapıldı:** Kullanıcı bildirdi: gerçek cihazda ödeme İyzico'nun
  sayfasında "Ödeme Tamamlandı" gösteriyor ama `shouldOverrideUrlLoading`
  hiç tetiklenmiyor, WebView kapanmıyor, kullanıcı manuel kapatınca
  (aslında başarılı olan ödeme) "iptal edildi" gibi davranıyor. Kod
  DEĞİŞTİRİLMEDEN önce iki şey araştırıldı: (1) `docs/api/openapi.json`
  `InitializeCheckoutFormDto` şeması (satır 5097-5141) yeniden okundu —
  `callbackUrl` diye bir property YOK (yalnız `price`/`description`/
  `basketId`/`enabledInstallments`/`buyer`); (2)
  `IyzicoRepository.initializeCheckoutForm` doğrulandı — zaten şemada
  olmayan bir alanı dolduramayacağından yalnızca
  `price`/`description`/`basketId` gönderiyor. Sonuç: istemci callback
  URL'ini seçemiyor, backend kendi sabit `callbackUrl`'ini kullanıyor
  olmalı. Bunun ötesinde openapi'deki "İyzico sunucudaki `POST /iyzico/
  checkout-form/callback` adresine token gönderir" cümlesi iki farklı
  şekilde okunabildiğinden (sunucu-sunucu webhook mu, yoksa tarayıcı
  yönlendirmesi mi — ikisi de WebView davranışını farklı şekillerde
  açıklar) KESİN kök neden bu oturumda belirlenemedi; `ui/components/
  IyzicoPaymentWebViewScreen.kt`'deki `WebViewClient`'a `onPageStarted`/
  `onPageFinished`/`shouldOverrideUrlLoading`'in HER ÜÇÜNE de geçici
  `Log.d("IyzicoWebView", ...)` eklendi (RencarMap.kt'nin 2026-07-15
  tarihli "TEŞHİS" bloğu emsaliyle aynı desende, `// TEŞHİS:` yorumuyla
  işaretlendi).
- **Değişen dosyalar:** `ui/components/IyzicoPaymentWebViewScreen.kt`
  (yalnızca geçici log — WebView akış mantığı değişmedi).
- **Neden bu şekilde yapıldı:** Agent.md §2.2 (uydurma yasağı) gereği kök
  neden hakkında kanıtsız bir varsayımla kod DÜZELTİLMEDİ. `onPageStarted`/
  `onPageFinished` yalnızca TAM SAYFA navigasyonunda tetiklenir (AJAX/
  `fetch` çağrılarında tetiklenmez) — bu ayrım tam olarak aranan kanıt:
  ödeme tamamlandıktan sonra Logcat'te YENİ bir `onPageStarted`/
  `onPageFinished` hiç görünmüyorsa, İyzico'nun sayfası saf JS ile
  "başarılı" gösterip hiç yönlendirme yapmıyor demektir (gerçek sonuç
  yalnızca backend'e sunucu-sunucu webhook ile gidiyor olmalı) — bu
  durumda mevcut `shouldOverrideUrlLoading` tabanlı yaklaşım YAPISAL
  OLARAK terk edilip başka bir tetikleyiciye (örn. kullanıcı onayı veya
  tek seferlik gecikmeli `getCheckoutFormResult` sorgusu) geçilmesi
  gerekecek. Eğer yeni bir navigasyon GÖRÜNÜYOR ama `CHECKOUT_FORM_
  CALLBACK_PATH` ile eşleşmiyorsa, gerçek URL Logcat'ten okunup path
  sabiti düzeltilecek.
- **Kendi kontrolüm:** `./gradlew :app:compileDebugKotlin` ile derlendi,
  BUILD SUCCESSFUL. Bu bir teşhis batch'i olduğundan runtime doğrulaması
  YAPILAMADI (bağlı cihaz yok) — kullanıcı gerçek cihazda bir ödeme
  deneyip `adb logcat -s IyzicoWebView` çıktısını paylaştığında kök neden
  netleştirilip kalıcı düzeltme planlanacak.
- **Hatırlatma:** `IyzicoPaymentWebViewScreen.kt`'deki üç `Log.d` satırı
  KALICI DEĞİL — kök neden bulunup kalıcı düzeltme yapıldığında
  kaldırılmalı.

### 2026-07-19 — İyzico WebView kök nedeni bulundu ve düzeltildi: callback yakalama onPageStarted'a taşındı

- **Ne yapıldı:** Kullanıcı gerçek cihazda test edip Logcat sonucunu
  paylaştı: `onPageStarted`/`onPageFinished` callback URL'ini güvenilir
  şekilde yakalıyor ama `shouldOverrideUrlLoading` HİÇ loglanmıyor — bilinen
  bir WebView davranışı: sunucu tarafı yönlendirmelerde (İyzico'nun
  backend'e 302 redirect'i) `shouldOverrideUrlLoading` tetiklenmeyebiliyor.
  Bir önceki oturumun iki hipotezinden biri ("URL değişimi hiç olmuyor,
  saf JS/webhook") ELENDİ — üçüncü, daha önce düşünülmemiş bir olasılık
  (native WebView engine seviyesinde 302'nin `shouldOverrideUrlLoading`'i
  atlaması) doğru çıktı. `ui/components/IyzicoPaymentWebViewScreen.kt`'de
  `CheckoutFormWebView`'daki `WebViewClient` yeniden düzenlendi: callback
  path kontrolü artık `onPageStarted` içinde yapılıyor (URL eşleşirse
  `onCallbackUrlReached` orada tetikleniyor); `shouldOverrideUrlLoading`
  hâlâ aynı kontrolü yapıyor (JS/link tabanlı bir navigasyon ihtimaline
  karşı belt-and-braces) ama artık HER ZAMAN `false` döndürüyor (WebView'ın
  kendi yükleme akışını bloklamıyor — zaten bu yöntemin sunucu
  yönlendirmelerinde tetiklenmediği kanıtlandı, blocking mantığı bu yöntemde
  anlamsız hale geldi). İki tetikleyici noktanın (`onPageStarted` VE
  `shouldOverrideUrlLoading`) `onCallbackUrlReached`'i BİRDEN FAZLA kez
  çağırmaması için ortak bir `callbackHandled` bayrağı (WebView factory
  closure'ında) + paylaşılan `maybeHandleCallback(url)` yardımcı fonksiyonu
  eklendi. Üç `Log.d` satırı (`onPageStarted`/`onPageFinished`/
  `shouldOverrideUrlLoading`) KALICI OLARAK bırakıldı — kullanıcı düzeltme
  sonrası tekrar Logcat ile doğrulama isteyeceğinden bilinçli olarak
  kaldırılmadı; TEŞHİS yorumu artık kök nedeni ve kalıcı çözümü açıklayan
  bir yorumla güncellendi (RencarMap.kt emsalinde olduğu gibi kök neden
  bulununca teşhis yorumları kalıcı açıklamaya dönüştürüldü, silinmedi).
- **Değişen dosyalar:** `ui/components/IyzicoPaymentWebViewScreen.kt`.
- **Neden bu şekilde yapıldı:** `shouldOverrideUrlLoading`'in artık her
  zaman `false` dönmesi BİLİNÇLİ bir karar: önceki davranışı (path
  eşleşirse `true`, WebView'ın yüklemeyi durdurması) korumak, bu yöntemin
  zaten güvenilmez olduğu (kök nedenin ta kendisi) kanıtlanmış bir yerde
  gereksiz bir dal bırakırdı; asıl "yükleme durdurma" işlevi zaten gerekli
  değil çünkü `onCallbackUrlReached` tetiklendiğinde `RentalPaymentRoute`
  `state.showIyzicoDialog`'u `false` yaparak Dialog'u komple kaldırıyor
  (WebView instance'ı kısa süre içinde tamamen dispose ediliyor). Ortak
  `callbackHandled` bayrağı olmadan, `onPageStarted` VE
  `shouldOverrideUrlLoading` aynı callback URL'i için (bazı WebView
  sürümlerinde ikisi de tetiklenebiliyor) İKİ AYRI `getCheckoutFormResult`
  çağrısına yol açabilirdi — ViewModel tarafındaki `hasResolved` guard'ı
  yalnızca EFFECT gönderimini tekilleştiriyordu, gereksiz ikinci ağ
  çağrısını engellemiyordu; bu yüzden tekilleştirme kaynağa (WebView
  katmanına) taşındı.
- **Kendi kontrolüm:** `./gradlew :app:compileDebugKotlin` ile derlendi,
  BUILD SUCCESSFUL. Bağlı cihaz olmadığından bu oturumda runtime testi
  YAPILAMADI — kullanıcı gerçek cihazda tekrar test edip Logcat'i
  (`adb logcat -s IyzicoWebView`) paylaşacak; `onPageStarted` çağrısında
  callback path'inin gerçekten yakalandığı VE WebView dialog'unun kapanıp
  `getCheckoutFormResult`'un tetiklendiği doğrulanmalı.

### 2026-07-19 — İyzico Sandbox ödemesi uçtan uca doğrulandı + teşhis logları temizlendi

- **Ne yapıldı:** Kullanıcı gerçek cihazda İyzico Sandbox ile uçtan uca bir
  ödeme denedi; Logcat kanıtı PAID durumunu ve doğru `iyzicoPaymentId`'yi
  gösterdi — `onPageStarted` tabanlı callback yakalama düzeltmesi (bir
  önceki girdi) çalıştığı doğrulandı. Ardından `ui/components/
  IyzicoPaymentWebViewScreen.kt`'deki üç geçici `Log.d("IyzicoWebView",
  ...)` satırı (URL loglama — `onPageStarted`/`onPageFinished`/
  `shouldOverrideUrlLoading` içinde) kaldırıldı; artık kullanılmayan
  `android.util.Log` importu temizlendi. `onPageFinished` override'ı
  yalnızca bu log satırı için vardı ve kaldırılınca `super.onPageFinished(...)`
  çağırmaktan başka hiçbir şey yapmayan ölü bir override'a dönüştüğünden
  TAMAMEN silindi (WebViewClient'ın varsayılan davranışı zaten yeterli).
  `onPageStarted`/`shouldOverrideUrlLoading` içindeki callback yakalama
  mantığı (`maybeHandleCallback` çağrıları) DOKUNULMADAN korundu — yalnızca
  log satırları kaldırıldı. Fabrika closure'ındaki açıklama yorumu, artık
  var olmayan "Logcat'te doğrulandı" ifadesinden arındırılıp yalnızca
  mimari kararı (neden `onPageStarted`, neden `callbackHandled` bayrağı)
  anlatacak şekilde sadeleştirildi.
- **Değişen dosyalar:** `ui/components/IyzicoPaymentWebViewScreen.kt`.
- **Neden bu şekilde yapıldı:** Kullanıcının gerekçesi: token içeren
  URL'leri (İyzico callback URL'i token query/path parametresi taşıyabilir)
  production Logcat'ine düz metin yazmak hafif bir gizlilik riski —
  `AuthInterceptor`'ın 2026-07-14 tarihli release/debug log seviyesi
  ayrımıyla (parola/token'ların release Logcat'ine yazılmaması) aynı
  prensip. Log satırları zaten yalnızca kök nedeni teşhis etmek için
  geçiciydi (önceki iki girdide böyle işaretlenmişti) ve kök neden
  bulunup düzeltme doğrulandığından artık işlevsizdi.
- **Kendi kontrolüm:** `./gradlew :app:compileDebugKotlin` ile derlendi,
  BUILD SUCCESSFUL, yeni uyarı yok. Gerçek cihaz testi kullanıcı
  tarafından BİR ÖNCEKİ adımda (log'lu haliyle) zaten yapılmıştı; bu
  batch yalnızca log satırlarını kaldırdığından ve callback yakalama
  mantığına dokunmadığından ayrı bir runtime testi gerektirmiyor — yine
  de sonraki bir ödeme denemesinde davranışın aynı kaldığı gözle
  doğrulanabilir.
- **İyzico entegrasyonu artık uçtan uca ÇALIŞIYOR:** Checkout Form
  initialize → WebView → callback yakalama → `getCheckoutFormResult` →
  `RentalsRepository.payRental(..., iyzicoPaymentId)` → Kiralamalarım
  akışının tamamı Sandbox'ta doğrulandı.

### 2026-07-19 — PostAuthNavigationResolver: PENDING + UNDER_REVIEW artık Confirmation yerine Home'a düşüyor

- **Ne yapıldı:** `PostAuthNavigationResolver.resolve()` içinde ehliyet
  durumu `UNDER_REVIEW` olan `PENDING` rollü kullanıcılar için dönüş değeri
  `PostAuthDestination.LicensePending` yerine `PostAuthDestination.Home`
  olarak değiştirildi (tek satır). `CUSTOMER -> Home` ve
  `NOT_SUBMITTED`/`REJECTED -> LicenseUpload` dalları değişmedi.
  `SplashViewModel`, `OtpViewModel`, `RegisterViewModel` dosyalarının
  hiçbiri değiştirilmedi — üçü de zaten `PostAuthDestination.Home ->
  Effect.NavigateToHome` dalına sahip olduğundan yeni davranışı otomatik
  miras aldılar; `LoginViewModel`'in ise resolver'ı hiç çağırmadığı
  (post-auth kararını OTP doğrulaması sonrası `OtpViewModel` veriyor)
  doğrulandı, bu dosyada değişecek bir şey yoktu.
- **Değişen dosyalar:** `domain/PostAuthNavigationResolver.kt`.
- **Neden bu şekilde yapıldı:** `PostAuthDestination.LicensePending` enum
  değeri kullanıcı isteğiyle SİLİNMEDİ (ileride Profil'deki "Kontrol Et"
  butonu gibi başka bir tetikleyiciden yeniden kullanılabilir); yalnızca
  resolver artık bu değeri döndürmüyor. Bu yüzden 3 ViewModel'deki
  `LicensePending -> NavigateToConfirmation` dalları kod olarak yerinde
  kaldı ama artık resolver tarafından hiç tetiklenmiyor (ulaşılmaz, silinmiş
  değil). `ConfirmationScreen`/`ConfirmationRoute`/`RenCarNavHost`'taki
  `CONFIRMATION` composable'ı ve Selfie akışının kendi
  `onNavigateToConfirmation` çağrısı (ehliyet fotoğrafı gönderildikten
  hemen sonraki ayrı, resolver'dan bağımsız akış) kullanıcı talimatıyla
  bilinçli olarak dokunulmadan bırakıldı.
- **Kendi kontrolüm:** `./gradlew :app:compileDebugKotlin` ile derlendi,
  BUILD SUCCESSFUL. Bağlı cihaz/emülatör olmadığından gerçek bir
  `UNDER_REVIEW` hesabıyla Splash/OTP/Register akışlarının artık Home'a
  düştüğü runtime'da DOĞRULANAMADI — sonraki oturumda `PENDING` +
  `UNDER_REVIEW` durumundaki bir test hesabıyla (uygulamayı kapat-aç,
  login+OTP, register sonrası) elle test edilmeli; ayrıca `NOT_SUBMITTED`/
  `REJECTED` ve `CUSTOMER` hesaplarıyla regresyon kontrolü yapılmalı.

### 2026-07-19 — İlk kayıt akışında da Confirmation'a düşme sorunu giderildi: SelfieViewModel artık Home'a yönlendiriyor

- **Ne yapıldı:** Bir önceki girdi yalnızca YENİDEN GİRİŞ senaryosunu
  (`PostAuthNavigationResolver`) düzeltmişti; ehliyet+selfie İLK KEZ
  gönderildiğinde `SelfieViewModel`'in kendi `onNavigateToConfirmation`
  çağrısı hâlâ APPROVED olmadan "Devam Et"i disabled tutan Confirmation
  ekranına gidiyordu. `SelfieContract.Effect.NavigateToConfirmation`
  `NavigateToHome` ile değiştirildi; yeni `Effect.ShowInfo(message: String)`
  eklendi. `SelfieViewModel.handleUploadStateChanged`'de `isUploaded == true`
  dalı artık önce `ShowInfo("Ehliyetin gönderildi, inceleniyor.")` sonra
  `NavigateToHome` gönderiyor. `SelfieRoute.kt`'nin `onNavigateToConfirmation`
  parametresi `onNavigateToHome` olarak yeniden adlandırıldı, `ShowInfo`
  Toast ile gösteriliyor. `RenCarNavHost.kt`'deki `SELFIE_VERIFICATION`
  composable'ı artık `SelfieRoute`'a `navController.navigate(HOME) {
  popUpTo(ONBOARDING) { inclusive = true } }` geçiyor.
- **Değişen dosyalar:** `feature/auth/selfie/SelfieContract.kt`,
  `feature/auth/selfie/SelfieViewModel.kt`, `feature/auth/selfie/SelfieRoute.kt`,
  `navigation/RenCarNavHost.kt`.
- **Neden bu şekilde yapıldı:** Kullanıcı ilk talimatta "Tek dosya
  (SelfieViewModel.kt) değişecek" varsaymıştı; kod okunduğunda
  `SelfieRoute.kt`'nin imzasının (`onNavigateToConfirmation`) ve
  `RenCarNavHost.kt`'deki kablolamanın da değişmesi gerektiği görüldü —
  bu, kullanıcıya raporlanıp onay alındıktan sonra 4 dosyalık bir batch
  olarak uygulandı (Agent.md §2.1 sınırı içinde). `popUpTo(ONBOARDING)`
  deseni uydurulmadı — `ConfirmationRoute`'un kendi `onNavigateToHome`'unda
  zaten kullandığı (`RenCarNavHost.kt`) birebir aynı desen tekrar
  kullanıldı. Eski `Effect.NavigateToConfirmation` hiçbir yerden
  üretilmez/tüketilmez hale geldiğinden (resolver görevindeki
  `LicensePending`'in aksine, burada "gelecekte lazım olabilir" notu
  yoktu), Iyzico akışındaki ölü `ShowInfo` effect'inin kaldırılması
  emsaline uyularak TAMAMEN silindi, ulaşılmaz kod olarak bırakılmadı.
  Bilgi Toast'ı `LENGTH_LONG` ile gösteriliyor (mevcut `ShowError`
  `LENGTH_SHORT` kullanıyor ama bu mesaj artık gösterilmeyen bir ekranın
  yerini tuttuğundan okunması için daha uzun süre tercih edildi).
  `ConfirmationScreen`/`ConfirmationRoute`/`ConfirmationViewModel`/
  `ConfirmationContract` talimat gereği DOKUNULMADAN bırakıldı.
- **Kendi kontrolüm:** `./gradlew :app:compileDebugKotlin` ile derlendi,
  BUILD SUCCESSFUL. Bağlı cihaz/emülatör olmadığından ehliyet+selfie ilk
  kez gönderildiğinde Toast'ın göründüğü ve Home'a düştüğü runtime'da
  DOĞRULANAMADI — sonraki oturumda yeni bir hesapla uçtan uca (register →
  ehliyet fotoğrafı → selfie → yükleme tamamlanınca Toast + Home) elle
  test edilmeli.

### 2026-07-19 — Selfie'de Toast yerine AlertDialog + Profil'e "Kontrol Et" butonu (3 batch, 10 dosya)

- **Ne yapıldı:** Bir önceki girdideki `ShowInfo` Toast'ı, kullanıcı
  "Tamam"a basana kadar açık kalması gereken bir `AlertDialog`'a çevrildi
  (Batch 1). `SelfieContract.Effect.ShowInfo` kaldırıldı; `State`'e
  `showLicenseSubmittedDialog: Boolean` eklendi; `handleUploadStateChanged`
  artık `isUploaded` olduğunda bu alanı `true` yapıyor (Effect değil, State
  — mvi-contracts.md'nin "State'e yansıtılabilecek hiçbir şey Effect
  olmamalıdır" kuralı gereği). Yeni `Intent.LicenseSubmittedDialogConfirmed`
  dialog'u kapatıp ardından `NavigateToHome` effect'i gönderiyor (sıra:
  dialog kapat → Home'a git). `SelfieScreen.kt`'ye mesajı gösteren
  `AlertDialog` eklendi.
  Ardından Profil ekranına, yalnızca `role == "PENDING"` iken görünen bir
  "Kontrol Et" butonu eklendi (Batch 2): `ProfileContract.State`'e `role`,
  `isCheckingLicense`, `licenseCheckDialog` (yeni top-level
  `sealed interface LicenseCheckDialog { Approved; data class Rejected(reason) }`)
  eklendi. `ProfileViewModel.handleCheckLicenseClicked()`
  `licenseRepository.getStatus()` çağırıyor: `UNDER_REVIEW` →
  `ShowToast("Hâlâ inceleniyor, biraz daha bekle.")`; `APPROVED` →
  `authRepository.refresh()` (yeni CUSTOMER rollü token) başarılıysa
  `loadProfile()` (rozet/buton günceller) + `Approved` dialog'u; `REJECTED`
  → `rejectReason`'ı taşıyan `Rejected` dialog'u ("Ehliyeti Yeniden Yükle"
  seçeneğiyle); ağ hatası → `ShowToast(result.message)` (dead-end yok, buton
  tekrar aktif). Son olarak (Batch 3) `MainScaffold.kt` ve
  `RenCarNavHost.kt`'deki `HomeGraph`, Profil'in "Ehliyeti Yeniden Yükle"
  seçiminin kök `navController` ile `LICENSE_VERIFICATION`'a gidebilmesi
  için `onNavigateToLicenseVerification` callback'iyle kablolandı.
- **Değişen dosyalar:**
  Batch 1 — `feature/auth/selfie/SelfieContract.kt`,
  `feature/auth/selfie/SelfieViewModel.kt`,
  `feature/auth/selfie/SelfieScreen.kt`, `feature/auth/selfie/SelfieRoute.kt`.
  Batch 2 — `feature/profile/ProfileContract.kt`,
  `feature/profile/ProfileViewModel.kt`, `feature/profile/ProfileRoute.kt`,
  `feature/profile/ProfileScreen.kt`.
  Batch 3 — `navigation/MainScaffold.kt`, `navigation/RenCarNavHost.kt`.
- **Neden bu şekilde yapıldı:** Kullanıcı "dosya sayısını netleştir" demişti;
  kod okunduğunda iki isteğin toplam 10 dosyayı etkilediği görüldü (Agent.md
  §2.1'in 5 dosya sınırını aşıyor) — 2026-07-19 tarihli Iyzico girdisindeki
  "birden fazla batch, TEK onay" emsaline uyularak 3 batch'e bölünüp tek
  seferde onay alındı, her batch sonrası ayrı ayrı derlendi. `ProfileRoute`
  ve `MainScaffold`'daki yeni parametreler diğer tüm callback'ler gibi
  `= {}` varsayılanıyla eklendi — bu sayede Batch 2, Batch 3 uygulanmadan
  önce de tek başına derlenebiliyordu (ara adımda kırık bir derleme riski
  yok). `UNDER_REVIEW` ve ağ hatası durumları için yeni bir dialog türü
  icat edilmedi, var olan `Effect.ShowToast` yeniden kullanıldı (kullanıcı
  bu ikisi için "dialog/mesaj" ve "anlamlı mesaj" demişti, esnek bırakmıştı);
  `APPROVED`/`REJECTED` ise özel içerik/aksiyon gerektirdiğinden State
  tabanlı `AlertDialog` olarak `ProfileScreen.kt`'de (Route değil, Screen —
  mvi-overview.md'nin "Screen tamamen State'ten render olur" kuralına uygun)
  render edildi. "Onaylandın! 🎉" mesajındaki emoji, kullanıcının birebir
  verdiği uygulama-içi metin olduğu için AYNEN kullanıldı — Agent.md §4'teki
  emoji yasağının kendi raporlarım/planlarım için geçerli olduğu, kullanıcının
  dikte ettiği UI metni için geçerli olmadığı yorumu kullanıcıya önceden
  bildirilip itiraz gelmedi. `handleLicenseApproved()` içinde `loadProfile()`
  çağrısı (kendi coroutine'ini fire-and-forget başlatan, suspend olmayan bir
  fonksiyon) ile hemen ardından gelen `_state.update { isCheckingLicense =
  false, licenseCheckDialog = Approved }` arasında race koşulu YOK —
  `loadProfile()` yalnızca `isLoading`/`userName`/`phoneNumber`/
  `referralCode`/`role`/`license` alanlarına dokunuyor, `isCheckingLicense`/
  `licenseCheckDialog`'a hiç dokunmuyor.
- **Kendi kontrolüm:** Üç batch'in her biri ayrı ayrı
  `./gradlew :app:compileDebugKotlin` ile derlendi, üçünde de BUILD
  SUCCESSFUL. Bağlı cihaz/emülatör olmadığından runtime'da DOĞRULANAMADI —
  sonraki oturumda: (1) `PENDING`+ilk kez ehliyet gönderen bir hesapla
  Selfie dialog'unun "Tamam"a kadar açık kaldığı ve sonra Home'a düştüğü;
  (2) Profil'de `PENDING` hesapla "Kontrol Et" butonunun göründüğü; (3) bu
  hesabın ehliyeti admin tarafından `UNDER_REVIEW`/`APPROVED`/`REJECTED`
  yapılarak üç dalın da (mesaj / onay dialog'u + rozet güncellenmesi + buton
  kaybolması / ret dialog'u + "Ehliyeti Yeniden Yükle" ile License ekranına
  gidiş) doğru çalıştığı; (4) ağ bağlantısı kesikken "Kontrol Et"e basılınca
  anlamlı bir mesaj gösterip butonun tekrar aktif kaldığı elle test
  edilmeli.

### 2026-07-19 — CurrentUserSession: Maps/History/Wallet artık PENDING→CUSTOMER geçişini yakalıyor (5 dosya)

- **Ne yapıldı:** Profil'deki "Kontrol Et" ile CUSTOMER'a yükseltme
  gerçekten başarılı oluyordu ama `MainScaffold.kt`'nin iç `NavHost`'u
  `saveState/restoreState` kullandığından (`MainScaffold.kt:108-114`)
  Maps/History/Wallet sekmelerinin ViewModel'leri sekmeler arası geçişte
  YENİDEN KURULMUYOR — bu ekranlar PENDING rolüyle ilk açıldıklarında
  `docs/api/openapi.json`'da belgeli 403 (`GET /vehicles`, `GET /rentals`,
  `GET /wallet`, `GET /cards` hepsi "CUSTOMER olmayan kullanıcılar 403
  alır" diyor) ile boş/hatalı kalıyor, rol sonradan CUSTOMER'a yükselse
  bile bu ekranlara dönüldüğünde bayat kalmaya devam ediyordu (yalnızca
  uygulamayı kapat-aç düzeltiyordu). Yeni `data/local/CurrentUserSession.kt`
  (`TokenStore` emsaliyle `@Singleton @Inject constructor()`, tek alanı
  `StateFlow<String?> role`) eklendi. `AuthRepository`'nin `register()`,
  `verifyOtp()`, `refresh()` fonksiyonları başarılı her `AuthResponseDto`
  alışında `currentUserSession.updateRole(result.data.user.role)` çağırıyor
  (mevcut `tokenStore.saveTokens(...)` çağrısının yanına eklendi);
  `logout()`'un `finally` bloğu da `tokenStore.clear()`'ın yanına
  `currentUserSession.updateRole(null)` ekledi. `MapsViewModel`,
  `HistoryViewModel`, `WalletViewModel`'in üçü de `currentUserSession`
  inject edip `init` içinde `observeRoleChanges()` çağırıyor;
  `lastKnownRole == "PENDING" && role == "CUSTOMER"` geçişini
  yakaladıklarında sırasıyla `loadVehicles()`/`loadRentals()`/
  `loadWalletAndCards()`'ı sessizce yeniden tetikliyorlar.
- **Değişen dosyalar:** `data/local/CurrentUserSession.kt` (yeni),
  `data/repository/AuthRepository.kt`, `feature/maps/MapsViewModel.kt`,
  `feature/history/HistoryViewModel.kt`, `feature/wallet/WalletViewModel.kt`.
- **Neden bu şekilde yapıldı:** Kullanıcı ilk talimatta yalnızca Maps'i
  hedeflemiş, History/Wallet'ın "PENDING iken zaten erişilemez/boş
  kalmadığı" varsayımıyla kapsam dışı bırakılabileceğini düşünmüştü; kod +
  `docs/api/openapi.json` okunarak bu varsayım DOĞRULANAMADI (üçünün de
  backend'i CUSTOMER'a kilitli, `errorMessage` state'inde sessizce
  hata gösteriyorlar) — bu yüzden aynı düzeltme kullanıcı onayıyla üçüne
  birden uygulandı (toplam 5 dosya, Agent.md §2.1 sınırı içinde tek
  batch). `di/` altında ayrı bir modül YAZILMADI — `CurrentUserSession`,
  `TokenStore`'un zaten kullandığı `@Singleton @Inject constructor()`
  desenini izleyerek Hilt tarafından otomatik sağlanıyor. Geçiş algılama
  yalnızca birebir `"PENDING" -> "CUSTOMER"` dizisini kontrol ediyor
  (kullanıcının talimatındaki tanım); `getMe()` bilinçli olarak session'ı
  güncellemiyor (talimat yalnızca login/verifyOtp/register/refresh'i
  listelemişti, Profil zaten `refresh()` üzerinden bu akışa dahil).
- **Kendi kontrolüm:** `./gradlew :app:compileDebugKotlin` ile derlendi,
  BUILD SUCCESSFUL. Bağlı cihaz/emülatör olmadığından runtime'da
  DOĞRULANAMADI — sonraki oturumda: PENDING hesapla Maps/History/Wallet
  sekmelerinde boş/hata durumunun oluştuğu, ardından Profil'de "Kontrol
  Et" ile CUSTOMER'a yükseldikten sonra bu üç sekmeye (uygulamayı
  kapatmadan) geri dönüldüğünde verinin otomatik yüklendiği elle test
  edilmeli.

### 2026-07-19 — Ödeme sonrası AlertDialog: WALLET/CARD/IYZICO artık aynı ortak fonksiyondan geçiyor

- **Ne yapıldı:** WALLET/CARD ve IYZICO ödemeleri başarılı dönünce
  kullanıcıyı hiçbir onay olmadan doğrudan Kiralamalarım'a düşüren akış,
  "Tamam"a basılana kadar açık kalan bir "Ödeme başarılı" `AlertDialog`'una
  bağlandı. `RentalPaymentContract.State`'e `showPaymentSuccessDialog:
  Boolean` ve `paymentMethodLabel` (computed) eklendi; `Intent`'e
  `PaymentSuccessDialogConfirmed` eklendi. `RentalPaymentViewModel`'de yeni
  ortak `handlePaymentSuccess()` fonksiyonu hem `handlePayClicked()`'ın hem
  `handleIyzicoPaymentSucceeded()`'ın başarı dalından çağrılıyor (kod tekrarı
  kalkmış oldu); `handlePaymentSuccessDialogConfirmed()` dialog'u kapatıp
  ardından mevcut `Effect.NavigateToHistory`'yi gönderiyor.
- **Değişen dosyalar:** `feature/rental/payment/RentalPaymentContract.kt`,
  `feature/rental/payment/RentalPaymentViewModel.kt`,
  `feature/rental/payment/RentalPaymentScreen.kt`.
- **Neden bu şekilde yapıldı:** Kullanıcı görevde "State/Effect:
  ShowPaymentSuccessDialog" demişti, ancak `docs/architecture/mvi-contracts.md`
  §4 "State'e yansıtılabilecek hiçbir şey Effect olmamalıdır" kuralını
  bağlayıcı kılıyor; "Tamam"a kadar açık kalması gereken bir dialog State
  alanıdır — 2026-07-19 tarihli Selfie ekranı emsaliyle (`showLicenseSubmittedDialog`
  + `Intent.LicenseSubmittedDialogConfirmed`) birebir tutarlı olacak şekilde
  yalnızca State + Intent ile modellendi, ayrı bir Effect eklenmedi. Dialog'ta
  gösterilecek tutar/yöntem için State'e yeni alan açılmadı — zaten mevcut
  `totalPrice`/`selectedMethod` yeniden kullanıldı (dialog açıkken değişmezler).
  `RentalPaymentRoute.kt`'ye dokunulmadı: `Effect.NavigateToHistory` işleyişi
  zaten vardı, sadece tetiklenme anı (dialog kapandıktan sonra) değişti.
- **Kendi kontrolüm:** `./gradlew :app:compileDebugKotlin` ile derlendi,
  BUILD SUCCESSFUL. Bağlı cihaz/emülatör olmadığından runtime'da
  DOĞRULANAMADI — sonraki oturumda: (1) WALLET ile öde → dialog → Tamam →
  Kiralamalarım; (2) kayıtlı kartla CARD ile öde → aynı dialog, "Kart"
  etiketiyle; (3) İyzico WebView'de ödeme tamamlanınca gerçek `payRental()`
  başarılı dönüp aynı dialog "İyzico" etiketiyle açıldığı; (4) ödeme hatası
  dönerse (üç yöntemde de) dialog'un HİÇ açılmayıp mevcut `errorMessage`
  davranışının bozulmadığı elle test edilmeli.

### 2026-07-20 — Repo köküne README.md eklendi

- **Ne yapıldı:** Repo kökünde eksik olan README.md oluşturuldu: proje
  tanıtımı, özellik listesi, `docs/screenshots/` altındaki 23 gerçek görselin
  3 sütunlu tablo grid'i (GIF ayrı "Canlı Konum Takibi" başlığında),
  `gradle/libs.versions.toml`'dan okunan gerçek sürüm numaralarıyla teknoloji
  yığını tablosu, MVI mimarisi + paket yapısı + `docs/decisions.md`'deki
  kararların (String enum, nav-graph-scoped ViewModel istisnası,
  `PostAuthNavigationResolver`) özeti, kurulum adımları ve İyzico sandbox
  test verileri.
- **Değişen dosyalar:** `README.md` (yeni).
- **Neden bu şekilde yapıldı:** Kullanıcı ekran görüntüsü dosya adlarının
  UYDURULMAMASINI istemişti; bu yüzden `docs/screenshots/` gerçekten
  listelendi (23 dosya) ve dört tanesi (`koyumod.jpeg`, `davetet.jpeg`,
  `aracdurumu.jpeg`, `izyco.jpg`) anlamı dosya adından net çıkmadığı için
  görsel olarak açılıp doğrulandı (sırasıyla: Ayarlar > Görünüm/koyu mod,
  "Davet Et" referans ekranı, "Araç durumu" 4 yönlü teslim-öncesi fotoğraf
  ekranı, İyzico sandbox kart formu) — kalan başlıklar dosya adından
  doğrudan okunabildiği için ayrıca açılmadı. Sürüm numaraları
  `gradle/libs.versions.toml`'dan birebir kopyalandı, tahmin edilmedi.
  Backend adresi `di/NetworkModule.kt` ve `VehicleLocationSocketClient.kt`
  içinde grep ile doğrulandı.
- **Kendi kontrolüm:** Dosya adları docs/screenshots/ çıktısıyla, sürüm
  numaraları libs.versions.toml içeriğiyle satır satır karşılaştırıldı;
  markdown tablo/grid yapısı görsel olarak gözden geçirildi (kod
  derlemesi/çalıştırma gerektiren bir değişiklik değil, yalnızca
  dokümantasyon).

### 2026-07-20 — docs/screenshots/ dosya adlarındaki Türkçe karakterler ASCII'ye çevrildi

- **Ne yapıldı:** `docs/screenshots/` altında Türkçe karakter (ğ, ı) içeren
  4 dosya ASCII karakterli adlara yeniden adlandırıldı:
  `ehliyetdoğrulama.jpeg` → `ehliyetdogrulama.jpeg`,
  `yardımdestek.jpeg` → `yardimdestek.jpeg`,
  `yolculukdetayı.jpeg` → `yolculukdetayi.jpeg`,
  `yolculuktamamlandı.jpeg` → `yolculuktamamlandi.jpeg`. README.md'deki
  ilgili 4 `<img src="...">` yolu yeni dosya adlarıyla güncellendi.
- **Değişen dosyalar:** `docs/screenshots/ehliyetdogrulama.jpeg` (yeniden adlandırıldı),
  `docs/screenshots/yardimdestek.jpeg` (yeniden adlandırıldı),
  `docs/screenshots/yolculukdetayi.jpeg` (yeniden adlandırıldı),
  `docs/screenshots/yolculuktamamlandi.jpeg` (yeniden adlandırıldı),
  `README.md`.
- **Neden bu şekilde yapıldı:** Kullanıcı Türkçe karakterli dosya adlarının
  bazı ortamlarda (URL encoding, farklı işletim sistemleri/araçlar) sorun
  çıkarabileceğini düşünerek ASCII'ye çevrilmesini istedi. `docs/screenshots/`
  klasörü henüz git tarafından takip edilmediğinden (`git status` çıktısında
  `?? docs/screenshots/`) `git mv` kullanılamadı, düz `mv` ile yeniden
  adlandırıldı. İlk taramada yalnızca 3 dosya tespit edilmişti;
  `yolculuktamamlandı.jpeg`'in sonundaki `ı` karakteri gözden kaçmıştı, ikinci
  bir kontrolde fark edilip aynı işleme dahil edildi.
- **Kendi kontrolüm:** Yeniden adlandırma sonrası `docs/screenshots/`
  klasörü listelenerek tüm dosya adlarının ASCII olduğu doğrulandı;
  README.md'deki 4 satır, yeni dosya adlarıyla birebir eşleşecek şekilde
  gözden geçirildi.

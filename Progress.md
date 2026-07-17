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
  `SymbolLayer`'ı ve ilgili `Expression` import'u kaldırıldı),
  `feature/maps/MapsStyle.kt` (net değişiklik yok — denenen `glyphs`
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
  için çökerdi. Bu, uygulamaya geçmeden önce kullanıcıya açıkça sunuldu ve
  3. dosya olarak onaylandı. `Onboarding`'in kullanılmayan `NavigateToHome`
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
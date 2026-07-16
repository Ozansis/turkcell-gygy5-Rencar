## decisions.md

Projede verilen bütün mimarisel-teknik kararları ve karar geçmişini içeren dökümantasyondur.

---

### Dependency Injection Kütüphanesi

- Seçim: **Hilt**

- Son Güncelleme Tarihi:

- Alternatifler: **Koin**

- Sebep: Opsiyonel

### Navigasyon

- Seçim: **Compose Navigation**

- Son Güncelleme Tarihi:

### Ekran Mimarisi

- Seçim: **MVI (Model-View-Intent)**

- Son Güncelleme Tarihi:

- Alternatifler: MVVM (ViewModel + StateFlow direkt bağlantı)

- Sebep: Tek yönlü veri akışı zorunluluğu, State/Intent/Effect üçlüsüyle her ekranın bağımsız ve
  test edilebilir olması, büyük ekip çalışmasında ekranlar arası tutarlılık.

- Referans: `docs/architecture/mvi-overview.md`

### Annotation Processing

- Seçim: **KSP (Kotlin Symbol Processing)**

- Son Güncelleme Tarihi: 

- Alternatifler: KAPT

- Sebep: Kotlin 2.x ile KAPT deprecated durumdadır. KSP derleme süresini kısaltır ve Hilt 2.48+
  sürümünden itibaren tam destek sunmaktadır.

### Repository Stub Stratejisi

- Seçim: **FakeRepository**

- Son Güncelleme Tarihi: 

- Sebep: Backend API hazır olana kadar UI geliştirmesinin bloke olmaması için. ViewModel yalnızca
  interface'e bağımlı olduğundan, gerçek implementasyon hazır olduğunda yalnızca
  `di/<Feature>Module.kt` güncellenir; ViewModel ve Screen'e dokunulmaz.

- Mevcut Durum: Hilt entegrasyonu tamamlanana kadar ViewModel doğrudan `<Feature>MockSource`
  nesnesini kullanır. Hilt entegre edildiğinde ViewModel interface'e bağımlı hale getirilir ve
  sahte/gerçek implementasyon seçimi `di/<Feature>Module.kt` dosyasına taşınır.

---

### Parametre Alan ViewModel Factory Stratejisi

- Seçim: **ViewModelProvider.Factory** (geçici) → **@AssistedInject** (Hilt entegrasyonu sonrası)

- Son Güncelleme Tarihi: 2026-07-02

- Alternatifler: SavedStateHandle üzerinden parametre aktarımı

- Mevcut Durum: `OtpRoute.kt` içinde manuel `ViewModelProvider.Factory` implementasyonu
  kullanılmaktadır. Route dosyasına `private class OtpViewModelFactory` eklenerek
  `remember(phoneNumber) { OtpViewModelFactory(phoneNumber) }` ile factory sağlanır.

- Planlanan Değişiklik: Hilt entegrasyonu tamamlandığında `@AssistedInject` ve
  `@AssistedFactory` anotasyonları kullanılacaktır. Bu durumda manuel factory sınıfı
  kaldırılır; Route dosyasında yalnızca `hiltViewModel(creationCallback = ...)` çağrısı kalır.

- Sebep: `@AssistedInject` ile factory boilerplate ortadan kalkar ve ViewModel, Hilt
  dependency graph'ına tam olarak entegre olur.

- Referans: `app/src/main/java/com/turkcell/rencar_pair/feature/auth/otp/OtpRoute.kt`



### Karar: Nav-Graph-Scoped Paylaşılan ViewModel'ler MVI Contract Kullanmaz
LicenseFlowViewModel gibi birden fazla ekran arasında ham state paylaşan
ViewModel'ler (klasik ekran ViewModel'i değil, "veri havuzu" rolünde),
Intent/Effect sözleşmesi olmadan doğrudan fonksiyon çağrılarıyla kontrol
edilir. Sebep: bu ViewModel'ler doğrudan bir Screen'e bağlı değil, birden
fazla ekranın ortak kullandığı geçici state taşıyıcısı — MVI'nin "tek
ekran, tek Contract" varsayımı burada uygulanmaz.

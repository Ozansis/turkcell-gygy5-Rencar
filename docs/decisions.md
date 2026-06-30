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

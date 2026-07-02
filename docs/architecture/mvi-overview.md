# MVI Genel Yapı ve Katman Düzeni

Bu belge, RenCar projesinde uygulanan MVI (Model-View-Intent) mimarisinin genel yapısını,
katman düzenini ve Route/Screen ayrımını tanımlar. Mevcut implementasyondan türetilmiştir;
yorum yapılamaz, atlanamaz.

---

## 1) Katman Düzeni

Her ekran dört dosyadan oluşur. Bağımlılık yönü tek yönlüdür: Contract ← ViewModel ← Route ← Screen.

```
<Feature>Contract.kt   — State / Intent / Effect tanımları
<Feature>ViewModel.kt  — İş mantığı, state yönetimi
<Feature>Route.kt      — ViewModel bağlantısı, Effect tüketimi, navigasyon
<Feature>Screen.kt     — Tamamen stateless Composable UI
```

---

## 2) Paket Yapısı

```
feature/
  <feature>/                        # Tekil özellik (örn. onboarding, home)
    <Feature>Contract.kt
    <Feature>ViewModel.kt
    <Feature>Route.kt
    <Feature>Screen.kt
    <Feature>MockSource.kt          # Sahte veri kaynağı (varsa)

  <domain>/
    <feature>/                      # Gruplanan özellik (örn. auth/login, auth/otp)
      <Feature>Contract.kt
      ...

navigation/
  RenCarNavHost.kt                  # Uygulama seviyesi NavHost
  MainScaffold.kt                   # BottomNavigation + iç NavHost
  BottomNavItem.kt                  # BottomNav rotaları
```

Veri modeli sınıfları (data class) küçükse Contract dosyasına eklenir; bağımsız bir entity
niteliği taşıyorsa ayrı bir `<Model>.kt` dosyasına alınır (örn. `RentalRecord.kt`).

---

## 3) Route / Screen Ayrımı

**Screen**: Yalnızca UI. ViewModel veya navigasyon bağımlılığı yoktur.

```kotlin
@Composable
fun HistoryScreen(
    state: HistoryContract.State,
    onIntent: (HistoryContract.Intent) -> Unit
)
```

**Route**: ViewModel ile köprü görevi görür. Üç sorumluluğu vardır:

1. State'i `collectAsState()` ile abone olur.
2. Effect'leri `LaunchedEffect(Unit)` içinde tüketir ve navigasyon callback'lerini tetikler.
3. Screen'i çağırır; `state` ve `viewModel::onIntent` geçer.

```kotlin
@Composable
fun HistoryRoute(
    onNavigateToDetail: (String) -> Unit = {},
    viewModel: HistoryViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is HistoryContract.Effect.NavigateToDetail -> onNavigateToDetail(effect.rentalId)
            }
        }
    }

    HistoryScreen(state = state, onIntent = viewModel::onIntent)
}
```

Screen asla Route'a veya NavController'a doğrudan erişmez.

---

## 4) Navigasyon Entegrasyonu

Rotalar `RenCarNavHost` içindeki `private object RenCarDestinations` altında sabit string olarak
tanımlanır.

```kotlin
private object RenCarDestinations {
    const val ONBOARDING = "onboarding"
    const val OTP        = "otp/{phoneNumber}"

    fun otpRoute(phoneNumber: String) = "otp/$phoneNumber"
}
```

Kural: Rota parametreleri URL path segmenti olarak geçilir (`"route/{param}"`). Query string
kullanılmaz. Argümanlar `backStackEntry.arguments?.getString("key").orEmpty()` ile okunur.

BottomNavigationBar ile yönetilen ekranlar `MainScaffold` içindeki iç NavHost'a eklenir;
`RenCarNavHost`'a eklenmez.

---

## 5) Sahte Veri (FakeRepository) Stratejisi

**Mevcut durum:** ViewModel doğrudan `<Feature>MockSource` nesnesinden veri alır. Hilt
entegrasyonu henüz yapılmamıştır.

**Hilt entegrasyonu sonrası:**

- ViewModel yalnızca interface'e bağımlı olacaktır.
- Gerçek/sahte implementasyon seçimi `di/<Feature>Module.kt` dosyasında sağlanacaktır.
- ViewModel ve Screen'e dokunulmayacaktır.

Karar geçmişi: `docs/decisions.md` — "Repository Stub Stratejisi"

---

## 6) Parametre Alan ViewModel

Navigasyon argümanı gerektiren ekranlarda (örn. detay ekranı, OTP ekranı) Route dosyasına
`ViewModelProvider.Factory` eklenir. Hilt entegrasyonu sonrası bu pattern `@AssistedInject`
ile değiştirilecektir.

Karar geçmişi: `docs/decisions.md` — "Parametre Alan ViewModel Factory Stratejisi"

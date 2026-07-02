# MVI ViewModel Şablonu ve Zorunlu Kurallar

Bu belge, her MVI ViewModel'inin uyması gereken şablonu ve zorunlu kuralları tanımlar.

---

## 1) Temel Şablon

```kotlin
class <Feature>ViewModel : ViewModel() {

    private val _state = MutableStateFlow(<Feature>Contract.State())
    val state: StateFlow<<Feature>Contract.State> = _state.asStateFlow()

    private val _effect = Channel<<Feature>Contract.Effect>(Channel.BUFFERED)
    val effect: Flow<<Feature>Contract.Effect> = _effect.receiveAsFlow()

    fun onIntent(intent: <Feature>Contract.Intent) {
        when (intent) {
            is <Feature>Contract.Intent.<DataIntent>  -> handle<DataIntent>(intent.<param>)
            <Feature>Contract.Intent.<ObjectIntent>   -> handle<ObjectIntent>()
            <Feature>Contract.Intent.<NavIntent>      -> sendEffect(<Feature>Contract.Effect.<NavEffect>)
        }
    }

    private fun handle<Action>() {
        // İş mantığı
        _state.update { it.copy(/* ... */) }
    }

    private fun sendEffect(effect: <Feature>Contract.Effect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}
```

---

## 2) State Akışı Kuralları

- State her zaman `MutableStateFlow` + `asStateFlow()` çiftiyle sunulur.
- `_state` private, `state` public ve `StateFlow` tipinde olmalıdır.
- State güncellemeleri yalnızca `_state.update { it.copy(...) }` ile yapılır.
- `_state.value = ...` ataması yasaktır.

```kotlin
private val _state = MutableStateFlow(<Feature>Contract.State())
val state: StateFlow<<Feature>Contract.State> = _state.asStateFlow()
```

---

## 3) Effect Akışı Kuralları

- Effect her zaman `Channel<T>(Channel.BUFFERED)` + `receiveAsFlow()` çiftiyle sunulur.
- `_effect` private, `effect` public ve `Flow` tipinde olmalıdır.
- Tüm effect gönderimleri `sendEffect()` yardımcısı üzerinden yapılır; `_effect.send()` doğrudan
  `onIntent` içinde çağrılmaz.

```kotlin
private val _effect = Channel<<Feature>Contract.Effect>(Channel.BUFFERED)
val effect: Flow<<Feature>Contract.Effect> = _effect.receiveAsFlow()

private fun sendEffect(effect: <Feature>Contract.Effect) {
    viewModelScope.launch { _effect.send(effect) }
}
```

---

## 4) onIntent Kuralları

- Tek public giriş noktası `fun onIntent(intent: <Feature>Contract.Intent)` olmalıdır.
- `when` dalları exhaustive olmalıdır; `else` bloğu yasaktır.
- Basit navigasyon yönlendirmeleri için inline `sendEffect(...)` çağrısı kabul edilir.
- İş mantığı içeren Intent'ler için private `handle<IntentName>()` fonksiyonu zorunludur.

```kotlin
fun onIntent(intent: LoginContract.Intent) {
    when (intent) {
        is LoginContract.Intent.PhoneNumberChanged -> handlePhoneNumberChanged(intent.value)
        LoginContract.Intent.SendCode              -> handleSendCode()
        LoginContract.Intent.GoToRegister          -> sendEffect(LoginContract.Effect.NavigateToRegister)
        LoginContract.Intent.NavigateBack          -> sendEffect(LoginContract.Effect.NavigateBack)
    }
}
```

---

## 5) handle* Fonksiyon Kuralları

- İsimlendirme: `private fun handle<IntentName>()`
- Parametre gerektiriyorsa: `private fun handle<IntentName>(value: Type)`
- Erken dönüş (guard clause) geçersiz durumlar için kullanılır:

```kotlin
private fun handleSendCode() {
    if (!_state.value.isPhoneNumberValid) return
    sendEffect(LoginContract.Effect.NavigateToOtp(_state.value.phoneNumber))
}
```

---

## 6) init Bloğu

`init` bloğu yalnızca ekran açıldığında veri yüklemesi gereken durumlarda kullanılır.
`init` içinde yalnızca private yardımcı fonksiyon çağrısı yapılır; iş mantığı `init` içinde
yazılmaz.

```kotlin
init {
    loadRentals()
}

private fun loadRentals() {
    _state.update { it.copy(rentals = HistoryMockSource.currentMonthRentals) }
}
```

---

## 7) Parametre Alan ViewModel (Factory Deseni)

ViewModel constructor argümanı gerektiriyorsa (örn. detay ekranında id aktarımı),
Route dosyasına `ViewModelProvider.Factory` implementasyonu eklenir.

```kotlin
// <Feature>Route.kt
@Composable
fun OtpRoute(
    phoneNumber: String,
    ...,
    viewModel: OtpViewModel = viewModel(
        factory = remember(phoneNumber) { OtpViewModelFactory(phoneNumber) }
    )
)

private class OtpViewModelFactory(private val phoneNumber: String) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = OtpViewModel(phoneNumber) as T
}
```

`remember(phoneNumber)` ile factory, parametre değiştiğinde yeniden oluşturulur.

---

## 8) Coroutine İçeren İşler

Zamanlayıcı veya arka plan işlemi gerektiren durumlar `Job` referansıyla yönetilir.
Yeniden başlatma öncesinde mevcut Job iptal edilir (`cancel()`).

```kotlin
private var countdownJob: Job? = null

private fun startCountdown() {
    countdownJob?.cancel()
    countdownJob = viewModelScope.launch {
        while (_state.value.remainingSeconds > 0) {
            delay(1000)
            _state.update { it.copy(remainingSeconds = it.remainingSeconds - 1) }
        }
    }
}
```

---

## 9) Zorunlu Kurallar Özeti

| Kural                                              | Durum     |
|----------------------------------------------------|-----------|
| `_state` MutableStateFlow, `state` StateFlow       | Zorunlu   |
| State güncellemesi yalnızca `_state.update {}`    | Zorunlu   |
| `_effect` Channel(BUFFERED), `effect` Flow         | Zorunlu   |
| Effect yalnızca `sendEffect()` ile gönderilir      | Zorunlu   |
| Tek public giriş noktası `onIntent()`              | Zorunlu   |
| `when` içinde `else` bloğu                         | Yasak     |
| `_state.value = ...` doğrudan atama                | Yasak     |
| İş mantığı `handle*()` private fonksiyona alınır  | Zorunlu   |

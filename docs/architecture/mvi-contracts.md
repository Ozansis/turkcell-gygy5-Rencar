# MVI Contract Kuralları ve İsimlendirme Standartları

Bu belge, her MVI ekranının `Contract.kt` dosyasında uygulanması zorunlu olan State / Intent /
Effect yapılarını ve isimlendirme standartlarını tanımlar.

---

## 1) Contract Nesnesi

Her Contract, feature paketi içindeki tek bir `object` üzerinde tanımlanır.

```kotlin
object <Feature>Contract {
    data class State(...)
    sealed interface Intent { ... }
    sealed interface Effect { ... }
}
```

İsimlendirme: `<Feature>Contract` — Feature adı PascalCase, suffix sabit olarak `Contract`.

Sabit değerler (magic number yerine geçen) Contract object'i içinde `const val` olarak
tanımlanır:

```kotlin
object OtpContract {
    const val CODE_LENGTH             = 6
    const val RESEND_COOLDOWN_SECONDS = 42
    ...
}
```

---

## 2) State

- `data class State(...)` olarak tanımlanır.
- Tüm alanlar varsayılan değer alır; böylece `State()` her zaman geçerli bir başlangıç durumu
  döner.
- UI'da hesaplanan türetilmiş değerler, State alanı **değil**, `val` computed property olarak
  tanımlanır:

```kotlin
data class State(
    val phoneNumber: String = "",
    val isLoading: Boolean  = false
) {
    val isPhoneNumberValid: Boolean get() = phoneNumber.length == 10
}
```

- Boolean yükleme durumu için alan adı `isLoading` kullanılır.
- Boolean doğrulama durumları için `isVerifying`, `isSubmitting` gibi işleme odaklı isimler
  tercih edilir.

---

## 3) Intent

- `sealed interface Intent` olarak tanımlanır.
- Her alt tip, kullanıcının gerçekleştirdiği bir eylemi temsil eder.
- Veri taşıyan Intent: `data class <Action>(val <param>: <Type>) : Intent`
- Veri taşımayan Intent: `data object <Action> : Intent`

```kotlin
sealed interface Intent {
    data class PhoneNumberChanged(val value: String) : Intent   // veri taşıyan
    data object SendCode                             : Intent   // veri taşımayan
    data object NavigateBack                         : Intent   // veri taşımayan
}
```

İsimlendirme:
- Kullanıcı eylemleri için fiil + nesne: `PhoneNumberChanged`, `SendCode`, `RentalSelected`
- Navigasyon tetikleyicileri için: `NavigateBack`, `GoToLogin`, `GoToRegister`

---

## 4) Effect

- `sealed interface Effect` olarak tanımlanır.
- ViewModel'den Screen'e tek seferlik iletilmesi gereken olayları temsil eder.
- Yalnızca navigasyon ve toast/snackbar gibi tek kullanımlık yan etkiler için kullanılır.
- State'e yansıtılabilecek hiçbir şey Effect olmamalıdır.

```kotlin
sealed interface Effect {
    data class NavigateToOtp(val phoneNumber: String) : Effect  // veri taşıyan
    data object NavigateToHome                        : Effect  // veri taşımayan
    data object NavigateBack                          : Effect  // veri taşımayan
}
```

İsimlendirme:
- Tüm Effect alt tipleri `Navigate` veya `Show` prefixi ile başlar.
- Navigasyon: `NavigateTo<Destination>`, `NavigateBack`
- Bildirim: `ShowError`, `ShowToast`

---

## 5) İsimlendirme Özet Tablosu

| Eleman                   | Format                          | Örnek                              |
|--------------------------|---------------------------------|------------------------------------|
| Contract nesnesi         | `<Feature>Contract`             | `LoginContract`                    |
| State                    | `data class State`              | —                                  |
| Computed property        | `val <name>: Boolean get() = …` | `val isLastPage: Boolean get() = …`|
| Intent (veri taşıyan)    | `data class <Action>`           | `data class CodeChanged(val value: String)` |
| Intent (veri taşımayan)  | `data object <Action>`          | `data object Verify`               |
| Effect (veri taşıyan)    | `data class Navigate<To>`       | `data class NavigateToOtp(val phoneNumber: String)` |
| Effect (veri taşımayan)  | `data object Navigate<To>`      | `data object NavigateBack`         |

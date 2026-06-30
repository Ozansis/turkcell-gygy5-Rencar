# RenCar — Renk Sistemi

> Bu dosya Splash / Onboarding ekranlarından türetilmiş ve tüm uygulama genelinde bağlayıcı nitelikteki renk tokenlarını tanımlar.
> Renk değerleri hex formatında verilmiştir; Compose tarafındaki karşılığı `Color(0xFF…)` biçimindedir.

---

## 1. Temel Palet (Primitive Tokens)

Bu katman ham renk değerlerini tanımlar. Doğrudan Composable'larda **kullanılmaz**; yalnızca
semantik token tanımlarında referans alınır.

| Token Adı          | Hex       | Açıklama                          |
|--------------------|-----------|-----------------------------------|
| `Blue500`          | `#2563EB` | Birincil eylem rengi              |
| `Blue400`          | `#3B82F6` | Karanlık temada birincil ton      |
| `Blue500Alpha20`   | `#332563EB`| İkon glow efekti (dark, %20 alpha)|
| `White`            | `#FFFFFF` | Saf beyaz                         |
| `Black`            | `#000000` | Saf siyah                         |
| `Gray50`           | `#F9FAFB` |                                   |
| `Gray100`          | `#F3F4F6` |                                   |
| `Gray300`          | `#D1D5DB` |                                   |
| `Gray500`          | `#6B7280` |                                   |
| `Gray400`          | `#9CA3AF` |                                   |
| `Neutral900`       | `#111827` | Açık temada ana metin             |
| `Dark100`          | `#1A1C23` | Karanlık temada yüzey             |
| `Dark200`          | `#13151A` | Karanlık temada arka plan         |
| `Dark300`          | `#0D0F14` | Karanlık temada en derin arka plan|

---

## 2. Semantik Tokenlar (Semantic Tokens)

Composable'larda yalnızca bu katmandaki isimler kullanılır. Token isimleri
`MaterialTheme.colorScheme.*` ile uyumlu tutulmuştur; özel tokenlar `RenCarTheme.colors.*`
altında tanımlanacaktır.

### 2.1 Arka Plan ve Yüzey

| Token                    | Light             | Dark              | Kullanım Yeri                             |
|--------------------------|-------------------|-------------------|-------------------------------------------|
| `background`             | `#EEF2F9`         | `#0D0F14`         | Tüm ekranların kök arka planı             |
| `surface`                | `#FFFFFF`         | `#1A1C23`         | Kart, bottom sheet, dialog yüzeyleri      |
| `surfaceVariant`         | `#F3F4F6`         | `#13151A`         | Girdi alanları, chip arka planı           |

### 2.2 Birincil Renk (Primary)

| Token                    | Light             | Dark              | Kullanım Yeri                             |
|--------------------------|-------------------|-------------------|-------------------------------------------|
| `primary`                | `#2563EB`         | `#3B82F6`         | CTA buton arka planı, aktif gösterge      |
| `onPrimary`              | `#FFFFFF`         | `#FFFFFF`         | Birincil yüzey üzerindeki metin/ikon      |
| `primaryContainer`       | `#DBEAFE`         | `#1D3461`         | İkon arka planı, chip seçili hali         |
| `onPrimaryContainer`     | `#1E3A8A`         | `#93C5FD`         | primaryContainer üzerindeki metin/ikon    |

### 2.3 Metin

| Token                    | Light             | Dark              | Kullanım Yeri                             |
|--------------------------|-------------------|-------------------|-------------------------------------------|
| `onBackground`           | `#111827`         | `#F9FAFB`         | Ekran başlığı, ana gövde metni           |
| `onSurface`              | `#111827`         | `#F9FAFB`         | Kart ve yüzey üzerindeki metin           |
| `onSurfaceVariant`       | `#6B7280`         | `#9CA3AF`         | Alt başlık, yardımcı metin, placeholder  |

### 2.4 Bağlantı ve Vurgu

| Token                    | Light             | Dark              | Kullanım Yeri                             |
|--------------------------|-------------------|-------------------|-------------------------------------------|
| `link`                   | `#2563EB`         | `#3B82F6`         | Tıklanabilir metin bağlantısı             |
| `outline`                | `#D1D5DB`         | `#374151`         | Girdi kenarlığı, ayırıcı çizgi           |
| `outlineVariant`         | `#E5E7EB`         | `#1F2937`         | Hafif ayırıcı                            |

### 2.5 Özel Efekt Tokenları

| Token                    | Değer / Açıklama                                          | Kullanım Yeri                 |
|--------------------------|-----------------------------------------------------------|-------------------------------|
| `iconGlowColor`          | `Blue500` @ `%20` alpha — yalnızca **dark** tema          | Splash ikonunun hale efekti   |
| `iconGlowRadius`         | `80.dp`                                                   | Splash ikonunun hale yarıçapı |
| `pageIndicatorActive`    | `primary` token                                           | Aktif onboarding noktası      |
| `pageIndicatorInactive`  | Light: `Gray300` / Dark: `#374151`                        | Pasif onboarding noktası      |

---

## 3. Durum Renkleri (State Colors)

| Token        | Hex       | Kullanım                              |
|--------------|-----------|---------------------------------------|
| `error`      | `#DC2626` | Hata mesajı, hatalı girdi kenarlığı   |
| `onError`    | `#FFFFFF`  | Hata yüzeyindeki metin               |
| `success`    | `#16A34A` | Başarı bildirimi                      |
| `warning`    | `#D97706` | Uyarı bildirimi                       |

---

## 4. Opaklık / Overlay Kuralları

| Durum             | Opaklık | Uygulama                                  |
|-------------------|---------|-------------------------------------------|
| Disabled          | `38 %`  | Tüm devre dışı bileşenler                 |
| Hovered (ripple)  | `8 %`   | `primary` rengi üzerine uygulanır         |
| Pressed (ripple)  | `12 %`  | `primary` rengi üzerine uygulanır         |
| Modal scrim       | `60 %`  | Bottom sheet / dialog arka plan örtüsü    |

---

## 5. Compose Uygulama Notu

```kotlin
// ui/theme/Color.kt  — primitive layer
val Blue500   = Color(0xFF2563EB)
val Blue400   = Color(0xFF3B82F6)
val Dark300   = Color(0xFF0D0F14)
// …

// ui/theme/Theme.kt  — semantic layer
private val LightColorScheme = lightColorScheme(
    primary          = Blue500,
    onPrimary        = White,
    background       = Color(0xFFEEF2F9),
    onBackground     = Color(0xFF111827),
    onSurfaceVariant = Color(0xFF6B7280),
    // …
)

private val DarkColorScheme = darkColorScheme(
    primary          = Blue400,
    onPrimary        = White,
    background       = Dark300,
    onBackground     = Color(0xFFF9FAFB),
    onSurfaceVariant = Color(0xFF9CA3AF),
    // …
)
```

---

*Son Güncelleme: 2026-06-30 — Splash / Onboarding ekranından türetildi.*

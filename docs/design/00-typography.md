# RenCar — Tipografi Sistemi

> Bu dosya Splash / Onboarding ekranlarından türetilmiş ve tüm uygulama genelinde bağlayıcı nitelikteki
> tipografi tokenlarını tanımlar. Compose tarafında `MaterialTheme.typography.*` ve özel uzantılar
> `RenCarTheme.typography.*` üzerinden erişilir.

---

## 1. Font Ailesi

| Rol              | Aile           | Kaynak                                    |
|------------------|----------------|-------------------------------------------|
| Birincil         | **Inter**      | Google Fonts — `res/font/inter_*.ttf`     |
| Yedek (fallback) | **Roboto**     | Android sistem varsayılanı                |

> **Karar gerekçesi:** Ekrandaki "Rencar" başlığı Roboto'dan daha sıkı tracking ve geometrik
> harf biçimleriyle öne çıkmaktadır. Inter bu görünümü birebir karşılamakta ve tüm ağırlık
> kademelerini tek dosya (variable font) olarak sunar. Değiştirilmesi gerekirse `docs/decisions.md`
> güncellenmeli ve bu dosyaya not düşülmelidir.

---

## 2. Ağırlık (Weight) Tanımları

| Sabit Adı         | Değer | Kullanım                                |
|-------------------|-------|-----------------------------------------|
| `WeightRegular`   | 400   | Gövde metni, açıklama, link etiketi     |
| `WeightMedium`    | 500   | Alt başlık, etiket                      |
| `WeightSemiBold`  | 600   | Buton metni, form etiketi               |
| `WeightBold`      | 700   | Ekran başlığı, uygulama adı             |
| `WeightExtraBold` | 800   | Hero başlık (gerekirse)                 |

---

## 3. Tip Ölçeği (Type Scale)

Aşağıdaki tokenlar Material 3 isim uzlaşısına uymaktadır. `lineHeight` değerleri
`sp` cinsinden, `letterSpacing` `em` cinsinden verilmiştir.

| Token              | size (sp) | lineHeight (sp) | weight     | letterSpacing | Kullanım Yeri                         |
|--------------------|-----------|-----------------|------------|---------------|---------------------------------------|
| `displayLarge`     | 36        | 44              | Bold 700   | −0.02 em      | Gelecekte kullanılabilecek hero başlık|
| `displayMedium`    | 28        | 36              | Bold 700   | −0.01 em      | —                                     |
| `headlineLarge`    | 28        | 36              | Bold 700   | 0 em          | **Splash uygulama adı "Rencar"**      |
| `headlineMedium`   | 24        | 32              | Bold 700   | 0 em          | Onboarding bölüm başlığı              |
| `headlineSmall`    | 20        | 28              | SemiBold 600 | 0 em        | Ekran başlığı (AppBar)                |
| `titleLarge`       | 18        | 26              | SemiBold 600 | 0 em        | Kart başlığı, bölüm etiketi           |
| `titleMedium`      | 16        | 24              | Medium 500 | +0.01 em      | Liste öğesi başlığı                   |
| `titleSmall`       | 14        | 20              | Medium 500 | +0.01 em      | Küçük kart başlığı                    |
| `bodyLarge`        | 16        | 24              | Regular 400| 0 em          | Uzun açıklama paragrafı               |
| `bodyMedium`       | 14        | 20              | Regular 400| +0.01 em      | **Splash alt başlık ("Yakındaki…")**  |
| `bodySmall`        | 12        | 16              | Regular 400| +0.04 em      | Uyarı, ipucu metni                    |
| `labelLarge`       | 16        | 24              | SemiBold 600 | +0.01 em    | **"Hemen Başla" buton metni**         |
| `labelMedium`      | 13        | 18              | Regular 400| +0.05 em      | **Splash alt link ("Zaten hesabım var")**|
| `labelSmall`       | 11        | 14              | Medium 500 | +0.05 em      | Chip, badge, küçük etiket             |

---

## 4. Ekran Düzeyinde Kullanım Kılavuzu

### 4.1 Splash / Onboarding Ekranı

| UI Öğesi                            | Token          | Renk Referansı              |
|-------------------------------------|----------------|-----------------------------|
| Uygulama adı "Rencar"               | `headlineLarge`| `onBackground`              |
| Alt başlık "Yakındaki aracı bul…"   | `bodyMedium`   | `onSurfaceVariant`          |
| Birincil buton "Hemen Başla"        | `labelLarge`   | `onPrimary`                 |
| Footer metin "Zaten hesabım var ·"  | `labelMedium`  | `onSurfaceVariant`          |
| Footer bağlantı "Giriş yap"         | `labelMedium`  | `link`                      |

### 4.2 Genel Kurallar

- Bir Composable içinde **en fazla 2 farklı tip token** kullanılır. Daha fazlası gerekiyorsa
  ekranın hiyerarşisi yeniden değerlendirilmelidir.
- `fontWeight` hiçbir zaman Composable içinde hard-code edilmez; her zaman token üzerinden taşınır.
- Satır uzunluğu `320.dp` altındaki ekranlarda `bodyMedium` için `maxLines = 3` uygulanır.

---

## 5. Compose Uygulama Notu

```kotlin
// ui/theme/Type.kt
val RenCarTypography = Typography(
    headlineLarge = TextStyle(
        fontFamily  = InterFontFamily,
        fontWeight  = FontWeight.Bold,
        fontSize    = 28.sp,
        lineHeight  = 36.sp,
        letterSpacing = 0.sp,
    ),
    bodyMedium = TextStyle(
        fontFamily  = InterFontFamily,
        fontWeight  = FontWeight.Normal,
        fontSize    = 14.sp,
        lineHeight  = 20.sp,
        letterSpacing = 0.1.sp,
    ),
    labelLarge = TextStyle(
        fontFamily  = InterFontFamily,
        fontWeight  = FontWeight.SemiBold,
        fontSize    = 16.sp,
        lineHeight  = 24.sp,
        letterSpacing = 0.1.sp,
    ),
    labelMedium = TextStyle(
        fontFamily  = InterFontFamily,
        fontWeight  = FontWeight.Normal,
        fontSize    = 13.sp,
        lineHeight  = 18.sp,
        letterSpacing = 0.5.sp,
    ),
    // diğer tokenlar…
)
```

> `InterFontFamily` tanımı için `res/font/` altına variable font dosyası eklendikten sonra
> `FontFamily(Font(R.font.inter_variable, variableAxes = listOf(…)))` biçiminde tanımlanabilir
> ya da sabit ağırlık dosyaları kullanılabilir.

---

*Son Güncelleme: 2026-06-30 — Splash / Onboarding ekranından türetildi.*

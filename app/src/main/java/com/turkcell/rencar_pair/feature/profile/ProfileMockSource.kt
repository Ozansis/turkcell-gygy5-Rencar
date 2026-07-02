package com.turkcell.rencar_pair.feature.profile

object ProfileMockSource {

    val userName = "Deniz Yılmaz"

    val phoneNumber = "+90 532 000 00 00"

    val license = LicenseVerification(
        isVerified   = true,
        licenseClass = "B sınıfı",
        statusLabel  = "Onaylı"
    )
}

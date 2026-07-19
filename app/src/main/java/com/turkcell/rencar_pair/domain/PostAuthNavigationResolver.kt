package com.turkcell.rencar_pair.domain

import com.turkcell.rencar_pair.data.repository.AuthRepository
import com.turkcell.rencar_pair.data.repository.AuthResult
import com.turkcell.rencar_pair.data.repository.LicenseRepository
import javax.inject.Inject
import javax.inject.Singleton

sealed interface PostAuthDestination {
    data object Home           : PostAuthDestination
    data object LicenseUpload  : PostAuthDestination
    data object LicensePending : PostAuthDestination
}

@Singleton
class PostAuthNavigationResolver @Inject constructor(
    private val authRepository: AuthRepository,
    private val licenseRepository: LicenseRepository
) {

    suspend fun resolve(): PostAuthDestination {
        val me = authRepository.getMe()
        if (me !is AuthResult.Success) return PostAuthDestination.Home

        if (me.data.role != "PENDING") return PostAuthDestination.Home

        val status = licenseRepository.getStatus()
        if (status !is AuthResult.Success) return PostAuthDestination.Home

        return if (status.data.status == "UNDER_REVIEW") {
            PostAuthDestination.Home
        } else {
            PostAuthDestination.LicenseUpload
        }
    }
}

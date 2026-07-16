package com.turkcell.rencar_pair.data.repository

import android.content.Context
import android.net.Uri
import com.turkcell.rencar_pair.data.network.LicenseApiService
import com.turkcell.rencar_pair.data.network.dto.LicenseResponseDto
import com.turkcell.rencar_pair.data.network.dto.LicenseStatusResponseDto
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

@Singleton
class LicenseRepository @Inject constructor(
    private val licenseApiService: LicenseApiService,
    @ApplicationContext private val context: Context
) {

    suspend fun upload(front: Uri, back: Uri, selfie: Uri): AuthResult<LicenseResponseDto> {
        return try {
            val response = licenseApiService.upload(
                uriToPart("front", front),
                uriToPart("back", back),
                uriToPart("selfie", selfie)
            )
            val body = response.body()
            if (response.isSuccessful && body != null) {
                AuthResult.Success(body)
            } else {
                AuthResult.Error(response.code(), "Sunucu hatası (kod: ${response.code()}).")
            }
        } catch (e: IOException) {
            AuthResult.Error(code = null, message = "Bağlantı hatası, lütfen tekrar deneyin.")
        }
    }

    suspend fun getStatus(): AuthResult<LicenseStatusResponseDto> {
        return try {
            val response = licenseApiService.status()
            val body = response.body()
            if (response.isSuccessful && body != null) {
                AuthResult.Success(body)
            } else {
                AuthResult.Error(response.code(), "Sunucu hatası (kod: ${response.code()}).")
            }
        } catch (e: IOException) {
            AuthResult.Error(code = null, message = "Bağlantı hatası, lütfen tekrar deneyin.")
        }
    }

    private suspend fun uriToPart(partName: String, uri: Uri): MultipartBody.Part =
        withContext(Dispatchers.IO) {
            val resolver = context.contentResolver
            val mimeType = resolver.getType(uri) ?: "image/jpeg"
            val bytes = resolver.openInputStream(uri)?.use { it.readBytes() }
                ?: throw IOException("Görsel okunamadı: $uri")
            val extension = if (mimeType.contains("png")) "png" else "jpg"
            val requestBody = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
            MultipartBody.Part.createFormData(partName, "$partName.$extension", requestBody)
        }
}

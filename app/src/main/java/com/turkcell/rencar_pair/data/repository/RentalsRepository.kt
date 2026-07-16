package com.turkcell.rencar_pair.data.repository

import android.content.Context
import android.net.Uri
import com.turkcell.rencar_pair.data.network.RentalsApiService
import com.turkcell.rencar_pair.data.network.dto.ActiveRentalResponseDto
import com.turkcell.rencar_pair.data.network.dto.CreateRentalDto
import com.turkcell.rencar_pair.data.network.dto.RentalPhotosStateDto
import com.turkcell.rencar_pair.data.network.dto.RentalResponseDto
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
class RentalsRepository @Inject constructor(
    private val rentalsApiService: RentalsApiService,
    @ApplicationContext private val context: Context
) {

    suspend fun createRental(vehicleId: String, plan: String, endDate: String? = null): AuthResult<RentalResponseDto> {
        return try {
            val response = rentalsApiService.createRental(CreateRentalDto(vehicleId, plan, endDate))
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

    suspend fun getActiveRental(): AuthResult<ActiveRentalResponseDto> {
        return try {
            val response = rentalsApiService.getActiveRental()
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

    suspend fun finishRental(id: String): AuthResult<RentalResponseDto> {
        return try {
            val response = rentalsApiService.finishRental(id)
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

    suspend fun uploadPhoto(rentalId: String, side: String, imageUri: Uri): AuthResult<RentalPhotosStateDto> {
        return try {
            val sideBody = side.toRequestBody("text/plain".toMediaTypeOrNull())
            val filePart = uriToPart(imageUri)
            val response = rentalsApiService.uploadPhoto(rentalId, sideBody, filePart)
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

    suspend fun getPhotos(rentalId: String): AuthResult<RentalPhotosStateDto> {
        return try {
            val response = rentalsApiService.getPhotos(rentalId)
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

    suspend fun startRental(rentalId: String): AuthResult<RentalResponseDto> {
        return try {
            val response = rentalsApiService.startRental(rentalId)
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

    private suspend fun uriToPart(uri: Uri): MultipartBody.Part =
        withContext(Dispatchers.IO) {
            val resolver = context.contentResolver
            val mimeType = resolver.getType(uri) ?: "image/jpeg"
            val bytes = resolver.openInputStream(uri)?.use { it.readBytes() }
                ?: throw IOException("Görsel okunamadı: $uri")
            val extension = if (mimeType.contains("png")) "png" else "jpg"
            val requestBody = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
            MultipartBody.Part.createFormData("file", "photo.$extension", requestBody)
        }
}

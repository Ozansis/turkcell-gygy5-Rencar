package com.turkcell.rencar_pair.data.repository

import android.content.Context
import android.net.Uri
import com.turkcell.rencar_pair.data.network.RentalsApiService
import com.turkcell.rencar_pair.data.network.dto.ActiveRentalResponseDto
import com.turkcell.rencar_pair.data.network.dto.CreateRentalDto
import com.turkcell.rencar_pair.data.network.dto.PayRentalDto
import com.turkcell.rencar_pair.data.network.dto.PayRentalResponseDto
import com.turkcell.rencar_pair.data.network.dto.RentalPhotosStateDto
import com.turkcell.rencar_pair.data.network.dto.RentalResponseDto
import com.turkcell.rencar_pair.data.network.dto.RentalStatsResponseDto
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

    suspend fun createRental(vehicleId: String, plan: String, endDate: String? = null): AuthResult<RentalResponseDto> =
        safeCall { rentalsApiService.createRental(CreateRentalDto(vehicleId, plan, endDate)) }

    suspend fun listMine(): AuthResult<List<RentalResponseDto>> =
        safeCall { rentalsApiService.listMine() }

    suspend fun getRental(id: String): AuthResult<RentalResponseDto> =
        safeCall { rentalsApiService.getRental(id) }

    suspend fun getStats(month: String? = null): AuthResult<RentalStatsResponseDto> =
        safeCall { rentalsApiService.getStats(month) }

    suspend fun getActiveRental(): AuthResult<ActiveRentalResponseDto> =
        safeCall { rentalsApiService.getActiveRental() }

    suspend fun finishRental(id: String): AuthResult<RentalResponseDto> =
        safeCall { rentalsApiService.finishRental(id) }

    suspend fun payRental(
        id: String,
        method: String,
        cardId: String? = null,
        iyzicoPaymentId: String? = null
    ): AuthResult<PayRentalResponseDto> =
        safeCall { rentalsApiService.payRental(id, PayRentalDto(method, cardId, iyzicoPaymentId)) }

    suspend fun cancelRental(id: String): AuthResult<Unit> =
        safeUnitCall { rentalsApiService.cancelRental(id) }

    suspend fun uploadPhoto(rentalId: String, side: String, imageUri: Uri): AuthResult<RentalPhotosStateDto> =
        safeCall {
            val sideBody = side.toRequestBody("text/plain".toMediaTypeOrNull())
            val filePart = uriToPart(imageUri)
            rentalsApiService.uploadPhoto(rentalId, sideBody, filePart)
        }

    suspend fun getPhotos(rentalId: String): AuthResult<RentalPhotosStateDto> =
        safeCall { rentalsApiService.getPhotos(rentalId) }

    suspend fun startRental(rentalId: String): AuthResult<RentalResponseDto> =
        safeCall { rentalsApiService.startRental(rentalId) }

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

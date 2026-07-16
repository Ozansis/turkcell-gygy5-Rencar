package com.turkcell.rencar_pair.feature.rental.photos

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import java.io.File

@Composable
fun VehiclePhotosRoute(
    rentalId: String,
    vehicleId: String,
    onNavigateToActiveRental: (String) -> Unit = {},
    onNavigateBack: () -> Unit = {},
    viewModel: VehiclePhotosViewModel = hiltViewModel<VehiclePhotosViewModel, VehiclePhotosViewModel.Factory>(
        creationCallback = { factory -> factory.create(rentalId, vehicleId) }
    )
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    var pendingSide by remember { mutableStateOf<VehiclePhotosContract.PhotoSide?>(null) }
    var pendingUri by remember { mutableStateOf<Uri?>(null) }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        val side = pendingSide
        val uri = pendingUri
        if (success && side != null && uri != null) {
            viewModel.onIntent(VehiclePhotosContract.Intent.PhotoCaptured(side, uri))
        }
        pendingSide = null
        pendingUri = null
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is VehiclePhotosContract.Effect.LaunchCamera -> {
                    val uri = createDeliveryPhotoUri(context, effect.side)
                    pendingSide = effect.side
                    pendingUri = uri
                    takePictureLauncher.launch(uri)
                }
                is VehiclePhotosContract.Effect.NavigateToActiveRental -> onNavigateToActiveRental(effect.rentalId)
                is VehiclePhotosContract.Effect.ShowError              -> Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                VehiclePhotosContract.Effect.NavigateBack              -> onNavigateBack()
            }
        }
    }

    VehiclePhotosScreen(state = state, onIntent = viewModel::onIntent)
}

private fun createDeliveryPhotoUri(context: Context, side: VehiclePhotosContract.PhotoSide): Uri {
    val imagesDir = File(context.cacheDir, "license_images").apply { mkdirs() }
    val file = File(imagesDir, "delivery_${side.name.lowercase()}_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
}

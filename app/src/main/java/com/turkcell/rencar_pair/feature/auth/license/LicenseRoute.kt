package com.turkcell.rencar_pair.feature.auth.license

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.turkcell.rencar_pair.feature.auth.LicenseFlowViewModel
import java.io.File

private enum class LicenseImageSide { FRONT, BACK }

@Composable
fun LicenseRoute(
    licenseFlowViewModel: LicenseFlowViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: LicenseViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    var dialogSide by remember { mutableStateOf<LicenseImageSide?>(null) }
    var pendingCameraUri by remember { mutableStateOf<Uri?>(null) }
    var pendingSide by remember { mutableStateOf<LicenseImageSide?>(null) }

    fun dispatchSelectedUri(side: LicenseImageSide, uri: Uri) {
        when (side) {
            LicenseImageSide.FRONT -> {
                licenseFlowViewModel.setFrontUri(uri)
                viewModel.onIntent(LicenseContract.Intent.FrontImageSelected(uri))
            }
            LicenseImageSide.BACK -> {
                licenseFlowViewModel.setBackUri(uri)
                viewModel.onIntent(LicenseContract.Intent.BackImageSelected(uri))
            }
        }
    }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        val uri = pendingCameraUri
        val side = pendingSide
        if (success && uri != null && side != null) {
            dispatchSelectedUri(side, uri)
        }
        pendingCameraUri = null
        pendingSide = null
    }

    val pickMediaLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        val side = pendingSide
        if (uri != null && side != null) {
            dispatchSelectedUri(side, uri)
        }
        pendingSide = null
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                LicenseContract.Effect.ShowFrontImageSourceDialog -> dialogSide = LicenseImageSide.FRONT
                LicenseContract.Effect.ShowBackImageSourceDialog  -> dialogSide = LicenseImageSide.BACK
                LicenseContract.Effect.NavigateToHome             -> onNavigateToHome()
                LicenseContract.Effect.NavigateBack               -> onNavigateBack()
            }
        }
    }

    dialogSide?.let { side ->
        ImageSourceDialog(
            onDismiss = { dialogSide = null },
            onCameraSelected = {
                dialogSide = null
                val uri = createLicenseImageUri(context, side)
                pendingCameraUri = uri
                pendingSide = side
                takePictureLauncher.launch(uri)
            },
            onGallerySelected = {
                dialogSide = null
                pendingSide = side
                pickMediaLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
        )
    }

    LicenseScreen(
        state    = state,
        onIntent = viewModel::onIntent
    )
}

@Composable
private fun ImageSourceDialog(
    onDismiss: () -> Unit,
    onCameraSelected: () -> Unit,
    onGallerySelected: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Görsel kaynağı seç") },
        text = {
            Text("Kameradan çekebilir veya galeriden bir görsel seçebilirsin.")
        },
        confirmButton = {
            TextButton(onClick = onCameraSelected) {
                Text("Kameradan Çek")
            }
        },
        dismissButton = {
            TextButton(onClick = onGallerySelected) {
                Text("Galeriden Seç")
            }
        }
    )
}

private fun createLicenseImageUri(context: Context, side: LicenseImageSide): Uri {
    val imagesDir = File(context.cacheDir, "license_images").apply { mkdirs() }
    val file = File(imagesDir, "license_${side.name.lowercase()}_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
}

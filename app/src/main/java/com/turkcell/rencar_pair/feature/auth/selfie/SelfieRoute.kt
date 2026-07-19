package com.turkcell.rencar_pair.feature.auth.selfie

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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.turkcell.rencar_pair.feature.auth.LicenseFlowViewModel
import java.io.File

@Composable
fun SelfieRoute(
    licenseFlowViewModel: LicenseFlowViewModel,
    onNavigateToHome: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: SelfieViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    val licenseFlowState by licenseFlowViewModel.state.collectAsState()
    val context = LocalContext.current

    var pendingSelfieUri by remember { mutableStateOf<Uri?>(null) }

    val takePictureLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        val uri = pendingSelfieUri
        if (success && uri != null) {
            licenseFlowViewModel.setSelfieUri(uri)
            viewModel.onIntent(SelfieContract.Intent.SelfieImageSelected(uri))
        }
        pendingSelfieUri = null
    }

    LaunchedEffect(licenseFlowState.isUploading, licenseFlowState.isUploaded, licenseFlowState.uploadError) {
        viewModel.onIntent(
            SelfieContract.Intent.UploadStateChanged(
                isUploading = licenseFlowState.isUploading,
                isUploaded  = licenseFlowState.isUploaded,
                uploadError = licenseFlowState.uploadError
            )
        )
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                SelfieContract.Effect.LaunchCamera -> {
                    val uri = createSelfieImageUri(context)
                    pendingSelfieUri = uri
                    takePictureLauncher.launch(uri)
                }
                SelfieContract.Effect.TriggerUpload          -> licenseFlowViewModel.uploadIfReady()
                SelfieContract.Effect.NavigateToHome         -> onNavigateToHome()
                is SelfieContract.Effect.ShowError           -> Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                SelfieContract.Effect.NavigateBack           -> onNavigateBack()
            }
        }
    }

    SelfieScreen(
        state    = state,
        onIntent = viewModel::onIntent
    )
}

private fun createSelfieImageUri(context: Context): Uri {
    val imagesDir = File(context.cacheDir, "license_images").apply { mkdirs() }
    val file = File(imagesDir, "selfie_${System.currentTimeMillis()}.jpg")
    return FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
}

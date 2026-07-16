package com.turkcell.rencar_pair.feature.rental.photos

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable
fun VehiclePhotosScreen(
    state: VehiclePhotosContract.State,
    onIntent: (VehiclePhotosContract.Intent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onIntent(VehiclePhotosContract.Intent.NavigateBack) },
                modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
            }
        }

        Column(modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(text = "Araç durumu", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Başlamadan önce 4 yönü çek",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${state.brand} ${state.model} · ${state.plate}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = state.progressLabel,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PhotoTile(
                    modifier = Modifier.weight(1f),
                    label = "Ön",
                    imageUri = state.uriFor(VehiclePhotosContract.PhotoSide.FRONT),
                    isUploading = state.uploadingSide == VehiclePhotosContract.PhotoSide.FRONT,
                    onClick = { onIntent(VehiclePhotosContract.Intent.CapturePhotoClicked(VehiclePhotosContract.PhotoSide.FRONT)) }
                )
                PhotoTile(
                    modifier = Modifier.weight(1f),
                    label = "Arka",
                    imageUri = state.uriFor(VehiclePhotosContract.PhotoSide.BACK),
                    isUploading = state.uploadingSide == VehiclePhotosContract.PhotoSide.BACK,
                    onClick = { onIntent(VehiclePhotosContract.Intent.CapturePhotoClicked(VehiclePhotosContract.PhotoSide.BACK)) }
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                PhotoTile(
                    modifier = Modifier.weight(1f),
                    label = "Sol",
                    imageUri = state.uriFor(VehiclePhotosContract.PhotoSide.LEFT),
                    isUploading = state.uploadingSide == VehiclePhotosContract.PhotoSide.LEFT,
                    onClick = { onIntent(VehiclePhotosContract.Intent.CapturePhotoClicked(VehiclePhotosContract.PhotoSide.LEFT)) }
                )
                PhotoTile(
                    modifier = Modifier.weight(1f),
                    label = "Sağ",
                    imageUri = state.uriFor(VehiclePhotosContract.PhotoSide.RIGHT),
                    isUploading = state.uploadingSide == VehiclePhotosContract.PhotoSide.RIGHT,
                    onClick = { onIntent(VehiclePhotosContract.Intent.CapturePhotoClicked(VehiclePhotosContract.PhotoSide.RIGHT)) }
                )
            }

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.WarningAmber,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Hasarları net çek — teslim sonrası anlaşmazlığı önler.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(Modifier.weight(1f))

        Surface(tonalElevation = 4.dp, color = MaterialTheme.colorScheme.surface) {
            Button(
                onClick = { onIntent(VehiclePhotosContract.Intent.StartRentalClicked) },
                enabled = state.canStart,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .height(52.dp)
            ) {
                if (state.isStarting) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(state.startButtonLabel)
                }
            }
        }
    }
}

@Composable
private fun PhotoTile(
    label: String,
    imageUri: Uri?,
    isUploading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1.2f)
            .clip(RoundedCornerShape(16.dp))
            .then(
                if (imageUri == null) {
                    Modifier.border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                } else {
                    Modifier
                }
            )
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .clickable(onClick = onClick)
    ) {
        if (imageUri != null) {
            AsyncImage(
                model = imageUri,
                contentDescription = label,
                modifier = Modifier.fillMaxSize()
            )
        }

        Surface(
            modifier = Modifier.align(Alignment.TopStart).padding(8.dp),
            shape = RoundedCornerShape(6.dp),
            color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.6f)
        ) {
            Text(
                text = label,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                style = MaterialTheme.typography.labelSmall,
                color = Color.White
            )
        }

        if (imageUri != null) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF43A047)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        } else if (isUploading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center).size(28.dp), strokeWidth = 2.dp)
        } else {
            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.PhotoCamera,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(Modifier.height(6.dp))
                Text(text = "Fotoğraf çek", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

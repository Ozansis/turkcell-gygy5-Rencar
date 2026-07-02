package com.turkcell.rencar_pair.feature.auth.license

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.turkcell.rencar_pair.ui.components.StepIndicator

@Composable
fun LicenseScreen(
    state: LicenseContract.State,
    onIntent: (LicenseContract.Intent) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            BackButton(onClick = { onIntent(LicenseContract.Intent.NavigateBack) })

            Spacer(Modifier.height(24.dp))

            Text(
                text  = "Ehliyet doğrulama",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text  = "Kiralamadan önce tek seferlik",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(24.dp))

            StepIndicator(currentStep = 1)

            Spacer(Modifier.height(28.dp))

            UploadStatusRow(
                label      = "Ehliyet ön yüz",
                isUploaded = state.isFrontUploaded
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text       = "Ehliyet arka yüz",
                style      = MaterialTheme.typography.labelLarge,
                color      = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(Modifier.height(12.dp))

            UploadDropzone(
                isUploaded = state.isBackUploaded,
                onClick    = { onIntent(LicenseContract.Intent.UploadBackSide) }
            )

            Spacer(Modifier.height(20.dp))

            InfoBanner()

            Spacer(Modifier.weight(1f))

            Button(
                onClick   = { onIntent(LicenseContract.Intent.Continue) },
                modifier  = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape     = RoundedCornerShape(16.dp),
                colors    = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text  = "Devam Et",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White
                )
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun BackButton(onClick: () -> Unit) {
    IconButton(
        onClick  = onClick,
        modifier = Modifier
            .size(40.dp)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = CircleShape
            ),
        colors = IconButtonDefaults.iconButtonColors(
            contentColor = MaterialTheme.colorScheme.onBackground
        )
    ) {
        Icon(
            imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Geri",
            modifier           = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun UploadStatusRow(
    label: String,
    isUploaded: Boolean
) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text(
            text       = label,
            style      = MaterialTheme.typography.labelLarge,
            color      = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold
        )

        if (isUploaded) {
            UploadedBadge()
        }
    }
}

@Composable
private fun UploadedBadge() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier          = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0xFF16A34A))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Icon(
            imageVector        = Icons.Filled.Check,
            contentDescription = null,
            tint               = Color.White,
            modifier           = Modifier.size(14.dp)
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text       = "Yüklendi",
            style      = MaterialTheme.typography.labelMedium,
            color      = Color.White,
            fontWeight = FontWeight.SemiBold
        )
    }
}

@Composable
private fun UploadDropzone(
    isUploaded: Boolean,
    onClick: () -> Unit
) {
    if (isUploaded) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            UploadedBadge()
        }
        return
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                contentAlignment = Alignment.Center,
                modifier         = Modifier
                    .size(40.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
            ) {
                Icon(
                    imageVector        = Icons.Filled.CameraAlt,
                    contentDescription = null,
                    tint               = Color.White,
                    modifier           = Modifier.size(20.dp)
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text  = "Arka yüzü çek veya yükle",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun InfoBanner() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector        = Icons.Filled.Info,
            contentDescription = null,
            tint               = MaterialTheme.colorScheme.primary,
            modifier           = Modifier.size(18.dp)
        )

        Spacer(Modifier.width(8.dp))

        val annotatedText = buildAnnotatedString {
            append("Bilgilerin güvenle saklanır. Doğrulama genelde ")
            withStyle(SpanStyle(fontWeight = FontWeight.SemiBold)) {
                append("birkaç dakika")
            }
            append(" sürer.")
        }

        Text(
            text  = annotatedText,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

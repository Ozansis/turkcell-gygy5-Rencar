package com.turkcell.rencar_pair.feature.auth.confirmation

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.CheckCircle
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.turkcell.rencar_pair.ui.components.StepIndicator

@Composable
fun ConfirmationScreen(
    state: ConfirmationContract.State,
    onIntent: (ConfirmationContract.Intent) -> Unit
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

            BackButton(onClick = { onIntent(ConfirmationContract.Intent.NavigateBack) })

            Spacer(Modifier.height(24.dp))

            Text(
                text  = "Onay",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text  = "Bilgilerin inceleniyor",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(24.dp))

            StepIndicator(currentStep = 3)

            Spacer(Modifier.height(40.dp))

            ReviewStatus()

            Spacer(Modifier.height(20.dp))

            InfoBanner()

            Spacer(Modifier.weight(1f))

            Button(
                onClick   = { onIntent(ConfirmationContract.Intent.Continue) },
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
private fun ReviewStatus() {
    Column(
        modifier            = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier         = Modifier
                .size(72.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                )
        ) {
            Icon(
                imageVector        = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint               = Color.White,
                modifier           = Modifier.size(36.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text      = "Ehliyet ve selfie bilgilerin alındı",
            style     = MaterialTheme.typography.labelLarge,
            color     = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center
        )
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

        Text(
            text  = "Doğrulama genelde birkaç dakika sürer. Bu süre boyunca uygulamayı kullanmaya devam edebilirsin.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

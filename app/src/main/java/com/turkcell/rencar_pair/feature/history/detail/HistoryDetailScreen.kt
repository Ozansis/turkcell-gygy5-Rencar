package com.turkcell.rencar_pair.feature.history.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun HistoryDetailScreen(
    state: HistoryDetailContract.State,
    onIntent: (HistoryDetailContract.Intent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        DetailTopBar(onBackClick = { onIntent(HistoryDetailContract.Intent.NavigateBack) })

        when {
            state.isLoading -> LoadingState()
            state.errorMessage != null -> EmptyState(message = state.errorMessage)
            else -> DetailContent(state = state)
        }
    }
}

@Composable
private fun DetailTopBar(onBackClick: () -> Unit) {
    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .padding(top = 12.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
        }
        Spacer(Modifier.width(4.dp))
        Text(
            text  = "Yolculuk Detayı",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier         = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
private fun EmptyState(message: String) {
    Box(
        modifier         = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text      = message,
            style     = MaterialTheme.typography.bodyMedium,
            color     = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun DetailContent(state: HistoryDetailContract.State) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Text(
            text       = state.vehicleTitle,
            style      = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color      = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text  = "${state.plate} · ${state.planLabel}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(16.dp))

        InfoCard {
            InfoRow("Durum", state.statusLabel)
            InfoRow("Başlangıç", state.formattedStartedAt)
            InfoRow("Bitiş", state.formattedEndedAt)
            InfoRow("Süre", state.formattedDuration)
            InfoRow("Mesafe", state.formattedDistance)
        }

        Spacer(Modifier.height(12.dp))

        InfoCard {
            InfoRow("Başlangıç ücreti", state.formattedStartFee)
            InfoRow("Servis ücreti", state.formattedServiceFee)
            InfoRow("İndirim", state.formattedDiscount)
            HorizontalDivider(Modifier.padding(vertical = 8.dp))
            InfoRow("Toplam tutar", state.formattedTotalPrice, emphasize = true)
            InfoRow("Ödeme durumu", state.paymentStatus)
        }
    }
}

@Composable
private fun InfoCard(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier       = Modifier.fillMaxWidth(),
        shape          = RoundedCornerShape(16.dp),
        color          = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content  = content
        )
    }
}

@Composable
private fun InfoRow(label: String, value: String, emphasize: Boolean = false) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text  = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text       = value,
            style      = MaterialTheme.typography.bodyMedium,
            fontWeight = if (emphasize) FontWeight.Bold else FontWeight.Normal,
            color      = MaterialTheme.colorScheme.onSurface
        )
    }
}

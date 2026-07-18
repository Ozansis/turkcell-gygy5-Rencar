package com.turkcell.rencar_pair.feature.history

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun HistoryScreen(
    state: HistoryContract.State,
    onIntent: (HistoryContract.Intent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        HistoryHeader(
            tripCount      = state.monthlyTripCount,
            totalSpending  = state.monthlySpending
        )

        when {
            state.isLoading -> LoadingState()
            state.rentals.isEmpty() -> EmptyState(message = state.errorMessage ?: "Henüz bir kiralaman yok.")
            else -> {
                LazyColumn(
                    contentPadding      = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(state.rentals, key = { it.id }) { rental ->
                        RentalCard(
                            rental  = rental,
                            onClick = { onIntent(HistoryContract.Intent.RentalSelected(rental.id)) }
                        )
                    }
                    item { Spacer(Modifier.height(8.dp)) }
                }
            }
        }
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
private fun HistoryHeader(
    tripCount: Int,
    totalSpending: Double
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 28.dp, bottom = 4.dp)
    ) {
        Text(
            text  = "Kiralamalarım",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text  = "Bu ay $tripCount yolculuk · ₺${Math.round(totalSpending)} harcama",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun RentalCard(
    rental: RentalRecord,
    onClick: () -> Unit
) {
    Surface(
        modifier       = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        shape          = RoundedCornerShape(16.dp),
        color          = MaterialTheme.colorScheme.surface,
        shadowElevation = 2.dp,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RouteThumbnail(rental = rental)

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text(
                        text       = "${rental.carBrand} ${rental.carModel}",
                        style      = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color      = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text       = formatPrice(rental.totalPrice),
                        style      = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color      = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(Modifier.height(4.dp))

                Text(
                    text  = rental.dateLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(8.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    StatLabel("${rental.durationMinutes} dk")
                    StatDot()
                    StatLabel(formatDistance(rental.distanceKm))
                }
            }
        }
    }
}

private val ROUTE_START_COLOR = Color(0xFF34C759)
private val ROUTE_END_COLOR = Color(0xFF4285F4)

// API geçmiş kiralamalar için gerçek GPS/rota verisi döndürmüyor; bu yüzden stilize bir önizleme çizilir (varyant, id'den türetilip sabit tutulur).
@Composable
private fun RouteThumbnail(rental: RentalRecord) {
    val variant = Math.floorMod(rental.id.hashCode(), 4)

    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        val gridColor = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)

        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Sokak dokusu izlenimi veren birkaç ince ızgara çizgisi.
            drawLine(gridColor, Offset(w * 0.35f, 0f), Offset(w * 0.35f, h), strokeWidth = 1.5f)
            drawLine(gridColor, Offset(0f, h * 0.65f), Offset(w, h * 0.65f), strokeWidth = 1.5f)

            val (start, end, control) = when (variant) {
                0 -> Triple(Offset(w * 0.18f, h * 0.82f), Offset(w * 0.82f, h * 0.18f), Offset(w * 0.85f, h * 0.75f))
                1 -> Triple(Offset(w * 0.18f, h * 0.18f), Offset(w * 0.82f, h * 0.82f), Offset(w * 0.85f, h * 0.2f))
                2 -> Triple(Offset(w * 0.2f, h * 0.5f), Offset(w * 0.85f, h * 0.15f), Offset(w * 0.4f, h * 0.1f))
                else -> Triple(Offset(w * 0.15f, h * 0.2f), Offset(w * 0.8f, h * 0.55f), Offset(w * 0.75f, h * 0.1f))
            }

            val path = Path().apply {
                moveTo(start.x, start.y)
                quadraticTo(control.x, control.y, end.x, end.y)
            }
            drawPath(path, color = ROUTE_END_COLOR, style = Stroke(width = 5f))

            drawCircle(ROUTE_START_COLOR, radius = 5f, center = start)
            drawCircle(Color.White, radius = 6.5f, center = end, style = Stroke(width = 2f))
            drawCircle(ROUTE_END_COLOR, radius = 5f, center = end)
        }
    }
}

@Composable
private fun StatLabel(text: String) {
    Text(
        text  = text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun StatDot() {
    Text(
        text  = " · ",
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

private fun formatPrice(price: Double): String {
    val cents    = Math.round(price * 100)
    val intPart  = cents / 100
    val decPart  = cents % 100
    return "₺$intPart,${decPart.toString().padStart(2, '0')}"
}

private fun formatDistance(km: Double): String {
    val tenths  = Math.round(km * 10)
    val intPart = tenths / 10
    val decPart = tenths % 10
    return "$intPart,$decPart km"
}

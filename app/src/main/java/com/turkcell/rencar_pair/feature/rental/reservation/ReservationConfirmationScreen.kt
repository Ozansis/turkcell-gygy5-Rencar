package com.turkcell.rencar_pair.feature.rental.reservation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
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

@Composable
fun ReservationConfirmationScreen(
    state: ReservationConfirmationContract.State,
    onIntent: (ReservationConfirmationContract.Intent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        TopBar(onBack = { onIntent(ReservationConfirmationContract.Intent.NavigateBack) })

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            VehicleSummaryCard(state)

            Spacer(Modifier.height(20.dp))

            Text(
                text = "Kiralama planı",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PlanOption(
                    modifier = Modifier.weight(1f),
                    label = "Dakikalık",
                    priceLabel = state.formattedPricePerMinute,
                    isSelected = state.selectedPlan == ReservationConfirmationContract.RentalPlan.PER_MINUTE,
                    onClick = { onIntent(ReservationConfirmationContract.Intent.PlanSelected(ReservationConfirmationContract.RentalPlan.PER_MINUTE)) }
                )
                PlanOption(
                    modifier = Modifier.weight(1f),
                    label = "Saatlik",
                    priceLabel = state.formattedPricePerHour,
                    isSelected = state.selectedPlan == ReservationConfirmationContract.RentalPlan.HOURLY,
                    onClick = { onIntent(ReservationConfirmationContract.Intent.PlanSelected(ReservationConfirmationContract.RentalPlan.HOURLY)) }
                )
                PlanOption(
                    modifier = Modifier.weight(1f),
                    label = "Günlük",
                    priceLabel = state.formattedPricePerDay,
                    isSelected = state.selectedPlan == ReservationConfirmationContract.RentalPlan.DAILY,
                    onClick = { onIntent(ReservationConfirmationContract.Intent.PlanSelected(ReservationConfirmationContract.RentalPlan.DAILY)) }
                )
            }

            Spacer(Modifier.height(20.dp))

            FeeBreakdownCard(state)

            Spacer(Modifier.height(20.dp))

            Row(verticalAlignment = Alignment.Top) {
                Checkbox(
                    checked = state.isTermsAccepted,
                    onCheckedChange = { onIntent(ReservationConfirmationContract.Intent.TermsToggled) }
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "Kullanım şartlarını ve kasko/sigorta koşullarını okudum, onaylıyorum.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }

            Spacer(Modifier.height(16.dp))
        }

        Surface(
            tonalElevation = 4.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Button(
                onClick = { onIntent(ReservationConfirmationContract.Intent.CompleteReservationClicked) },
                enabled = state.canComplete,
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .height(52.dp)
            ) {
                if (state.isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(20.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Rezervasyonu Tamamla")
                }
            }
        }
    }
}

@Composable
private fun TopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
        }
        Spacer(Modifier.width(12.dp))
        Text(
            text = "Rezervasyon Onayı",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun VehicleSummaryCard(state: ReservationConfirmationContract.State) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "${state.brand} ${state.model}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "${state.plate} · ${state.transmission} · ${state.seatCount} kişi",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = "Yakıt %${state.fuelPercent}",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
private fun PlanOption(
    label: String,
    priceLabel: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
    Surface(
        modifier = modifier
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = BorderStroke(2.dp, borderColor)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(text = priceLabel, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun FeeBreakdownCard(state: ReservationConfirmationContract.State) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            FeeRow(label = "Ücretsiz rezervasyon", value = "15 dk")
            Spacer(Modifier.height(10.dp))
            FeeRow(label = "Başlangıç ücreti", value = state.formattedStartFee)
            Spacer(Modifier.height(10.dp))
            FeeRow(
                label = "Tahmini ücret (${state.previewMinutes} dk)",
                value = state.formattedEstimatedTotal,
                emphasize = true
            )
        }
    }
}

@Composable
private fun FeeRow(label: String, value: String, emphasize: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(
            text = value,
            style = if (emphasize) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            fontWeight = if (emphasize) FontWeight.Bold else FontWeight.Normal
        )
    }
}

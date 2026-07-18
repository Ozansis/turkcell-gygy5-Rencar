package com.turkcell.rencar_pair.feature.maps.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.EventSeat
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.turkcell.rencar_pair.feature.maps.GeoPoint
import com.turkcell.rencar_pair.feature.maps.NearbyVehicle
import com.turkcell.rencar_pair.feature.maps.RencarMap
import org.maplibre.android.geometry.LatLng

@Composable
fun VehicleDetailScreen(
    state: VehicleDetailContract.State,
    onIntent: (VehicleDetailContract.Intent) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        val vehicleLocation = state.vehicleLocation
        if (vehicleLocation != null) {
            RencarMap(
                myLocation = state.myLocation,
                vehicles = listOf(state.toMapVehicle(vehicleLocation)),
                modifier = Modifier.fillMaxSize(),
                initialCenter = vehicleLocation.toLatLng(),
                initialZoom = 15.0
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )
        }

        IconButton(
            onClick = { onIntent(VehicleDetailContract.Intent.NavigateBack) },
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(16.dp)
                .background(MaterialTheme.colorScheme.surface, CircleShape)
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Geri")
        }

        VehicleDetailSheet(
            state = state,
            onIntent = onIntent,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun VehicleDetailSheet(
    state: VehicleDetailContract.State,
    onIntent: (VehicleDetailContract.Intent) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth().navigationBarsPadding(),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        tonalElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .size(width = 36.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colorScheme.outlineVariant)
            )

            Spacer(Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "${state.brand} ${state.model}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.width(8.dp))
                StatusBadge(label = state.statusLabel, isPositive = state.canReserve)
            }

            Spacer(Modifier.height(4.dp))

            Text(
                text = "${state.plate} · ${state.distanceLabel}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.LocalGasStation,
                    label = "Yakıt",
                    value = "%${state.fuelPercent}",
                    sublabel = state.tankLabel,
                    fuelPercent = state.fuelPercent
                )
                InfoCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Route,
                    label = "Menzil",
                    value = "~${state.rangeKm} km",
                    sublabel = state.tankLabel
                )
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Settings,
                    label = "Vites",
                    value = state.transmission
                )
                InfoCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.EventSeat,
                    label = "Koltuk",
                    value = "${state.seatCount} kişi"
                )
            }

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = state.formattedPricePerMinute,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = state.formattedPricePerHour,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { onIntent(VehicleDetailContract.Intent.ReserveClicked) },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    enabled = state.isUnlocked,
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Rezerve Et")
                }
                Button(
                    onClick = { onIntent(VehicleDetailContract.Intent.UnlockClicked) },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    enabled = state.canUnlock,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Kilidi Aç")
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(label: String, isPositive: Boolean) {
    val containerColor = if (isPositive) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.errorContainer
    }
    val contentColor = if (isPositive) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onErrorContainer
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = containerColor
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = contentColor
        )
    }
}

@Composable
private fun InfoCard(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    sublabel: String? = null,
    fuelPercent: Int? = null
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            if (fuelPercent != null) {
                Spacer(Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { fuelPercent / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = fuelBarColor(fuelPercent),
                    trackColor = MaterialTheme.colorScheme.outlineVariant
                )
            }
            if (sublabel != null) {
                Spacer(Modifier.height(if (fuelPercent != null) 4.dp else 0.dp))
                Text(
                    text = sublabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun fuelBarColor(fuelPercent: Int): Color = when {
    fuelPercent < 20 -> Color(0xFFE53935)
    fuelPercent < 50 -> Color(0xFFFB8C00)
    else             -> Color(0xFF43A047)
}

private fun VehicleDetailContract.State.toMapVehicle(location: GeoPoint): NearbyVehicle = NearbyVehicle(
    id = vehicleId,
    plate = plate,
    brand = brand,
    model = model,
    type = type,
    status = status,
    pricePerDay = pricePerDay,
    location = location,
    distanceMeters = distanceMeters,
    fuelPercent = fuelPercent,
    tankLabel = tankLabel,
    rangeKm = rangeKm,
    transmission = transmission,
    seatCount = seatCount,
    pricePerMinute = pricePerMinute,
    pricePerHour = pricePerHour
)

private fun GeoPoint.toLatLng(): LatLng = LatLng(latitude, longitude)

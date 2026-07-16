package com.turkcell.rencar_pair.feature.rental.active

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.turkcell.rencar_pair.feature.maps.GeoPoint
import com.turkcell.rencar_pair.feature.maps.NearbyVehicle
import com.turkcell.rencar_pair.feature.maps.RencarMap
import com.turkcell.rencar_pair.feature.maps.VehicleStatus
import com.turkcell.rencar_pair.feature.maps.VehicleType
import org.maplibre.android.geometry.LatLng

@Composable
fun ActiveRentalScreen(
    state: ActiveRentalContract.State,
    onIntent: (ActiveRentalContract.Intent) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        val vehicleLocation = state.vehicleLocation
        if (vehicleLocation != null) {
            RencarMap(
                myLocation = null,
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

        StatusPill(
            label = state.statusLabel,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp)
        )

        ActiveRentalSheet(
            state = state,
            onIntent = onIntent,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun StatusPill(label: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        tonalElevation = 4.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF43A047))
            )
            Spacer(Modifier.width(8.dp))
            Text(text = label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ActiveRentalSheet(
    state: ActiveRentalContract.State,
    onIntent: (ActiveRentalContract.Intent) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        tonalElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(width = 36.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colorScheme.outlineVariant)
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Geçen süre",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = state.elapsedLabel,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                InfoCard(modifier = Modifier.weight(1f), label = "Anlık ücret", value = state.formattedCurrentCost)
                InfoCard(modifier = Modifier.weight(1f), label = "Mesafe", value = state.formattedDistanceKm)
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { onIntent(ActiveRentalContract.Intent.LockUnlockClicked) },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Kilitle / Aç")
                }
                Button(
                    onClick = { onIntent(ActiveRentalContract.Intent.FinishRentalClicked) },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    enabled = !state.isFinishing,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    if (state.isFinishing) {
                        CircularProgressIndicator(
                            modifier = Modifier.height(20.dp),
                            color = MaterialTheme.colorScheme.onError,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Kiralamayı Bitir")
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoCard(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

private fun ActiveRentalContract.State.toMapVehicle(location: GeoPoint): NearbyVehicle = NearbyVehicle(
    id = vehicleId,
    plate = "",
    brand = brand,
    model = model,
    type = VehicleType.SEDAN,
    status = VehicleStatus.RENTED,
    pricePerDay = vehiclePricePerDay,
    location = location,
    distanceMeters = 0,
    fuelPercent = 0,
    tankLabel = "",
    rangeKm = 0,
    transmission = "",
    seatCount = 0,
    pricePerMinute = 0.0,
    pricePerHour = 0.0
)

private fun GeoPoint.toLatLng(): LatLng = LatLng(latitude, longitude)

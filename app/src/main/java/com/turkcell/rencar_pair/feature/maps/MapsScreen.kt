package com.turkcell.rencar_pair.feature.maps

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun MapsScreen(
    state: MapsContract.State,
    mapController: RencarMapController,
    onIntent: (MapsContract.Intent) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        RencarMap(
            myLocation = state.myLocation,
            vehicles = state.filteredVehicles,
            onVehicleClick = { vehicleId -> onIntent(MapsContract.Intent.VehicleMarkerClicked(vehicleId)) },
            modifier = Modifier.fillMaxSize(),
            controller = mapController
        )

        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SearchBar(
                query = state.searchQuery,
                onQueryChange = { onIntent(MapsContract.Intent.SearchQueryChanged(it)) },
                onTuneClick = { onIntent(MapsContract.Intent.FilterPanelToggled) },
                hasActiveFilters = state.activeExtraFilterCount > 0
            )
            if (state.activeRentalId != null) {
                ActiveRentalBanner(
                    vehicleLabel = state.activeRentalVehicleLabel,
                    onClick = { onIntent(MapsContract.Intent.ActiveRentalBannerClicked) }
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 296.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ZoomButton(icon = Icons.Default.Add, contentDescription = "Yakınlaştır", onClick = { mapController.zoomIn() })
            ZoomButton(icon = Icons.Default.Remove, contentDescription = "Uzaklaştır", onClick = { mapController.zoomOut() })
        }

        FloatingActionButton(
            onClick = { onIntent(MapsContract.Intent.RecenterClicked) },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 232.dp),
            shape = CircleShape,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Default.MyLocation, contentDescription = "Konumuma git")
        }

        NearbyVehiclesSheet(
            state = state,
            onIntent = onIntent,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun ZoomButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onClick,
        modifier = modifier.size(40.dp),
        shape = CircleShape,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.primary
    ) {
        Icon(icon, contentDescription = contentDescription)
    }
}

@Composable
private fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onTuneClick: () -> Unit,
    hasActiveFilters: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 4.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.width(8.dp))
                Box(modifier = Modifier.weight(1f)) {
                    if (query.isEmpty()) {
                        Text(
                            text = "Marka, model veya plaka ara",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    BasicTextField(
                        value = query,
                        onValueChange = onQueryChange,
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface
                        ),
                        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable(onClick = onTuneClick)
                    .padding(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = "Filtrele",
                    tint = if (hasActiveFilters) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (hasActiveFilters) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                }
            }
        }
    }
}

@Composable
private fun ActiveRentalBanner(
    vehicleLabel: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 4.dp,
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Aktif yolculuğunuz var",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = vehicleLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Button(onClick = onClick, shape = RoundedCornerShape(10.dp)) {
                Text("Devam Et")
            }
        }
    }
}

@Composable
private fun NearbyVehiclesSheet(
    state: MapsContract.State,
    onIntent: (MapsContract.Intent) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Yakınında ${state.nearbyVehicleCount} araç",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { onIntent(MapsContract.Intent.FilterPanelToggled) }) {
                    Icon(Icons.Default.Tune, contentDescription = "Filtrele")
                }
            }

            Spacer(Modifier.height(12.dp))

            VehicleTypeFilterRow(
                selectedType = state.selectedType,
                onTypeSelected = { type -> onIntent(MapsContract.Intent.TypeFilterSelected(type)) }
            )

            if (state.isFilterPanelExpanded) {
                Spacer(Modifier.height(12.dp))
                ExtraFiltersPanel(state = state, onIntent = onIntent)
            }

            Spacer(Modifier.height(16.dp))

            if (state.filteredVehicles.isEmpty()) {
                EmptyFilterResult()
                Spacer(Modifier.height(16.dp))
            }

            Button(
                onClick = { onIntent(MapsContract.Intent.FindNearestClicked) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = state.myLocation != null && state.filteredVehicles.isNotEmpty(),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(Icons.Default.LocationOn, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("En Yakın Aracı Bul")
            }
        }
    }
}

@Composable
private fun EmptyFilterResult(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(28.dp)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Bu özelliklerde araç yok, tekrar deneyin",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun VehicleTypeFilterRow(
    selectedType: VehicleType?,
    onTypeSelected: (VehicleType?) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SelectableChip(label = "Tümü", selected = selectedType == null, onClick = { onTypeSelected(null) })
        VehicleType.entries.forEach { type ->
            SelectableChip(label = type.label(), selected = selectedType == type, onClick = { onTypeSelected(type) })
        }
    }
}

private fun VehicleType.label(): String = when (this) {
    VehicleType.SEDAN     -> "Sedan"
    VehicleType.SUV       -> "SUV"
    VehicleType.HATCHBACK -> "Hatchback"
    VehicleType.STATION   -> "Station"
    VehicleType.MINIVAN   -> "Minivan"
}

private val MIN_SEATS_FILTER_OPTIONS = listOf(2, 4, 5, 7)

@Composable
private fun ExtraFiltersPanel(
    state: MapsContract.State,
    onIntent: (MapsContract.Intent) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        FilterSectionLabel("Segment")
        Spacer(Modifier.height(6.dp))
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SelectableChip(
                label = "Tümü",
                selected = state.selectedSegment == null,
                onClick = { onIntent(MapsContract.Intent.SegmentFilterSelected(null)) }
            )
            VehicleSegment.entries.forEach { segment ->
                SelectableChip(
                    label = segment.label(),
                    selected = state.selectedSegment == segment,
                    onClick = { onIntent(MapsContract.Intent.SegmentFilterSelected(segment)) }
                )
            }
        }

        if (state.availableTransmissions.isNotEmpty()) {
            Spacer(Modifier.height(12.dp))
            FilterSectionLabel("Vites")
            Spacer(Modifier.height(6.dp))
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SelectableChip(
                    label = "Tümü",
                    selected = state.selectedTransmission == null,
                    onClick = { onIntent(MapsContract.Intent.TransmissionFilterSelected(null)) }
                )
                state.availableTransmissions.forEach { transmission ->
                    SelectableChip(
                        label = transmission,
                        selected = state.selectedTransmission == transmission,
                        onClick = { onIntent(MapsContract.Intent.TransmissionFilterSelected(transmission)) }
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))
        FilterSectionLabel("Min. koltuk")
        Spacer(Modifier.height(6.dp))
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SelectableChip(
                label = "Tümü",
                selected = state.selectedMinSeats == null,
                onClick = { onIntent(MapsContract.Intent.MinSeatsFilterSelected(null)) }
            )
            MIN_SEATS_FILTER_OPTIONS.forEach { seats ->
                SelectableChip(
                    label = "$seats+",
                    selected = state.selectedMinSeats == seats,
                    onClick = { onIntent(MapsContract.Intent.MinSeatsFilterSelected(seats)) }
                )
            }
        }

        if (state.activeExtraFilterCount > 0) {
            Spacer(Modifier.height(8.dp))
            TextButton(onClick = { onIntent(MapsContract.Intent.FiltersCleared) }) {
                Text("Filtreleri temizle")
            }
        }
    }
}

@Composable
private fun FilterSectionLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun SelectableChip(label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = Color.White
        )
    )
}

private fun VehicleSegment.label(): String = when (this) {
    VehicleSegment.ECONOMY -> "Ekonomik"
    VehicleSegment.COMFORT -> "Konfor"
    VehicleSegment.SUV     -> "SUV"
}

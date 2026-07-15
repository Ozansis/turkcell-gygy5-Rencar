package com.turkcell.rencar_pair.feature.maps.detail

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.location.LocationServices
import com.turkcell.rencar_pair.feature.maps.GeoPoint

@Composable
fun VehicleDetailRoute(
    vehicleId: String,
    distanceMeters: Int,
    onNavigateBack: () -> Unit = {},
    viewModel: VehicleDetailViewModel = hiltViewModel<VehicleDetailViewModel, VehicleDetailViewModel.Factory>(
        creationCallback = { factory -> factory.create(vehicleId, distanceMeters) }
    )
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    // Sessiz kontrol: izin diyaloğu bu ekranda AÇILMAZ, yalnızca mevcut izin durumu okunur.
    // İzin varsa cihazın bilinen son konumu tek seferlik alınır (sürekli abonelik kurulmaz).
    val hasLocationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            fetchLastLocation(context) { location ->
                viewModel.onIntent(VehicleDetailContract.Intent.LocationChanged(location))
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                VehicleDetailContract.Effect.NavigateBack             -> onNavigateBack()
                VehicleDetailContract.Effect.ShowReservationConfirmed -> Unit
                VehicleDetailContract.Effect.ShowUnlockConfirmed      -> Unit
                is VehicleDetailContract.Effect.ShowError             -> Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    VehicleDetailScreen(state = state, onIntent = viewModel::onIntent)
}

@SuppressLint("MissingPermission")
private fun fetchLastLocation(context: Context, onLocation: (GeoPoint) -> Unit) {
    LocationServices.getFusedLocationProviderClient(context).lastLocation
        .addOnSuccessListener { location ->
            if (location != null) onLocation(GeoPoint(location.latitude, location.longitude))
        }
}

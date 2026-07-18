package com.turkcell.rencar_pair.feature.maps

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent as AndroidIntent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Looper
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import org.maplibre.android.geometry.LatLng

@Composable
fun MapsRoute(
    onNavigateToVehicleDetail: (String, Int) -> Unit = { _, _ -> },
    onNavigateToActiveRental: (String) -> Unit = {},
    onLocationPermissionStatusChanged: (Boolean) -> Unit = {},
    permissionRequestTrigger: Int = 0,
    viewModel: MapsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.state.collectAsState()
    val mapController = rememberRencarMapController()

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        val granted = result[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            result[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        hasLocationPermission = granted
        viewModel.onIntent(
            if (granted) MapsContract.Intent.LocationPermissionGranted
            else MapsContract.Intent.LocationPermissionDenied
        )
    }

    val enableLocationServicesLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) {
        // Sonuç kabul/iptal fark etmez; gerçek durumu sistemden tekrar sorgula.
        checkLocationSettings(context) { enabled, _ ->
            viewModel.onIntent(
                if (enabled) MapsContract.Intent.LocationServicesEnabled
                else MapsContract.Intent.LocationServicesDisabled
            )
        }
    }

    // İzin daha önce verilmemişse ekran, onay verilene kadar MapsScreen'deki
    // engelleyici izin ekranında kalır (bkz. MapsScreen.LocationRequiredOverlay);
    // burada sistem diyaloğu yalnızca ilk denemede otomatik açılır.
    LaunchedEffect(Unit) {
        if (hasLocationPermission) {
            viewModel.onIntent(MapsContract.Intent.LocationPermissionGranted)
        } else {
            permissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    // İzin durumu her değiştiğinde üst bileşene (MainScaffold) bildirilir.
    LaunchedEffect(hasLocationPermission) {
        onLocationPermissionStatusChanged(hasLocationPermission)
    }

    // İzin verilir verilmez konum servisinin (GPS/Ağ) fiilen açık olup olmadığı kontrol edilir;
    // kapalıysa sistemin "Konumu Aç" diyaloğu uygulama içinden tetiklenir — kullanıcı artık
    // ayarlara gidip elle açmak zorunda kalmaz.
    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            checkLocationSettings(context) { enabled, resolvable ->
                viewModel.onIntent(
                    if (enabled) MapsContract.Intent.LocationServicesEnabled
                    else MapsContract.Intent.LocationServicesDisabled
                )
                if (!enabled) resolvable?.let { enableLocationServicesLauncher.launch(it) }
            }
        }
    }

    // Üst bileşen, izinsiz sekme geçişi engellendiğinde bu tetikleyiciyi artırarak
    // sistem izin diyaloğunun tekrar açılmasını ister.
    LaunchedEffect(permissionRequestTrigger) {
        if (permissionRequestTrigger > 0 && !hasLocationPermission) {
            permissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    val fusedClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // Kullanıcı konum servisini uygulama dışında (hızlı ayarlar/sistem ayarları) açıp
    // kapattığında ekranın anında haberdar olması için sistem yayını dinlenir — böylece
    // "telefondan konumu açman gerekiyor" sonrası uygulamaya dönünce elle yenileme gerekmez.
    DisposableEffect(hasLocationPermission) {
        if (!hasLocationPermission) return@DisposableEffect onDispose { }

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(receiverContext: Context, intent: AndroidIntent) {
                checkLocationSettings(context) { enabled, _ ->
                    viewModel.onIntent(
                        if (enabled) MapsContract.Intent.LocationServicesEnabled
                        else MapsContract.Intent.LocationServicesDisabled
                    )
                }
            }
        }
        ContextCompat.registerReceiver(
            context,
            receiver,
            IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )

        onDispose { context.unregisterReceiver(receiver) }
    }

    DisposableEffect(hasLocationPermission) {
        if (!hasLocationPermission) return@DisposableEffect onDispose { }

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { loc ->
                    viewModel.onIntent(MapsContract.Intent.LocationChanged(GeoPoint(loc.latitude, loc.longitude)))
                }
            }
        }

        startLocationUpdates(fusedClient, callback)

        onDispose { fusedClient.removeLocationUpdates(callback) }
    }

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                MapsContract.Effect.RequestLocationRefresh -> {
                    if (hasLocationPermission) {
                        fetchCurrentLocation(fusedClient) { target ->
                            viewModel.onIntent(MapsContract.Intent.LocationChanged(target))
                            mapController.animateTo(LatLng(target.latitude, target.longitude), zoom = 14.0)
                        }
                    } else {
                        permissionLauncher.launch(
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                        )
                    }
                }
                MapsContract.Effect.RequestLocationPermission -> permissionLauncher.launch(
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
                )
                MapsContract.Effect.RequestEnableLocationServices -> checkLocationSettings(context) { _, resolvable ->
                    resolvable?.let { enableLocationServicesLauncher.launch(it) }
                }
                is MapsContract.Effect.NavigateToVehicleDetail -> onNavigateToVehicleDetail(effect.vehicleId, effect.distanceMeters)
                is MapsContract.Effect.NavigateToActiveRental  -> onNavigateToActiveRental(effect.rentalId)
                MapsContract.Effect.ShowLocationPermissionDeniedMessage -> Unit
                is MapsContract.Effect.ShowError -> Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    MapsScreen(
        state = state,
        mapController = mapController,
        onIntent = viewModel::onIntent
    )
}

private fun checkLocationSettings(
    context: Context,
    onResult: (enabled: Boolean, resolvable: IntentSenderRequest?) -> Unit
) {
    val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5_000L).build()
    val settingsRequest = LocationSettingsRequest.Builder().addLocationRequest(request).build()

    LocationServices.getSettingsClient(context).checkLocationSettings(settingsRequest)
        .addOnSuccessListener { onResult(true, null) }
        .addOnFailureListener { exception ->
            val resolvable = (exception as? ResolvableApiException)
                ?.let { IntentSenderRequest.Builder(it.resolution).build() }
            onResult(false, resolvable)
        }
}

@SuppressLint("MissingPermission")
private fun fetchCurrentLocation(
    fusedClient: FusedLocationProviderClient,
    onLocation: (GeoPoint) -> Unit
) {
    // Cache'lenmiş son konum yerine taze bir konum iste.
    fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, CancellationTokenSource().token)
        .addOnSuccessListener { location ->
            if (location != null) onLocation(GeoPoint(location.latitude, location.longitude))
        }
}

@SuppressLint("MissingPermission")
private fun startLocationUpdates(
    fusedClient: FusedLocationProviderClient,
    callback: LocationCallback
) {
    // 5sn aralıklarla konumu yenile.
    val request = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        5_000L
    ).setMinUpdateIntervalMillis(2_000L).build()

    fusedClient.lastLocation.addOnSuccessListener { location ->
        if (location != null) {
            callback.onLocationResult(LocationResult.create(listOf(location)))
        }
    }

    fusedClient.requestLocationUpdates(request, callback, Looper.getMainLooper())
}

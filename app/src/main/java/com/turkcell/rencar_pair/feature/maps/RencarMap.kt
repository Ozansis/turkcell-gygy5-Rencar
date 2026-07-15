package com.turkcell.rencar_pair.feature.maps

import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.gson.JsonObject
import com.turkcell.rencar_pair.R
import org.maplibre.android.MapLibre
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.camera.CameraUpdateFactory
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.geometry.LatLngBounds
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import org.maplibre.android.style.layers.CircleLayer
import org.maplibre.android.style.layers.PropertyFactory
import org.maplibre.android.style.layers.SymbolLayer
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.geojson.Feature
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.Point
import kotlin.math.roundToInt

val DEFAULT_CENTER: LatLng = LatLng(38.51740367746754, 27.161930350129918)

private val ME_MARKER_COLOR = Color.parseColor("#4285F4")
private val VEHICLE_MARKER_COLOR = Color.parseColor("#FB923C")

private const val VEHICLES_SOURCE_ID = "vehicles"
private const val VEHICLE_CIRCLE_LAYER_ID = "vehicle-circle-layer"
private const val VEHICLE_ICON_LAYER_ID = "vehicle-icon-layer"
private const val VEHICLE_ICON_IMAGE_ID = "vehicle-car-icon"

class RencarMapController internal constructor() {
    internal var map: MapLibreMap? = null

    fun animateTo(target: LatLng, zoom: Double = 10.0) {
        map?.animateCamera(CameraUpdateFactory.newLatLngZoom(target, zoom))
    }

    fun zoomIn() {
        map?.animateCamera(CameraUpdateFactory.zoomIn())
    }

    fun zoomOut() {
        map?.animateCamera(CameraUpdateFactory.zoomOut())
    }
}

@Composable
fun rememberRencarMapController(): RencarMapController = remember { RencarMapController() }

@Composable
fun RencarMap(
    myLocation: GeoPoint?,
    vehicles: List<NearbyVehicle> = emptyList(),
    onVehicleClick: (String) -> Unit = {},
    modifier: Modifier = Modifier,
    initialCenter: LatLng = DEFAULT_CENTER,
    initialZoom: Double = 10.0,
    controller: RencarMapController? = null
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val mapView = remember {
        MapLibre.getInstance(context) // Native motoru başlat.
        MapView(context).apply { onCreate(null) }
    }

    var mapAndStyle by remember { mutableStateOf<Pair<MapLibreMap, Style>?>(null) }

    // Sayfa açıldığında bu method çalışsın, kapandığında da bir şeyler yapıcam..
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START  -> mapView.onStart()
                Lifecycle.Event.ON_STOP   -> mapView.onStop()
                Lifecycle.Event.ON_PAUSE  -> mapView.onPause()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.onDestroy()
        }
    }

    // Harita ve stil kurulumu -> yalnızca bir kez çalışır.
    LaunchedEffect(Unit) {
        mapView.getMapAsync { map ->
            controller?.map = map

            map.cameraPosition = CameraPosition.Builder().target(initialCenter).zoom(initialZoom).build()

            map.addOnMapClickListener { point ->
                val screenPoint = map.projection.toScreenLocation(point)
                val features = map.queryRenderedFeatures(screenPoint, VEHICLE_CIRCLE_LAYER_ID)
                val vehicleId = features.firstOrNull()?.getStringProperty("id")
                if (vehicleId != null) {
                    onVehicleClick(vehicleId)
                    true
                } else {
                    false
                }
            }

            map.setStyle(Style.Builder().fromJson(OSM_STYLE_JSON)) { loaded ->
                // Araç marker ikonu -> vector drawable bitmap'e çevrilip stile kaydedilir.
                ContextCompat.getDrawable(context, R.drawable.ic_car_marker)?.let { drawable ->
                    loaded.addImage(VEHICLE_ICON_IMAGE_ID, drawable.toBitmap())
                }

                loaded.addSource(GeoJsonSource("me"))
                // Dış halka -> konumun etrafında yumuşak, yarı saydam bir halo.
                loaded.addLayer(
                    CircleLayer("me-halo-layer", "me").withProperties(
                        PropertyFactory.circleColor(ME_MARKER_COLOR),
                        PropertyFactory.circleRadius(20f),
                        PropertyFactory.circleOpacity(0.2f),
                        PropertyFactory.circleBlur(0.4f)
                    )
                )
                // İç nokta -> beyaz kenarlıklı mavi nokta ("my location" tarzı).
                loaded.addLayer(
                    CircleLayer("me-layer", "me").withProperties(
                        PropertyFactory.circleColor(ME_MARKER_COLOR),
                        PropertyFactory.circleRadius(9f),
                        PropertyFactory.circleStrokeColor(Color.WHITE),
                        PropertyFactory.circleStrokeWidth(3f)
                    )
                )

                loaded.addSource(GeoJsonSource(VEHICLES_SOURCE_ID))
                // Dış halka -> araç konumunun etrafında yumuşak, yarı saydam bir halo (uzaktan da fark edilsin).
                loaded.addLayer(
                    CircleLayer("vehicle-halo-layer", VEHICLES_SOURCE_ID).withProperties(
                        PropertyFactory.circleColor(VEHICLE_MARKER_COLOR),
                        PropertyFactory.circleRadius(22f),
                        PropertyFactory.circleOpacity(0.25f),
                        PropertyFactory.circleBlur(0.5f)
                    )
                )
                // Araç konumu -> fiyat baloncuğunun arka planı.
                loaded.addLayer(
                    CircleLayer(VEHICLE_CIRCLE_LAYER_ID, VEHICLES_SOURCE_ID).withProperties(
                        PropertyFactory.circleColor(VEHICLE_MARKER_COLOR),
                        PropertyFactory.circleRadius(17f),
                        PropertyFactory.circleStrokeColor(Color.WHITE),
                        PropertyFactory.circleStrokeWidth(3f)
                    )
                )
                // Araç simgesi -> baloncuğun ortasındaki araç ikonu.
                loaded.addLayer(
                    SymbolLayer(VEHICLE_ICON_LAYER_ID, VEHICLES_SOURCE_ID).withProperties(
                        PropertyFactory.iconImage(VEHICLE_ICON_IMAGE_ID),
                        PropertyFactory.iconSize(0.6f),
                        PropertyFactory.iconAllowOverlap(true),
                        PropertyFactory.iconIgnorePlacement(true)
                    )
                )
                mapAndStyle = map to loaded
            }
        }
    }

    // Konum noktası -> myLocation her değiştiğinde güncellenir, kamera oynamaz.
    LaunchedEffect(mapAndStyle, myLocation) {
        val (_, style) = mapAndStyle ?: return@LaunchedEffect
        updateMe(style, myLocation)
    }

    // Araç konumları -> vehicles listesi her değiştiğinde güncellenir.
    LaunchedEffect(mapAndStyle, vehicles) {
        val (_, style) = mapAndStyle ?: return@LaunchedEffect
        updateVehicles(style, vehicles)
    }

    // İlk açılışta kameranın tek seferlik konumlanması -> kullanıcı konumu geldiyse ona,
    // gelmediyse (izin reddedildiyse/GPS gecikirse) araç kümesinin tamamına odaklanır.
    // Aksi halde kamera DEFAULT_CENTER'da asılı kalır ve araçlar ekranın dışında görünmez olur.
    var hasFramedInitialView by remember { mutableStateOf(false) }
    LaunchedEffect(mapAndStyle, myLocation, vehicles) {
        if (hasFramedInitialView) return@LaunchedEffect
        val (map, _) = mapAndStyle ?: return@LaunchedEffect
        val points = buildList {
            myLocation?.let { add(it.toLatLng()) }
            addAll(vehicles.map { it.location.toLatLng() })
        }
        if (points.isEmpty()) return@LaunchedEffect

        hasFramedInitialView = true
        if (points.size == 1) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(points.first(), 14.0))
        } else {
            val bounds = LatLngBounds.Builder().includes(points).build()
            map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 120))
        }
    }

    // AndroidView -> Android ile @Composable köprüsü.
    AndroidView(factory = { mapView }, modifier = modifier)
}

private fun updateMe(style: Style, myLocation: GeoPoint?) {
    val source = style.getSourceAs<GeoJsonSource>("me") ?: return
    if (myLocation == null) {
        source.setGeoJson(FeatureCollection.fromFeatures(emptyList()))
    } else {
        source.setGeoJson(Point.fromLngLat(myLocation.longitude, myLocation.latitude))
    }
}

private fun updateVehicles(style: Style, vehicles: List<NearbyVehicle>) {
    val source = style.getSourceAs<GeoJsonSource>(VEHICLES_SOURCE_ID) ?: return
    val features = vehicles.map { vehicle ->
        val properties = JsonObject().apply {
            addProperty("id", vehicle.id)
            addProperty("price", "₺${vehicle.pricePerDay.roundToInt()}")
        }
        Feature.fromGeometry(
            Point.fromLngLat(vehicle.location.longitude, vehicle.location.latitude),
            properties
        )
    }
    source.setGeoJson(FeatureCollection.fromFeatures(features))
}

private fun GeoPoint.toLatLng(): LatLng = LatLng(latitude, longitude)

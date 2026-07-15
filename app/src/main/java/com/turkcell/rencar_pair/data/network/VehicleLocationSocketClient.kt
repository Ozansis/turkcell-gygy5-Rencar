package com.turkcell.rencar_pair.data.network

import com.turkcell.rencar_pair.data.local.TokenStore
import com.turkcell.rencar_pair.data.repository.AuthRepository
import com.turkcell.rencar_pair.data.repository.AuthResult
import com.turkcell.rencar_pair.feature.maps.GeoPoint
import io.socket.client.IO
import io.socket.client.Socket
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

private const val SOCKET_HOST = "https://rencarv2.halitkalayci.com"
private const val NAMESPACE = "/ws/locations"
private const val MY_VEHICLE_EVENT = "my-vehicle"

data class VehicleLocationUpdate(
    val vehicleId: String,
    val location: GeoPoint
)

/**
 * `/ws/locations` namespace'ine CUSTOMER token'ıyla bağlanır; yalnız kullanıcının aktif
 * kiralamasındaki aracın konumu `my-vehicle` event'iyle gelir. Aktif kiralama yoksa event
 * hiç gelmez, akış sessiz kalır (bkz. sözleşme notu — bu bilinçli bir sunucu davranışı).
 */
@Singleton
class VehicleLocationSocketClient @Inject constructor(
    private val tokenStore: TokenStore,
    private val authRepository: AuthRepository
) {
    fun vehicleLocationUpdates(): Flow<VehicleLocationUpdate> = callbackFlow {
        var socket: Socket? = null
        var triedRefresh = false

        fun teardown() {
            socket?.off()
            socket?.disconnect()
            socket?.close()
            socket = null
        }

        fun connectWith(token: String) {
            val options = IO.Options().apply {
                auth = mapOf("token" to token)
                forceNew = true
                reconnection = true
            }
            val newSocket = IO.socket(SOCKET_HOST + NAMESPACE, options)

            newSocket.on(MY_VEHICLE_EVENT) { args ->
                parseUpdate(args)?.let { trySend(it) }
            }
            newSocket.on(Socket.EVENT_CONNECT_ERROR) {
                if (triedRefresh) return@on
                triedRefresh = true
                launch {
                    val refreshResult = authRepository.refresh()
                    teardown()
                    val freshToken = (refreshResult as? AuthResult.Success)?.let { tokenStore.accessToken }
                    if (freshToken != null) connectWith(freshToken) else close()
                }
            }
            socket = newSocket
            newSocket.connect()
        }

        val token = tokenStore.accessToken
        if (token == null) {
            close()
        } else {
            connectWith(token)
        }

        awaitClose { teardown() }
    }

    private fun parseUpdate(args: Array<Any>): VehicleLocationUpdate? {
        val root = args.getOrNull(0) as? JSONObject ?: return null
        val vehicle = root.optJSONObject("vehicle") ?: return null
        val vehicleId = vehicle.optString("vehicleId").takeIf { it.isNotBlank() } ?: return null
        val latitude = vehicle.optDouble("latitude", Double.NaN)
        val longitude = vehicle.optDouble("longitude", Double.NaN)
        if (latitude.isNaN() || longitude.isNaN()) return null
        return VehicleLocationUpdate(vehicleId, GeoPoint(latitude, longitude))
    }
}

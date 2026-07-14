package com.turkcell.rencar_pair.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.turkcell.rencar_pair.feature.history.HistoryRoute
import com.turkcell.rencar_pair.feature.maps.MapsRoute
import com.turkcell.rencar_pair.feature.profile.ProfileRoute
import com.turkcell.rencar_pair.feature.wallet.WalletRoute
import kotlinx.coroutines.launch

private val bottomNavItems = listOf(
    BottomNavItem.Map,
    BottomNavItem.History,
    BottomNavItem.Listings,
    BottomNavItem.Profile
)

@Composable
fun MainScaffold(
    onNavigateToVehicleDetail: (String) -> Unit = {}
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    var hasLocationPermission by remember { mutableStateOf(false) }
    var permissionRequestTrigger by remember { mutableIntStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) { Snackbar(it) } },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor   = MaterialTheme.colorScheme.onSurface
            ) {
                bottomNavItems.forEach { item ->
                    val selected = currentDestination
                        ?.hierarchy
                        ?.any { it.route == item.route } == true

                    NavigationBarItem(
                        selected = selected,
                        onClick  = {
                            // Konum izni verilmeden Harita dışındaki sekmelere geçiş engellenir.
                            if (item != BottomNavItem.Map && !hasLocationPermission) {
                                coroutineScope.launch {
                                    val result = snackbarHostState.showSnackbar(
                                        message = "Devam etmek için konum iznini vermelisiniz.",
                                        actionLabel = "İzin Ver"
                                    )
                                    // Snackbar'daki "İzin Ver" aksiyonuna tıklanınca izin diyaloğu tekrar açılır.
                                    if (result == SnackbarResult.ActionPerformed) {
                                        permissionRequestTrigger += 1
                                    }
                                }
                                return@NavigationBarItem
                            }

                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState    = true
                            }
                        },
                        icon  = {
                            Icon(
                                imageVector    = item.icon,
                                contentDescription = item.label
                            )
                        },
                        label = {
                            Text(
                                text  = item.label,
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor   = MaterialTheme.colorScheme.primary,
                            selectedTextColor   = MaterialTheme.colorScheme.primary,
                            // Pill göstergesini kaldır; sadece renk değişimi yeterli
                            indicatorColor      = MaterialTheme.colorScheme.surface,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController    = navController,
            startDestination = BottomNavItem.Map.route,
            modifier         = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Map.route) {
                MapsRoute(
                    onNavigateToVehicleDetail = onNavigateToVehicleDetail,
                    onLocationPermissionStatusChanged = { hasLocationPermission = it },
                    permissionRequestTrigger = permissionRequestTrigger
                )
            }
            composable(BottomNavItem.History.route)  { HistoryRoute() }
            composable(BottomNavItem.Listings.route) { WalletRoute() }
            composable(BottomNavItem.Profile.route)  { ProfileRoute() }
        }
    }
}

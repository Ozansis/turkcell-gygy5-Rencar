package com.turkcell.rencar_pair.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
) {
    data object Map : BottomNavItem(
        route = "home/map",
        label = "Harita",
        icon  = Icons.Default.LocationOn
    )

    data object History : BottomNavItem(
        route = "home/history",
        label = "Geçmiş",
        icon  = Icons.Default.AccessTime
    )

    data object Listings : BottomNavItem(
        route = "home/listings",
        label = "Cüzdan",
        icon  = Icons.Default.List
    )

    data object Profile : BottomNavItem(
        route = "home/profile",
        label = "Profil",
        icon  = Icons.Default.Person
    )
}

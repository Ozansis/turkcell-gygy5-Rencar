package com.turkcell.rencar_pair.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.turkcell.rencar_pair.feature.auth.confirmation.ConfirmationRoute
import com.turkcell.rencar_pair.feature.auth.license.LicenseRoute
import com.turkcell.rencar_pair.feature.auth.login.LoginRoute
import com.turkcell.rencar_pair.feature.auth.otp.OtpRoute
import com.turkcell.rencar_pair.feature.auth.selfie.SelfieRoute
import com.turkcell.rencar_pair.feature.maps.detail.VehicleDetailRoute
import com.turkcell.rencar_pair.feature.onboarding.OnboardingRoute

private object RenCarDestinations {
    const val ONBOARDING = "onboarding"
    const val LOGIN = "login"
    const val OTP = "otp/{phoneNumber}"
    const val LICENSE_VERIFICATION = "license-verification"
    const val SELFIE_VERIFICATION = "selfie-verification"
    const val CONFIRMATION = "confirmation"
    const val HOME = "home"
    const val VEHICLE_DETAIL = "vehicle-detail/{vehicleId}/{distanceMeters}"

    fun otpRoute(phoneNumber: String) = "otp/$phoneNumber"
    fun vehicleDetailRoute(vehicleId: String, distanceMeters: Int) = "vehicle-detail/$vehicleId/$distanceMeters"
}

@Composable
fun RenCarNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController    = navController,
        startDestination = RenCarDestinations.ONBOARDING
    ) {
        composable(RenCarDestinations.ONBOARDING) {
            OnboardingRoute(
                onNavigateToHome = {
                    navController.navigate(RenCarDestinations.HOME) {
                        popUpTo(RenCarDestinations.ONBOARDING) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(RenCarDestinations.LOGIN)
                }
            )
        }

        composable(RenCarDestinations.LOGIN) {
            LoginRoute(
                onNavigateToOtp = { phoneNumber ->
                    navController.navigate(RenCarDestinations.otpRoute(phoneNumber))
                },
                onNavigateToRegister = { /* TODO: Kayit ekrani hazir oldugunda baglanacak */ },
                onNavigateBack        = { navController.popBackStack() }
            )
        }

        composable(
            route     = RenCarDestinations.OTP,
            arguments = listOf(navArgument("phoneNumber") { type = NavType.StringType })
        ) { backStackEntry ->
            val phoneNumber = backStackEntry.arguments?.getString("phoneNumber").orEmpty()
            OtpRoute(
                phoneNumber = phoneNumber,
                onNavigateToHome = {
                    navController.navigate(RenCarDestinations.LICENSE_VERIFICATION)
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(RenCarDestinations.LICENSE_VERIFICATION) {
            LicenseRoute(
                onNavigateToHome = {
                    navController.navigate(RenCarDestinations.SELFIE_VERIFICATION)
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(RenCarDestinations.SELFIE_VERIFICATION) {
            SelfieRoute(
                onNavigateToConfirmation = {
                    navController.navigate(RenCarDestinations.CONFIRMATION)
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(RenCarDestinations.CONFIRMATION) {
            ConfirmationRoute(
                onNavigateToHome = {
                    navController.navigate(RenCarDestinations.HOME) {
                        popUpTo(RenCarDestinations.ONBOARDING) { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(RenCarDestinations.HOME) {
            MainScaffold(
                onNavigateToVehicleDetail = { vehicleId, distanceMeters ->
                    navController.navigate(RenCarDestinations.vehicleDetailRoute(vehicleId, distanceMeters))
                }
            )
        }

        composable(
            route     = RenCarDestinations.VEHICLE_DETAIL,
            arguments = listOf(
                navArgument("vehicleId") { type = NavType.StringType },
                navArgument("distanceMeters") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getString("vehicleId").orEmpty()
            val distanceMeters = backStackEntry.arguments?.getInt("distanceMeters") ?: 0
            VehicleDetailRoute(
                vehicleId      = vehicleId,
                distanceMeters = distanceMeters,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

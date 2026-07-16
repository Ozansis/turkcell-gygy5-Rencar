package com.turkcell.rencar_pair.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.turkcell.rencar_pair.feature.auth.LicenseFlowViewModel
import com.turkcell.rencar_pair.feature.auth.confirmation.ConfirmationRoute
import com.turkcell.rencar_pair.feature.auth.license.LicenseRoute
import com.turkcell.rencar_pair.feature.auth.login.LoginRoute
import com.turkcell.rencar_pair.feature.auth.otp.OtpRoute
import com.turkcell.rencar_pair.feature.auth.register.RegisterRoute
import com.turkcell.rencar_pair.feature.auth.selfie.SelfieRoute
import com.turkcell.rencar_pair.feature.maps.detail.VehicleDetailRoute
import com.turkcell.rencar_pair.feature.onboarding.OnboardingRoute
import com.turkcell.rencar_pair.feature.splash.SplashRoute

private object RenCarDestinations {
    const val SPLASH = "splash"
    const val ONBOARDING = "onboarding"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val OTP = "otp/{phoneNumber}"
    const val LICENSE_FLOW_GRAPH = "license-flow"
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
        startDestination = RenCarDestinations.SPLASH
    ) {
        composable(RenCarDestinations.SPLASH) {
            SplashRoute(
                onNavigateToHome = {
                    navController.navigate(RenCarDestinations.HOME) {
                        popUpTo(RenCarDestinations.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToOnboarding = {
                    navController.navigate(RenCarDestinations.ONBOARDING) {
                        popUpTo(RenCarDestinations.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(RenCarDestinations.LOGIN) {
                        popUpTo(RenCarDestinations.SPLASH) { inclusive = true }
                    }
                }
            )
        }

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
                onNavigateToRegister = {
                    navController.navigate(RenCarDestinations.REGISTER)
                },
                onNavigateBack        = { navController.popBackStack() }
            )
        }

        composable(RenCarDestinations.REGISTER) {
            RegisterRoute(
                onNavigateToLicenseVerification = {
                    navController.navigate(RenCarDestinations.LICENSE_VERIFICATION)
                },
                onNavigateToLogin = { navController.popBackStack() },
                onNavigateBack = { navController.popBackStack() }
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

        navigation(
            startDestination = RenCarDestinations.LICENSE_VERIFICATION,
            route            = RenCarDestinations.LICENSE_FLOW_GRAPH
        ) {
            composable(RenCarDestinations.LICENSE_VERIFICATION) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(RenCarDestinations.LICENSE_FLOW_GRAPH)
                }
                val licenseFlowViewModel: LicenseFlowViewModel =
                    hiltViewModel(viewModelStoreOwner = parentEntry)

                LicenseRoute(
                    onNavigateToHome = {
                        navController.navigate(RenCarDestinations.SELFIE_VERIFICATION)
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            composable(RenCarDestinations.SELFIE_VERIFICATION) { backStackEntry ->
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(RenCarDestinations.LICENSE_FLOW_GRAPH)
                }
                val licenseFlowViewModel: LicenseFlowViewModel =
                    hiltViewModel(viewModelStoreOwner = parentEntry)

                SelfieRoute(
                    onNavigateToConfirmation = {
                        navController.navigate(RenCarDestinations.CONFIRMATION)
                    },
                    onNavigateBack = { navController.popBackStack() }
                )
            }
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

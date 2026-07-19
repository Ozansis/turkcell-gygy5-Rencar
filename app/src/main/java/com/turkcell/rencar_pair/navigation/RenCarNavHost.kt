package com.turkcell.rencar_pair.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
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
import com.turkcell.rencar_pair.feature.help.HelpRoute
import com.turkcell.rencar_pair.feature.history.detail.HistoryDetailRoute
import com.turkcell.rencar_pair.feature.invite.InviteRoute
import com.turkcell.rencar_pair.feature.maps.detail.VehicleDetailRoute
import com.turkcell.rencar_pair.feature.onboarding.OnboardingRoute
import com.turkcell.rencar_pair.feature.rental.active.ActiveRentalRoute
import com.turkcell.rencar_pair.feature.rental.payment.RentalPaymentRoute
import com.turkcell.rencar_pair.feature.rental.photos.VehiclePhotosRoute
import com.turkcell.rencar_pair.feature.rental.reservation.ReservationConfirmationRoute
import com.turkcell.rencar_pair.feature.settings.SettingsRoute
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
    const val HOME_HISTORY = "home-history"
    const val VEHICLE_DETAIL = "vehicle-detail/{vehicleId}/{distanceMeters}"
    const val RESERVATION_CONFIRMATION = "reservation-confirmation/{vehicleId}"
    const val RENTAL_ACTIVE = "rental-active/{rentalId}"
    const val RENTAL_PAYMENT = "rental-payment/{rentalId}"
    const val VEHICLE_PHOTOS = "vehicle-photos/{rentalId}/{vehicleId}"
    const val HISTORY_DETAIL = "history-detail/{rentalId}"
    const val SETTINGS = "settings"
    const val HELP = "help"
    const val INVITE = "invite/{referralCode}"

    fun otpRoute(phoneNumber: String) = "otp/$phoneNumber"
    fun vehicleDetailRoute(vehicleId: String, distanceMeters: Int) = "vehicle-detail/$vehicleId/$distanceMeters"
    fun reservationConfirmationRoute(vehicleId: String) = "reservation-confirmation/$vehicleId"
    fun rentalActiveRoute(rentalId: String) = "rental-active/$rentalId"
    fun rentalPaymentRoute(rentalId: String) = "rental-payment/$rentalId"
    fun vehiclePhotosRoute(rentalId: String, vehicleId: String) = "vehicle-photos/$rentalId/$vehicleId"
    fun historyDetailRoute(rentalId: String) = "history-detail/$rentalId"
    fun inviteRoute(referralCode: String) = "invite/$referralCode"
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
                },
                onNavigateToLicenseVerification = {
                    navController.navigate(RenCarDestinations.LICENSE_VERIFICATION) {
                        popUpTo(RenCarDestinations.SPLASH) { inclusive = true }
                    }
                },
                onNavigateToConfirmation = {
                    navController.navigate(RenCarDestinations.CONFIRMATION) {
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
                onNavigateToConfirmation = {
                    navController.navigate(RenCarDestinations.CONFIRMATION)
                },
                onNavigateToHome = {
                    navController.navigate(RenCarDestinations.HOME) {
                        popUpTo(RenCarDestinations.LOGIN) { inclusive = true }
                    }
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
                    navController.navigate(RenCarDestinations.HOME) {
                        popUpTo(RenCarDestinations.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToLicenseVerification = {
                    navController.navigate(RenCarDestinations.LICENSE_VERIFICATION)
                },
                onNavigateToConfirmation = {
                    navController.navigate(RenCarDestinations.CONFIRMATION)
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
                    licenseFlowViewModel = licenseFlowViewModel,
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
                    licenseFlowViewModel = licenseFlowViewModel,
                    onNavigateToHome = {
                        navController.navigate(RenCarDestinations.HOME) {
                            popUpTo(RenCarDestinations.ONBOARDING) { inclusive = true }
                        }
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
                onNavigateToLicenseVerification = {
                    navController.navigate(RenCarDestinations.LICENSE_VERIFICATION) {
                        popUpTo(RenCarDestinations.CONFIRMATION) { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(RenCarDestinations.HOME) {
            HomeGraph(navController = navController, startTab = BottomNavItem.Map)
        }

        composable(RenCarDestinations.HOME_HISTORY) {
            HomeGraph(navController = navController, startTab = BottomNavItem.History)
        }

        composable(RenCarDestinations.SETTINGS) {
            SettingsRoute(onNavigateBack = { navController.popBackStack() })
        }

        composable(RenCarDestinations.HELP) {
            HelpRoute(onNavigateBack = { navController.popBackStack() })
        }

        composable(
            route     = RenCarDestinations.INVITE,
            arguments = listOf(navArgument("referralCode") { type = NavType.StringType })
        ) { backStackEntry ->
            val referralCode = backStackEntry.arguments?.getString("referralCode").orEmpty()
            InviteRoute(
                referralCode   = referralCode,
                onNavigateBack = { navController.popBackStack() }
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
                onNavigateBack = { navController.popBackStack() },
                onNavigateToReservationConfirmation = { id ->
                    navController.navigate(RenCarDestinations.reservationConfirmationRoute(id))
                }
            )
        }

        composable(
            route     = RenCarDestinations.HISTORY_DETAIL,
            arguments = listOf(navArgument("rentalId") { type = NavType.StringType })
        ) { backStackEntry ->
            val rentalId = backStackEntry.arguments?.getString("rentalId").orEmpty()
            HistoryDetailRoute(
                rentalId       = rentalId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route     = RenCarDestinations.RESERVATION_CONFIRMATION,
            arguments = listOf(navArgument("vehicleId") { type = NavType.StringType })
        ) { backStackEntry ->
            val vehicleId = backStackEntry.arguments?.getString("vehicleId").orEmpty()
            ReservationConfirmationRoute(
                vehicleId      = vehicleId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToActiveRental = { rentalId ->
                    navController.navigate(RenCarDestinations.rentalActiveRoute(rentalId)) {
                        popUpTo(RenCarDestinations.HOME)
                    }
                },
                onNavigateToVehiclePhotos = { rentalId, vId ->
                    navController.navigate(RenCarDestinations.vehiclePhotosRoute(rentalId, vId)) {
                        popUpTo(RenCarDestinations.HOME)
                    }
                }
            )
        }

        composable(
            route     = RenCarDestinations.RENTAL_ACTIVE,
            arguments = listOf(navArgument("rentalId") { type = NavType.StringType })
        ) { backStackEntry ->
            val rentalId = backStackEntry.arguments?.getString("rentalId").orEmpty()
            ActiveRentalRoute(
                rentalId = rentalId,
                onNavigateToPayment = { finishedRentalId ->
                    navController.navigate(RenCarDestinations.rentalPaymentRoute(finishedRentalId)) {
                        popUpTo(RenCarDestinations.HOME)
                    }
                }
            )
        }

        composable(
            route     = RenCarDestinations.RENTAL_PAYMENT,
            arguments = listOf(navArgument("rentalId") { type = NavType.StringType })
        ) { backStackEntry ->
            val rentalId = backStackEntry.arguments?.getString("rentalId").orEmpty()
            RentalPaymentRoute(
                rentalId = rentalId,
                onNavigateToHistory = {
                    navController.navigate(RenCarDestinations.HOME_HISTORY) {
                        popUpTo(RenCarDestinations.HOME) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route     = RenCarDestinations.VEHICLE_PHOTOS,
            arguments = listOf(
                navArgument("rentalId") { type = NavType.StringType },
                navArgument("vehicleId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val rentalId = backStackEntry.arguments?.getString("rentalId").orEmpty()
            val vehicleId = backStackEntry.arguments?.getString("vehicleId").orEmpty()
            VehiclePhotosRoute(
                rentalId   = rentalId,
                vehicleId  = vehicleId,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToActiveRental = { activeRentalId ->
                    navController.navigate(RenCarDestinations.rentalActiveRoute(activeRentalId)) {
                        popUpTo(RenCarDestinations.HOME)
                    }
                }
            )
        }
    }
}

@Composable
private fun HomeGraph(navController: NavHostController, startTab: BottomNavItem) {
    MainScaffold(
        startTab = startTab,
        onNavigateToVehicleDetail = { vehicleId, distanceMeters ->
            navController.navigate(RenCarDestinations.vehicleDetailRoute(vehicleId, distanceMeters))
        },
        onNavigateToActiveRental = { rentalId ->
            navController.navigate(RenCarDestinations.rentalActiveRoute(rentalId))
        },
        onNavigateToHistoryDetail = { rentalId ->
            navController.navigate(RenCarDestinations.historyDetailRoute(rentalId))
        },
        onNavigateToSettings = {
            navController.navigate(RenCarDestinations.SETTINGS)
        },
        onNavigateToHelp = {
            navController.navigate(RenCarDestinations.HELP)
        },
        onNavigateToInvite = { referralCode ->
            navController.navigate(RenCarDestinations.inviteRoute(referralCode))
        },
        onNavigateToLicenseVerification = {
            navController.navigate(RenCarDestinations.LICENSE_VERIFICATION)
        },
        onNavigateToLogin = {
            navController.navigate(RenCarDestinations.LOGIN) {
                popUpTo(0) { inclusive = true }
            }
        }
    )
}

package com.turkcell.rencar_pair.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.turkcell.rencar_pair.feature.auth.login.LoginRoute
import com.turkcell.rencar_pair.feature.auth.otp.OtpRoute
import com.turkcell.rencar_pair.feature.onboarding.OnboardingRoute

private object RenCarDestinations {
    const val ONBOARDING = "onboarding"
    const val LOGIN = "login"
    const val OTP = "otp/{phoneNumber}"
    const val HOME = "home"

    fun otpRoute(phoneNumber: String) = "otp/$phoneNumber"
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
                    navController.navigate(RenCarDestinations.HOME) {
                        popUpTo(RenCarDestinations.ONBOARDING) { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(RenCarDestinations.HOME) {
            MainScaffold()
        }
    }
}

package com.example.prog7313_poe_kachinga.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.prog7313_poe_kachinga.*
import com.example.prog7313_poe_kachinga.viewmodel.*

@Composable
fun NavGraph(navController: NavHostController) {

    val authViewModel: AuthViewModel = viewModel()
    val transactionViewModel: TransactionViewModel = viewModel()
    val categoryViewModel: CategoryViewModel = viewModel()
    val savingsViewModel: SavingsViewModel = viewModel()
    val achievementsViewModel: AchievementsViewModel = viewModel()
    val profileViewModel: ProfileViewModel = viewModel()
    val homeViewModel: HomeViewModel = viewModel()

    // Check if already logged in — skip straight to HOME
    val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
    val startDestination = if (isLoggedIn) NavRoutes.HOME else NavRoutes.SPLASH

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(NavRoutes.SPLASH) {
            SplashScreen(onFinished = {
                navController.navigate(NavRoutes.LAUNCH) {
                    popUpTo(NavRoutes.SPLASH) { inclusive = true }
                }
            })
        }

        composable(NavRoutes.LAUNCH) {
            LaunchScreen(
                onLoginClick = { navController.navigate(NavRoutes.LOGIN) },
                onSignUpClick = { navController.navigate(NavRoutes.CREATE_ACCOUNT) }
            )
        }

        composable(NavRoutes.LOGIN) {
            LoginScreen(
                onLoginClick = {
                    navController.navigate(NavRoutes.HOME) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onSignUpClick = {
                    navController.navigate(NavRoutes.CREATE_ACCOUNT)
                },
                authViewModel = authViewModel
            )
        }

        composable(NavRoutes.CREATE_ACCOUNT) {
            CreateAccountScreen(
                onSignUpClick = {
                    navController.navigate(NavRoutes.LOGIN) {
                        popUpTo(NavRoutes.CREATE_ACCOUNT) { inclusive = true }
                    }
                },
                onLoginClick = { navController.popBackStack() },
                authViewModel = authViewModel
            )
        }

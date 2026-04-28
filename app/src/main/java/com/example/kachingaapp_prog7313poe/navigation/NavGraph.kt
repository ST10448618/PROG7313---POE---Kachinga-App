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
        composable(NavRoutes.HOME) {
            HomeScreen(
                transactionViewModel = transactionViewModel,
                homeViewModel = homeViewModel,
                onNavigate = { route -> navController.navigate(route) }
            )
        }

        composable(NavRoutes.TRANSACTIONS) {
            TransactionsScreen(
                onBackClick = { navController.popBackStack() },
                transactionViewModel = transactionViewModel,
                onNavigate = { route -> navController.navigate(route) }
            )
        }

        composable(NavRoutes.ADD_TRANSACTION) {
            AddTransactionScreen(
                onBackClick = { navController.popBackStack() },
                transactionViewModel = transactionViewModel,
                categoryViewModel = categoryViewModel,
                onSuccess = {
                    // Always go back to HOME after adding a transaction
                    navController.navigate(NavRoutes.HOME) {
                        popUpTo(NavRoutes.HOME) { inclusive = false }
                    }
                }
            )
        }

        composable(NavRoutes.CATEGORIES) {
            CategoriesScreen(
                onBackClick = { navController.popBackStack() },
                categoryViewModel = categoryViewModel,
                onNavigate = { route -> navController.navigate(route) }
            )
        }

        composable(NavRoutes.ADD_CATEGORY) {
            AddCategoryScreen(
                onBackClick = { navController.popBackStack() },
                categoryViewModel = categoryViewModel,
                onSuccess = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.SAVINGS) {
            SavingsScreen(
                onBackClick = { navController.popBackStack() },
                onGoalClick = { goal ->
                    navController.navigate(NavRoutes.savingsDetail(goal.id))
                },
                savingsViewModel = savingsViewModel,
                onNavigate = { route -> navController.navigate(route) }
            )
        }

        composable(
            route = NavRoutes.SAVINGS_DETAIL,
            arguments = listOf(navArgument("goalId") { type = NavType.IntType })
        ) { backStackEntry ->
            val goalId = backStackEntry.arguments?.getInt("goalId") ?: 0
            SavingsDetailScreen(
                goalId = goalId,
                onBackClick = { navController.popBackStack() },
                savingsViewModel = savingsViewModel,
                onNavigate = { route -> navController.navigate(route) }
            )
        }

        composable(NavRoutes.ADD_SAVINGS_GOAL) {
            AddSavingsGoalScreen(
                onBackClick = { navController.popBackStack() },
                savingsViewModel = savingsViewModel,
                onSuccess = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.CALENDAR) {
            CalendarScreen(
                onBackClick = { navController.popBackStack() },
                transactionViewModel = transactionViewModel
            )
        }
        composable(NavRoutes.ACHIEVEMENTS) {
            AchievementsScreen(
                onBackClick = { navController.popBackStack() },
                achievementsViewModel = achievementsViewModel
            )
        }

        composable(NavRoutes.PROFILE) {
            ProfileScreen(
                onBackClick = { navController.popBackStack() },
                authViewModel = authViewModel,
                profileViewModel = profileViewModel,
                onNavigate = { route -> navController.navigate(route) }
            )
        }

        composable(NavRoutes.CATEGORY_REPORT) {
            CategoryReportScreen(
                onBackClick = { navController.popBackStack() },
                transactionViewModel = transactionViewModel
            )
        }
    }
}
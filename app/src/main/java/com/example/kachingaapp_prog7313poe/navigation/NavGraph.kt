package com.example.prog7313_poe_kachinga.navigation

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



    NavHost(
        navController = navController,
    ) {
        composable(NavRoutes.SPLASH) {
            SplashScreen(onFinished = {
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
            )
        }

        composable(NavRoutes.TRANSACTIONS) {
            TransactionsScreen(
                onBackClick = { navController.popBackStack() },
                transactionViewModel = transactionViewModel,
            )
        }

        composable(NavRoutes.ADD_TRANSACTION) {
            AddTransactionScreen(
                onBackClick = { navController.popBackStack() },
                transactionViewModel = transactionViewModel,
                categoryViewModel = categoryViewModel,
            )
        }

        composable(NavRoutes.CATEGORIES) {
            CategoriesScreen(
                onBackClick = { navController.popBackStack() },
                categoryViewModel = categoryViewModel,
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
            )
        }
        composable(NavRoutes.ACHIEVEMENTS) {
            AchievementsScreen(
                onBackClick = { navController.popBackStack() },
            )
        }

        composable(NavRoutes.PROFILE) {
            ProfileScreen(
                onBackClick = { navController.popBackStack() },
                authViewModel = authViewModel,
                profileViewModel = profileViewModel,
            )
        }

        composable(NavRoutes.CATEGORY_REPORT) {
            CategoryReportScreen(
                onBackClick = { navController.popBackStack() },
            )
        }
    }
}
package com.example.prog7313_poe_kachinga.navigation

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.prog7313_poe_kachinga.*
import com.example.prog7313_poe_kachinga.viewmodel.*
import androidx.compose.ui.platform.LocalContext
import androidx.activity.ComponentActivity
import com.example.prog7313_poe_kachinga.data.SessionManager

@Composable
fun NavGraph(navController: NavHostController) {

    val context = LocalContext.current
    val activity = context as ComponentActivity

    val authViewModel: AuthViewModel = viewModel(activity)
    val transactionViewModel: TransactionViewModel = viewModel(activity)
    val categoryViewModel: CategoryViewModel = viewModel(activity)
    val savingsViewModel: SavingsViewModel = viewModel(activity)
    val achievementsViewModel: AchievementsViewModel = viewModel(activity)
    val profileViewModel: ProfileViewModel = viewModel(activity)
    val homeViewModel: HomeViewModel = viewModel(activity)
    val insightsViewModel: InsightsViewModel = viewModel(activity)
    val billViewModel: BillViewModel = viewModel(activity)

    val sessionManager = remember { SessionManager(context) }
    val isLoggedIn by sessionManager.isLoggedIn.collectAsState(initial = false)
    val userId by sessionManager.userId.collectAsState(initial = -1)

    val onNavigate: (String) -> Unit = { route ->
        navController.navigate(route) {
            when (route) {
                NavRoutes.HOME,
                NavRoutes.CALENDAR,
                NavRoutes.SAVINGS,
                NavRoutes.PROFILE,
                NavRoutes.BILLS,
                NavRoutes.INSIGHTS -> {
                    popUpTo(NavRoutes.HOME) { saveState = true }
                    launchSingleTop = true
                    restoreState = true
                }
                else -> {
                    launchSingleTop = true
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = NavRoutes.SPLASH
    ) {
        composable(NavRoutes.SPLASH) {
            SplashScreen(onFinished = {
                val destination = if (isLoggedIn && userId > 0) NavRoutes.HOME else NavRoutes.LAUNCH
                navController.navigate(destination) {
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
                onSignUpClick = { navController.navigate(NavRoutes.CREATE_ACCOUNT) },
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
                onNavigate = onNavigate,
                homeViewModel = homeViewModel
            )
        }

        composable(NavRoutes.TRANSACTIONS) {
            TransactionsScreen(
                onBackClick = { navController.popBackStack() },
                transactionViewModel = transactionViewModel,
                onNavigate = onNavigate
            )
        }

        composable(NavRoutes.ADD_TRANSACTION) {
            AddTransactionScreen(
                onBackClick = { navController.popBackStack() },
                transactionViewModel = transactionViewModel,
                categoryViewModel = categoryViewModel,
                onSuccess = { navController.popBackStack() }
            )
        }

        composable(NavRoutes.CATEGORIES) {
            CategoriesScreen(
                onBackClick = { navController.popBackStack() },
                categoryViewModel = categoryViewModel,
                onNavigate = onNavigate
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
                onNavigate = onNavigate
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
                onNavigate = onNavigate
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
                transactionViewModel = transactionViewModel,
                onNavigate = onNavigate
            )
        }

        composable(NavRoutes.ACHIEVEMENTS) {
            AchievementsScreen(
                onBackClick = { navController.popBackStack() },
                achievementsViewModel = achievementsViewModel,
                onNavigate = onNavigate
            )
        }

        composable(NavRoutes.PROFILE) {
            ProfileScreen(
                onBackClick = { navController.popBackStack() },
                authViewModel = authViewModel,
                profileViewModel = profileViewModel,
                onNavigate = onNavigate
            )
        }

        composable(NavRoutes.CATEGORY_REPORT) {
            CategoryReportScreen(
                onBackClick = { navController.popBackStack() },
                transactionViewModel = transactionViewModel,
                onNavigate = onNavigate
            )
        }

        composable(NavRoutes.INSIGHTS) {
            InsightsScreen(
                onBackClick = { navController.popBackStack() },
                insightsViewModel = insightsViewModel,
                onNavigate = onNavigate
            )
        }

        composable(NavRoutes.BILLS) {
            BillsScreen(
                onBackClick = { navController.popBackStack() },
                billViewModel = billViewModel,
                onNavigate = onNavigate
            )
        }

        composable(NavRoutes.ADD_BILL) {
            AddBillScreen(
                onBackClick = { navController.popBackStack() },
                billViewModel = billViewModel,
                onSuccess = { navController.popBackStack() }
            )
        }
    }
}
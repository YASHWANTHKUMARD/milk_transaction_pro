package com.example.milk_homecalci.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.milk_homecalci.MilkApplication
import com.example.milk_homecalci.ui.screens.home.HomeScreen
import com.example.milk_homecalci.ui.screens.home.HomeViewModel
import com.example.milk_homecalci.ui.screens.add_transaction.AddTransactionScreen
import com.example.milk_homecalci.ui.screens.add_transaction.AddTransactionViewModel
import com.example.milk_homecalci.ui.screens.login.LoginScreen
import com.example.milk_homecalci.ui.screens.reports.ReportsScreen
import com.example.milk_homecalci.ui.screens.reports.ReportsViewModel

@Composable
fun MilkNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val repository = (context.applicationContext as MilkApplication).repository

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route,
        modifier = modifier
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Home.route) {
            val viewModel: HomeViewModel = viewModel(
                factory = HomeViewModel.Factory(repository)
            )
            HomeScreen(
                viewModel = viewModel,
                onAddTransactionClick = { navController.navigate(Screen.AddTransaction.route) },
                onReportsClick = { navController.navigate(Screen.Reports.route) }
            )
        }
        composable(Screen.AddTransaction.route) {
            val viewModel: AddTransactionViewModel = viewModel(
                factory = AddTransactionViewModel.Factory(repository)
            )
            AddTransactionScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Reports.route) {
            val viewModel: ReportsViewModel = viewModel(
                factory = ReportsViewModel.Factory(repository)
            )
            ReportsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

package com.example.milk_homecalci.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.milk_homecalci.MilkApplication
import com.example.milk_homecalci.ui.screens.home.HomeScreen
import com.example.milk_homecalci.ui.screens.home.HomeViewModel
import com.example.milk_homecalci.ui.screens.add_transaction.AddTransactionScreen
import com.example.milk_homecalci.ui.screens.add_transaction.AddTransactionViewModel
import com.example.milk_homecalci.ui.screens.login.LoginScreen
import com.example.milk_homecalci.ui.screens.login.LoginViewModel
import com.example.milk_homecalci.ui.screens.profile.ProfileScreen
import com.example.milk_homecalci.ui.screens.profile.ProfileViewModel
import com.example.milk_homecalci.ui.screens.reports.ReportsScreen
import com.example.milk_homecalci.ui.screens.reports.ReportsViewModel
import com.example.milk_homecalci.ui.screens.transactions.TransactionsScreen
import com.example.milk_homecalci.ui.screens.transactions.TransactionsViewModel

@Composable
fun MilkNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Login.route
) {
    val context = LocalContext.current
    val app = context.applicationContext as MilkApplication
    val repository = app.repository
    val authRepository = app.authRepository

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable(Screen.Login.route) {
            val loginViewModel: LoginViewModel = viewModel(
                factory = LoginViewModel.Factory(authRepository, repository)
            )
            LoginScreen(
                viewModel = loginViewModel,
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(Screen.Home.route) {
            MainScaffold(navController) { innerPadding ->
                val viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory(repository))
                HomeScreen(
                    viewModel = viewModel,
                    onAddTransactionClick = { navController.navigate(Screen.AddTransaction.route) },
                    onLogoutClick = {
                        authRepository.signOut()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
        composable(Screen.Transactions.route) {
            MainScaffold(navController) { innerPadding ->
                val viewModel: TransactionsViewModel = viewModel(factory = TransactionsViewModel.Factory(repository))
                TransactionsScreen(
                    viewModel = viewModel,
                    onAddTransactionClick = { navController.navigate(Screen.AddTransaction.route) },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
        composable(Screen.Reports.route) {
            MainScaffold(navController) { innerPadding ->
                val viewModel: ReportsViewModel = viewModel(factory = ReportsViewModel.Factory(repository))
                ReportsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { navController.popBackStack() },
                    onLogoutClick = {
                        authRepository.signOut()
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
        composable(Screen.Profile.route) {
            MainScaffold(navController) { innerPadding ->
                val viewModel: ProfileViewModel = viewModel(
                    factory = ProfileViewModel.Factory(authRepository, repository)
                )
                ProfileScreen(
                    viewModel = viewModel,
                    onLogoutClick = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }
        composable(Screen.AddTransaction.route) {
            val viewModel: AddTransactionViewModel = viewModel(factory = AddTransactionViewModel.Factory(repository))
            AddTransactionScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun MainScaffold(
    navController: NavHostController,
    content: @Composable (androidx.compose.foundation.layout.PaddingValues) -> Unit
) {
    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination
                Screen.bottomNavItems.forEach { screen ->
                    NavigationBarItem(
                        icon = {
                            val icon = screen.icon
                            if (icon != null) {
                                Icon(icon, contentDescription = screen.label)
                            } else {
                                Icon(Icons.Default.Home, contentDescription = screen.label)
                            }
                        },
                        label = { Text(screen.label) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        content(innerPadding)
    }
}

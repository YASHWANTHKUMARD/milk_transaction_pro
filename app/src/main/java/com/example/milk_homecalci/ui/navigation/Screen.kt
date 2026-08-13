package com.example.milk_homecalci.ui.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Home : Screen("home")
    object AddTransaction : Screen("add_transaction")
    object Reports : Screen("reports")
}

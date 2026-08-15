package com.example.milk_homecalci.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val label: String = "", val icon: ImageVector? = null) {
    object Login : Screen("login")
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Transactions : Screen("transactions", "Ledger", Icons.AutoMirrored.Filled.List)
    object Reports : Screen("reports", "Reports", Icons.Default.Assessment)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)
    object AddTransaction : Screen("add_transaction")

    companion object {
        val bottomNavItems: List<Screen>
            get() = listOf(Home, Transactions, Reports, Profile)
    }
}

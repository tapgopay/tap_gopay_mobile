package com.example.tapgopay

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tapgopay.data.NetworkMonitor
import com.example.tapgopay.screens.HomeScreen
import com.example.tapgopay.screens.LoginScreen
import com.example.tapgopay.screens.ProfileScreen
import com.example.tapgopay.screens.RegisterScreen
import com.example.tapgopay.screens.Routes
import com.example.tapgopay.ui.theme.TapGoPayTheme

class MainActivity : ComponentActivity() {
    companion object {
        const val TAG: String = "TapGoPay"
        lateinit var networkMonitor: NetworkMonitor
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        networkMonitor = NetworkMonitor(this)

        enableEdgeToEdge()
        setContent {
            TapGoPayTheme {
                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Routes.HomeScreen.name,
                        modifier = Modifier.padding(innerPadding),
                    ) {
                        composable(route = Routes.LoginScreen.name) {
                            LoginScreen(
                                navigateToSignup = {
                                    navController.navigate(route = Routes.RegisterScreen.name)
                                },
                                navigateToHomePage = {
                                    navController.navigate(
                                        route = Routes.HomeScreen.name
                                    )
                                }
                            )
                        }

                        composable(route = Routes.RegisterScreen.name) {
                            RegisterScreen(
                                navigateToLogin = {
                                    navController.navigate(route = Routes.LoginScreen.name)
                                }
                            )
                        }

                        composable(route = Routes.HomeScreen.name) {
                            HomeScreen(
                                navigateTo = { route ->
                                    navController.navigate(route = route.name)
                                }
                            )
                        }

                        composable(route = Routes.ProfileScreen.name) {
                            ProfileScreen(
                                navigateToHomeScreen = {
                                    navController.navigate(route= Routes.HomeScreen.name)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

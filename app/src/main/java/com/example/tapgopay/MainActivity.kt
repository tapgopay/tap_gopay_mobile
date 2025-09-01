package com.example.tapgopay

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tapgopay.remote.NetworkMonitor
import com.example.tapgopay.screens.ForgotPasswordScreen
import com.example.tapgopay.screens.HomeScreen
import com.example.tapgopay.screens.LoginScreen
import com.example.tapgopay.screens.ProfileScreen
import com.example.tapgopay.screens.RegisterScreen
import com.example.tapgopay.screens.ResetPasswordScreen
import com.example.tapgopay.screens.Routes
import com.example.tapgopay.ui.theme.TapGoPayTheme

class MainActivity : ComponentActivity() {
    companion object {
        const val TAG: String = "TapGoPay"
        const val SHARED_PREFERENCES: String = "SHARED_PREFERENCES"

        lateinit var instance: MainActivity
        lateinit var networkMonitor: NetworkMonitor
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        networkMonitor = NetworkMonitor(this)
        instance = this

        enableEdgeToEdge()
        setContent {
            TapGoPayTheme {
                val navController = rememberNavController()

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Routes.LoginScreen.name,
                        modifier = Modifier.padding(innerPadding),
                    ) {
                        composable(route = Routes.LoginScreen.name) {
                            LoginScreen(
                                navigateToSignup = {
                                    navController.navigate(route = Routes.RegisterScreen.name)
                                },
                                navigateToHomePage = {
                                    navController.navigate(route = Routes.HomeScreen.name)
                                },
                                navigateToForgotPasswordScreen = {
                                    navController.navigate(route = Routes.ForgotPasswordScreen.name)
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
                                    navController.navigate(route = Routes.HomeScreen.name)
                                }
                            )
                        }

                        composable(route = Routes.ForgotPasswordScreen.name) {
                            ForgotPasswordScreen(
                                navigateToLoginScreen = {
                                    navController.navigate(Routes.LoginScreen.name)
                                },
                                navigateToResetPasswordScreen = { email ->
                                    navController.navigate("${Routes.ResetPasswordScreen.name}/$email")
                                }
                            )
                        }

                        composable(
                            route = "${Routes.ResetPasswordScreen.name}/{email}",
                            arguments = listOf(navArgument("email") {
                                type = NavType.StringType
                            })  // Specify argument type
                        ) { backStackEntry ->
                            val email = backStackEntry.arguments?.getString("email")
                                ?: ""  // Retrieve argument

                            ResetPasswordScreen(
                                email = email,
                                navigateToLoginScreen = {
                                    navController.navigate(Routes.LoginScreen.name)
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}

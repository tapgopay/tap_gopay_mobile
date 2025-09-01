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
import androidx.navigation.navigation
import com.example.tapgopay.data.AuthViewModel
import com.example.tapgopay.screens.ForgotPasswordScreen
import com.example.tapgopay.screens.HomeScreen
import com.example.tapgopay.screens.LoginScreen
import com.example.tapgopay.screens.ProfileScreen
import com.example.tapgopay.screens.ResetPasswordScreen
import com.example.tapgopay.screens.Routes
import com.example.tapgopay.screens.SignUpScreen
import com.example.tapgopay.ui.theme.TapGoPayTheme
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security

class MainActivity : ComponentActivity() {
    companion object {
        const val TAG: String = "TapGoPay"
        const val SHARED_PREFERENCES: String = "SHARED_PREFERENCES"
        const val PRIVATE_KEY_FILENAME: String = "PRIVATE_KEY_FILENAME"

        lateinit var instance: MainActivity
    }

    init {
        Security.addProvider(BouncyCastleProvider())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                                navigateTo = { route ->
                                    navController.navigate(route = route.name)
                                },
                            )
                        }

                        composable(route = Routes.SignUpScreen.name) {
                            SignUpScreen(
                                navigateTo = { route ->
                                    navController.navigate(route = route.name)
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

                        navigation(
                            startDestination = Routes.ForgotPasswordScreen.name,
                            route = "Forgot_Password"
                        ) {
                            val authViewModel = AuthViewModel(instance.application)

                            composable(route = Routes.ForgotPasswordScreen.name) {
                                ForgotPasswordScreen(
                                    authViewModel = authViewModel,
                                    navigateTo = { route ->
                                        navController.navigate(route.name)
                                    },
                                )
                            }
                            composable(route = Routes.ResetPasswordScreen.name) {
                                ResetPasswordScreen(
                                    authViewModel = authViewModel,
                                    navigateTo = { route ->
                                        navController.navigate(route.name)
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

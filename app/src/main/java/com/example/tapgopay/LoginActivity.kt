package com.example.tapgopay

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tapgopay.data.AuthViewModel
import com.example.tapgopay.screens.ForgotPasswordScreen
import com.example.tapgopay.screens.LoginScreen
import com.example.tapgopay.screens.ResetPasswordScreen
import com.example.tapgopay.screens.Routes
import com.example.tapgopay.screens.SignUpScreen
import com.example.tapgopay.ui.theme.TapGoPayTheme

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val authViewModel = AuthViewModel(this@LoginActivity.application)

        enableEdgeToEdge()
        setContent {
            TapGoPayTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = Routes.LoginScreen,
                ) {
                    composable(route = Routes.LoginScreen.name) {
                        LoginScreen(
                            navigateTo = { route ->
                                when (route) {
                                    Routes.HomeScreen -> {
                                        val intent =
                                            Intent(this@LoginActivity, MainActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }

                                    else -> {
                                        navController.navigate(route = route.name)
                                    }
                                }
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
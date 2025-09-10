package com.example.tapgopay

import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import com.example.tapgopay.screens.RequestPaymentScreen
import com.example.tapgopay.screens.ResetPasswordScreen
import com.example.tapgopay.screens.Routes
import com.example.tapgopay.screens.ScanQRCodeScreen
import com.example.tapgopay.screens.SignUpScreen
import com.example.tapgopay.ui.theme.TapGoPayTheme
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security

class MainActivity : ComponentActivity() {
    companion object {
        const val TAG: String = "TapGoPay"
        const val SHARED_PREFERENCES: String = "SHARED_PREFERENCES"
        const val USERNAME: String = "USERNAME"
        const val PRIVATE_KEY_FILENAME: String = "PRIVATE_KEY_FILENAME"

        lateinit var instance: MainActivity
    }

    private var qrCodeContents: Map<String, String>? = null

    private val barcodeLauncher = registerForActivityResult(ScanContract()) { results ->
        try {
            val contents: String = results.contents
            Log.d(TAG, "Scanned QR Code contents: $contents")

            val type = object : TypeToken<Map<String, Any>>() {}.type
            qrCodeContents = Gson().fromJson(contents, type)

        } catch (e: Exception) {
            Log.e(TAG, "Error extracting transaction message; ${e.message}")
            Toast.makeText(
                this.applicationContext,
                "Error extracting transaction message",
                Toast.LENGTH_LONG
            )
                .show()
        }
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
                                navigateTo = { route ->
                                    navController.navigate(route = route.name)
                                },
                                goBack = {
                                    navController.popBackStack()
                                }
                            )
                        }

                        composable(route = Routes.RequestPaymentScreen.name) {
                            RequestPaymentScreen(
                                navigateTo = { route ->
                                    navController.navigate(route.name)
                                }
                            )
                        }

                        composable(route = Routes.ScanQRCodeScreen.name) {
                            ScanQRCodeScreen(
                                qrCodeContents = qrCodeContents ?: emptyMap(),
                                scanQRCode = {
                                    val options = ScanOptions().apply {
                                        setDesiredBarcodeFormats(ScanOptions.QR_CODE)
                                        setPrompt("Scan QR Code")
                                        setCameraId(0)
                                        setBeepEnabled(true)
                                    }
                                    barcodeLauncher.launch(options)
                                },
                                navigateTo = { route ->
                                    navController.navigate(route.name)
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

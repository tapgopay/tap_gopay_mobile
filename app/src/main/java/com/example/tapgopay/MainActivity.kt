package com.example.tapgopay

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tapgopay.data.AppViewModel
import com.example.tapgopay.remote.NotificationService
import com.example.tapgopay.screens.HomeScreen
import com.example.tapgopay.screens.PaymentScreen
import com.example.tapgopay.screens.ProfileScreen
import com.example.tapgopay.screens.RequestPaymentScreen
import com.example.tapgopay.screens.Routes
import com.example.tapgopay.screens.ScanQRCodeScreen
import com.example.tapgopay.ui.theme.TapGoPayTheme
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions

class MainActivity : ComponentActivity() {
    companion object {
        const val TAG: String = "TapGoPay"
        const val SHARED_PREFERENCES: String = "SHARED_PREFERENCES"
        const val EMAIL: String = "EMAIL"
        const val PRIVATE_KEY_FILENAME: String = "PRIVATE_KEY_FILENAME"
        const val PUBLIC_KEY_FILENAME: String = "PUBLIC_KEY_FILENAME"
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = Intent(this, NotificationService::class.java)
        ContextCompat.startForegroundService(this, intent)

        val appViewModel = AppViewModel(this@MainActivity.application)

        enableEdgeToEdge()
        setContent {
            TapGoPayTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = Routes.LoginScreen.name,
                ) {
                    composable(route = Routes.HomeScreen.name) {
                        HomeScreen(
                            appViewModel = appViewModel,
                            navigateTo = { route ->
                                when (route) {
                                    Routes.Logout -> {
                                        val intent =
                                            Intent(this@MainActivity, LoginActivity::class.java)
                                        startActivity(intent)
                                        finish()
                                    }

                                    else -> {
                                        navController.navigate(route = route.name)
                                    }
                                }
                            }
                        )
                    }

                    composable(route = Routes.PaymentScreen.name) {
                        PaymentScreen(
                            appViewModel = appViewModel,
                            goBack = {
                                navController.popBackStack()
                            },
                            navigateTo = { route ->
                                navController.navigate(route.name)
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
                }
            }
        }
    }
}

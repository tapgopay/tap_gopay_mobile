package com.example.tapgopay.screens

import android.app.Application
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tapgopay.R
import com.example.tapgopay.data.AuthViewModel
import com.example.tapgopay.data.MIN_PIN_LENGTH
import com.example.tapgopay.data.UIMessage
import com.example.tapgopay.screens.widgets.InputField
import com.example.tapgopay.screens.widgets.MessageBanner
import com.example.tapgopay.screens.widgets.PasswordField
import com.example.tapgopay.ui.theme.TapGoPayTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    navigateTo: (Routes) -> Unit,
    authViewModel: AuthViewModel = viewModel(),
) {
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
        ) {
            // Main Screen Content
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                ) {
                    Text(
                        text = "Login",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )

                    Text(
                        text = "Welcome Back!",
                        style = MaterialTheme.typography.headlineSmall,
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    InputField(
                        label = "Email",
                        value = authViewModel.email,
                        onValueChange = { value ->
                            authViewModel.email = value
                        },
                        leadingIconId = R.drawable.mail_24dp,
                        keyboardType = KeyboardType.Email,
                    )

                    PasswordField(
                        label = "Enter Your 4 Digit Pin",
                        value = authViewModel.pin,
                        onValueChange = { value ->
                            if (value.length > MIN_PIN_LENGTH) {
                                return@PasswordField
                            }
                            authViewModel.pin = value
                        },
                        onForgotPassword = {
                            navigateTo(Routes.ForgotPasswordScreen)
                        },
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 8.dp),
                    ) {
                        val scope = rememberCoroutineScope()

                        ElevatedButton(
                            onClick = {
                                scope.launch {
                                    val loginSuccess = authViewModel.loginUser()
                                    if (loginSuccess) {
                                        navigateTo(Routes.HomeScreen)
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 24.dp),
                            colors = ButtonDefaults.buttonColors().copy(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                            ),
                            shape = RoundedCornerShape(12.dp),
                        ) {
                            Text(
                                text = "Login",
                                modifier = Modifier.padding(vertical = 12.dp),
                                style = MaterialTheme.typography.titleLarge,
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "Don't have an account?",
                                style = MaterialTheme.typography.titleMedium,
                            )

                            TextButton(
                                onClick = {
                                    navigateTo(Routes.SignUpScreen)
                                },
                            ) {
                                Text(
                                    text = "Register",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium,
                                    textDecoration = TextDecoration.Underline,
                                )
                            }
                        }
                    }
                }
            }

            // Message Banner
            var error by remember { mutableStateOf<UIMessage?>(null) }

            LaunchedEffect(Unit) {
                authViewModel.uiMessages.collectLatest { uiMessage ->
                    error = uiMessage
                    launch {
                        delay(5000)  // Hide after n seconds
                        error = null
                    }
                }
            }

            error?.let {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.TopCenter,
                ) {
                    MessageBanner(it)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    val app = Application()
    val fakeViewModel = object : AuthViewModel(app) {
        // override state with sample data
    }

    TapGoPayTheme {
        LoginScreen(
            authViewModel = fakeViewModel,
            navigateTo = {},
        )
    }
}
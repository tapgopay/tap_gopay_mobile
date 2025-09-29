package com.example.tapgopay.screens

import android.app.Application
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.text.style.TextAlign
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
fun SignUpScreen(
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
                        .padding(horizontal = 8.dp, vertical = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "Let's get you started",
                        style = MaterialTheme.typography.headlineLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.primary,
                    )

                    Text(
                        "Create an account to send, receive, and manage money with ease.",
                        style = MaterialTheme.typography.titleLarge,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.scrim.copy(
                            alpha = 0.5f
                        ),
                        fontWeight = FontWeight.Medium,
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    InputField(
                        label = "Enter your username",
                        value = authViewModel.username,
                        onValueChange = { value ->
                            authViewModel.username = value
                        },
                        leadingIconId = R.drawable.person_24dp,
                    )

                    InputField(
                        label = "Enter your email",
                        value = authViewModel.email,
                        onValueChange = { value ->
                            authViewModel.email = value
                        },
                        keyboardType = KeyboardType.Email,
                        leadingIconId = R.drawable.mail_24dp,
                    )

                    InputField(
                        label = "Enter your phone number",
                        value = authViewModel.phoneNo,
                        onValueChange = { value ->
                            authViewModel.phoneNo = value
                        },
                        keyboardType = KeyboardType.Phone,
                        leadingIconId = R.drawable.call_24dp,
                    )

                    PasswordField(
                        label = "Enter 4 digit pin",
                        value = authViewModel.pin,
                        onValueChange = { value ->
                            if (value.length > MIN_PIN_LENGTH) {
                                return@PasswordField
                            }
                            authViewModel.pin = value
                        },
                    )

                    // Terms And Conditions section
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(12.dp),
                    ) {
                        Checkbox(
                            checked = authViewModel.agreedToTerms,
                            onCheckedChange = { value ->
                                authViewModel.agreedToTerms = value
                            },
                            modifier = Modifier.size(32.dp),
                        )

                        Text(
                            text = "I agree to the Terms and Conditions",
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        val scope = rememberCoroutineScope()

                        ElevatedButton(
                            onClick = {
                                scope.launch {
                                    val signupSuccess = authViewModel.registerUser()
                                    if (signupSuccess) {
                                        delay(2000)
                                        navigateTo(Routes.LoginScreen)
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            colors = ButtonDefaults.buttonColors().copy(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary,
                            ),
                            shape = RoundedCornerShape(12.dp),
                        ) {
                            Text(
                                text = "Create Account",
                                modifier = Modifier.padding(vertical = 12.dp),
                                style = MaterialTheme.typography.titleLarge,
                            )
                        }

                        Row(
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Already have an account?",
                                style = MaterialTheme.typography.titleMedium,
                            )

                            TextButton(
                                onClick = {
                                    navigateTo(Routes.LoginScreen)
                                },
                            ) {
                                Text(
                                    text = "Login",
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
fun PreviewSignUpScreen() {
    val app = Application()
    val fakeViewModel = object : AuthViewModel(app) {
        // override state with sample data
    }

    TapGoPayTheme {
        SignUpScreen(
            authViewModel = fakeViewModel,
            navigateTo = {}
        )
    }
}
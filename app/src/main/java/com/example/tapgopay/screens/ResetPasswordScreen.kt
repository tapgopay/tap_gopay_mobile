package com.example.tapgopay.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.tapgopay.R
import com.example.tapgopay.data.AuthViewModel
import com.example.tapgopay.screens.widgets.EnterOtpNumber
import com.example.tapgopay.screens.widgets.MessageBanner
import com.example.tapgopay.screens.widgets.PasswordField
import com.example.tapgopay.utils.titlecase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordScreen(
    authViewModel: AuthViewModel,
    navigateTo: (Routes) -> Unit,
) {
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        authViewModel.errors.collectLatest {
            error = it
            launch {
                delay(5000)  // Hide after n seconds
                error = null
            }
        }
    }

    error?.let {
        MessageBanner(it.titlecase())
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        topBar = {
            CenterAlignedTopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navigateTo(Routes.LoginScreen)
                        },
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.chevron_left_24dp),
                            contentDescription = "Back to Login Page",
                            modifier = Modifier.size(32.dp),
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors().copy(
                    containerColor = MaterialTheme.colorScheme.onPrimary,
                )
            )
        }
    ) { innerPadding ->
        val scope = rememberCoroutineScope()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            var index by remember { mutableIntStateOf(0) }

            when (index) {
                0 -> {
                    EnterOtpNumber(
                        requiredOtpLength = AuthViewModel.MIN_OTP_LENGTH,
                        otpNumber = authViewModel.otpNumber,
                        onNewOtpValue = { newOtpValue ->
                            authViewModel.otpNumber = newOtpValue

                            if (authViewModel.otpNumber.length == AuthViewModel.MIN_OTP_LENGTH) {
                                index++
                            }
                        },
                        resendOtp = {
                            scope.launch {
                                authViewModel.forgotPassword()
                            }
                        }
                    )
                }

                1 -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(24.dp),
                        ) {
                            Text(
                                text = "Reset Password",
                                style = MaterialTheme.typography.headlineSmall,
                            )

                            Text(
                                text = "Enter your new password",
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }

                        PasswordField(
                            label = "New Password",
                            value = authViewModel.password,
                            onValueChanged = { value ->
                                authViewModel.password = value
                            },
                        )

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(horizontal = 8.dp),
                        ) {
                            ElevatedButton(
                                onClick = {
                                    scope.launch {
                                        authViewModel.resetPassword()
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 24.dp),
                                colors = ButtonDefaults.buttonColors().copy(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onPrimary,
                                ),
                                shape = RoundedCornerShape(8.dp),
                            ) {
                                Text(
                                    text = "Send Password Reset",
                                    modifier = Modifier.padding(vertical = 8.dp),
                                    style = MaterialTheme.typography.bodyLarge,
                                )
                            }
                        }
                    }
                }
            }

        }
    }
}

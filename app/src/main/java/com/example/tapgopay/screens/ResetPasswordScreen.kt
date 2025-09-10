package com.example.tapgopay.screens

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tapgopay.R
import com.example.tapgopay.data.AuthViewModel
import com.example.tapgopay.data.MIN_OTP_LENGTH
import com.example.tapgopay.data.UIMessage
import com.example.tapgopay.screens.widgets.DialPad
import com.example.tapgopay.screens.widgets.MessageBanner
import com.example.tapgopay.screens.widgets.PasswordField
import com.example.tapgopay.ui.theme.TapGoPayTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResetPasswordScreen(
    authViewModel: AuthViewModel,
    navigateTo: (Routes) -> Unit,
) {
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
        MessageBanner(it)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
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
                            painter = painterResource(R.drawable.baseline_arrow_back_24),
                            contentDescription = "Back to Login Page",
                            modifier = Modifier.size(48.dp),
                        )
                    }
                },
            )
        }
    ) { innerPadding ->
        val scope = rememberCoroutineScope()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
        ) {
            var index by remember { mutableIntStateOf(0) }

            when (index) {
                0 -> {
                    EnterOtpNumber(
                        requiredOtpLength = MIN_OTP_LENGTH,
                        otp = authViewModel.otp,
                        onNewOtpValue = { newOtpValue ->
                            authViewModel.otp = newOtpValue

                            if (authViewModel.otp.length == MIN_OTP_LENGTH) {
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
                            value = authViewModel.pin,
                            onValueChange = { value ->
                                authViewModel.pin = value
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
                                shape = RoundedCornerShape(50),
                            ) {
                                Text(
                                    text = "Send Password Reset",
                                    modifier = Modifier.padding(vertical = 12.dp),
                                    style = MaterialTheme.typography.titleLarge,
                                )
                            }
                        }
                    }
                }
            }

        }
    }
}

@Composable
fun EnterOtpNumber(
    requiredOtpLength: Int,
    otp: String,
    onNewOtpValue: (String) -> Unit,
    resendOtp: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(top = 48.dp),
        ) {
            Text(
                "Enter OTP code",
                style = MaterialTheme.typography.headlineLarge,
            )

            Text(
                "An OTP code has been sent to your email. Enter OTP code to continue",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
            )
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp),
                modifier = Modifier.padding(vertical = 12.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    repeat(requiredOtpLength) { index ->
                        val isColored = index < otp.length
                        val borderColor =
                            if (isColored) Color.Transparent else MaterialTheme.colorScheme.scrim
                        val backgroundColor =
                            if (isColored) MaterialTheme.colorScheme.primary else Color.Transparent

                        val otpChar: String =
                            otp.toCharArray().getOrNull(index)?.toString() ?: ""

                        Box(
                            modifier = Modifier
                                .border(2.dp, color = borderColor, shape = RoundedCornerShape(8.dp))
                                .background(
                                    color = backgroundColor,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .size(64.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                otpChar,
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onPrimary,
                            )
                        }
                    }
                }

                TextButton(
                    onClick = resendOtp,
                ) {
                    Text(
                        "Get new code or resend",
                        style = MaterialTheme.typography.titleLarge,
                        textDecoration = TextDecoration.Underline,
                    )
                }
            }

            DialPad(
                value = otp,
                onValueChange = { newValue ->
                    if (newValue.isNotEmpty() && newValue.last() == '.') {
                        return@DialPad
                    }

                    onNewOtpValue(newValue)
                },
            )
        }
    }
}

@Preview(showBackground = true, device = Devices.PIXEL)
@Composable
fun PreviewEnterOtpNumber() {
    TapGoPayTheme {
        EnterOtpNumber(
            requiredOtpLength = 4,
            otp = "123",
            onNewOtpValue = {},
            resendOtp = {},
        )
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_7)
@Composable
fun PreviewResetPasswordScreen() {
    val app = Application()
    val fakeViewModel = object : AuthViewModel(app) {
        // override state with sample data
    }

    TapGoPayTheme {
        ResetPasswordScreen(
            authViewModel = fakeViewModel,
            navigateTo = {},
        )
    }
}
package com.example.tapgopay.screens.widgets

import androidx.compose.animation.core.InfiniteRepeatableSpec
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tapgopay.data.Status

@Composable
fun EnterPinNumber(
    subTitle: String = "",
    pinNumber: String,
    onNewPinNumber: (String) -> Unit,
    authStatus: Status?,
    prev: () -> Unit,
    forgotPin: (() -> Unit)? = null,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Navbar(
            title = "",
            prev = prev,
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 24.dp),
        ) {
            Text(
                "Enter Your Pin",
                style = MaterialTheme.typography.headlineSmall,
            )

            Text(
                subTitle,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
            )
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(32.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    repeat(4) { index ->
                        val isColored = index < pinNumber.length
                        val borderColor =
                            if (isColored) Color.Transparent else MaterialTheme.colorScheme.scrim
                        val backgroundColor = when {
                            isColored -> {
                                if (authStatus == Status.Success) {
                                    MaterialTheme.colorScheme.tertiary
                                } else {
                                    MaterialTheme.colorScheme.primary
                                }
                            }

                            else -> Color.Transparent
                        }
                        val isAuthenticating =
                            authStatus == Status.Loading || authStatus == Status.Success

                        val animatedWidth by animateDpAsState(
                            targetValue = if (isAuthenticating) 28.dp else 24.dp,
                            animationSpec = if (isAuthenticating) {
                                InfiniteRepeatableSpec(
                                    animation = tween(durationMillis = 400),
                                    repeatMode = RepeatMode.Reverse,
                                )
                            } else tween(durationMillis = 400),
                            label = ""
                        )

                        Box(
                            modifier = Modifier
                                .border(2.dp, color = borderColor, shape = CircleShape)
                                .background(color = backgroundColor, shape = CircleShape)
                                .size(animatedWidth)
                        )
                    }
                }

                forgotPin?.let {
                    TextButton(
                        onClick = forgotPin,
                    ) {
                        Text(
                            "Forgot Your Pin?",
                            style = MaterialTheme.typography.bodyLarge,
                            textDecoration = TextDecoration.Underline,
                        )
                    }
                }
            }

            SoftKeyboard(
                value = pinNumber,
                onValueChange = { newValue ->
                    onNewPinNumber(newValue)
                },
            )
        }
    }
}

@Preview(showBackground = true, device = Devices.PIXEL)
@Composable
fun PreviewEnterPinNumber() {
    MaterialTheme {
        EnterPinNumber(
            pinNumber = "12",
            onNewPinNumber = {},
            authStatus = null,
            prev = {},
            forgotPin = {},
        )
    }
}
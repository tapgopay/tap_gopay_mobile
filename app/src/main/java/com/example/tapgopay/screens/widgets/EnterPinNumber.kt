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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tapgopay.data.MIN_PIN_LENGTH
import com.example.tapgopay.ui.theme.TapGoPayTheme

@Composable
fun EnterPinNumber(
    subtitle: String = "",
    goBack: () -> Unit,
    onContinue: (String) -> Unit,
    isLoading: Boolean = false,
    forgotPin: (() -> Unit)? = null,
) {
    var pin by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Navbar(
            title = "",
            goBack = goBack,
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
                subtitle,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.padding(vertical = 24.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(32.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                repeat(4) { index ->
                    val isColored = index < pin.length

                    val animatedWidth by animateDpAsState(
                        targetValue = if (isLoading) 40.dp else 32.dp,
                        animationSpec = if (isLoading) {
                            InfiniteRepeatableSpec(
                                animation = tween(durationMillis = 400),
                                repeatMode = RepeatMode.Reverse,
                            )
                        } else tween(durationMillis = 400),
                        label = ""
                    )

                    Box(
                        modifier = Modifier
                            .border(2.dp, Color.Transparent, shape = CircleShape)
                            .background(
                                color = if (isColored) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.surface
                                },
                                shape = CircleShape,
                            )
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
                        style = MaterialTheme.typography.titleMedium,
                        textDecoration = TextDecoration.Underline,
                    )
                }
            }
        }

        DialPad(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            value = pin,
            onValueChange = { newValue ->
                if (newValue.length > MIN_PIN_LENGTH) {
                    return@DialPad
                }
                pin = newValue
            },
        )

        ElevatedButton(
            onClick = {
                if (pin.length != MIN_PIN_LENGTH) {
                    return@ElevatedButton
                }
                onContinue(pin)
            },
            enabled = pin.length == MIN_PIN_LENGTH,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 24.dp),
            colors = ButtonDefaults.buttonColors().copy(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
            shape = RoundedCornerShape(50),
        ) {
            Text(
                text = "Continue",
                modifier = Modifier.padding(vertical = 12.dp),
                style = MaterialTheme.typography.titleLarge,
            )
        }
    }
}

@Preview(showBackground = true, device = Devices.PIXEL)
@Composable
fun PreviewEnterPinNumber() {
    TapGoPayTheme {
        EnterPinNumber(
            subtitle = "Never share your pin with anyone",
            goBack = {},
            onContinue = {},
        )
    }
}
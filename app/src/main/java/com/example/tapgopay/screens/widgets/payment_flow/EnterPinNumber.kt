package com.example.tapgopay.screens.widgets.payment_flow

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
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tapgopay.data.MIN_PIN_LENGTH
import com.example.tapgopay.screens.widgets.DialPad
import com.example.tapgopay.ui.theme.TapGoPayTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnterPinNumber(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String = "",
    onContinue: (String) -> Unit,
    onForgotPin: (() -> Unit)? = null,
) {
    var pin by remember { mutableStateOf("") }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
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
                title,
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center,
            )

            Text(
                subtitle,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.scrim.copy(
                    alpha = 0.5f
                ),
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.padding(vertical = 24.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                repeat(4) { index ->
                    val char: String =
                        pin.toCharArray().getOrNull(index)?.toString() ?: ""

                    Box(
                        modifier = Modifier
                            .border(
                                width = 2.dp,
                                color = Color.Transparent,
                                shape = RoundedCornerShape(24.dp)
                            )
                            .background(
                                color = MaterialTheme.colorScheme.surfaceContainer,
                                shape = RoundedCornerShape(24.dp)
                            )
                            .size(72.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            char,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }

            onForgotPin?.let {
                TextButton(
                    onClick = onForgotPin,
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
            shape = RoundedCornerShape(12.dp),
        ) {
            Text(
                text = "Continue",
                modifier = Modifier.padding(vertical = 12.dp),
                style = MaterialTheme.typography.titleLarge,
            )
        }
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_7)
@Composable
fun PreviewEnterPinNumber() {
    TapGoPayTheme {
        EnterPinNumber(
            title = "Enter your pin",
            subtitle = "Enter your PIN to confirm this transaction.",
            onContinue = {},
        )
    }
}
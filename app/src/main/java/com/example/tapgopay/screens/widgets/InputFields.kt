package com.example.tapgopay.screens.widgets

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tapgopay.R
import com.example.tapgopay.ui.theme.TapGoPayTheme


@Composable
fun textFieldColors() = TextFieldDefaults.colors().copy(
    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
    disabledIndicatorColor = Color.Transparent,
    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
)

@Composable
fun InputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    @DrawableRes leadingIconId: Int? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleLarge,
        )

        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(8.dp)),
            textStyle = MaterialTheme.typography.titleLarge,
            leadingIcon = {
                // Display leading icon if leadingIconId is not null
                leadingIconId?.let {
                    Icon(
                        painter = painterResource(leadingIconId),
                        contentDescription = null,
                    )
                }
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = keyboardType
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            colors = textFieldColors()
        )

    }
}

@Composable
fun PasswordField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    onForgotPassword: (() -> Unit)? = null,
) {
    var passwordVisible: Boolean by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleLarge,
        )

        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(8.dp)),
            textStyle = MaterialTheme.typography.titleLarge,
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.lock_24dp),
                    contentDescription = null,
                )
            },
            trailingIcon = {
                val trailingIconId: Int = if (passwordVisible) {
                    R.drawable.visibility_off
                } else {
                    R.drawable.visibility
                }

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        painter = painterResource(id = trailingIconId),
                        contentDescription = "display password",
                        tint = MaterialTheme.colorScheme.surfaceTint,
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.NumberPassword,
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            colors = textFieldColors(),
        )

        onForgotPassword?.let {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = onForgotPassword,
                ) {
                    Text(
                        text = "Forgot Password?",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@Composable
fun NumberInput(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    min: Int = Int.MIN_VALUE,
    max: Int = Int.MAX_VALUE,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            label,
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = {
                    onValueChange(value - 1)
                },
                enabled = value > min,
                colors = IconButtonDefaults.iconButtonColors().copy(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            ) {
                Icon(
                    painter = painterResource(R.drawable.remove_24dp),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
            }

            Text(
                "$value",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            IconButton(
                onClick = {
                    onValueChange(value + 1)
                },
                colors = IconButtonDefaults.iconButtonColors().copy(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                enabled = value < max,
            ) {
                Icon(
                    painter = painterResource(R.drawable.add_24dp),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewInputField() {
    TapGoPayTheme {
        InputField(
            label = "Enter Username",
            leadingIconId = R.drawable.person_24dp,
            value = "John Doe",
            onValueChange = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPasswordField() {
    TapGoPayTheme {
        PasswordField(
            label = "Enter Password",
            value = "Password",
            onValueChange = {},
            onForgotPassword = {}
        )
    }
}
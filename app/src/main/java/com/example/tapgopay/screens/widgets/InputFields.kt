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
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
fun defaultTextFieldColors() = TextFieldDefaults.colors().copy(
    focusedIndicatorColor = MaterialTheme.colorScheme.surfaceContainer,
    unfocusedIndicatorColor = MaterialTheme.colorScheme.surfaceContainer,
    disabledIndicatorColor = MaterialTheme.colorScheme.surfaceContainer,
    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
)

@Composable
fun transparentTextFieldColors() = TextFieldDefaults.colors().copy(
    focusedIndicatorColor = Color.Transparent,
    unfocusedIndicatorColor = Color.Transparent,
    disabledIndicatorColor = Color.Transparent,
    focusedContainerColor = Color.Transparent,
    unfocusedContainerColor = Color.Transparent,
)

@Composable
fun InputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    colors: TextFieldColors = defaultTextFieldColors(),
    @DrawableRes leadingIconId: Int? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .height(64.dp),
        textStyle = MaterialTheme.typography.titleMedium,
        leadingIcon = {
            // Display leading icon if leadingIconId is not null
            leadingIconId?.let {
                Icon(
                    painter = painterResource(leadingIconId),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp),
                )
            }
        },
        placeholder = {
            Text(
                text = label,
                style = MaterialTheme.typography.titleMedium,
            )
        },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = keyboardType
        ),
        shape = RoundedCornerShape(8.dp),
        singleLine = true,
        colors = colors,
    )
}

@Composable
fun PasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    onForgotPassword: (() -> Unit)? = null,
) {
    var passwordVisible: Boolean by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .fillMaxWidth(),
    ) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            textStyle = MaterialTheme.typography.titleMedium,
            leadingIcon = {
                Icon(
                    painter = painterResource(R.drawable.lock_24dp),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp),
                )
            },
            placeholder = {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium,
                )
            },
            trailingIcon = {
                val trailingIconId: Int = if (passwordVisible) {
                    R.drawable.visibility_off_24dp
                } else {
                    R.drawable.visibility_24dp
                }

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        painter = painterResource(id = trailingIconId),
                        contentDescription = "display password",
                        tint = MaterialTheme.colorScheme.surfaceTint,
                        modifier = Modifier.size(32.dp),
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.NumberPassword,
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            colors = defaultTextFieldColors(),
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
fun NumberInputColumn(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    min: Int = Int.MIN_VALUE,
    max: Int = Int.MAX_VALUE,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            label,
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
        )

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                onClick = {
                    onValueChange(value - 1)
                },
                enabled = value > min,
                colors = IconButtonDefaults.iconButtonColors().copy(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    contentColor = MaterialTheme.colorScheme.primary,
                ),
            ) {
                Icon(
                    painter = painterResource(R.drawable.remove_24dp),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }

            Text(
                "$value",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(horizontal = 32.dp)
            )

            IconButton(
                onClick = {
                    onValueChange(value + 1)
                },
                colors = IconButtonDefaults.iconButtonColors().copy(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    contentColor = MaterialTheme.colorScheme.primary,
                ),
                enabled = value < max,
            ) {
                Icon(
                    painter = painterResource(R.drawable.add_24dp),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
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
fun PreviewNumberInput() {
    TapGoPayTheme {
        NumberInputColumn(
            label = "Enter number",
            value = 4,
            onValueChange = {},
            min = 0,
            max = 10
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
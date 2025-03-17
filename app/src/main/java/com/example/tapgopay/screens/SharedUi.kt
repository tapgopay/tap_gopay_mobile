package com.example.tapgopay.screens

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tapgopay.MainActivity
import com.example.tapgopay.R
import com.example.tapgopay.utils.titlecase

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
    labelText: String,
    value: String,
    onValueChanged: (String) -> Unit,
    @DrawableRes leadingIconId: Int? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        Text(
            text = labelText,
            style = MaterialTheme.typography.bodyLarge,
        )

        TextField(
            value = value,
            onValueChange = onValueChanged,
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(8.dp)),
            textStyle = MaterialTheme.typography.bodyLarge,
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
    labelText: String,
    value: String,
    onValueChanged: (String) -> Unit,
    displayForgotPassword: Boolean = false,
) {
    var passwordVisible: Boolean by remember { mutableStateOf(false) }

    Column(
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = labelText,
            style = MaterialTheme.typography.bodyLarge,
        )

        TextField(
            value = value,
            onValueChange = onValueChanged,
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(8.dp)),
            textStyle = MaterialTheme.typography.bodyLarge,
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
                keyboardType = KeyboardType.Password
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            colors = textFieldColors(),
        )

        if (displayForgotPassword) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = {
                    Log.d(MainActivity.TAG, "Forgot password button clicked")
                }) {
                    Text(
                        text = "Forgot Password?",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ErrorMessage(
    message: String,
    onDismissRequest: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState()

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        shape = RoundedCornerShape(4.dp),
        sheetState = sheetState,
        dragHandle = null,
        containerColor = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
    ) {
        Text(
            text = message.titlecase(),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(24.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewInputField() {
    MaterialTheme {
        InputField(
            labelText = "Enter Username",
            leadingIconId = R.drawable.person_24dp,
            value = "Death By Romy",
            onValueChanged = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPasswordField() {
    MaterialTheme {
        PasswordField(
            labelText = "Enter Password",
            value = "NoMercy",
            onValueChanged = {},
        )
    }
}
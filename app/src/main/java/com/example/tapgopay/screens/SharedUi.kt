package com.example.tapgopay.screens

import android.util.Log
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Snackbar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.example.tapgopay.MainActivity
import com.example.tapgopay.R
import com.example.tapgopay.utils.titlecase


@Composable
fun InputField(
    labelText: String,
    value: String,
    onValueChanged: (String) -> Unit,
    @DrawableRes leadingIconId: Int? = null,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChanged,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
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
        label = {
            Text(
                text = labelText,
                style = MaterialTheme.typography.labelLarge,
            )
        },
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = keyboardType
        ),
        shape = RoundedCornerShape(4.dp),
        singleLine = true,
    )
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
    ) {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChanged,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
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
            label = {
                Text(
                    text = labelText, style = MaterialTheme.typography.labelLarge
                )
            },
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Password
            ),
            shape = RoundedCornerShape(4.dp),
            singleLine = true,
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

@Composable
fun ErrorMessage(message: String) {
    Snackbar(
        containerColor = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
        shape = RoundedCornerShape(4.dp),
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 12.dp),
    ) {
        Text(
            text = message.titlecase(),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(4.dp),
        )
    }
}

@Composable
fun NoItemsFound(
    modifier: Modifier = Modifier,
    errMessage: String,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(R.drawable.empty_box_icon),
            contentDescription = "No Items Found",
            modifier = Modifier
                .size(240.dp)
                .padding(vertical=18.dp),
        )

        Text(
            errMessage,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
        )
    }
}
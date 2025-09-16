package com.example.tapgopay.screens.widgets

import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.tapgopay.R
import com.example.tapgopay.data.AppViewModel
import com.example.tapgopay.screens.Routes

@Composable
fun Menu(
    appViewModel: AppViewModel,
    navigateTo: (route: Routes) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Column {
        IconButton(
            onClick = {
                expanded = !expanded
            }
        ) {
            Icon(
                painter = painterResource(R.drawable.menu_24dp),
                contentDescription = "Menu",
                modifier = Modifier.size(32.dp),
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            MenuItem(
                text = "View Profile",
                onClick = {
                    expanded = false
                    navigateTo(Routes.ProfileScreen)
                },
                leadingIconId = R.drawable.person_24dp
            )

            MenuItem(
                text = "Messages",
                onClick = {
                    expanded = false
                    Toast.makeText(context, "Not Yet Implemented", Toast.LENGTH_LONG)
                        .show()
                },
                leadingIconId = R.drawable.inbox_24dp,
                trailingIcon = {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(
                                MaterialTheme.colorScheme.tertiary,
                                shape = CircleShape
                            )
                    ) {
                        Text(
                            "2",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            )

            MenuItem(
                text = "Account Settings",
                onClick = {
                    expanded = false
                    Toast.makeText(context, "Not Yet Implemented", Toast.LENGTH_LONG)
                        .show()
                },
                leadingIconId = R.drawable.settings_24dp
            )

            HorizontalDivider()

            MenuItem(
                text = "Logout",
                onClick = {
                    expanded = false

                    appViewModel.clearCookies()
                    navigateTo(Routes.LoginScreen)
                },
                leadingIconId = R.drawable.logout_24dp
            )
        }
    }
}

@Composable
fun MenuItem(
    text: String,
    onClick: () -> Unit,
    @DrawableRes leadingIconId: Int,
    trailingIcon: @Composable (() -> Unit)? = null,
) {
    DropdownMenuItem(
        text = {
            Text(
                text,
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        onClick = onClick,
        modifier = Modifier
            .padding(4.dp)
            .width(172.dp),
        leadingIcon = {
            Icon(
                painter = painterResource(leadingIconId),
                contentDescription = null
            )
        },
        trailingIcon = trailingIcon
    )
}
package com.example.tapgopay.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tapgopay.R
import com.example.tapgopay.ui.theme.TapGoPayTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen() {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text("Profile")
                },
                navigationIcon = {
                    IconButton(
                        onClick = {}
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_arrow_back_24),
                            contentDescription = "Previous Screen",
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primary.copy(
                                    alpha = 0.8f
                                ),
                            ),
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(color = Color.White, shape = CircleShape)
                            .size(72.dp)
                            .clickable { },
                        contentAlignment = Alignment.BottomEnd,
                    ) {
                        Image(
                            painter = painterResource(R.drawable.avatar_thinking),
                            contentDescription = "Profile Picture",
                            modifier = Modifier.padding(4.dp)
                        )

                        Box(
                            modifier = Modifier.size(32.dp),
                            contentAlignment = Alignment.BottomEnd,
                        ) {
                            Image(
                                painter = painterResource(R.drawable.verified_image),
                                contentDescription = "Account Verified",
                            )
                        }
                    }

                    val currentTime = LocalDateTime.now()
                    val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                    val lastLogin = currentTime.format(formatter)

                    Column {
                        Text(
                            "Hari Bahadur",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                        Text(
                            "Last Login: $lastLogin",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                    }
                }
            }

            SettingsItem(
                title = "Add Nominee",
                subtitle = "Add a third person to your account in case of unexpected event",
                iconId = R.drawable.group_add_24dp,
                onClick = {}
            )

            SettingsItem(
                title = "Credit Cards",
                subtitle = "Manage all your physical and virtual credit cards",
                iconId = R.drawable.credit_card_24dp,
                onClick = {}
            )

            SettingsItem(
                title = "Device & Credentials",
                subtitle = "Manage your usernames and passwords",
                iconId = R.drawable.phone_android_24dp,
                onClick = {}
            )

            SettingsItem(
                title = "Delete My Account",
                iconId = R.drawable.delete_24dp,
                onClick = {}
            )
        }
    }
}

@Composable
fun SettingsItem(
    title: String,
    subtitle: String? = null,
    @DrawableRes iconId: Int,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .border(width = 2.dp, color = Color.Transparent, shape = RoundedCornerShape(4.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(iconId),
                contentDescription = null,
            )

            Column {
                Text(
                    title,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold,
                    ),
                )

                subtitle?.let {
                    Text(
                        subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }

    }

}

@Preview(showBackground = true, widthDp = 411, heightDp = 891)
@Composable
fun PreviewProfileScreen() {
    TapGoPayTheme {
        ProfileScreen()
    }
}
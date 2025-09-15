package com.example.tapgopay.screens

import android.content.Context
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tapgopay.MainActivity
import com.example.tapgopay.R
import com.example.tapgopay.ui.theme.TapGoPayTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navigateTo: (Routes) -> Unit,
    goBack: () -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Profile",
                        style = MaterialTheme.typography.headlineSmall,
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = goBack,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_arrow_back_24),
                            contentDescription = "Previous Screen",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(8.dp)
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
                        shape = RoundedCornerShape(32.dp)
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
                        Icon(
                            painter = painterResource(R.drawable.person_add_24dp),
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

                    val context = LocalContext.current
                    val sharedPreferences = remember {
                        context.getSharedPreferences(
                            MainActivity.SHARED_PREFERENCES,
                            Context.MODE_PRIVATE
                        )
                    }
                    val username = remember {
                        sharedPreferences.getString(MainActivity.USERNAME, "there") ?: "there"
                    }
                    val lastLogin = remember {
                        val currentTime = LocalDateTime.now()
                        val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                        currentTime.format(formatter)
                    }

                    Column {
                        Text(
                            "Hello $username",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                        Text(
                            "Last Login: $lastLogin",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Normal,
                            ),
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
                title = "Wallets",
                subtitle = "Manage all your physical and virtual wallets",
                iconId = R.drawable.wallet2_24dp,
                onClick = {}
            )

            SettingsItem(
                title = "Device & Credentials",
                subtitle = "Manage your usernames and passwords",
                iconId = R.drawable.phone_android_24dp,
                onClick = {}
            )

            SettingsItem(
                title = "Send or Request Money",
                subtitle = "Send or Request money from family, friends or business clients",
                iconId = R.drawable.qr_code_24dp,
                onClick = {
                    navigateTo(Routes.RequestPaymentScreen)
                }
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
        ) {
            Icon(
                painter = painterResource(iconId),
                contentDescription = null,
                modifier = Modifier
                    .size(32.dp)
                    .padding(top = 8.dp)
            )

            Column {
                Text(
                    title,
                    style = MaterialTheme.typography.titleMedium,
                )

                subtitle?.let {
                    Text(
                        subtitle,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Normal,
                        ),
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
        ProfileScreen(
            navigateTo = {},
            goBack = {},
        )
    }
}
package com.example.tapgopay.screens.widgets

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tapgopay.R
import com.example.tapgopay.screens.Routes
import com.example.tapgopay.ui.theme.TapGoPayTheme

@Composable
fun getContainerColor(currentRoute: Routes, selectedRoute: Routes): Color {
    return if (currentRoute == selectedRoute) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceContainer
    }
}

@Composable
fun getContentColor(currentRoute: Routes, selectedRoute: Routes): Color {
    return if (currentRoute == selectedRoute) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.scrim.copy(
            alpha = 0.6f
        )
    }
}

@Composable
fun MyBottomAppBar(
    modifier: Modifier = Modifier,
    currentRoute: Routes,
    navigateTo: (Routes) -> Unit,
) {
    BottomAppBar(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .shadow(elevation = 12.dp, shape = RoundedCornerShape(50)),
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        contentColor = MaterialTheme.colorScheme.scrim.copy(
            alpha = 0.6f
        ),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
        ) {
            // Home
            IconButton(
                onClick = {
                    navigateTo(Routes.HomeScreen)
                },
                modifier = Modifier.size(56.dp),
                colors = IconButtonDefaults.iconButtonColors().copy(
                    containerColor = getContainerColor(currentRoute, Routes.HomeScreen),
                    contentColor = getContentColor(currentRoute, Routes.HomeScreen),
                )
            ) {
                Icon(
                    painter = painterResource(R.drawable.home_24dp),
                    contentDescription = "View Home Page",
                    modifier = Modifier.size(32.dp)
                )
            }

            // Request Payment
            IconButton(
                onClick = {
                    navigateTo(Routes.RequestPaymentScreen)
                },
                modifier = Modifier.size(56.dp),
                colors = IconButtonDefaults.iconButtonColors().copy(
                    containerColor = getContainerColor(currentRoute, Routes.RequestPaymentScreen),
                    contentColor = getContentColor(currentRoute, Routes.RequestPaymentScreen),
                )
            ) {
                Icon(
                    painter = painterResource(R.drawable.request_payment_24dp),
                    contentDescription = "Request Payment",
                    modifier = Modifier.size(32.dp)
                )
            }

            // Scan QR Code
            IconButton(
                onClick = {
                    navigateTo(Routes.ScanQRCodeScreen)
                },
                modifier = Modifier.size(56.dp),
                colors = IconButtonDefaults.iconButtonColors().copy(
                    containerColor = getContainerColor(currentRoute, Routes.ScanQRCodeScreen),
                    contentColor = getContentColor(currentRoute, Routes.ScanQRCodeScreen),
                )
            ) {
                Icon(
                    painter = painterResource(R.drawable.qr_code_24dp),
                    contentDescription = "QR Code",
                    modifier = Modifier.size(32.dp)
                )
            }

            val context = LocalContext.current

            // View Wallet Details
            IconButton(
                onClick = {
                    // View Wallet Details
                    Toast.makeText(context, "Not Yet Implemented", Toast.LENGTH_SHORT)
                        .show()
                },
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.wallet_24dp),
                    contentDescription = "View Wallet",
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_7)
@Composable
fun PreviewMyBottomAppBar() {
    TapGoPayTheme {
        MyBottomAppBar(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            currentRoute = Routes.ScanQRCodeScreen,
            navigateTo = {},
        )
    }
}
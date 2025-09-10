package com.example.tapgopay.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedButton
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.tapgopay.R
import com.example.tapgopay.screens.widgets.MyBottomAppBar
import com.example.tapgopay.ui.theme.TapGoPayTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanQRCodeScreen(
    qrCodeContents: Map<String, String>,
    scanQRCode: () -> Unit,
    navigateTo: (Routes) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Scan QR Code",
                        style = MaterialTheme.typography.headlineSmall,
                    )
                },
                modifier = Modifier.padding(horizontal = 12.dp),
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navigateTo(Routes.HomeScreen)
                        },
                        modifier = Modifier.size(24.dp),
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.close_24dp),
                            contentDescription = "Go Back",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = scanQRCode,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.refresh_24dp),
                            contentDescription = "Go Back",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            )
        },
        bottomBar = {
            MyBottomAppBar(
                modifier = Modifier.padding(8.dp),
                currentRoute = Routes.ScanQRCodeScreen,
                navigateTo = navigateTo,
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                "Scan QR Code to receive or send money",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 48.dp),
            )

            // Regenerate the QR Code that was scanned.
            // This is more for aesthetic rather than utility purposes
            val qrCode: Bitmap? = remember(qrCodeContents) {
                generateQRCode(qrCodeContents)
            }

            // Display QR Code
            CornerBorderBox(
                modifier = Modifier
                    .size(256.dp)
                    .padding(vertical = 20.dp)
            ) {
                qrCode?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "QR Code",
                        contentScale = ContentScale.Fit
                    )
                }
            }

            // QR Code contents
            LazyColumn(
                modifier = Modifier.padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                itemsIndexed(qrCodeContents.keys.toList()) { index, key ->
                    val value = qrCodeContents[key]
                    val message = buildAnnotatedString {
                        append(key.replaceFirstChar { it.uppercaseChar() })
                        append(": ")

                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium,
                            )
                        ) {
                            append(value)
                        }
                    }
                    Text(
                        message,
                        style = MaterialTheme.typography.titleLarge,
                    )
                }
            }

            ElevatedButton(
                onClick = {
                    // TODO: Send request funds request to server
                },
                enabled = qrCodeContents.isNotEmpty(),
                colors = ButtonDefaults.buttonColors().copy(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                shape = RoundedCornerShape(50),
            ) {
                Text(
                    text = "Send Money",
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
                    style = MaterialTheme.typography.titleLarge,
                )
            }
        }
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_7)
@Composable
fun PreviewScanQRCodeScreen() {
    TapGoPayTheme {
        ScanQRCodeScreen(
            qrCodeContents = mapOf(
                "receiver" to "John Doe",
                "amount" to "25"
            ),
            scanQRCode = {},
            navigateTo = {},
        )
    }
}
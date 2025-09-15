package com.example.tapgopay.screens

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set
import com.example.tapgopay.R
import com.example.tapgopay.screens.widgets.InputField
import com.example.tapgopay.screens.widgets.MyBottomAppBar
import com.example.tapgopay.ui.theme.TapGoPayTheme
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestPaymentScreen(
    navigateTo: (Routes) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Create Payment Request",
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
            )
        },
        bottomBar = {
            MyBottomAppBar(
                modifier = Modifier.padding(8.dp),
                currentRoute = Routes.RequestPaymentScreen,
                navigateTo = navigateTo,
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                "Request funds from family, friends or business clients",
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp),
            )

            var amount by remember { mutableStateOf<Double?>(null) }
            var receiver by remember { mutableStateOf<String>("") }
            val qrCode: Bitmap? = remember(amount, receiver) {
                val content = mapOf<String, String>(
                    "receiver" to receiver,
                    "amount" to amount.toString()
                )
                return@remember generateQRCode(content)
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(horizontal = 12.dp)
            ) {
                InputField(
                    value = amount?.toString() ?: "",
                    onValueChange = { newValue ->
                        newValue.toDoubleOrNull()?.let {
                            amount = it
                        }
                    },
                    label = "Enter Amount",
                    keyboardType = KeyboardType.Number
                )
                InputField(
                    value = receiver,
                    onValueChange = {
                        receiver = it
                    },
                    label = "Wallet/Phone Number",
                    keyboardType = KeyboardType.Number,
                )
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

            ElevatedButton(
                onClick = {
                    // TODO: Send payment request to server
                },
                enabled = qrCode != null,
                colors = ButtonDefaults.buttonColors().copy(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text(
                    text = "Request Payment",
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
                    style = MaterialTheme.typography.titleLarge,
                )
            }
        }
    }
}

@Composable
fun CornerBorderBox(
    modifier: Modifier = Modifier,
    cornerLength: Dp = 16.dp,
    strokeWidth: Dp = 4.dp,
    color: Color = MaterialTheme.colorScheme.primary,
    cornerRadius: Dp = 4.dp, // optional rounded corners
    content: @Composable BoxScope.() -> Unit
) {
    val density = LocalDensity.current
    val cornerPx = with(density) { cornerLength.toPx() }
    val strokePx = with(density) { strokeWidth.toPx() }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val width = size.width
            val height = size.height

            // Top-left corner
            drawLine(color, Offset(0f, 0f), Offset(cornerPx, 0f), strokePx)
            drawLine(color, Offset(0f, 0f), Offset(0f, cornerPx), strokePx)

            // Top-right corner
            drawLine(color, Offset(width - cornerPx, 0f), Offset(width, 0f), strokePx)
            drawLine(color, Offset(width, 0f), Offset(width, cornerPx), strokePx)

            // Bottom-left corner
            drawLine(color, Offset(0f, height - cornerPx), Offset(0f, height), strokePx)
            drawLine(color, Offset(0f, height), Offset(cornerPx, height), strokePx)

            // Bottom-right corner
            drawLine(color, Offset(width - cornerPx, height), Offset(width, height), strokePx)
            drawLine(color, Offset(width, height - cornerPx), Offset(width, height), strokePx)
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
                .padding(8.dp),
            contentAlignment = Alignment.Center,
        ) {
            content()
        }
    }
}


fun generateQRCode(content: Map<String, String>, size: Int = 512): Bitmap? {
    try {
        if (content.isEmpty()) return null

        val data: String = Gson().toJson(content)

        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(data, BarcodeFormat.QR_CODE, size, size)
        val bitmap = createBitmap(size, size, Bitmap.Config.RGB_565)

        for (x in 0 until size) {
            for (y in 0 until size) {
                bitmap[x, y] =
                    if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE
            }
        }
        return bitmap

    } catch (e: Exception) {
        // Error generating QR Code
        return null
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_7)
@Composable
fun PreviewRequestPaymentScreen() {
    TapGoPayTheme {
        RequestPaymentScreen(
            navigateTo = {},
        )
    }
}
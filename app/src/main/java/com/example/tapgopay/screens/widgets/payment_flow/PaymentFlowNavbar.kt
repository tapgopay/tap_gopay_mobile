package com.example.tapgopay.screens.widgets.payment_flow

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.tapgopay.R

@Composable
fun PaymentFlowNavbar(
    title: String,
    prev: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        IconButton(
            onClick = prev,
        ) {
            Icon(
                painter = painterResource(R.drawable.chevron_left_24dp),
                contentDescription = "Exit payment flow",
                modifier = Modifier.size(32.dp),
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Text(
            title,
            style = MaterialTheme.typography.headlineSmall,
        )

        Spacer(modifier = Modifier.weight(1f))
    }
}
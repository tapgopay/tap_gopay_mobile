package com.example.tapgopay.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.tapgopay.R
import com.example.tapgopay.screens.widgets.Menu
import com.example.tapgopay.screens.widgets.Transactions
import com.example.tapgopay.ui.theme.TapGoPayTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigateTo: (route: Routes) -> Unit,
) {
    val transactionsSheetState = rememberModalBottomSheetState()
    var viewAllTransactions by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Home",
                        style = MaterialTheme.typography.titleLarge,
                    )
                },
                modifier = Modifier.padding(horizontal = 16.dp),
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navigateTo(Routes.ProfileScreen)
                        },
                        modifier = Modifier.size(24.dp),
                    ) {
                        Image(
                            painter = painterResource(R.drawable.person_add_24dp),
                            contentDescription = "User Profile",
                        )
                    }
                },
                actions = {
                    Menu(
                        navigateTo = navigateTo
                    )
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                navigateHome = {
                    navigateTo(Routes.HomeScreen)
                },
                viewTransactions = {
                    viewAllTransactions = true
                    scope.launch {
                        transactionsSheetState.expand()
                    }
                },
                viewReports = {},
                manageAccount = {},
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CreditCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(224.dp)
                )

                CardActions(
                    onDetailsBtnClicked = {},
                    onTransferBtnClicked = {},
                    onLimitsBtnClicked = {},
                    onFreezeBtnClicked = {},
                )
            }

            Transactions()

            if(viewAllTransactions) {
                ModalBottomSheet(
                    onDismissRequest = {
                        viewAllTransactions = false
                    },
                    sheetState = transactionsSheetState,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        Transactions()
                    }
                }
            }
        }
    }
}

@Composable
fun CardActions(
    onDetailsBtnClicked: () -> Unit,
    onTransferBtnClicked: () -> Unit,
    onLimitsBtnClicked: () -> Unit,
    onFreezeBtnClicked: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
    ) {
        ActionButton(
            onClick = onDetailsBtnClicked,
            text = "Details",
            iconId = R.drawable.credit_card_24dp,
            defaultElevation = 4.dp
        )

        ActionButton(
            onClick = onTransferBtnClicked,
            text = "Transfer",
            iconId = R.drawable.arrow_upward_24dp,
            defaultElevation = 4.dp
        )

        ActionButton(
            onClick = onLimitsBtnClicked,
            text = "Limits",
            iconId = R.drawable.filter_alt_24dp,
            defaultElevation = 4.dp
        )

        ActionButton(
            onClick = onFreezeBtnClicked,
            text = "Freeze",
            iconId = R.drawable.mode_cool_24dp,
            defaultElevation = 4.dp
        )
    }
}

@Composable
fun BottomAppBar(
    navigateHome: () -> Unit,
    viewTransactions: () -> Unit,
    viewReports: () -> Unit,
    manageAccount: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        // Top Border
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(MaterialTheme.colorScheme.surfaceContainer)
        )

        // Content
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            ActionButton(
                onClick = navigateHome,
                text = "Home",
                iconId = R.drawable.home_24dp,
            )

            ActionButton(
                onClick = viewTransactions,
                text = "Transactions",
                iconId = R.drawable.credit_card_24dp,
            )

            ActionButton(
                onClick = viewReports,
                text = "Reports",
                iconId = R.drawable.bar_chart_24dp,
            )

            ActionButton(
                onClick = manageAccount,
                text = "Manage",
                iconId = R.drawable.widgets_24dp,
            )
        }
    }

}

@Composable
fun ActionButton(
    onClick: () -> Unit,
    text: String,
    @DrawableRes iconId: Int,
    defaultElevation: Dp = 0.dp
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
        modifier = Modifier.border(width = 0.dp, color = Color.Transparent, shape = CircleShape)
    ) {
        ElevatedButton(
            onClick = onClick,
            modifier = Modifier.size(44.dp),
            shape = CircleShape,
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.elevatedButtonColors().copy(
                containerColor = Color.White,
            ),
            elevation = ButtonDefaults.elevatedButtonElevation(
                defaultElevation = defaultElevation
            )
        ) {
            Icon(
                painter = painterResource(iconId),
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(24.dp)
            )
        }

        Text(
            text,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}


@Composable
fun CreditCard(
    modifier: Modifier,
) {
    Column {
        Card(
            modifier = modifier
                .clickable { },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors().copy(
                containerColor = MaterialTheme.colorScheme.primary.copy(
                    alpha = 0.9F
                ),
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.8f)
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(
                                    MaterialTheme.colorScheme.inversePrimary,
                                    shape = CircleShape
                                )
                        )

                        Text(
                            "Active",
                            style = MaterialTheme.typography.labelLarge,
                        )
                    }

                    Text(
                        "Physical Card",
                        style = MaterialTheme.typography.labelLarge,
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.wifi_24dp),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .size(28.dp)
                                .rotate(-90f)
                        )

                        Image(
                            painter = painterResource(R.drawable.chip),
                            contentDescription = null,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Text(
                        "1234 5678 9101 1213",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(0.8f)
                        .background(color = MaterialTheme.colorScheme.primary),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Text(
                                "Account Holder",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                "John Doe",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }

                        Column(
                            verticalArrangement = Arrangement.Center,
                        ) {
                            Text(
                                "Expires",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                "xx/xx",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        }

        // Balance
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 36.dp)
                .background(
                    color = MaterialTheme.colorScheme.onPrimary,
                    shape = RoundedCornerShape(4.dp),
                ),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                "Balance",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                ),
                modifier = Modifier.padding(12.dp)
            )

            Text(
                "€ 4,594",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                ),
                modifier = Modifier.padding(12.dp)
            )
        }
    }

}

@Preview(showBackground = true, widthDp = 411, heightDp = 891)
@Composable
fun PreviewHomeScreen() {
    TapGoPayTheme {
        HomeScreen(
            navigateTo = {}
        )
    }
}


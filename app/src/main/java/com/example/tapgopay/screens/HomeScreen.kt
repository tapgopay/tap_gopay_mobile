package com.example.tapgopay.screens

import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.tapgopay.MainActivity
import com.example.tapgopay.R
import com.example.tapgopay.ui.theme.TapGoPayTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navigateTo: (route: Routes) -> Unit,
) {
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
                            painter = painterResource(R.drawable.avatar_thinking),
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
            BottomAppBar()
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

                CardActions()
            }

            var showBottomSheet by remember { mutableStateOf(false) }
            val sheetState = rememberModalBottomSheetState()
            val scope = rememberCoroutineScope()

            Transactions(
                showMore = {
                    showBottomSheet = true
                    scope.launch {
                        Log.d(MainActivity.TAG, "Expanding bottom sheet")
                        sheetState.expand()
                    }
                }
            )

            if(showBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showBottomSheet = false
                    },
                    sheetState = sheetState,
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 24.dp)
                    ) {
                        Transactions()
                    }
                }
            }
        }
    }
}

@Composable
fun CardActions() {
    val context = LocalContext.current

    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
    ) {
        ActionButton(
            onClick = {
                Toast.makeText(context, "Not Yet Implemented", Toast.LENGTH_LONG)
                    .show()
            },
            text = "Details",
            iconId = R.drawable.credit_card_24dp,
            defaultElevation = 4.dp
        )

        ActionButton(
            onClick = {
                Toast.makeText(context, "Not Yet Implemented", Toast.LENGTH_LONG)
                    .show()
            },
            text = "Transfer",
            iconId = R.drawable.arrow_upward_24dp,
            defaultElevation = 4.dp
        )

        ActionButton(
            onClick = {
                Toast.makeText(context, "Not Yet Implemented", Toast.LENGTH_LONG)
                    .show()
            },
            text = "Limits",
            iconId = R.drawable.filter_alt_24dp,
            defaultElevation = 4.dp
        )

        ActionButton(
            onClick = {
                Toast.makeText(context, "Not Yet Implemented", Toast.LENGTH_LONG)
                    .show()
            },
            text = "Freeze",
            iconId = R.drawable.mode_cool_24dp,
            defaultElevation = 4.dp
        )
    }
}

@Composable
fun BottomAppBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
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
                onClick = {},
                text = "Home",
                iconId = R.drawable.home_24dp,
            )

            ActionButton(
                onClick = {},
                text = "Transactions",
                iconId = R.drawable.credit_card_24dp,
            )

            ActionButton(
                onClick = {},
                text = "Reports",
                iconId = R.drawable.bar_chart_24dp,
            )

            ActionButton(
                onClick = {},
                text = "Manage",
                iconId = R.drawable.widgets_24dp,
            )
        }
    }

}

@Composable
fun Transactions(
    showMore: (() -> Unit)? = null,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                "Transactions",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                ),
            )

            showMore?.let {
                TextButton(
                    onClick = {
                        showMore()
                    },
                ) {
                    Text(
                        "View All",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                        ),
                        textDecoration = TextDecoration.Underline,
                    )
                }
            }

        }

        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .padding(vertical = 4.dp)
                .fillMaxHeight()
                .verticalScroll(scrollState)
        ) {
            repeat(9) {
                TransactionCard()
            }
        }
    }
}

@Composable
fun TransactionCard() {
    Card(
        colors = CardDefaults.cardColors().copy(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.clickable { }
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceContainer,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        "J",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                        ),
                    )
                }

                Column {
                    Text(
                        "From James",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Bold,
                        ),
                    )
                    Text(
                        "Income - July 1st 2024, 12:32",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            Text(
                "+ € 24.00",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
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
fun Menu(
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
                contentDescription = "Menu"
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
                    Toast.makeText(context, "Not Yet Implemented", Toast.LENGTH_LONG)
                        .show()
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

@Composable
fun CreditCard(
    modifier: Modifier,
) {
    Column {
        Card(
            modifier = modifier
                .clickable { },
            shape = RoundedCornerShape(24.dp),
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


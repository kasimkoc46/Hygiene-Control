package com.hygienecontrol.app

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            HygieneControlApp(this)
        }
    }
}

@Composable
fun HygieneControlApp(context: Context) {

    var currentScreen by remember { mutableStateOf("home") }

    when (currentScreen) {

        "settings" -> {
            RestaurantSettings(
                context = context,
                onBack = {
                    currentScreen = "home"
                }
            )
        }

        else -> {
            ProfessionalDashboard(
                onSettingsClick = {
                    currentScreen = "settings"
                }
            )
        }
    }
}

@Composable
fun ProfessionalDashboard(
    onSettingsClick: () -> Unit
) {

    val darkBackground = Color(0xFF101414)
    val cardColor = Color(0xFF1A2020)
    val green = Color(0xFFB7D52B)
    val white = Color(0xFFF5F5F5)
    val grey = Color(0xFF9AA3A3)
    val orange = Color(0xFFFFB74D)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = darkBackground
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp)
            ) {

                Text(
                    text = "HYGIENE CONTROL",
                    color = green,
                    fontSize = 27.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Restaurant Food Safety Management",
                    color = grey,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Today's Overview",
                    color = white,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {

                    StatusCard(
                        modifier = Modifier.weight(1f),
                        number = "0",
                        title = "Completed",
                        color = green
                    )

                    StatusCard(
                        modifier = Modifier.weight(1f),
                        number = "0",
                        title = "Pending",
                        color = orange
                    )

                    StatusCard(
                        modifier = Modifier.weight(1f),
                        number = "0",
                        title = "Issues",
                        color = Color(0xFFE57373)
                    )
                }

                Spacer(modifier = Modifier.height(26.dp))

                SectionTitle("Daily Checks", green)

                DashboardActionCard(
                    title = "Hygiene Checks",
                    subtitle = "Daily food safety and hygiene controls",
                    cardColor = cardColor,
                    titleColor = white,
                    subtitleColor = grey
                )

                DashboardActionCard(
                    title = "Temperature Checks",
                    subtitle = "Fridge, freezer and food temperatures",
                    cardColor = cardColor,
                    titleColor = white,
                    subtitleColor = grey
                )

                DashboardActionCard(
                    title = "Cleaning Checks",
                    subtitle = "Cleaning tasks and completion records",
                    cardColor = cardColor,
                    titleColor = white,
                    subtitleColor = grey
                )

                Spacer(modifier = Modifier.height(22.dp))

                SectionTitle("Compliance", green)

                DashboardActionCard(
                    title = "Inspection Records",
                    subtitle = "View and manage inspection history",
                    cardColor = cardColor,
                    titleColor = white,
                    subtitleColor = grey
                )

                DashboardActionCard(
                    title = "Corrective Actions",
                    subtitle = "Record problems and corrective measures",
                    cardColor = cardColor,
                    titleColor = white,
                    subtitleColor = grey
                )

                DashboardActionCard(
                    title = "Staff Checks",
                    subtitle = "Staff hygiene and training records",
                    cardColor = cardColor,
                    titleColor = white,
                    subtitleColor = grey
                )

                Spacer(modifier = Modifier.height(22.dp))

                SectionTitle("Reports", green)

                DashboardActionCard(
                    title = "Inspection Report",
                    subtitle = "Prepare a complete inspection report",
                    cardColor = cardColor,
                    titleColor = white,
                    subtitleColor = grey
                )

                DashboardActionCard(
                    title = "PDF Reports",
                    subtitle = "Generate and export professional reports",
                    cardColor = cardColor,
                    titleColor = white,
                    subtitleColor = grey
                )

                Spacer(modifier = Modifier.height(22.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF202727)
                    ),
                    onClick = onSettingsClick
                ) {

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Text(
                                text = "Restaurant Settings",
                                color = white,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Restaurant information",
                                color = grey,
                                fontSize = 13.sp
                            )
                        }

                        Text(
                            text = "›",
                            color = green,
                            fontSize = 30.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            BottomNavigation(
                background = Color(0xFF151A1A),
                green = green,
                grey = grey
            )
        }
    }
}

@Composable
fun StatusCard(
    modifier: Modifier,
    number: String,
    title: String,
    color: Color
) {

    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A2020)
        )
    ) {

        Column(
            modifier = Modifier.padding(14.dp)
        ) {

            Text(
                text = number,
                color = color,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = title,
                color = Color(0xFF9AA3A3),
                fontSize = 11.sp
            )
        }
    }
}

@Composable
fun SectionTitle(
    title: String,
    color: Color
) {

    Text(
        text = title,
        color = color,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun DashboardActionCard(
    title: String,
    subtitle: String,
    cardColor: Color,
    titleColor: Color,
    subtitleColor: Color
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        shape = RoundedCornerShape(15.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(17.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = title,
                    color = titleColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = subtitle,
                    color = subtitleColor,
                    fontSize = 12.sp
                )
            }

            Text(
                text = "›",
                color = Color(0xFFB7D52B),
                fontSize = 28.sp
            )
        }
    }
}

@Composable
fun BottomNavigation(
    background: Color,
    green: Color,
    grey: Color
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(background)
            .navigationBarsPadding()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {

        BottomItem("Home", green)
        BottomItem("Checks", grey)
        BottomItem("Records", grey)
        BottomItem("Reports", grey)
        BottomItem("Settings", grey)
    }
}

@Composable
fun BottomItem(
    title: String,
    color: Color
) {

    Text(
        text = title,
        color = color,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
fun RestaurantSettings(
    context: Context,
    onBack: () -> Unit
) {

    val preferences = remember {
        context.getSharedPreferences(
            "restaurant_settings",
            Context.MODE_PRIVATE
        )
    }

    var restaurantName by remember {
        mutableStateOf(
            preferences.getString("restaurantName", "") ?: ""
        )
    }

    var address by remember {
        mutableStateOf(
            preferences.getString("address", "") ?: ""
        )
    }

    var phone by remember {
        mutableStateOf(
            preferences.getString("phone", "") ?: ""
        )
    }

    var email by remember {
        mutableStateOf(
            preferences.getString("email", "") ?: ""
        )
    }

    var manager by remember {
        mutableStateOf(
            preferences.getString("manager", "") ?: ""
        )
    }

    var saved by remember {
        mutableStateOf(false)
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF101414)
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp)
        ) {

            TextButton(
                onClick = onBack
            ) {
                Text(
                    text = "← Back",
                    color = Color(0xFFB7D52B)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Restaurant Settings",
                color = Color(0xFFF5F5F5),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(24.dp))

            SettingField(
                value = restaurantName,
                label = "Restaurant Name",
                onValueChange = {
                    restaurantName = it
                    saved = false
                }
            )

            SettingField(
                value = address,
                label = "Address",
                onValueChange = {
                    address = it
                    saved = false
                }
            )

            SettingField(
                value = phone,
                label = "Phone",
                onValueChange = {
                    phone = it
                    saved = false
                }
            )

            SettingField(
                value = email,
                label = "Email",
                onValueChange = {
                    email = it
                    saved = false
                }
            )

            SettingField(
                value = manager,
                label = "Manager / Owner",
                onValueChange = {
                    manager = it
                    saved = false
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {

                    preferences.edit()
                        .putString("restaurantName", restaurantName)
                        .putString("address", address)
                        .putString("phone", phone)
                        .putString("email", email)
                        .putString("manager", manager)
                        .apply()

                    saved = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {

                Text("Save Restaurant Information")
            }

            if (saved) {

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Information saved successfully.",
                    color = Color(0xFFB7D52B)
                )
            }
        }
    }
}

@Composable
fun SettingField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit
) {

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(label)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 14.dp)
    )
}

package com.hygienecontrol.app

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import org.json.JSONArray
import org.json.JSONObject
import java.util.Calendar


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            HygieneControlApp(this)
        }
    }
}


/* =========================================================
   MAIN APP
   ========================================================= */

@Composable
fun HygieneControlApp(context: Context) {

    var currentScreen by remember {
        mutableStateOf("home")
    }

    when (currentScreen) {

        "hygiene" -> {

            HygieneChecks(
                context = context,
                onBack = {
                    currentScreen = "home"
                }
            )
        }

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
                onHygieneClick = {
                    currentScreen = "hygiene"
                },
                onSettingsClick = {
                    currentScreen = "settings"
                }
            )
        }
    }
}


/* =========================================================
   PROFESSIONAL DASHBOARD
   ========================================================= */

@Composable
fun ProfessionalDashboard(
    onHygieneClick: () -> Unit,
    onSettingsClick: () -> Unit
) {

    val background = Color(0xFF101414)
    val cardColor = Color(0xFF1A2020)
    val green = Color(0xFFB7D52B)
    val white = Color(0xFFF5F5F5)
    val grey = Color(0xFF9AA3A3)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = background
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

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = "Restaurant Food Safety Management",
                    color = grey,
                    fontSize = 14.sp
                )

                Spacer(
                    modifier = Modifier.height(24.dp)
                )

                Text(
                    text = "Today's Overview",
                    color = white,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

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
                        color = Color(0xFFFFB74D)
                    )

                    StatusCard(
                        modifier = Modifier.weight(1f),
                        number = "0",
                        title = "Issues",
                        color = Color(0xFFE57373)
                    )
                }

                Spacer(
                    modifier = Modifier.height(26.dp)
                )

                SectionTitle(
                    title = "Daily Checks",
                    color = green
                )

                DashboardActionCard(
                    title = "Hygiene Checks",
                    subtitle = "Daily food safety and hygiene controls",
                    cardColor = cardColor,
                    titleColor = white,
                    subtitleColor = grey,
                    onClick = onHygieneClick
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

                Spacer(
                    modifier = Modifier.height(22.dp)
                )

                SectionTitle(
                    title = "Compliance",
                    color = green
                )

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

                Spacer(
                    modifier = Modifier.height(22.dp)
                )

                SectionTitle(
                    title = "Reports",
                    color = green
                )

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

                Spacer(
                    modifier = Modifier.height(22.dp)
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onSettingsClick()
                        },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF202727)
                    )
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

                            Spacer(
                                modifier = Modifier.height(4.dp)
                            )

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

                Spacer(
                    modifier = Modifier.height(20.dp)
                )
            }

            BottomNavigation(
                background = Color(0xFF151A1A),
                green = green,
                grey = grey,
                onSettingsClick = onSettingsClick
            )
        }
    }
}


/* =========================================================
   HYGIENE CHECK SCREEN
   ========================================================= */

@Composable
fun HygieneChecks(
    context: Context,
    onBack: () -> Unit
) {

    var recordDate by remember {
        mutableStateOf("")
    }

    var time by remember {
        mutableStateOf("")
    }

    var area by remember {
        mutableStateOf("")
    }

    var hygieneItem by remember {
        mutableStateOf("")
    }

    var staff by remember {
        mutableStateOf("")
    }

    var notes by remember {
        mutableStateOf("")
    }

    var status by remember {
        mutableStateOf("Pass")
    }

    var saved by remember {
        mutableStateOf(false)
    }

    val background = Color(0xFF101414)
    val white = Color.White
    val grey = Color(0xFF9AA3A3)
    val green = Color(0xFFB7D52B)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = background
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(
                    rememberScrollState()
                )
                .imePadding()
                .navigationBarsPadding()
                .padding(20.dp)
        ) {

            TextButton(
                onClick = onBack
            ) {

                Text(
                    text = "← Back",
                    color = green,
                    fontSize = 16.sp
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "Hygiene Check",
                color = white,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Record a food safety and hygiene check",
                color = grey,
                fontSize = 14.sp
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )


            /* =================================================
               DATE PICKER
               ================================================= */

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp)
                    .clickable {

                        val calendar =
                            Calendar.getInstance()

                        DatePickerDialog(
                            context,
                            { _, year, month, day ->

                                recordDate =
                                    "%02d/%02d/%04d".format(
                                        day,
                                        month + 1,
                                        year
                                    )

                                saved = false
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)
                        ).show()
                    }
            ) {

                OutlinedTextField(
                    value = recordDate,
                    onValueChange = {},
                    readOnly = true,
                    enabled = false,
                    label = {
                        Text("Record Date")
                    },
                    placeholder = {
                        Text("Select date")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(

                        disabledTextColor =
                            Color.White,

                        disabledLabelColor =
                            Color(0xFFB7D52B),

                        disabledPlaceholderColor =
                            Color(0xFF9AA3A3),

                        disabledBorderColor =
                            Color(0xFF777777)
                    )
                )
            }


            /* =================================================
               TIME PICKER
               ================================================= */

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp)
                    .clickable {

                        val calendar =
                            Calendar.getInstance()

                        TimePickerDialog(
                            context,
                            { _, hour, minute ->

                                time =
                                    "%02d:%02d".format(
                                        hour,
                                        minute
                                    )

                                saved = false
                            },
                            calendar.get(
                                Calendar.HOUR_OF_DAY
                            ),
                            calendar.get(
                                Calendar.MINUTE
                            ),
                            true
                        ).show()
                    }
            ) {

                OutlinedTextField(
                    value = time,
                    onValueChange = {},
                    readOnly = true,
                    enabled = false,
                    label = {
                        Text("Time")
                    },
                    placeholder = {
                        Text("Select time")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(

                        disabledTextColor =
                            Color.White,

                        disabledLabelColor =
                            Color(0xFFB7D52B),

                        disabledPlaceholderColor =
                            Color(0xFF9AA3A3),

                        disabledBorderColor =
                            Color(0xFF777777)
                    )
                )
            }


            /* =================================================
               CHECK AREA
               ================================================= */

            HygieneField(
                value = area,
                label = "Check Area",
                placeholder =
                    "e.g. Kitchen, Storage, Bar",
                onValueChange = {

                    area = it
                    saved = false
                }
            )


            /* =================================================
               HYGIENE ITEM
               ================================================= */

            HygieneField(
                value = hygieneItem,
                label = "Hygiene Item",
                placeholder =
                    "e.g. Hand washing station",
                onValueChange = {

                    hygieneItem = it
                    saved = false
                }
            )


            Spacer(
                modifier = Modifier.height(8.dp)
            )


            /* =================================================
               STATUS
               ================================================= */

            Text(
                text = "Status",
                color = white,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {

                Button(
                    onClick = {

                        status = "Pass"
                        saved = false
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(

                        containerColor =
                            if (status == "Pass")
                                Color(0xFF6E8F18)
                            else
                                Color(0xFF252D2D)
                    )
                ) {

                    Text(
                        text = "PASS",
                        fontWeight =
                            FontWeight.Bold
                    )
                }


                Button(
                    onClick = {

                        status = "Fail"
                        saved = false
                    },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(

                        containerColor =
                            if (status == "Fail")
                                Color(0xFF9E4545)
                            else
                                Color(0xFF252D2D)
                    )
                ) {

                    Text(
                        text = "FAIL",
                        fontWeight =
                            FontWeight.Bold
                    )
                }
            }


            Spacer(
                modifier = Modifier.height(16.dp)
            )


            /* =================================================
               STAFF
               ================================================= */

            HygieneField(
                value = staff,
                label = "Staff Member",
                placeholder =
                    "Enter staff name",
                onValueChange = {

                    staff = it
                    saved = false
                }
            )


            /* =================================================
               NOTES
               ================================================= */

            OutlinedTextField(
                value = notes,
                onValueChange = {

                    notes = it
                    saved = false
                },
                label = {
                    Text("Notes")
                },
                placeholder = {
                    Text("Additional information")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                colors =
                    OutlinedTextFieldDefaults.colors(

                        focusedTextColor =
                            Color.White,

                        unfocusedTextColor =
                            Color.White,

                        focusedLabelColor =
                            green,

                        unfocusedLabelColor =
                            grey,

                        cursorColor =
                            green
                    )
            )


            Spacer(
                modifier = Modifier.height(24.dp)
            )


            /* =================================================
               SAVE BUTTON
               ================================================= */

            Button(
                onClick = {

                    saveHygieneRecord(
                        context = context,
                        date = recordDate,
                        time = time,
                        area = area,
                        item = hygieneItem,
                        status = status,
                        staff = staff,
                        notes = notes
                    )

                    saved = true

                    recordDate = ""
                    time = ""
                    area = ""
                    hygieneItem = ""
                    staff = ""
                    notes = ""
                    status = "Pass"
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = green,
                    contentColor = Color.Black
                )
            ) {

                Text(
                    text = "Save Hygiene Check",
                    fontWeight = FontWeight.Bold
                )
            }


            if (saved) {

                Spacer(
                    modifier = Modifier.height(14.dp)
                )

                Text(
                    text =
                        "Hygiene check saved successfully.",
                    color = green,
                    fontWeight =
                        FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(30.dp)
            )
        }
    }
}


/* =========================================================
   HYGIENE TEXT FIELD
   ========================================================= */

@Composable
fun HygieneField(
    value: String,
    label: String,
    placeholder: String,
    onValueChange: (String) -> Unit
) {

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,

        label = {
            Text(label)
        },

        placeholder = {
            Text(placeholder)
        },

        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 14.dp),

        colors =
            OutlinedTextFieldDefaults.colors(

                focusedTextColor =
                    Color.White,

                unfocusedTextColor =
                    Color.White,

                focusedLabelColor =
                    Color(0xFFB7D52B),

                unfocusedLabelColor =
                    Color(0xFF9AA3A3),

                cursorColor =
                    Color(0xFFB7D52B)
            )
    )
}


/* =========================================================
   SAVE HYGIENE RECORD
   ========================================================= */

fun saveHygieneRecord(
    context: Context,
    date: String,
    time: String,
    area: String,
    item: String,
    status: String,
    staff: String,
    notes: String
) {

    val preferences =
        context.getSharedPreferences(
            "hygiene_records",
            Context.MODE_PRIVATE
        )

    val existing =
        preferences.getString(
            "records",
            "[]"
        ) ?: "[]"

    val records =
        JSONArray(existing)

    val record =
        JSONObject()

    record.put(
        "date",
        date
    )

    record.put(
        "time",
        time
    )

    record.put(
        "area",
        area
    )

    record.put(
        "item",
        item
    )

    record.put(
        "status",
        status
    )

    record.put(
        "staff",
        staff
    )

    record.put(
        "notes",
        notes
    )

    records.put(record)

    preferences.edit()
        .putString(
            "records",
            records.toString()
        )
        .apply()
}


/* =========================================================
   STATUS CARD
   ========================================================= */

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

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = title,
                color = Color(0xFF9AA3A3),
                fontSize = 11.sp
            )
        }
    }
}


/* =========================================================
   SECTION TITLE
   ========================================================= */

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
        modifier = Modifier.padding(
            bottom = 8.dp
        )
    )
}


/* =========================================================
   DASHBOARD ACTION CARD
   ========================================================= */

@Composable
fun DashboardActionCard(
    title: String,
    subtitle: String,
    cardColor: Color,
    titleColor: Color,
    subtitleColor: Color,
    onClick: (() -> Unit)? = null
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp)
            .then(

                if (onClick != null) {

                    Modifier.clickable {
                        onClick()
                    }

                } else {

                    Modifier
                }
            ),

        shape = RoundedCornerShape(15.dp),

        colors =
            CardDefaults.cardColors(
                containerColor = cardColor
            )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(17.dp),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = title,
                    color = titleColor,
                    fontSize = 16.sp,
                    fontWeight =
                        FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

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


/* =========================================================
   BOTTOM NAVIGATION
   ========================================================= */

@Composable
fun BottomNavigation(
    background: Color,
    green: Color,
    grey: Color,
    onSettingsClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(background)
            .navigationBarsPadding()
            .padding(vertical = 12.dp),

        horizontalArrangement =
            Arrangement.SpaceEvenly,

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        BottomItem(
            title = "Home",
            color = green
        )

        BottomItem(
            title = "Checks",
            color = grey
        )

        BottomItem(
            title = "Records",
            color = grey
        )

        BottomItem(
            title = "Reports",
            color = grey
        )

        BottomItem(
            title = "Settings",
            color = grey,
            onClick = onSettingsClick
        )
    }
}


/* =========================================================
   BOTTOM NAV ITEM
   ========================================================= */

@Composable
fun BottomItem(
    title: String,
    color: Color,
    onClick: (() -> Unit)? = null
) {

    Text(
        text = title,
        color = color,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,

        modifier = Modifier
            .padding(horizontal = 8.dp)
            .clickable {
                onClick?.invoke()
            }
    )
}


/* =========================================================
   RESTAURANT SETTINGS
   ========================================================= */

@Composable
fun RestaurantSettings(
    context: Context,
    onBack: () -> Unit
) {

    val preferences =
        remember {

            context.getSharedPreferences(
                "restaurant_settings",
                Context.MODE_PRIVATE
            )
        }

    var restaurantName by remember {

        mutableStateOf(
            preferences.getString(
                "restaurantName",
                ""
            ) ?: ""
        )
    }

    var address by remember {

        mutableStateOf(
            preferences.getString(
                "address",
                ""
            ) ?: ""
        )
    }

    var phone by remember {

        mutableStateOf(
            preferences.getString(
                "phone",
                ""
            ) ?: ""
        )
    }

    var email by remember {

        mutableStateOf(
            preferences.getString(
                "email",
                ""
            ) ?: ""
        )
    }

    var manager by remember {

        mutableStateOf(
            preferences.getString(
                "manager",
                ""
            ) ?: ""
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
                .verticalScroll(
                    rememberScrollState()
                )
                .imePadding()
                .navigationBarsPadding()
                .padding(20.dp)
        ) {

            TextButton(
                onClick = onBack
            ) {

                Text(
                    text = "← Back",
                    color = Color(0xFFB7D52B),
                    fontSize = 16.sp
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "Restaurant Settings",
                color = Color.White,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

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

            Spacer(
                modifier = Modifier.height(20.dp)
            )

            Button(
                onClick = {

                    preferences.edit()

                        .putString(
                            "restaurantName",
                            restaurantName
                        )

                        .putString(
                            "address",
                            address
                        )

                        .putString(
                            "phone",
                            phone
                        )

                        .putString(
                            "email",
                            email
                        )

                        .putString(
                            "manager",
                            manager
                        )

                        .apply()

                    saved = true
                },

                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Text(
                    text =
                        "Save Restaurant Information"
                )
            }

            if (saved) {

                Spacer(
                    modifier =
                        Modifier.height(12.dp)
                )

                Text(
                    text =
                        "Information saved successfully.",
                    color =
                        Color(0xFFB7D52B)
                )
            }

            Spacer(
                modifier =
                    Modifier.height(30.dp)
            )
        }
    }
}


/* =========================================================
   SETTINGS FIELD
   ========================================================= */

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
            .padding(bottom = 14.dp),

        colors =
            OutlinedTextFieldDefaults.colors(

                focusedTextColor =
                    Color.White,

                unfocusedTextColor =
                    Color.White,

                focusedLabelColor =
                    Color(0xFFB7D52B),

                unfocusedLabelColor =
                    Color(0xFF9AA3A3),

                cursorColor =
                    Color(0xFFB7D52B)
            )
    )
}

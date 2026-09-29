package com.hygienecontrol.app

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
   APP NAVIGATION
   ========================================================= */

@Composable
fun HygieneControlApp(context: Context) {

    var currentScreen by remember {
        mutableStateOf("home")
    }

    when (currentScreen) {

        "home" -> {

            ProfessionalDashboard(
                onHygieneClick = {
                    currentScreen = "hygiene"
                },
                onRecordsClick = {
                    currentScreen = "records"
                },
                onSettingsClick = {
                    currentScreen = "settings"
                }
            )
        }

        "hygiene" -> {

            HygieneChecks(
                context = context,
                onBack = {
                    currentScreen = "home"
                }
            )
        }

        "records" -> {

            RecordsScreen(
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
    }
}


/* =========================================================
   COLORS
   ========================================================= */

val AppBackground = Color(0xFF101414)
val AppCard = Color(0xFF1A2020)
val AppGreen = Color(0xFFB7D52B)
val AppWhite = Color(0xFFF5F5F5)
val AppGrey = Color(0xFF9AA3A3)


/* =========================================================
   DASHBOARD
   ========================================================= */

@Composable
fun ProfessionalDashboard(
    onHygieneClick: () -> Unit,
    onRecordsClick: () -> Unit,
    onSettingsClick: () -> Unit
) {

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = AppBackground
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
                    color = AppGreen,
                    fontSize = 27.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Restaurant Food Safety Management",
                    color = AppGrey,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = "Today's Overview",
                    color = AppWhite,
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
                        color = AppGreen
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

                Spacer(modifier = Modifier.height(26.dp))

                SectionTitle(
                    title = "Daily Checks",
                    color = AppGreen
                )

                DashboardActionCard(
                    title = "Hygiene Checks",
                    subtitle = "Daily food safety and hygiene controls",
                    onClick = onHygieneClick
                )

                DashboardActionCard(
                    title = "Temperature Checks",
                    subtitle = "Fridge, freezer and food temperatures"
                )

                DashboardActionCard(
                    title = "Cleaning Checks",
                    subtitle = "Cleaning tasks and completion records"
                )

                Spacer(modifier = Modifier.height(22.dp))

                SectionTitle(
                    title = "Compliance",
                    color = AppGreen
                )

                DashboardActionCard(
                    title = "Inspection Records",
                    subtitle = "View and manage inspection history",
                    onClick = onRecordsClick
                )

                DashboardActionCard(
                    title = "Corrective Actions",
                    subtitle = "Record problems and corrective measures"
                )

                DashboardActionCard(
                    title = "Staff Checks",
                    subtitle = "Staff hygiene and training records"
                )

                Spacer(modifier = Modifier.height(22.dp))

                SectionTitle(
                    title = "Reports",
                    color = AppGreen
                )

                DashboardActionCard(
                    title = "Inspection Report",
                    subtitle = "Prepare a complete inspection report"
                )

                DashboardActionCard(
                    title = "PDF Reports",
                    subtitle = "Generate and export professional reports"
                )

                Spacer(modifier = Modifier.height(22.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            onSettingsClick()
                        },
                    shape = RoundedCornerShape(15.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF202727)
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
                                text = "Restaurant Settings",
                                color = AppWhite,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = "Restaurant information",
                                color = AppGrey,
                                fontSize = 12.sp
                            )
                        }

                        Text(
                            text = "›",
                            color = AppGreen,
                            fontSize = 28.sp
                        )
                    }
                }
            }

            BottomNavigation(
                onRecordsClick = onRecordsClick,
                onSettingsClick = onSettingsClick
            )
        }
    }
}


/* =========================================================
   HYGIENE CHECK
   ========================================================= */

@Composable
fun HygieneChecks(
    context: Context,
    onBack: () -> Unit
) {

    var recordDate by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var area by remember { mutableStateOf("") }
    var hygieneItem by remember { mutableStateOf("") }
    var staff by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Pass") }
    var saved by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = AppBackground
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .navigationBarsPadding()
                .padding(20.dp)
        ) {

            TextButton(onClick = onBack) {

                Text(
                    text = "← Back",
                    color = AppGreen,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Hygiene Check",
                color = AppWhite,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Record a food safety and hygiene check",
                color = AppGrey,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(24.dp))


            /* DATE */

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp)
                    .clickable {

                        val calendar = Calendar.getInstance()

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
                        disabledTextColor = Color.White,
                        disabledLabelColor = AppGreen,
                        disabledPlaceholderColor = AppGrey,
                        disabledBorderColor = Color(0xFF777777)
                    )
                )
            }


            /* TIME */

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp)
                    .clickable {

                        val calendar = Calendar.getInstance()

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
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
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
                        disabledTextColor = Color.White,
                        disabledLabelColor = AppGreen,
                        disabledPlaceholderColor = AppGrey,
                        disabledBorderColor = Color(0xFF777777)
                    )
                )
            }


            HygieneField(
                value = area,
                label = "Check Area",
                placeholder = "e.g. Kitchen, Storage, Bar",
                onValueChange = {
                    area = it
                    saved = false
                }
            )

            HygieneField(
                value = hygieneItem,
                label = "Hygiene Item",
                placeholder = "e.g. Hand washing station",
                onValueChange = {
                    hygieneItem = it
                    saved = false
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Status",
                color = AppWhite,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
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

                    Text("PASS")
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

                    Text("FAIL")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            HygieneField(
                value = staff,
                label = "Staff Member",
                placeholder = "Enter staff name",
                onValueChange = {
                    staff = it
                    saved = false
                }
            )

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
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedLabelColor = AppGreen,
                    unfocusedLabelColor = AppGrey,
                    cursorColor = AppGreen
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

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
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppGreen,
                    contentColor = Color.Black
                )
            ) {

                Text(
                    text = "Save Hygiene Check",
                    fontWeight = FontWeight.Bold
                )
            }

            if (saved) {

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Hygiene check saved successfully.",
                    color = AppGreen,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}


/* =========================================================
   RECORDS SCREEN
   ========================================================= */

@Composable
fun RecordsScreen(
    context: Context,
    onBack: () -> Unit
) {

    var records by remember {
        mutableStateOf(
            loadHygieneRecords(context)
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = AppBackground
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 12.dp,
                        end = 20.dp,
                        top = 12.dp,
                        bottom = 8.dp
                    ),
                verticalAlignment = Alignment.CenterVertically
            ) {

                TextButton(
                    onClick = onBack
                ) {

                    Text(
                        text = "← Back",
                        color = AppGreen,
                        fontSize = 16.sp
                    )
                }

                Spacer(
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = "${records.size} Records",
                    color = AppGrey,
                    fontSize = 13.sp
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 20.dp)
            ) {

                Text(
                    text = "Inspection Records",
                    color = AppWhite,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "Hygiene check history",
                    color = AppGrey,
                    fontSize = 14.sp
                )

                Spacer(
                    modifier = Modifier.height(20.dp)
                )

                if (records.isEmpty()) {

                    EmptyRecords()

                } else {

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement =
                            Arrangement.spacedBy(12.dp),
                        contentPadding =
                            PaddingValues(bottom = 30.dp)
                    ) {

                        itemsIndexed(
                            records
                        ) { index, record ->

                            RecordCard(
                                record = record,
                                onDelete = {

                                    deleteHygieneRecord(
                                        context,
                                        index
                                    )

                                    records =
                                        loadHygieneRecords(
                                            context
                                        )
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}


/* =========================================================
   EMPTY RECORDS
   ========================================================= */

@Composable
fun EmptyRecords() {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = "No records yet",
            color = AppWhite,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            text = "Saved hygiene checks will appear here.",
            color = AppGrey,
            fontSize = 14.sp
        )
    }
}


/* =========================================================
   RECORD CARD
   ========================================================= */

@Composable
fun RecordCard(
    record: HygieneRecord,
    onDelete: () -> Unit
) {

    val statusColor =
        if (record.status == "Pass")
            AppGreen
        else
            Color(0xFFE57373)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = AppCard
        )
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text =
                            if (record.item.isNotBlank())
                                record.item
                            else
                                "Hygiene Check",
                        color = AppWhite,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(
                        modifier = Modifier.height(5.dp)
                    )

                    Text(
                        text =
                            "${record.date}  •  ${record.time}",
                        color = AppGrey,
                        fontSize = 13.sp
                    )
                }

                Surface(
                    shape =
                        RoundedCornerShape(8.dp),
                    color = statusColor
                ) {

                    Text(
                        text =
                            record.status.uppercase(),
                        color =
                            if (record.status == "Pass")
                                Color.Black
                            else
                                Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier =
                            Modifier.padding(
                                horizontal = 10.dp,
                                vertical = 6.dp
                            )
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            RecordLine(
                title = "Check Area",
                value = record.area
            )

            RecordLine(
                title = "Staff Member",
                value = record.staff
            )

            if (record.notes.isNotBlank()) {

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Text(
                    text = "Notes",
                    color = AppGrey,
                    fontSize = 12.sp
                )

                Text(
                    text = record.notes,
                    color = AppWhite,
                    fontSize = 14.sp
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            TextButton(
                onClick = onDelete
            ) {

                Text(
                    text = "Delete record",
                    color = Color(0xFFE57373),
                    fontSize = 12.sp
                )
            }
        }
    }
}


/* =========================================================
   RECORD LINE
   ========================================================= */

@Composable
fun RecordLine(
    title: String,
    value: String
) {

    if (value.isNotBlank()) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp)
        ) {

            Text(
                text = "$title: ",
                color = AppGrey,
                fontSize = 13.sp
            )

            Text(
                text = value,
                color = AppWhite,
                fontSize = 13.sp
            )
        }
    }
}


/* =========================================================
   DATA MODEL
   ========================================================= */

data class HygieneRecord(
    val date: String,
    val time: String,
    val area: String,
    val item: String,
    val status: String,
    val staff: String,
    val notes: String
)


/* =========================================================
   SAVE RECORD
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

    record.put("date", date)
    record.put("time", time)
    record.put("area", area)
    record.put("item", item)
    record.put("status", status)
    record.put("staff", staff)
    record.put("notes", notes)

    records.put(record)

    preferences.edit()
        .putString(
            "records",
            records.toString()
        )
        .apply()
}


/* =========================================================
   LOAD RECORDS
   ========================================================= */

fun loadHygieneRecords(
    context: Context
): List<HygieneRecord> {

    val preferences =
        context.getSharedPreferences(
            "hygiene_records",
            Context.MODE_PRIVATE
        )

    val data =
        preferences.getString(
            "records",
            "[]"
        ) ?: "[]"

    val jsonArray =
        JSONArray(data)

    val result =
        mutableListOf<HygieneRecord>()

    for (i in 0 until jsonArray.length()) {

        val obj =
            jsonArray.getJSONObject(i)

        result.add(
            HygieneRecord(
                date =
                    obj.optString("date"),
                time =
                    obj.optString("time"),
                area =
                    obj.optString("area"),
                item =
                    obj.optString("item"),
                status =
                    obj.optString(
                        "status",
                        "Pass"
                    ),
                staff =
                    obj.optString("staff"),
                notes =
                    obj.optString("notes")
            )
        )
    }

    return result.reversed()
}


/* =========================================================
   DELETE RECORD
   ========================================================= */

fun deleteHygieneRecord(
    context: Context,
    index: Int
) {

    val preferences =
        context.getSharedPreferences(
            "hygiene_records",
            Context.MODE_PRIVATE
        )

    val data =
        preferences.getString(
            "records",
            "[]"
        ) ?: "[]"

    val oldArray =
        JSONArray(data)

    val newArray =
        JSONArray()

    val reversedIndex =
        oldArray.length() - 1 - index

    for (i in 0 until oldArray.length()) {

        if (i != reversedIndex) {

            newArray.put(
                oldArray.getJSONObject(i)
            )
        }
    }

    preferences.edit()
        .putString(
            "records",
            newArray.toString()
        )
        .apply()
}


/* =========================================================
   HYGIENE FIELD
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
                    AppGreen,

                unfocusedLabelColor =
                    AppGrey,

                cursorColor =
                    AppGreen
            )
    )
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
            containerColor = AppCard
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
                color = AppGrey,
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
   DASHBOARD CARD
   ========================================================= */

@Composable
fun DashboardActionCard(
    title: String,
    subtitle: String,
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
        colors = CardDefaults.cardColors(
            containerColor = AppCard
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
                    color = AppWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = subtitle,
                    color = AppGrey,
                    fontSize = 12.sp
                )
            }

            Text(
                text = "›",
                color = AppGreen,
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
    onRecordsClick: () -> Unit,
    onSettingsClick: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF151A1A))
            .navigationBarsPadding()
            .padding(vertical = 12.dp),
        horizontalArrangement =
            Arrangement.SpaceEvenly,
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        BottomItem(
            title = "Home",
            color = AppGreen
        )

        BottomItem(
            title = "Checks",
            color = AppGrey
        )

        BottomItem(
            title = "Records",
            color = AppGrey,
            onClick = onRecordsClick
        )

        BottomItem(
            title = "Reports",
            color = AppGrey
        )

        BottomItem(
            title = "Settings",
            color = AppGrey,
            onClick = onSettingsClick
        )
    }
}


/* =========================================================
   BOTTOM ITEM
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
        color = AppBackground
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
                    color = AppGreen,
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
                modifier = Modifier.fillMaxWidth()
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
                        AppGreen
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
                    AppGreen,

                unfocusedLabelColor =
                    AppGrey,

                cursorColor =
                    AppGreen
            )
    )
}

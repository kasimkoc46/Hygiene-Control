package com.hygienecontrol.app

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.OnBackPressedCallback
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import org.json.JSONArray
import org.json.JSONObject
import java.util.Calendar


private val Background = Color(0xFF0D1112)
private val CardColor = Color(0xFF171D1E)
private val SecondaryText = Color(0xFF9BA4A5)
private val Lime = Color(0xFFB7E11B)
private val Danger = Color(0xFFE57373)
private val White = Color.White


data class HygieneRecord(
    val id: Long,
    val date: String,
    val time: String,
    val area: String,
    val item: String,
    val status: String,
    val staff: String,
    val notes: String
)


data class TemperatureRecord(
    val id: Long,
    val date: String,
    val time: String,
    val equipment: String,
    val temperature: String,
    val status: String,
    val staff: String,
    val notes: String
)


enum class AppScreen {
    HOME,
    HYGIENE,
    TEMPERATURE,
    CLEANING,
    RECORDS,
    CORRECTIVE,
    STAFF,
    REPORTS,
    SETTINGS
}


class MainActivity : ComponentActivity() {

    private var currentScreen = AppScreen.HOME

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {

                override fun handleOnBackPressed() {

                    if (currentScreen != AppScreen.HOME) {

                        currentScreen = AppScreen.HOME

                        setContent {
                            HygieneControlApp()
                        }

                    } else {

                        isEnabled = false
                        onBackPressedDispatcher.onBackPressed()
                    }
                }
            }
        )

        setContent {
            HygieneControlApp()
        }
    }


    fun navigateTo(screen: AppScreen) {

        currentScreen = screen

        setContent {
            HygieneControlApp()
        }
    }


    @Composable
    fun HygieneControlApp() {

        val context = LocalContext.current

        val records = remember {
            mutableStateListOf<HygieneRecord>().apply {
                addAll(loadRecords(context))
            }
        }

        MaterialTheme {

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Background
            ) {

                when (currentScreen) {

                    AppScreen.HOME -> {

                        HomeScreen(
                            records = records,
                            onNavigate = { navigateTo(it) }
                        )
                    }


                    AppScreen.HYGIENE -> {

                        HygieneCheckScreen(
                            onBack = {
                                navigateTo(AppScreen.HOME)
                            },
                            onSave = { record ->

                                records.add(0, record)

                                saveRecords(
                                    context,
                                    records
                                )

                                navigateTo(AppScreen.RECORDS)
                            }
                        )
                    }


                    AppScreen.RECORDS -> {

                        RecordsScreen(
                            records = records,
                            onBack = {
                                navigateTo(AppScreen.HOME)
                            },
                            onDelete = { record ->

                                records.remove(record)

                                saveRecords(
                                    context,
                                    records
                                )
                            }
                        )
                    }


                    AppScreen.SETTINGS -> {

                        RestaurantSettingsScreen(
                            onBack = {
                                navigateTo(AppScreen.HOME)
                            }
                        )
                    }


                    AppScreen.TEMPERATURE -> {

                        TemperatureCheckScreen(
                            onBack = {
                                navigateTo(AppScreen.HOME)
                            }
                        )
                    }


                    AppScreen.CLEANING -> {

                        SimpleScreen(
                            title = "Cleaning Checks",
                            subtitle = "Cleaning tasks and completion records",
                            onBack = {
                                navigateTo(AppScreen.HOME)
                            }
                        )
                    }


                    AppScreen.CORRECTIVE -> {

                        SimpleScreen(
                            title = "Corrective Actions",
                            subtitle = "Record problems and corrective measures",
                            onBack = {
                                navigateTo(AppScreen.HOME)
                            }
                        )
                    }


                    AppScreen.STAFF -> {

                        SimpleScreen(
                            title = "Staff Checks",
                            subtitle = "Staff hygiene and training records",
                            onBack = {
                                navigateTo(AppScreen.HOME)
                            }
                        )
                    }


                    AppScreen.REPORTS -> {

                        SimpleScreen(
                            title = "Reports",
                            subtitle = "Generate inspection reports",
                            onBack = {
                                navigateTo(AppScreen.HOME)
                            }
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun HomeScreen(
    records: List<HygieneRecord>,
    onNavigate: (AppScreen) -> Unit
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .navigationBarsPadding()
    ) {

        item {

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Text(
                text = "HYGIENE CONTROL",
                color = Lime,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Restaurant Food Safety Management",
                color = SecondaryText,
                fontSize = 17.sp
            )

            Spacer(
                modifier = Modifier.height(28.dp)
            )

            Text(
                text = "Today's Overview",
                color = White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                OverviewCard(
                    modifier = Modifier.weight(1f),
                    number = records.count {
                        it.status == "PASS"
                    }.toString(),
                    label = "Completed",
                    numberColor = Lime
                )

                OverviewCard(
                    modifier = Modifier.weight(1f),
                    number = "0",
                    label = "Pending",
                    numberColor = Color(0xFFFFB74D)
                )

                OverviewCard(
                    modifier = Modifier.weight(1f),
                    number = records.count {
                        it.status == "FAIL"
                    }.toString(),
                    label = "Issues",
                    numberColor = Danger
                )
            }

            Spacer(
                modifier = Modifier.height(30.dp)
            )

            Text(
                text = "Daily Checks",
                color = Lime,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )
        }


        item {

            MenuCard(
                title = "Hygiene Checks",
                subtitle = "Daily food safety and hygiene controls",
                onClick = {
                    onNavigate(AppScreen.HYGIENE)
                }
            )
        }


        item {

            MenuCard(
                title = "Temperature Checks",
                subtitle = "Fridge, freezer and food temperatures",
                onClick = {
                    onNavigate(AppScreen.TEMPERATURE)
                }
            )
        }


        item {

            MenuCard(
                title = "Cleaning Checks",
                subtitle = "Cleaning tasks and completion records",
                onClick = {
                    onNavigate(AppScreen.CLEANING)
                }
            )
        }


        item {

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Text(
                text = "Compliance",
                color = Lime,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )
        }


        item {

            MenuCard(
                title = "Inspection Records",
                subtitle = "View and manage inspection history",
                onClick = {
                    onNavigate(AppScreen.RECORDS)
                }
            )
        }


        item {

            MenuCard(
                title = "Corrective Actions",
                subtitle = "Record problems and corrective measures",
                onClick = {
                    onNavigate(AppScreen.CORRECTIVE)
                }
            )
        }


        item {

            MenuCard(
                title = "Staff Checks",
                subtitle = "Staff hygiene and training records",
                onClick = {
                    onNavigate(AppScreen.STAFF)
                }
            )
        }


        item {

            MenuCard(
                title = "Reports",
                subtitle = "Generate inspection reports",
                onClick = {
                    onNavigate(AppScreen.REPORTS)
                }
            )
        }


        item {

            MenuCard(
                title = "Restaurant Settings",
                subtitle = "Restaurant information",
                onClick = {
                    onNavigate(AppScreen.SETTINGS)
                }
            )

            Spacer(
                modifier = Modifier.height(25.dp)
            )
        }
    }
}


@Composable
fun OverviewCard(
    modifier: Modifier,
    number: String,
    label: String,
    numberColor: Color
) {

    Card(
        modifier = modifier.height(105.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardColor
        ),
        shape = RoundedCornerShape(20.dp)
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = number,
                color = numberColor,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = label,
                color = SecondaryText,
                fontSize = 14.sp
            )
        }
    }
}


@Composable
fun MenuCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable {
                onClick()
            },
        colors = CardDefaults.cardColors(
            containerColor = CardColor
        ),
        shape = RoundedCornerShape(22.dp)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 28.dp,
                    vertical = 20.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = title,
                    color = White,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(5.dp)
                )

                Text(
                    text = subtitle,
                    color = SecondaryText,
                    fontSize = 14.sp
                )
            }

            Text(
                text = "›",
                color = Lime,
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


@Composable
fun HygieneCheckScreen(
    onBack: () -> Unit,
    onSave: (HygieneRecord) -> Unit
) {

    val context = LocalContext.current

    var date by remember {
        mutableStateOf("")
    }

    var time by remember {
        mutableStateOf("")
    }

    var area by remember {
        mutableStateOf("")
    }

    var item by remember {
        mutableStateOf("")
    }

    var status by remember {
        mutableStateOf("PASS")
    }

    var staff by remember {
        mutableStateOf("")
    }

    var notes by remember {
        mutableStateOf("")
    }


    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp)
    ) {

        item {

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = "← Back",
                color = Lime,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    onBack()
                }
            )

            Spacer(
                modifier = Modifier.height(22.dp)
            )

            Text(
                text = "Hygiene Check",
                color = White,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Record a food safety and hygiene check",
                color = SecondaryText,
                fontSize = 17.sp
            )

            Spacer(
                modifier = Modifier.height(28.dp)
            )


            DateField(
                value = date,
                onClick = {

                    val calendar =
                        Calendar.getInstance()

                    DatePickerDialog(
                        context,
                        { _, year, month, day ->

                            date = String.format(
                                "%02d/%02d/%04d",
                                day,
                                month + 1,
                                year
                            )
                        },
                        calendar.get(
                            Calendar.YEAR
                        ),
                        calendar.get(
                            Calendar.MONTH
                        ),
                        calendar.get(
                            Calendar.DAY_OF_MONTH
                        )
                    ).show()
                }
            )


            Spacer(
                modifier = Modifier.height(16.dp)
            )


            TimeField(
                value = time,
                onClick = {

                    val calendar =
                        Calendar.getInstance()

                    TimePickerDialog(
                        context,
                        { _, hour, minute ->

                            time = String.format(
                                "%02d:%02d",
                                hour,
                                minute
                            )
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
            )


            Spacer(
                modifier = Modifier.height(16.dp)
            )


            WhiteTextField(
                value = area,
                onValueChange = {
                    area = it
                },
                label = "Check Area"
            )


            Spacer(
                modifier = Modifier.height(16.dp)
            )


            WhiteTextField(
                value = item,
                onValueChange = {
                    item = it
                },
                label = "Hygiene Item"
            )


            Spacer(
                modifier = Modifier.height(22.dp)
            )


            Text(
                text = "Status",
                color = White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )


            Spacer(
                modifier = Modifier.height(10.dp)
            )


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {

                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        status = "PASS"
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor =
                            if (status == "PASS")
                                Lime
                            else
                                CardColor,
                        contentColor =
                            if (status == "PASS")
                                Color.Black
                            else
                                White
                    )
                ) {
                    Text("PASS")
                }


                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        status = "FAIL"
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor =
                            if (status == "FAIL")
                                Danger
                            else
                                CardColor,
                        contentColor = White
                    )
                ) {
                    Text("FAIL")
                }
            }


            Spacer(
                modifier = Modifier.height(18.dp)
            )


            WhiteTextField(
                value = staff,
                onValueChange = {
                    staff = it
                },
                label = "Staff Member"
            )


            Spacer(
                modifier = Modifier.height(16.dp)
            )


            WhiteTextField(
                value = notes,
                onValueChange = {
                    notes = it
                },
                label = "Notes",
                singleLine = false,
                minLines = 4
            )


            Spacer(
                modifier = Modifier.height(22.dp)
            )


            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                onClick = {

                    if (
                        date.isNotBlank() &&
                        time.isNotBlank() &&
                        area.isNotBlank() &&
                        item.isNotBlank()
                    ) {

                        onSave(
                            HygieneRecord(
                                id =
                                    System.currentTimeMillis(),
                                date = date,
                                time = time,
                                area = area,
                                item = item,
                                status = status,
                                staff = staff,
                                notes = notes
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Lime,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(30.dp)
            ) {

                Text(
                    text = "Save Hygiene Check",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }


            Spacer(
                modifier = Modifier.height(35.dp)
            )
        }
    }
}


/* =========================================================
   TEMPERATURE CHECK SCREEN
   ========================================================= */

@Composable
fun TemperatureCheckScreen(
    onBack: () -> Unit
) {

    val context = LocalContext.current


    var date by remember {
        mutableStateOf("")
    }

    var time by remember {
        mutableStateOf("")
    }

    var equipment by remember {
        mutableStateOf("")
    }

    var temperature by remember {
        mutableStateOf("")
    }

    var status by remember {
        mutableStateOf("PASS")
    }

    var staff by remember {
        mutableStateOf("")
    }

    var notes by remember {
        mutableStateOf("")
    }


    val temperatureRecords =
        remember {

            mutableStateListOf<TemperatureRecord>().apply {

                addAll(
                    loadTemperatureRecords(context)
                )
            }
        }


    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp),
        verticalArrangement =
            Arrangement.spacedBy(12.dp)
    ) {


        item {

            Spacer(
                modifier = Modifier.height(12.dp)
            )


            Text(
                text = "← Back",
                color = Lime,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    onBack()
                }
            )


            Spacer(
                modifier = Modifier.height(22.dp)
            )


            Text(
                text = "Temperature Checks",
                color = White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )


            Text(
                text =
                    "Record fridge, freezer and food temperatures",
                color = SecondaryText,
                fontSize = 17.sp
            )


            Spacer(
                modifier = Modifier.height(18.dp)
            )
        }


        item {

            DateField(
                value = date,
                onClick = {

                    val calendar =
                        Calendar.getInstance()

                    DatePickerDialog(
                        context,
                        { _, year, month, day ->

                            date = String.format(
                                "%02d/%02d/%04d",
                                day,
                                month + 1,
                                year
                            )
                        },
                        calendar.get(
                            Calendar.YEAR
                        ),
                        calendar.get(
                            Calendar.MONTH
                        ),
                        calendar.get(
                            Calendar.DAY_OF_MONTH
                        )
                    ).show()
                }
            )
        }


        item {

            TimeField(
                value = time,
                onClick = {

                    val calendar =
                        Calendar.getInstance()

                    TimePickerDialog(
                        context,
                        { _, hour, minute ->

                            time = String.format(
                                "%02d:%02d",
                                hour,
                                minute
                            )
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
            )
        }


        item {

            WhiteTextField(
                value = equipment,
                onValueChange = {
                    equipment = it
                },
                label = "Equipment / Location"
            )
        }


        item {

            WhiteTextField(
                value = temperature,
                onValueChange = {
                    temperature = it
                },
                label = "Temperature (°C)"
            )
        }


        item {

            Text(
                text = "Result",
                color = White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.spacedBy(12.dp)
            ) {

                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        status = "PASS"
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor =
                            if (status == "PASS")
                                Lime
                            else
                                CardColor,
                        contentColor =
                            if (status == "PASS")
                                Color.Black
                            else
                                White
                    )
                ) {
                    Text("PASS")
                }


                Button(
                    modifier = Modifier.weight(1f),
                    onClick = {
                        status = "FAIL"
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor =
                            if (status == "FAIL")
                                Danger
                            else
                                CardColor,
                        contentColor = White
                    )
                ) {
                    Text("FAIL")
                }
            }
        }


        item {

            WhiteTextField(
                value = staff,
                onValueChange = {
                    staff = it
                },
                label = "Staff Member"
            )
        }


        item {

            WhiteTextField(
                value = notes,
                onValueChange = {
                    notes = it
                },
                label = "Notes",
                singleLine = false,
                minLines = 4
            )
        }


        item {

            Spacer(
                modifier = Modifier.height(5.dp)
            )


            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                onClick = {

                    if (
                        date.isNotBlank() &&
                        time.isNotBlank() &&
                        equipment.isNotBlank() &&
                        temperature.isNotBlank()
                    ) {

                        val record =
                            TemperatureRecord(
                                id =
                                    System.currentTimeMillis(),
                                date = date,
                                time = time,
                                equipment = equipment,
                                temperature = temperature,
                                status = status,
                                staff = staff,
                                notes = notes
                            )


                        temperatureRecords.add(
                            0,
                            record
                        )


                        saveTemperatureRecords(
                            context,
                            temperatureRecords
                        )


                        date = ""
                        time = ""
                        equipment = ""
                        temperature = ""
                        staff = ""
                        notes = ""
                        status = "PASS"
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Lime,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(30.dp)
            ) {

                Text(
                    text = "Save Temperature Record",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }


        item {

            Spacer(
                modifier = Modifier.height(18.dp)
            )


            Text(
                text = "Temperature Records",
                color = White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }


        if (temperatureRecords.isEmpty()) {

            item {

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = CardColor
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {

                    Text(
                        text = "No temperature records yet.",
                        color = SecondaryText,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(20.dp)
                    )
                }
            }

        } else {

            items(
                items = temperatureRecords,
                key = {
                    it.id
                }
            ) { record ->

                TemperatureRecordCard(
                    record = record,
                    onDelete = {

                        temperatureRecords.remove(
                            record
                        )

                        saveTemperatureRecords(
                            context,
                            temperatureRecords
                        )
                    }
                )
            }
        }


        item {

            Spacer(
                modifier = Modifier.height(30.dp)
            )
        }
    }
}


/* =========================================================
   TEMPERATURE RECORD CARD
   ========================================================= */

@Composable
fun TemperatureRecordCard(
    record: TemperatureRecord,
    onDelete: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardColor
        ),
        shape = RoundedCornerShape(20.dp)
    ) {

        Column(
            modifier = Modifier.padding(22.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween,
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(
                    text = record.equipment,
                    color = White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )


                Text(
                    text = record.status,
                    color =
                        if (record.status == "PASS")
                            Color.Black
                        else
                            White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(
                            if (record.status == "PASS")
                                Lime
                            else
                                Danger,
                            RoundedCornerShape(10.dp)
                        )
                        .padding(
                            horizontal = 12.dp,
                            vertical = 7.dp
                        )
                )
            }


            Spacer(
                modifier = Modifier.height(8.dp)
            )


            Text(
                text =
                    "${record.date} • ${record.time}",
                color = SecondaryText,
                fontSize = 15.sp
            )


            Spacer(
                modifier = Modifier.height(15.dp)
            )


            Text(
                text =
                    "${record.temperature} °C",
                color = Lime,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )


            Spacer(
                modifier = Modifier.height(10.dp)
            )


            if (record.staff.isNotBlank()) {

                Text(
                    text =
                        "Staff Member: ${record.staff}",
                    color = SecondaryText,
                    fontSize = 15.sp
                )
            }


            if (record.notes.isNotBlank()) {

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = "Notes",
                    color = SecondaryText,
                    fontSize = 14.sp
                )

                Text(
                    text = record.notes,
                    color = White,
                    fontSize = 15.sp
                )
            }


            Spacer(
                modifier = Modifier.height(12.dp)
            )


            TextButton(
                onClick = onDelete
            ) {

                Text(
                    text = "Delete record",
                    color = Danger,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


/* =========================================================
   DATE FIELD
   ========================================================= */

@Composable
fun DateField(
    value: String,
    onClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
    ) {

        OutlinedTextField(
            value = value,
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
            colors = fieldColors()
        )
    }
}


/* =========================================================
   TIME FIELD
   ========================================================= */

@Composable
fun TimeField(
    value: String,
    onClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
    ) {

        OutlinedTextField(
            value = value,
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
            colors = fieldColors()
        )
    }
}


/* =========================================================
   WHITE TEXT FIELD
   ========================================================= */

@Composable
fun WhiteTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    singleLine: Boolean = true,
    minLines: Int = 1
) {

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(label)
        },
        modifier = Modifier.fillMaxWidth(),
        singleLine = singleLine,
        minLines = minLines,
        colors = fieldColors()
    )
}


/* =========================================================
   FIELD COLORS
   ========================================================= */

@Composable
fun fieldColors() =
    androidx.compose.material3
        .OutlinedTextFieldDefaults
        .colors(

            focusedTextColor = White,

            unfocusedTextColor = White,

            disabledTextColor = White,

            focusedLabelColor = Lime,

            unfocusedLabelColor =
                SecondaryText,

            disabledLabelColor =
                SecondaryText,

            focusedBorderColor = Lime,

            unfocusedBorderColor =
                Color(0xFF6F7778),

            disabledBorderColor =
                Color(0xFF6F7778),

            cursorColor = Lime
        )


/* =========================================================
   INSPECTION RECORDS
   ========================================================= */

@Composable
fun RecordsScreen(
    records: List<HygieneRecord>,
    onBack: () -> Unit,
    onDelete: (HygieneRecord) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
    ) {

        Column(
            modifier = Modifier.padding(
                horizontal = 20.dp
            )
        ) {

            Spacer(
                modifier = Modifier.height(12.dp)
            )


            Text(
                text = "← Back",
                color = Lime,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    onBack()
                }
            )


            Spacer(
                modifier = Modifier.height(22.dp)
            )


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween,
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Column {

                    Text(
                        text = "Inspection Records",
                        color = White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Hygiene check history",
                        color = SecondaryText,
                        fontSize = 17.sp
                    )
                }


                Text(
                    text =
                        "${records.size} Records",
                    color = SecondaryText
                )
            }


            Spacer(
                modifier = Modifier.height(15.dp)
            )
        }


        if (records.isEmpty()) {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment =
                    Alignment.Center
            ) {

                Text(
                    text = "No records yet",
                    color = SecondaryText,
                    fontSize = 18.sp
                )
            }

        } else {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {

                items(
                    items = records,
                    key = {
                        it.id
                    }
                ) { record ->

                    RecordCard(
                        record = record,
                        onDelete = {
                            onDelete(record)
                        }
                    )
                }
            }
        }
    }
}


/* =========================================================
   HYGIENE RECORD CARD
   ========================================================= */

@Composable
fun RecordCard(
    record: HygieneRecord,
    onDelete: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardColor
        ),
        shape = RoundedCornerShape(22.dp)
    ) {

        Column(
            modifier = Modifier.padding(28.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween,
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(
                    text = record.item,
                    color = White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )


                Text(
                    text = record.status,
                    color =
                        if (record.status == "PASS")
                            Color.Black
                        else
                            White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(
                            if (record.status == "PASS")
                                Lime
                            else
                                Danger,
                            RoundedCornerShape(12.dp)
                        )
                        .padding(
                            horizontal = 16.dp,
                            vertical = 8.dp
                        )
                )
            }


            Spacer(
                modifier = Modifier.height(8.dp)
            )


            Text(
                text =
                    "${record.date} • ${record.time}",
                color = SecondaryText,
                fontSize = 15.sp
            )


            Spacer(
                modifier = Modifier.height(18.dp)
            )


            Text(
                text =
                    "Check Area: ${record.area}",
                color = SecondaryText,
                fontSize = 16.sp
            )


            Spacer(
                modifier = Modifier.height(7.dp)
            )


            Text(
                text =
                    "Staff Member: ${record.staff}",
                color = SecondaryText,
                fontSize = 16.sp
            )


            if (record.notes.isNotBlank()) {

                Spacer(
                    modifier = Modifier.height(14.dp)
                )


                Text(
                    text = "Notes",
                    color = SecondaryText,
                    fontSize = 15.sp
                )


                Text(
                    text = record.notes,
                    color = White,
                    fontSize = 16.sp
                )
            }


            Spacer(
                modifier = Modifier.height(18.dp)
            )


            TextButton(
                onClick = onDelete
            ) {

                Text(
                    text = "Delete record",
                    color = Danger,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}


/* =========================================================
   RESTAURANT SETTINGS
   ========================================================= */

@Composable
fun RestaurantSettingsScreen(
    onBack: () -> Unit
) {

    var restaurantName by remember {
        mutableStateOf("")
    }

    var address by remember {
        mutableStateOf("")
    }

    var phone by remember {
        mutableStateOf("")
    }

    var email by remember {
        mutableStateOf("")
    }

    var manager by remember {
        mutableStateOf("")
    }


    val context = LocalContext.current


    LaunchedEffect(Unit) {

        val preferences =
            context.getSharedPreferences(
                "restaurant_settings",
                Context.MODE_PRIVATE
            )


        restaurantName =
            preferences.getString(
                "name",
                ""
            ) ?: ""


        address =
            preferences.getString(
                "address",
                ""
            ) ?: ""


        phone =
            preferences.getString(
                "phone",
                ""
            ) ?: ""


        email =
            preferences.getString(
                "email",
                ""
            ) ?: ""


        manager =
            preferences.getString(
                "manager",
                ""
            ) ?: ""
    }


    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp)
    ) {

        item {

            Spacer(
                modifier = Modifier.height(12.dp)
            )


            Text(
                text = "← Back",
                color = Lime,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable {
                    onBack()
                }
            )


            Spacer(
                modifier = Modifier.height(22.dp)
            )


            Text(
                text = "Restaurant Settings",
                color = White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )


            Spacer(
                modifier = Modifier.height(25.dp)
            )


            WhiteTextField(
                value = restaurantName,
                onValueChange = {
                    restaurantName = it
                },
                label = "Restaurant Name"
            )


            Spacer(
                modifier = Modifier.height(16.dp)
            )


            WhiteTextField(
                value = address,
                onValueChange = {
                    address = it
                },
                label = "Address"
            )


            Spacer(
                modifier = Modifier.height(16.dp)
            )


            WhiteTextField(
                value = phone,
                onValueChange = {
                    phone = it
                },
                label = "Phone"
            )


            Spacer(
                modifier = Modifier.height(16.dp)
            )


            WhiteTextField(
                value = email,
                onValueChange = {
                    email = it
                },
                label = "Email"
            )


            Spacer(
                modifier = Modifier.height(16.dp)
            )


            WhiteTextField(
                value = manager,
                onValueChange = {
                    manager = it
                },
                label = "Manager / Owner"
            )


            Spacer(
                modifier = Modifier.height(25.dp)
            )


            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                onClick = {

                    context.getSharedPreferences(
                        "restaurant_settings",
                        Context.MODE_PRIVATE
                    )
                        .edit()
                        .putString(
                            "name",
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


                    onBack()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Lime,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(30.dp)
            ) {

                Text(
                    text =
                        "Save Restaurant Information",
                    fontWeight = FontWeight.Bold
                )
            }


            Spacer(
                modifier = Modifier.height(35.dp)
            )
        }
    }
}


/* =========================================================
   SIMPLE SCREEN
   ========================================================= */

@Composable
fun SimpleScreen(
    title: String,
    subtitle: String,
    onBack: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .navigationBarsPadding()
    ) {

        Spacer(
            modifier = Modifier.height(12.dp)
        )


        Text(
            text = "← Back",
            color = Lime,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable {
                onBack()
            }
        )


        Spacer(
            modifier = Modifier.height(25.dp)
        )


        Text(
            text = title,
            color = White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )


        Spacer(
            modifier = Modifier.height(8.dp)
        )


        Text(
            text = subtitle,
            color = SecondaryText,
            fontSize = 17.sp
        )


        Spacer(
            modifier = Modifier.height(35.dp)
        )


        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = CardColor
            ),
            shape = RoundedCornerShape(20.dp)
        ) {

            Text(
                text =
                    "This section is ready for the next step.",
                color = White,
                fontSize = 17.sp,
                modifier = Modifier.padding(25.dp)
            )
        }
    }
}


/* =========================================================
   SAVE HYGIENE RECORDS
   ========================================================= */

fun saveRecords(
    context: Context,
    records: List<HygieneRecord>
) {

    val array = JSONArray()


    records.forEach { record ->

        val objectRecord =
            JSONObject()

        objectRecord.put(
            "id",
            record.id
        )

        objectRecord.put(
            "date",
            record.date
        )

        objectRecord.put(
            "time",
            record.time
        )

        objectRecord.put(
            "area",
            record.area
        )

        objectRecord.put(
            "item",
            record.item
        )

        objectRecord.put(
            "status",
            record.status
        )

        objectRecord.put(
            "staff",
            record.staff
        )

        objectRecord.put(
            "notes",
            record.notes
        )


        array.put(objectRecord)
    }


    context.getSharedPreferences(
        "hygiene_records",
        Context.MODE_PRIVATE
    )
        .edit()
        .putString(
            "records",
            array.toString()
        )
        .apply()
}


/* =========================================================
   LOAD HYGIENE RECORDS
   ========================================================= */

fun loadRecords(
    context: Context
): List<HygieneRecord> {

    val preferences =
        context.getSharedPreferences(
            "hygiene_records",
            Context.MODE_PRIVATE
        )


    val json =
        preferences.getString(
            "records",
            null
        ) ?: return emptyList()


    return try {

        val array =
            JSONArray(json)

        val result =
            mutableListOf<HygieneRecord>()


        for (i in 0 until array.length()) {

            val item =
                array.getJSONObject(i)


            result.add(
                HygieneRecord(
                    id = item.optLong("id"),
                    date =
                        item.optString("date"),
                    time =
                        item.optString("time"),
                    area =
                        item.optString("area"),
                    item =
                        item.optString("item"),
                    status =
                        item.optString("status"),
                    staff =
                        item.optString("staff"),
                    notes =
                        item.optString("notes")
                )
            )
        }


        result

    } catch (
        e: Exception
    ) {

        emptyList()
    }
}


/* =========================================================
   SAVE TEMPERATURE RECORDS
   ========================================================= */

fun saveTemperatureRecords(
    context: Context,
    records: List<TemperatureRecord>
) {

    val array = JSONArray()


    records.forEach { record ->

        val objectRecord =
            JSONObject()


        objectRecord.put(
            "id",
            record.id
        )

        objectRecord.put(
            "date",
            record.date
        )

        objectRecord.put(
            "time",
            record.time
        )

        objectRecord.put(
            "equipment",
            record.equipment
        )

        objectRecord.put(
            "temperature",
            record.temperature
        )

        objectRecord.put(
            "status",
            record.status
        )

        objectRecord.put(
            "staff",
            record.staff
        )

        objectRecord.put(
            "notes",
            record.notes
        )


        array.put(objectRecord)
    }


    context.getSharedPreferences(
        "temperature_records",
        Context.MODE_PRIVATE
    )
        .edit()
        .putString(
            "records",
            array.toString()
        )
        .apply()
}


/* =========================================================
   LOAD TEMPERATURE RECORDS
   ========================================================= */

fun loadTemperatureRecords(
    context: Context
): List<TemperatureRecord> {

    val preferences =
        context.getSharedPreferences(
            "temperature_records",
            Context.MODE_PRIVATE
        )


    val json =
        preferences.getString(
            "records",
            null
        ) ?: return emptyList()


    return try {

        val array =
            JSONArray(json)

        val result =
            mutableListOf<TemperatureRecord>()


        for (i in 0 until array.length()) {

            val item =
                array.getJSONObject(i)


            result.add(
                TemperatureRecord(

                    id =
                        item.optLong("id"),

                    date =
                        item.optString("date"),

                    time =
                        item.optString("time"),

                    equipment =
                        item.optString("equipment"),

                    temperature =
                        item.optString("temperature"),

                    status =
                        item.optString("status"),

                    staff =
                        item.optString("staff"),

                    notes =
                        item.optString("notes")
                )
            )
        }


        result

    } catch (
        e: Exception
    ) {

        emptyList()
    }
}

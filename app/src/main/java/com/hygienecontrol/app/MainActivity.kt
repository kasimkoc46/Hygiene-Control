package com.hygienecontrol.app

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.json.JSONArray
import org.json.JSONObject
import java.util.Calendar
import kotlin.math.round

private val Background = Color(0xFF0D1112)
private val CardColor = Color(0xFF171D1E)
private val SecondaryText = Color(0xFF9BA4A5)
private val Lime = Color(0xFFB7E11B)
private val Danger = Color(0xFFE57373)
private val Warning = Color(0xFFFFB74D)
private val White = Color.White

data class AuditRecord(
    val id: Long,
    val type: String,
    val date: String,
    val time: String,
    val subject: String,
    val value: String,
    val status: String,
    val staff: String,
    val notes: String
)

data class RestaurantInfo(
    val name: String,
    val address: String,
    val phone: String,
    val email: String,
    val manager: String
)

enum class AppScreen {
    HOME,
    HYGIENE,
    TEMPERATURE,
    CLEANING,
    CORRECTIVE,
    STAFF,
    RECORDS,
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

    private fun navigateTo(screen: AppScreen) {
        currentScreen = screen
        setContent {
            HygieneControlApp()
        }
    }

    @Composable
    private fun HygieneControlApp() {

        val context = LocalContext.current

        val records = remember {
            mutableStateListOf<AuditRecord>().apply {
                addAll(loadRecords(context))
            }
        }

        MaterialTheme {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Background
            ) {
                when (currentScreen) {

                    AppScreen.HOME -> HomeScreen(
                        records = records,
                        onNavigate = { navigateTo(it) }
                    )

                    AppScreen.HYGIENE -> AuditFormScreen(
                        title = "Hygiene Checks",
                        subtitle = "Daily food safety and hygiene controls",
                        type = "Hygiene",
                        subjectLabel = "Hygiene Item",
                        subjectPlaceholder = "e.g. Hand washing area",
                        valueLabel = "Check Area",
                        valuePlaceholder = "e.g. Kitchen",
                        onBack = { navigateTo(AppScreen.HOME) },
                        onSave = {
                            records.add(0, it)
                            saveRecords(context, records)
                            navigateTo(AppScreen.RECORDS)
                        }
                    )

                    AppScreen.TEMPERATURE -> AuditFormScreen(
                        title = "Temperature Checks",
                        subtitle = "Record fridge, freezer and food temperatures",
                        type = "Temperature",
                        subjectLabel = "Equipment / Location",
                        subjectPlaceholder = "e.g. Fridge 1",
                        valueLabel = "Temperature (°C)",
                        valuePlaceholder = "e.g. 4.2",
                        onBack = { navigateTo(AppScreen.HOME) },
                        onSave = {
                            records.add(0, it)
                            saveRecords(context, records)
                            navigateTo(AppScreen.RECORDS)
                        }
                    )

                    AppScreen.CLEANING -> AuditFormScreen(
                        title = "Cleaning Checks",
                        subtitle = "Record cleaning tasks and completion",
                        type = "Cleaning",
                        subjectLabel = "Cleaning Task",
                        subjectPlaceholder = "e.g. Kitchen floor",
                        valueLabel = "Area",
                        valuePlaceholder = "e.g. Kitchen",
                        onBack = { navigateTo(AppScreen.HOME) },
                        onSave = {
                            records.add(0, it)
                            saveRecords(context, records)
                            navigateTo(AppScreen.RECORDS)
                        }
                    )

                    AppScreen.CORRECTIVE -> CorrectiveActionScreen(
                        onBack = { navigateTo(AppScreen.HOME) },
                        onSave = {
                            records.add(0, it)
                            saveRecords(context, records)
                            navigateTo(AppScreen.RECORDS)
                        }
                    )

                    AppScreen.STAFF -> AuditFormScreen(
                        title = "Staff Checks",
                        subtitle = "Record staff hygiene and training checks",
                        type = "Staff",
                        subjectLabel = "Staff Member",
                        subjectPlaceholder = "e.g. John",
                        valueLabel = "Check",
                        valuePlaceholder = "e.g. Hand hygiene",
                        onBack = { navigateTo(AppScreen.HOME) },
                        onSave = {
                            records.add(0, it)
                            saveRecords(context, records)
                            navigateTo(AppScreen.RECORDS)
                        }
                    )

                    AppScreen.RECORDS -> RecordsScreen(
                        records = records,
                        onBack = { navigateTo(AppScreen.HOME) },
                        onDelete = {
                            records.remove(it)
                            saveRecords(context, records)
                        }
                    )

                    AppScreen.REPORTS -> ReportsScreen(
                        records = records,
                        onBack = { navigateTo(AppScreen.HOME) }
                    )

                    AppScreen.SETTINGS -> SettingsScreen(
                        onBack = { navigateTo(AppScreen.HOME) }
                    )
                }
            }
        }
    }
}

@Composable
fun HomeScreen(
    records: List<AuditRecord>,
    onNavigate: (AppScreen) -> Unit
) {

    val score = calculateScore(records)
    val hygiene = categoryScore(records, "Hygiene")
    val temperature = categoryScore(records, "Temperature")
    val cleaning = categoryScore(records, "Cleaning")
    val corrective = categoryScore(records, "Corrective")
    val staff = categoryScore(records, "Staff")

    val completed = records.count { it.status == "PASS" }
    val issues = records.count { it.status == "FAIL" }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp)
    ) {

        item {
            Spacer(Modifier.height(18.dp))

            Text(
                "HYGIENE CONTROL",
                color = Lime,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                "Restaurant Food Safety Management",
                color = SecondaryText,
                fontSize = 16.sp
            )

            Spacer(Modifier.height(22.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = CardColor
                ),
                shape = RoundedCornerShape(24.dp)
            ) {

                Column(
                    modifier = Modifier.padding(22.dp)
                ) {

                    Text(
                        "FOOD SAFETY SCORE",
                        color = SecondaryText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(Modifier.height(5.dp))

                    if (score == null) {

                        Text(
                            "— / 10",
                            color = White,
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            "Not enough data",
                            color = Warning,
                            fontSize = 15.sp
                        )

                    } else {

                        Text(
                            "${formatScore(score)} / 10",
                            color = Lime,
                            fontSize = 42.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            scoreDescription(score),
                            color = White,
                            fontSize = 16.sp
                        )

                        Spacer(Modifier.height(15.dp))

                        ScoreRow("Hygiene", hygiene)
                        ScoreRow("Temperature", temperature)
                        ScoreRow("Cleaning", cleaning)
                        ScoreRow("Corrective Actions", corrective)
                        ScoreRow("Staff", staff)
                    }
                }
            }

            Spacer(Modifier.height(22.dp))

            Text(
                "Today's Overview",
                color = White,
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(10.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                OverviewCard(
                    Modifier.weight(1f),
                    completed.toString(),
                    "Completed",
                    Lime
                )

                OverviewCard(
                    Modifier.weight(1f),
                    "0",
                    "Pending",
                    Warning
                )

                OverviewCard(
                    Modifier.weight(1f),
                    issues.toString(),
                    "Issues",
                    Danger
                )
            }

            Spacer(Modifier.height(25.dp))

            Text(
                "Daily Checks",
                color = Lime,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            MenuCard(
                "Hygiene Checks",
                "Daily food safety and hygiene controls"
            ) {
                onNavigate(AppScreen.HYGIENE)
            }
        }

        item {
            MenuCard(
                "Temperature Checks",
                "Fridge, freezer and food temperatures"
            ) {
                onNavigate(AppScreen.TEMPERATURE)
            }
        }

        item {
            MenuCard(
                "Cleaning Checks",
                "Cleaning tasks and completion records"
            ) {
                onNavigate(AppScreen.CLEANING)
            }
        }

        item {
            Spacer(Modifier.height(15.dp))

            Text(
                "Compliance",
                color = Lime,
                fontSize = 21.sp,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            MenuCard(
                "Corrective Actions",
                "Record problems and corrective measures"
            ) {
                onNavigate(AppScreen.CORRECTIVE)
            }
        }

        item {
            MenuCard(
                "Staff Checks",
                "Staff hygiene and training records"
            ) {
                onNavigate(AppScreen.STAFF)
            }
        }

        item {
            MenuCard(
                "Inspection Records",
                "View all inspection records"
            ) {
                onNavigate(AppScreen.RECORDS)
            }
        }

        item {
            MenuCard(
                "Reports",
                "Create inspection PDF reports"
            ) {
                onNavigate(AppScreen.REPORTS)
            }
        }

        item {
            MenuCard(
                "Restaurant Settings",
                "Restaurant information"
            ) {
                onNavigate(AppScreen.SETTINGS)
            }

            Spacer(Modifier.height(30.dp))
        }
    }
}

@Composable
fun ScoreRow(
    name: String,
    score: Double?
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(name, color = SecondaryText, fontSize = 14.sp)

        Text(
            if (score == null) "—" else formatScore(score),
            color = if (score != null && score < 6) Danger else Lime,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun OverviewCard(
    modifier: Modifier,
    number: String,
    label: String,
    color: Color
) {
    Card(
        modifier = modifier.height(100.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardColor
        ),
        shape = RoundedCornerShape(18.dp)
    ) {
        Column(Modifier.padding(13.dp)) {
            Text(
                number,
                color = color,
                fontSize = 27.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                label,
                color = SecondaryText,
                fontSize = 12.sp
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
            .padding(vertical = 5.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = CardColor
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    title,
                    color = White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    subtitle,
                    color = SecondaryText,
                    fontSize = 13.sp
                )
            }

            Text(
                "›",
                color = Lime,
                fontSize = 34.sp
            )
        }
    }
}

@Composable
fun AuditFormScreen(
    title: String,
    subtitle: String,
    type: String,
    subjectLabel: String,
    subjectPlaceholder: String,
    valueLabel: String,
    valuePlaceholder: String,
    onBack: () -> Unit,
    onSave: (AuditRecord) -> Unit
) {

    val context = LocalContext.current

    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var value by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("PASS") }
    var staff by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        item {
            Spacer(Modifier.height(12.dp))

            BackButton(onBack)

            Spacer(Modifier.height(20.dp))

            Text(
                title,
                color = White,
                fontSize = 31.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                subtitle,
                color = SecondaryText,
                fontSize = 16.sp
            )

            Spacer(Modifier.height(10.dp))
        }

        item {
            DateField(date) {
                showDatePicker(context) { date = it }
            }
        }

        item {
            TimeField(time) {
                showTimePicker(context) { time = it }
            }
        }

        item {
            WhiteTextField(
                subject,
                { subject = it },
                subjectLabel,
                subjectPlaceholder
            )
        }

        item {
            WhiteTextField(
                value,
                { value = it },
                valueLabel,
                valuePlaceholder
            )
        }

        item {
            Text(
                "Result",
                color = White,
                fontWeight = FontWeight.Bold
            )

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                Button(
                    Modifier.weight(1f),
                    onClick = { status = "PASS" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor =
                            if (status == "PASS") Lime else CardColor,
                        contentColor =
                            if (status == "PASS") Color.Black else White
                    )
                ) {
                    Text("PASS")
                }

                Button(
                    Modifier.weight(1f),
                    onClick = { status = "FAIL" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor =
                            if (status == "FAIL") Danger else CardColor
                    )
                ) {
                    Text("FAIL")
                }
            }
        }

        item {
            WhiteTextField(
                staff,
                { staff = it },
                "Staff Member",
                "Enter staff name"
            )
        }

        item {
            WhiteTextField(
                notes,
                { notes = it },
                "Notes",
                "Additional notes",
                false,
                4
            )
        }

        item {
            Button(
                Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                onClick = {

                    if (
                        date.isBlank() ||
                        time.isBlank() ||
                        subject.isBlank() ||
                        value.isBlank()
                    ) {
                        Toast.makeText(
                            context,
                            "Please complete the required fields",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }

                    onSave(
                        AuditRecord(
                            System.currentTimeMillis(),
                            type,
                            date,
                            time,
                            subject,
                            value,
                            status,
                            staff,
                            notes
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Lime,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(
                    "Save $type Record",
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(30.dp))
        }
    }
}

@Composable
fun CorrectiveActionScreen(
    onBack: () -> Unit,
    onSave: (AuditRecord) -> Unit
) {

    val context = LocalContext.current

    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var problem by remember { mutableStateOf("") }
    var action by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("OPEN") }
    var staff by remember { mutableStateOf("") }

    LazyColumn(
        Modifier
            .fillMaxSize()
            .imePadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        item {
            Spacer(Modifier.height(12.dp))
            BackButton(onBack)

            Spacer(Modifier.height(20.dp))

            Text(
                "Corrective Actions",
                color = White,
                fontSize = 31.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                "Record problems and corrective measures",
                color = SecondaryText
            )
        }

        item {
            DateField(date) {
                showDatePicker(context) { date = it }
            }
        }

        item {
            TimeField(time) {
                showTimePicker(context) { time = it }
            }
        }

        item {
            WhiteTextField(
                problem,
                { problem = it },
                "Problem / Issue",
                "Describe the problem"
            )
        }

        item {
            WhiteTextField(
                action,
                { action = it },
                "Corrective Action",
                "What was done?",
                false,
                4
            )
        }

        item {
            Text(
                "Action Status",
                color = White,
                fontWeight = FontWeight.Bold
            )

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                Button(
                    Modifier.weight(1f),
                    onClick = { status = "OPEN" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor =
                            if (status == "OPEN") Warning else CardColor,
                        contentColor = Color.Black
                    )
                ) {
                    Text("OPEN")
                }

                Button(
                    Modifier.weight(1f),
                    onClick = { status = "CLOSED" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor =
                            if (status == "CLOSED") Lime else CardColor,
                        contentColor =
                            if (status == "CLOSED")
                                Color.Black
                            else
                                White
                    )
                ) {
                    Text("CLOSED")
                }
            }
        }

        item {
            WhiteTextField(
                staff,
                { staff = it },
                "Staff Member",
                "Responsible person"
            )
        }

        item {
            Button(
                Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                onClick = {

                    if (
                        date.isBlank() ||
                        time.isBlank() ||
                        problem.isBlank() ||
                        action.isBlank()
                    ) {
                        Toast.makeText(
                            context,
                            "Please complete the required fields",
                            Toast.LENGTH_SHORT
                        ).show()
                        return@Button
                    }

                    onSave(
                        AuditRecord(
                            System.currentTimeMillis(),
                            "Corrective",
                            date,
                            time,
                            problem,
                            action,
                            status,
                            staff,
                            ""
                        )
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Lime,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(
                    "Save Corrective Action",
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(30.dp))
        }
    }
}

@Composable
fun RecordsScreen(
    records: List<AuditRecord>,
    onBack: () -> Unit,
    onDelete: (AuditRecord) -> Unit
) {

    Column(
        Modifier
            .fillMaxSize()
            .navigationBarsPadding()
    ) {

        Column(
            Modifier.padding(horizontal = 20.dp)
        ) {

            Spacer(Modifier.height(12.dp))
            BackButton(onBack)

            Spacer(Modifier.height(18.dp))

            Text(
                "Inspection Records",
                color = White,
                fontSize = 31.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                "${records.size} total records",
                color = SecondaryText
            )

            Spacer(Modifier.height(15.dp))
        }

        if (records.isEmpty()) {

            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No records yet",
                    color = SecondaryText
                )
            }

        } else {

            LazyColumn(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
            ) {

                items(
                    records,
                    key = { it.id }
                ) { record ->

                    RecordCard(
                        record,
                        { onDelete(record) }
                    )
                }
            }
        }
    }
}

@Composable
fun RecordCard(
    record: AuditRecord,
    onDelete: () -> Unit
) {

    Card(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardColor
        ),
        shape = RoundedCornerShape(20.dp)
    ) {

        Column(Modifier.padding(20.dp)) {

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    record.type,
                    color = Lime,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    record.status,
                    color =
                        when (record.status) {
                            "PASS", "CLOSED" -> Lime
                            "FAIL" -> Danger
                            else -> Warning
                        },
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(7.dp))

            Text(
                record.subject,
                color = White,
                fontSize = 19.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                "${record.date} • ${record.time}",
                color = SecondaryText
            )

            Spacer(Modifier.height(8.dp))

            Text(
                record.value,
                color = White
            )

            if (record.staff.isNotBlank()) {
                Text(
                    "Staff: ${record.staff}",
                    color = SecondaryText
                )
            }

            if (record.notes.isNotBlank()) {
                Text(
                    "Notes: ${record.notes}",
                    color = SecondaryText
                )
            }

            TextButton(onClick = onDelete) {
                Text(
                    "Delete record",
                    color = Danger
                )
            }
        }
    }
}

@Composable
fun ReportsScreen(
    records: List<AuditRecord>,
    onBack: () -> Unit
) {

    val context = LocalContext.current

    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }

    val score = calculateScore(records)

    Column(
        Modifier
            .fillMaxSize()
            .imePadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp)
    ) {

        Spacer(Modifier.height(12.dp))

        BackButton(onBack)

        Spacer(Modifier.height(20.dp))

        Text(
            "Reports",
            color = White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            "Create a professional food safety report",
            color = SecondaryText
        )

        Spacer(Modifier.height(25.dp))

        DateField(startDate) {
            showDatePicker(context) {
                startDate = it
            }
        }

        Spacer(Modifier.height(12.dp))

        DateField(
            endDate
        ) {
            showDatePicker(context) {
                endDate = it
            }
        }

        Spacer(Modifier.height(20.dp))

        Card(
            Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = CardColor
            ),
            shape = RoundedCornerShape(20.dp)
        ) {

            Column(Modifier.padding(20.dp)) {

                Text(
                    "Food Safety Score",
                    color = SecondaryText
                )

                Text(
                    if (score == null)
                        "— / 10"
                    else
                        "${formatScore(score)} / 10",
                    color = Lime,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "${records.size} records available",
                    color = SecondaryText
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        Button(
            Modifier
                .fillMaxWidth()
                .height(56.dp),
            onClick = {

                if (startDate.isBlank() ||
                    endDate.isBlank()
                ) {

                    Toast.makeText(
                        context,
                        "Please select both dates",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {

                    generatePdfReport(
                        context,
                        records,
                        startDate,
                        endDate,
                        score
                    )
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Lime,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(28.dp)
        ) {

            Text(
                "Generate PDF Report",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SettingsScreen(
    onBack: () -> Unit
) {

    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var manager by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {

        val p = context.getSharedPreferences(
            "restaurant",
            Context.MODE_PRIVATE
        )

        name = p.getString("name", "") ?: ""
        address = p.getString("address", "") ?: ""
        phone = p.getString("phone", "") ?: ""
        email = p.getString("email", "") ?: ""
        manager = p.getString("manager", "") ?: ""
    }

    LazyColumn(
        Modifier
            .fillMaxSize()
            .imePadding()
            .navigationBarsPadding()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        item {

            Spacer(Modifier.height(12.dp))
            BackButton(onBack)

            Spacer(Modifier.height(20.dp))

            Text(
                "Restaurant Settings",
                color = White,
                fontSize = 31.sp,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            WhiteTextField(
                name,
                { name = it },
                "Restaurant Name",
                "Restaurant name"
            )
        }

        item {
            WhiteTextField(
                address,
                { address = it },
                "Address",
                "Restaurant address"
            )
        }

        item {
            WhiteTextField(
                phone,
                { phone = it },
                "Phone",
                "Phone number"
            )
        }

        item {
            WhiteTextField(
                email,
                { email = it },
                "Email",
                "Email address"
            )
        }

        item {
            WhiteTextField(
                manager,
                { manager = it },
                "Manager / Owner",
                "Manager name"
            )
        }

        item {

            Button(
                Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                onClick = {

                    context.getSharedPreferences(
                        "restaurant",
                        Context.MODE_PRIVATE
                    )
                        .edit()
                        .putString("name", name)
                        .putString("address", address)
                        .putString("phone", phone)
                        .putString("email", email)
                        .putString("manager", manager)
                        .apply()

                    Toast.makeText(
                        context,
                        "Restaurant information saved",
                        Toast.LENGTH_SHORT
                    ).show()

                    onBack()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Lime,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(
                    "Save Restaurant Information",
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(30.dp))
        }
    }
}

@Composable
fun BackButton(
    onBack: () -> Unit
) {
    Text(
        "← Back",
        color = Lime,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.clickable {
            onBack()
        }
    )
}

@Composable
fun DateField(
    value: String,
    onClick: () -> Unit
) {

    Box(
        Modifier
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
            label = { Text("Record Date") },
            placeholder = { Text("Select date") },
            modifier = Modifier.fillMaxWidth(),
            colors = fieldColors()
        )
    }
}

@Composable
fun TimeField(
    value: String,
    onClick: () -> Unit
) {

    Box(
        Modifier
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
            label = { Text("Time") },
            placeholder = { Text("Select time") },
            modifier = Modifier.fillMaxWidth(),
            colors = fieldColors()
        )
    }
}

@Composable
fun WhiteTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String = "",
    singleLine: Boolean = true,
    minLines: Int = 1
) {

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = {
            if (placeholder.isNotBlank()) {
                Text(placeholder)
            }
        },
        modifier = Modifier.fillMaxWidth(),
        singleLine = singleLine,
        minLines = minLines,
        colors = fieldColors()
    )
}

@Composable
fun fieldColors() =
    OutlinedTextFieldDefaults.colors(
        focusedTextColor = White,
        unfocusedTextColor = White,
        disabledTextColor = White,
        focusedLabelColor = Lime,
        unfocusedLabelColor = SecondaryText,
        disabledLabelColor = SecondaryText,
        focusedBorderColor = Lime,
        unfocusedBorderColor = Color(0xFF6F7778),
        disabledBorderColor = Color(0xFF6F7778),
        cursorColor = Lime
    )

fun showDatePicker(
    context: Context,
    onDateSelected: (String) -> Unit
) {

    val calendar = Calendar.getInstance()

    DatePickerDialog(
        context,
        { _, year, month, day ->

            onDateSelected(
                String.format(
                    "%02d/%02d/%04d",
                    day,
                    month + 1,
                    year
                )
            )
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).show()
}

fun showTimePicker(
    context: Context,
    onTimeSelected: (String) -> Unit
) {

    val calendar = Calendar.getInstance()

    TimePickerDialog(
        context,
        { _, hour, minute ->

            onTimeSelected(
                String.format(
                    "%02d:%02d",
                    hour,
                    minute
                )
            )
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    ).show()
}

fun calculateScore(
    records: List<AuditRecord>
): Double? {

    if (records.isEmpty()) return null

    val categories = listOf(
        "Hygiene" to 0.25,
        "Temperature" to 0.25,
        "Cleaning" to 0.15,
        "Corrective" to 0.15,
        "Staff" to 0.10
    )

    var total = 0.0
    var weight = 0.0

    for ((category, categoryWeight) in categories) {

        val categoryRecords =
            records.filter {
                it.type == category
            }

        if (categoryRecords.isNotEmpty()) {

            val passed =
                categoryRecords.count {
                    it.status == "PASS" ||
                    it.status == "CLOSED"
                }

            val categoryScore =
                passed.toDouble() /
                    categoryRecords.size.toDouble() *
                    10.0

            total += categoryScore * categoryWeight
            weight += categoryWeight
        }
    }

    if (weight == 0.0) return null

    val completeness =
        if (records.size >= 10) 10.0
        else records.size.toDouble()

    total += completeness * 0.10
    weight += 0.10

    return round(
        (total / weight) * 10.0
    ) / 10.0
}

fun categoryScore(
    records: List<AuditRecord>,
    category: String
): Double? {

    val list =
        records.filter {
            it.type == category
        }

    if (list.isEmpty()) return null

    val passed =
        list.count {
            it.status == "PASS" ||
            it.status == "CLOSED"
        }

    return round(
        passed.toDouble() /
            list.size.toDouble() *
            100.0
    ) / 10.0
}

fun formatScore(
    score: Double
): String {
    return String.format(
        "%.1f",
        score
    )
}

fun scoreDescription(
    score: Double
): String {

    return when {
        score >= 9.0 -> "Excellent food safety performance"
        score >= 8.0 -> "Very good performance"
        score >= 7.0 -> "Good performance"
        score >= 6.0 -> "Needs improvement"
        else -> "Immediate attention required"
    }
}

fun saveRecords(
    context: Context,
    records: List<AuditRecord>
) {

    val array = JSONArray()

    records.forEach {

        val obj = JSONObject()

        obj.put("id", it.id)
        obj.put("type", it.type)
        obj.put("date", it.date)
        obj.put("time", it.time)
        obj.put("subject", it.subject)
        obj.put("value", it.value)
        obj.put("status", it.status)
        obj.put("staff", it.staff)
        obj.put("notes", it.notes)

        array.put(obj)
    }

    context.getSharedPreferences(
        "audit_records",
        Context.MODE_PRIVATE
    )
        .edit()
        .putString(
            "records",
            array.toString()
        )
        .apply()
}

fun loadRecords(
    context: Context
): List<AuditRecord> {

    val json =
        context.getSharedPreferences(
            "audit_records",
            Context.MODE_PRIVATE
        )
            .getString(
                "records",
                null
            )
            ?: return emptyList()

    return try {

        val array = JSONArray(json)

        val result =
            mutableListOf<AuditRecord>()

        for (i in 0 until array.length()) {

            val obj =
                array.getJSONObject(i)

            result.add(
                AuditRecord(
                    obj.optLong("id"),
                    obj.optString("type"),
                    obj.optString("date"),
                    obj.optString("time"),
                    obj.optString("subject"),
                    obj.optString("value"),
                    obj.optString("status"),
                    obj.optString("staff"),
                    obj.optString("notes")
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

fun generatePdfReport(
    context: Context,
    records: List<AuditRecord>,
    startDate: String,
    endDate: String,
    score: Double?
) {

    val pdf =
        PdfDocument()

    val pageInfo =
        PdfDocument.PageInfo.Builder(
            595,
            842,
            1
        ).create()

    val page =
        pdf.startPage(pageInfo)

    val canvas =
        page.canvas

    val paint =
        Paint()

    paint.textSize = 24f
    paint.isFakeBoldText = true

    canvas.drawText(
        "FOOD SAFETY INSPECTION REPORT",
        35f,
        50f,
        paint
    )

    paint.textSize = 13f
    paint.isFakeBoldText = false

    var y = 80f

    val restaurant =
        loadRestaurant(context)

    canvas.drawText(
        "Restaurant: ${restaurant.name}",
        35f,
        y,
        paint
    )

    y += 20f

    canvas.drawText(
        "Address: ${restaurant.address}",
        35f,
        y,
        paint
    )

    y += 20f

    canvas.drawText(
        "Manager: ${restaurant.manager}",
        35f,
        y,
        paint
    )

    y += 30f

    paint.isFakeBoldText = true
    paint.textSize = 18f

    canvas.drawText(
        "Food Safety Score: ${
            if (score == null)
                "N/A"
            else
                "${formatScore(score)} / 10"
        }",
        35f,
        y,
        paint
    )

    y += 30f

    paint.isFakeBoldText = false
    paint.textSize = 12f

    canvas.drawText(
        "Inspection period: $startDate - $endDate",
        35f,
        y,
        paint
    )

    y += 30f

    canvas.drawText(
        "Total records: ${records.size}",
        35f,
        y,
        paint
    )

    y += 30f

    for (record in records.take(28)) {

        val line =
            "${record.type} | ${record.date} ${record.time} | " +
                    "${record.subject} | ${record.status}"

        canvas.drawText(
            line.take(85),
            35f,
            y,
            paint
        )

        y += 20f

        if (y > 790f) break
    }

    pdf.finishPage(page)

    val filename =
        "Hygiene_Report_${System.currentTimeMillis()}.pdf"

    try {

        if (Build.VERSION.SDK_INT >=
            Build.VERSION_CODES.Q
        ) {

            val values =
                ContentValues().apply {

                    put(
                        MediaStore.Downloads.DISPLAY_NAME,
                        filename
                    )

                    put(
                        MediaStore.Downloads.MIME_TYPE,
                        "application/pdf"
                    )

                    put(
                        MediaStore.Downloads.RELATIVE_PATH,
                        Environment.DIRECTORY_DOWNLOADS
                    )
                }

            val uri =
                context.contentResolver.insert(
                    MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                    values
                )

            if (uri != null) {

                context.contentResolver
                    .openOutputStream(uri)
                    ?.use {
                        pdf.writeTo(it)
                    }

                Toast.makeText(
                    context,
                    "PDF saved to Downloads",
                    Toast.LENGTH_LONG
                ).show()
            }

        } else {

            val file =
                java.io.File(
                    context.getExternalFilesDir(
                        Environment.DIRECTORY_DOCUMENTS
                    ),
                    filename
                )

            file.outputStream().use {
                pdf.writeTo(it)
            }

            Toast.makeText(
                context,
                "PDF report created",
                Toast.LENGTH_LONG
            ).show()
        }

    } catch (e: Exception) {

        Toast.makeText(
            context,
            "PDF error: ${e.message}",
            Toast.LENGTH_LONG
        ).show()
    }

    pdf.close()
}

fun loadRestaurant(
    context: Context
): RestaurantInfo {

    val p =
        context.getSharedPreferences(
            "restaurant",
            Context.MODE_PRIVATE
        )

    return RestaurantInfo(
        p.getString("name", "") ?: "",
        p.getString("address", "") ?: "",
        p.getString("phone", "") ?: "",
        p.getString("email", "") ?: "",
        p.getString("manager", "") ?: ""
    )
}

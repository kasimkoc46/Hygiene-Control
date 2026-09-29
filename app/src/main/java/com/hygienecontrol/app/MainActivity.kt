package com.hygienecontrol.app

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.round

private val Background = Color(0xFF050809)
private val CardColor = Color(0xFF0D1517)
private val CardBorder = Color(0xFF263638)
private val Lime = Color(0xFFB6FF32)
private val White = Color.White
private val Gray = Color(0xFFB8C0C2)
private val Green = Color(0xFF63E35A)
private val Blue = Color(0xFF35B8FF)
private val Purple = Color(0xFFB77CFF)
private val Orange = Color(0xFFFF914D)
private val Yellow = Color(0xFFFFD32A)
private val Red = Color(0xFFFF4D4D)

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            HygieneControlApp()
        }
    }
}

@Composable
fun HygieneControlApp() {

    var screen by remember {
        mutableStateOf(AppScreen.HOME)
    }

    BackHandler {

        if (screen == AppScreen.HOME) {
            return@BackHandler
        } else {
            screen = AppScreen.HOME
        }
    }

    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Lime,
            background = Background,
            surface = CardColor,
            onBackground = White,
            onSurface = White
        )
    ) {

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Background
        ) {

            when (screen) {

                AppScreen.HOME -> HomeScreen(
                    onNavigate = {
                        screen = it
                    }
                )

                AppScreen.HYGIENE -> AuditFormScreen(
                    title = "Hygiene Checks",
                    type = "Hygiene",
                    icon = Icons.Default.HealthAndSafety,
                    iconColor = Green,
                    onBack = {
                        screen = AppScreen.HOME
                    }
                )

                AppScreen.TEMPERATURE -> AuditFormScreen(
                    title = "Temperature Checks",
                    type = "Temperature",
                    icon = Icons.Default.Thermostat,
                    iconColor = Blue,
                    onBack = {
                        screen = AppScreen.HOME
                    }
                )

                AppScreen.CLEANING -> AuditFormScreen(
                    title = "Cleaning Checks",
                    type = "Cleaning",
                    icon = Icons.Default.CleaningServices,
                    iconColor = Purple,
                    onBack = {
                        screen = AppScreen.HOME
                    }
                )

                AppScreen.STAFF -> AuditFormScreen(
                    title = "Staff Checks",
                    type = "Staff",
                    icon = Icons.Default.Groups,
                    iconColor = Yellow,
                    onBack = {
                        screen = AppScreen.HOME
                    }
                )

                AppScreen.CORRECTIVE -> CorrectiveActionScreen(
                    onBack = {
                        screen = AppScreen.HOME
                    }
                )

                AppScreen.RECORDS -> RecordsScreen(
                    onBack = {
                        screen = AppScreen.HOME
                    }
                )

                AppScreen.REPORTS -> ReportsScreen(
                    onBack = {
                        screen = AppScreen.HOME
                    }
                )

                AppScreen.SETTINGS -> SettingsScreen(
                    onBack = {
                        screen = AppScreen.HOME
                    }
                )
            }
        }
    }
}

@Composable
fun BrandHeader(
    compact: Boolean = false
) {

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(
                id = R.drawable.gastro_guard_dashboard_logo
            ),
            contentDescription = "Gastro Guard",
            modifier = Modifier
                .fillMaxWidth()
                .height(
                    if (compact) {
                        100.dp
                    } else {
                        155.dp
                    }
                ),
            contentScale = ContentScale.Fit
        )
    }
}

@Composable
fun HomeScreen(
    onNavigate: (AppScreen) -> Unit
) {

    val context = LocalContext.current

    var records by remember {
        mutableStateOf(
            loadRecords(context)
        )
    }

    val score = calculateScore(records)

    val today = SimpleDateFormat(
        "dd/MM/yyyy",
        Locale.getDefault()
    ).format(Date())

    val todayRecords = records.filter {
        it.date == today
    }

    val completed = todayRecords.count {
        it.status == "PASS" ||
                it.status == "CLOSED"
    }

    val issues = todayRecords.count {
        it.status == "FAIL" ||
                it.status == "OPEN"
    }

    val pending = maxOf(
        0,
        10 - todayRecords.size
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 22.dp,
            bottom = 30.dp
        )
    ) {

        item {

            BrandHeader()

            Spacer(
                modifier = Modifier.height(22.dp)
            )

            ScoreCard(score)

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            Text(
                text = "Today's Overview",
                color = White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                OverviewCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.CheckCircle,
                    number = completed,
                    label = "Completed",
                    color = Green
                )

                OverviewCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Schedule,
                    number = pending,
                    label = "Pending",
                    color = Yellow
                )

                OverviewCard(
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.Error,
                    number = issues,
                    label = "Issues",
                    color = Red
                )
            }

            Spacer(
                modifier = Modifier.height(22.dp)
            )

            Text(
                text = "Quick Actions",
                color = White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )
        }

        /*
         * HYGIENE + CLEANING
         */

        item {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                MenuCard(
                    modifier = Modifier.weight(1f),
                    title = "Hygiene Checks",
                    icon = Icons.Default.HealthAndSafety,
                    color = Green
                ) {
                    onNavigate(
                        AppScreen.HYGIENE
                    )
                }

                MenuCard(
                    modifier = Modifier.weight(1f),
                    title = "Cleaning Checks",
                    icon = Icons.Default.CleaningServices,
                    color = Purple
                ) {
                    onNavigate(
                        AppScreen.CLEANING
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )
        }

        /*
         * TEMPERATURE - FULL WIDTH / ONE LINE
         */

        item {

            MenuCard(
                modifier = Modifier.fillMaxWidth(),
                title = "Temperature Checks",
                icon = Icons.Default.Thermostat,
                color = Blue,
                singleLine = true,
                height = 70.dp
            ) {
                onNavigate(
                    AppScreen.TEMPERATURE
                )
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )
        }

        /*
         * INSPECTION RECORDS + REPORTS
         */

        item {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {

                MenuCard(
                    modifier = Modifier.weight(1f),
                    title = "Inspection Records",
                    icon = Icons.Default.Description,
                    color = Blue
                ) {
                    onNavigate(
                        AppScreen.RECORDS
                    )
                }

                MenuCard(
                    modifier = Modifier.weight(1f),
                    title = "Reports",
                    icon = Icons.Default.BarChart,
                    color = Lime
                ) {
                    onNavigate(
                        AppScreen.REPORTS
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )
        }

        /*
         * RESTAURANT SETTINGS
         */

        item {

            MenuCard(
                modifier = Modifier.fillMaxWidth(),
                title = "Restaurant Settings",
                icon = Icons.Default.Settings,
                color = Color.LightGray,
                singleLine = true,
                height = 70.dp
            ) {
                onNavigate(
                    AppScreen.SETTINGS
                )
            }

            Spacer(
                modifier = Modifier.height(18.dp)
            )
        }

        /*
         * PDF BUTTON
         */

        item {

            Button(
                onClick = {

                    val uri = exportPdf(
                        context,
                        records
                    )

                    if (uri != null) {
                        openPdf(
                            context,
                            uri
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(58.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Lime,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(18.dp)
            ) {

                Icon(
                    Icons.Default.PictureAsPdf,
                    contentDescription = null
                )

                Spacer(
                    modifier = Modifier.width(10.dp)
                )

                Text(
                    text = "Generate PDF Report",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = null
                )
            }

            Spacer(
                modifier = Modifier.height(25.dp)
            )

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "Gastro Guard",
                    color = Gray,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun ScoreCard(
    score: Double?
) {

    val scoreText = score?.let {
        String.format(
            Locale.US,
            "%.1f",
            it
        )
    } ?: "—"

    val status = when {

        score == null ->
            "Not enough data"

        score >= 8.0 ->
            "Good"

        score >= 6.0 ->
            "Needs Attention"

        else ->
            "Action Required"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = CardColor
        ),
        shape = RoundedCornerShape(22.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            CardBorder
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier.size(125.dp),
                contentAlignment = Alignment.Center
            ) {

                CircularProgressIndicator(
                    progress = {
                        ((score ?: 0.0) / 10.0)
                            .toFloat()
                            .coerceIn(
                                0f,
                                1f
                            )
                    },
                    modifier = Modifier.fillMaxSize(),
                    strokeWidth = 9.dp,
                    color = Lime,
                    trackColor = Color(0xFF293133)
                )

                Column(
                    horizontalAlignment =
                        Alignment.CenterHorizontally
                ) {

                    Text(
                        text = scoreText,
                        color = White,
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "/ 10",
                        color = Gray,
                        fontSize = 13.sp
                    )
                }
            }

            Spacer(
                modifier = Modifier.width(20.dp)
            )

            Column {

                Text(
                    text = "Food Safety Score",
                    color = White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = status,
                    color = Lime,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(5.dp)
                )

                Text(
                    text =
                        "Based on recorded\nhygiene and safety checks",
                    color = Gray,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun OverviewCard(
    modifier: Modifier,
    icon: ImageVector,
    number: Int,
    label: String,
    color: Color
) {

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = CardColor
        ),
        shape = RoundedCornerShape(16.dp)
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Icon(
                icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(30.dp)
            )

            Text(
                text = number.toString(),
                color = White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = label,
                color = Gray,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun MenuCard(
    modifier: Modifier,
    title: String,
    icon: ImageVector,
    color: Color,
    singleLine: Boolean = false,
    height: androidx.compose.ui.unit.Dp = 88.dp,
    onClick: () -> Unit
) {

    Card(
        modifier = modifier
            .height(height)
            .clickable {
                onClick()
            },
        colors = CardDefaults.cardColors(
            containerColor = CardColor
        ),
        shape = RoundedCornerShape(17.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            CardBorder
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(13.dp),
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(34.dp)
            )

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            Text(
                text = title,
                color = White,
                fontSize = if (singleLine) {
                    15.sp
                } else {
                    14.sp
                },
                fontWeight = FontWeight.Medium,
                maxLines = if (singleLine) 1 else 2,
                softWrap = !singleLine,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            Icon(
                Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Gray
            )
        }
    }
}

@Composable
fun PageHeader(
    title: String,
    icon: ImageVector,
    color: Color,
    onBack: () -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp),
        verticalAlignment =
            Alignment.CenterVertically
    ) {

        IconButton(
            onClick = onBack
        ) {

            Icon(
                Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = White
            )
        }

        Icon(
            icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(30.dp)
        )

        Spacer(
            modifier = Modifier.width(10.dp)
        )

        Text(
            text = title,
            color = White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun AuditFormScreen(
    title: String,
    type: String,
    icon: ImageVector,
    iconColor: Color,
    onBack: () -> Unit
) {

    val context = LocalContext.current

    var date by remember {

        mutableStateOf(
            SimpleDateFormat(
                "dd/MM/yyyy",
                Locale.getDefault()
            ).format(Date())
        )
    }

    var time by remember {
        mutableStateOf("")
    }

    var subject by remember {
        mutableStateOf("")
    }

    var temperature by remember {
        mutableStateOf("")
    }

    var staff by remember {
        mutableStateOf("")
    }

    var notes by remember {
        mutableStateOf("")
    }

    var status by remember {
        mutableStateOf("PASS")
    }

    var savedMessage by remember {
        mutableStateOf("")
    }

    val scrollState =
        rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(18.dp)
            .imePadding()
    ) {

        PageHeader(
            title = title,
            icon = icon,
            color = iconColor,
            onBack = onBack
        )

        DateField(
            label = "Record Date",
            value = date
        ) {

            val calendar =
                Calendar.getInstance()

            DatePickerDialog(
                context,
                { _, year, month, day ->

                    date = String.format(
                        Locale.getDefault(),
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

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        DateField(
            label = "Time",
            value = time.ifEmpty {
                "Select time"
            }
        ) {

            val calendar =
                Calendar.getInstance()

            TimePickerDialog(
                context,
                { _, hour, minute ->

                    time = String.format(
                        Locale.getDefault(),
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

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        InputField(
            label =
                if (type == "Temperature") {
                    "Equipment / Location"
                } else {
                    "Check Area / Item"
                },
            value = subject,
            onValueChange = {
                subject = it
            }
        )

        if (type == "Temperature") {

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            InputField(
                label = "Temperature °C",
                value = temperature,
                onValueChange = {
                    temperature = it
                },
                keyboardType =
                    KeyboardType.Decimal
            )
        }

        Spacer(
            modifier = Modifier.height(15.dp)
        )

        Text(
            text = "Result",
            color = White,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                RadioButton(
                    selected =
                        status == "PASS",
                    onClick = {
                        status = "PASS"
                    },
                    colors =
                        RadioButtonDefaults.colors(
                            selectedColor = Green
                        )
                )

                Text(
                    "PASS",
                    color = Green
                )
            }

            Spacer(
                modifier = Modifier.width(30.dp)
            )

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                RadioButton(
                    selected =
                        status == "FAIL",
                    onClick = {
                        status = "FAIL"
                    },
                    colors =
                        RadioButtonDefaults.colors(
                            selectedColor = Red
                        )
                )

                Text(
                    "FAIL",
                    color = Red
                )
            }
        }

        Spacer(
            modifier = Modifier.height(10.dp)
        )

        InputField(
            label = "Staff Member",
            value = staff,
            onValueChange = {
                staff = it
            }
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        InputField(
            label = "Notes",
            value = notes,
            onValueChange = {
                notes = it
            },
            singleLine = false,
            minLines = 4
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Button(
            onClick = {

                if (subject.isNotBlank()) {

                    val record =
                        AuditRecord(
                            id =
                                System.currentTimeMillis(),
                            type = type,
                            date = date,
                            time = time,
                            subject = subject,
                            value = temperature,
                            status = status,
                            staff = staff,
                            notes = notes
                        )

                    saveRecord(
                        context,
                        record
                    )

                    savedMessage =
                        "Record saved successfully"

                    subject = ""
                    temperature = ""
                    staff = ""
                    notes = ""
                }

            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Lime,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(16.dp)
        ) {

            Icon(
                Icons.Default.Save,
                contentDescription = null
            )

            Spacer(
                modifier = Modifier.width(8.dp)
            )

            Text(
                "Save Record",
                fontWeight = FontWeight.Bold
            )
        }

        if (savedMessage.isNotEmpty()) {

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = savedMessage,
                color = Green,
                modifier = Modifier.align(
                    Alignment.CenterHorizontally
                )
            )
        }

        Spacer(
            modifier = Modifier.height(30.dp)
        )
    }
}

@Composable
fun DateField(
    label: String,
    value: String,
    onClick: () -> Unit
) {

    Column {

        Text(
            text = label,
            color = White,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        OutlinedButton(
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            colors =
                ButtonDefaults.outlinedButtonColors(
                    contentColor = White
                ),
            border =
                androidx.compose.foundation.BorderStroke(
                    1.dp,
                    CardBorder
                )
        ) {

            Icon(
                Icons.Default.CalendarMonth,
                contentDescription = null,
                tint = Lime
            )

            Spacer(
                modifier = Modifier.width(10.dp)
            )

            Text(
                value,
                color = White
            )
        }
    }
}

@Composable
fun InputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    keyboardType: KeyboardType =
        KeyboardType.Text,
    singleLine: Boolean = true,
    minLines: Int = 1
) {

    Column {

        Text(
            text = label,
            color = White,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )

        Spacer(
            modifier = Modifier.height(6.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = singleLine,
            minLines = minLines,
            keyboardOptions =
                KeyboardOptions(
                    keyboardType =
                        keyboardType
                ),
            colors =
                OutlinedTextFieldDefaults.colors(
                    focusedTextColor = White,
                    unfocusedTextColor = White,
                    focusedBorderColor = Lime,
                    unfocusedBorderColor =
                        CardBorder,
                    focusedLabelColor = Lime,
                    unfocusedLabelColor = Gray,
                    cursorColor = Lime
                )
        )
    }
}

@Composable
fun CorrectiveActionScreen(
    onBack: () -> Unit
) {

    val context =
        LocalContext.current

    var date by remember {

        mutableStateOf(
            SimpleDateFormat(
                "dd/MM/yyyy",
                Locale.getDefault()
            ).format(Date())
        )
    }

    var time by remember {
        mutableStateOf("")
    }

    var issue by remember {
        mutableStateOf("")
    }

    var action by remember {
        mutableStateOf("")
    }

    var staff by remember {
        mutableStateOf("")
    }

    var status by remember {
        mutableStateOf("OPEN")
    }

    var saved by remember {
        mutableStateOf(false)
    }

    val scrollState =
        rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(18.dp)
            .imePadding()
    ) {

        PageHeader(
            title = "Corrective Actions",
            icon = Icons.Default.WarningAmber,
            color = Orange,
            onBack = onBack
        )

        DateField(
            "Record Date",
            date
        ) {

            val c =
                Calendar.getInstance()

            DatePickerDialog(
                context,
                { _, year, month, day ->

                    date = String.format(
                        Locale.getDefault(),
                        "%02d/%02d/%04d",
                        day,
                        month + 1,
                        year
                    )
                },
                c.get(Calendar.YEAR),
                c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        DateField(
            "Time",
            time.ifEmpty {
                "Select time"
            }
        ) {

            val c =
                Calendar.getInstance()

            TimePickerDialog(
                context,
                { _, hour, minute ->

                    time = String.format(
                        Locale.getDefault(),
                        "%02d:%02d",
                        hour,
                        minute
                    )
                },
                c.get(
                    Calendar.HOUR_OF_DAY
                ),
                c.get(
                    Calendar.MINUTE
                ),
                true
            ).show()
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        InputField(
            "Issue",
            issue,
            {
                issue = it
            },
            singleLine = false,
            minLines = 3
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        InputField(
            "Corrective Action",
            action,
            {
                action = it
            },
            singleLine = false,
            minLines = 3
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        InputField(
            "Staff Member",
            staff,
            {
                staff = it
            }
        )

        Spacer(
            modifier = Modifier.height(15.dp)
        )

        Text(
            "Status",
            color = White,
            fontWeight = FontWeight.Bold
        )

        Row {

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                RadioButton(
                    selected =
                        status == "OPEN",
                    onClick = {
                        status = "OPEN"
                    },
                    colors =
                        RadioButtonDefaults.colors(
                            selectedColor = Orange
                        )
                )

                Text(
                    "OPEN",
                    color = Orange
                )
            }

            Spacer(
                modifier = Modifier.width(25.dp)
            )

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                RadioButton(
                    selected =
                        status == "CLOSED",
                    onClick = {
                        status = "CLOSED"
                    },
                    colors =
                        RadioButtonDefaults.colors(
                            selectedColor = Green
                        )
                )

                Text(
                    "CLOSED",
                    color = Green
                )
            }
        }

        Spacer(
            modifier = Modifier.height(18.dp)
        )

        Button(
            onClick = {

                if (issue.isNotBlank()) {

                    saveRecord(
                        context,
                        AuditRecord(
                            id =
                                System.currentTimeMillis(),
                            type = "Corrective",
                            date = date,
                            time = time,
                            subject = issue,
                            value = action,
                            status = status,
                            staff = staff,
                            notes = ""
                        )
                    )

                    saved = true

                    issue = ""
                    action = ""
                    staff = ""
                }

            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Lime,
                contentColor = Color.Black
            )
        ) {

            Icon(
                Icons.Default.Save,
                contentDescription = null
            )

            Spacer(
                modifier = Modifier.width(8.dp)
            )

            Text(
                "Save Corrective Action",
                fontWeight = FontWeight.Bold
            )
        }

        if (saved) {

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                "Corrective action saved successfully",
                color = Green,
                modifier = Modifier.align(
                    Alignment.CenterHorizontally
                )
            )
        }
    }
}

@Composable
fun RecordsScreen(
    onBack: () -> Unit
) {

    val context =
        LocalContext.current

    var records by remember {
        mutableStateOf(
            loadRecords(context)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(18.dp)
    ) {

        PageHeader(
            title = "Inspection Records",
            icon = Icons.Default.Description,
            color = Blue,
            onBack = onBack
        )

        Button(
            onClick = {
                records =
                    loadRecords(context)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = CardColor
            )
        ) {

            Icon(
                Icons.Default.Refresh,
                contentDescription = null,
                tint = Lime
            )

            Spacer(
                modifier = Modifier.width(8.dp)
            )

            Text("Refresh")
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        if (records.isEmpty()) {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment =
                    Alignment.Center
            ) {

                Text(
                    "No records yet",
                    color = Gray,
                    fontSize = 18.sp
                )
            }

        } else {

            LazyColumn(
                verticalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {

                items(
                    records.sortedByDescending {
                        it.id
                    }
                ) { record ->

                    RecordCard(record)
                }
            }
        }
    }
}

@Composable
fun RecordCard(
    record: AuditRecord
) {

    val statusColor =
        if (
            record.status == "PASS" ||
            record.status == "CLOSED"
        ) {
            Green
        } else {
            Red
        }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = CardColor
        ),
        border =
            androidx.compose.foundation.BorderStroke(
                1.dp,
                CardBorder
            )
    ) {

        Column(
            modifier = Modifier.padding(15.dp)
        ) {

            Row(
                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Text(
                    record.type,
                    color = Lime,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.weight(1f)
                )

                Text(
                    record.status,
                    color = statusColor,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(
                modifier = Modifier.height(7.dp)
            )

            Text(
                record.subject,
                color = White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                "${record.date}  ${record.time}",
                color = Gray,
                fontSize = 12.sp
            )

            if (record.value.isNotEmpty()) {

                Text(
                    "Value: ${record.value}",
                    color = White,
                    fontSize = 13.sp
                )
            }

            if (record.staff.isNotEmpty()) {

                Text(
                    "Staff: ${record.staff}",
                    color = Gray,
                    fontSize = 12.sp
                )
            }

            if (record.notes.isNotEmpty()) {

                Spacer(
                    modifier = Modifier.height(5.dp)
                )

                Text(
                    record.notes,
                    color = White,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
fun ReportsScreen(
    onBack: () -> Unit
) {

    val context =
        LocalContext.current

    var records by remember {
        mutableStateOf(
            loadRecords(context)
        )
    }

    var lastUri by remember {
        mutableStateOf<Uri?>(null)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState()
            )
            .padding(18.dp)
    ) {

        PageHeader(
            title = "Reports",
            icon = Icons.Default.BarChart,
            color = Lime,
            onBack = onBack
        )

        Text(
            "Food Safety Report",
            color = White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            modifier = Modifier.height(8.dp)
        )

        Text(
            "Generate a professional PDF report from your recorded checks.",
            color = Gray
        )

        Spacer(
            modifier = Modifier.height(25.dp)
        )

        Button(
            onClick = {

                records =
                    loadRecords(context)

                lastUri =
                    exportPdf(
                        context,
                        records
                    )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Lime,
                contentColor = Color.Black
            )
        ) {

            Icon(
                Icons.Default.PictureAsPdf,
                contentDescription = null
            )

            Spacer(
                modifier = Modifier.width(10.dp)
            )

            Text(
                "Generate PDF",
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(
            modifier = Modifier.height(15.dp)
        )

        lastUri?.let { uri ->

            OutlinedButton(
                onClick = {
                    openPdf(
                        context,
                        uri
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {

                Icon(
                    Icons.Default.Visibility,
                    contentDescription = null
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Text("Preview PDF")
            }

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            OutlinedButton(
                onClick = {
                    sharePdf(
                        context,
                        uri
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {

                Icon(
                    Icons.Default.Share,
                    contentDescription = null
                )

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Text("Share PDF")
            }
        }
    }
}

@Composable
fun SettingsScreen(
    onBack: () -> Unit
) {

    val context =
        LocalContext.current

    val prefs =
        remember {

            context.getSharedPreferences(
                "restaurant_settings",
                Context.MODE_PRIVATE
            )
        }

    var restaurantName by remember {

        mutableStateOf(
            prefs.getString(
                "restaurantName",
                ""
            ) ?: ""
        )
    }

    var address by remember {

        mutableStateOf(
            prefs.getString(
                "address",
                ""
            ) ?: ""
        )
    }

    var phone by remember {

        mutableStateOf(
            prefs.getString(
                "phone",
                ""
            ) ?: ""
        )
    }

    var email by remember {

        mutableStateOf(
            prefs.getString(
                "email",
                ""
            ) ?: ""
        )
    }

    var manager by remember {

        mutableStateOf(
            prefs.getString(
                "manager",
                ""
            ) ?: ""
        )
    }

    var saved by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState()
            )
            .padding(18.dp)
            .imePadding()
    ) {

        PageHeader(
            title = "Restaurant Settings",
            icon = Icons.Default.Settings,
            color = Color.LightGray,
            onBack = onBack
        )

        InputField(
            "Restaurant Name",
            restaurantName,
            {
                restaurantName = it
            }
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        InputField(
            "Address",
            address,
            {
                address = it
            },
            singleLine = false,
            minLines = 2
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        InputField(
            "Phone",
            phone,
            {
                phone = it
            },
            keyboardType =
                KeyboardType.Phone
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        InputField(
            "Email",
            email,
            {
                email = it
            },
            keyboardType =
                KeyboardType.Email
        )

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        InputField(
            "Manager / Owner",
            manager,
            {
                manager = it
            }
        )

        Spacer(
            modifier = Modifier.height(20.dp)
        )

        Button(
            onClick = {

                prefs.edit()
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
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Lime,
                contentColor = Color.Black
            )
        ) {

            Icon(
                Icons.Default.Save,
                contentDescription = null
            )

            Spacer(
                modifier = Modifier.width(8.dp)
            )

            Text(
                "Save Restaurant Settings",
                fontWeight = FontWeight.Bold
            )
        }

        if (saved) {

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                "Settings saved successfully",
                color = Green,
                modifier = Modifier.align(
                    Alignment.CenterHorizontally
                )
            )
        }

        Spacer(
            modifier = Modifier.height(30.dp)
        )

        BrandHeader(
            compact = true
        )
    }
}

fun saveRecord(
    context: Context,
    record: AuditRecord
) {

    val prefs =
        context.getSharedPreferences(
            "audit_records",
            Context.MODE_PRIVATE
        )

    val current =
        prefs.getString(
            "records",
            ""
        ) ?: ""

    val line =
        listOf(
            record.id.toString(),
            record.type,
            record.date,
            record.time,
            record.subject,
            record.value,
            record.status,
            record.staff,
            record.notes
        ).joinToString("|")

    prefs.edit()
        .putString(
            "records",
            if (current.isEmpty()) {
                line
            } else {
                "$current\n$line"
            }
        )
        .apply()
}

fun loadRecords(
    context: Context
): List<AuditRecord> {

    val prefs =
        context.getSharedPreferences(
            "audit_records",
            Context.MODE_PRIVATE
        )

    val data =
        prefs.getString(
            "records",
            ""
        ) ?: ""

    if (data.isBlank()) {
        return emptyList()
    }

    return data
        .split("\n")
        .mapNotNull { line ->

            try {

                val p =
                    line.split("|")

                if (p.size >= 9) {

                    AuditRecord(
                        id =
                            p[0].toLong(),
                        type = p[1],
                        date = p[2],
                        time = p[3],
                        subject = p[4],
                        value = p[5],
                        status = p[6],
                        staff = p[7],
                        notes = p[8]
                    )

                } else {
                    null
                }

            } catch (_: Exception) {

                null
            }
        }
}

fun calculateScore(
    records: List<AuditRecord>
): Double? {

    if (records.isEmpty()) {
        return null
    }

    val categories =
        listOf(
            "Hygiene" to 0.25,
            "Temperature" to 0.25,
            "Cleaning" to 0.15,
            "Corrective" to 0.15,
            "Staff" to 0.10
        )

    var total = 0.0
    var weight = 0.0

    categories.forEach { pair ->

        val category =
            pair.first

        val categoryWeight =
            pair.second

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

            total +=
                categoryScore *
                        categoryWeight

            weight +=
                categoryWeight
        }
    }

    if (weight == 0.0) {
        return null
    }

    val completeness =
        if (records.size >= 10) {
            10.0
        } else {
            records.size.toDouble()
        }

    total +=
        completeness * 0.10

    weight += 0.10

    return round(
        (total / weight) * 10.0
    ) / 10.0
}

fun exportPdf(
    context: Context,
    records: List<AuditRecord>
): Uri? {

    return try {

        val document =
            PdfDocument()

        var pageNumber = 1

        var pageInfo =
            PdfDocument.PageInfo.Builder(
                595,
                842,
                pageNumber
            ).create()

        var page =
            document.startPage(
                pageInfo
            )

        var canvas =
            page.canvas

        val paint =
            Paint()

        paint.color =
            android.graphics.Color.BLACK

        paint.textSize =
            24f

        paint.typeface =
            Typeface.create(
                Typeface.DEFAULT,
                Typeface.BOLD
            )

        canvas.drawText(
            "FOOD SAFETY INSPECTION REPORT",
            40f,
            55f,
            paint
        )

        paint.textSize =
            13f

        paint.typeface =
            Typeface.DEFAULT

        canvas.drawText(
            "Gastro Guard",
            40f,
            80f,
            paint
        )

        canvas.drawText(
            "Generated: ${
                SimpleDateFormat(
                    "dd/MM/yyyy HH:mm",
                    Locale.getDefault()
                ).format(Date())
            }",
            40f,
            102f,
            paint
        )

        val score =
            calculateScore(records)

        paint.typeface =
            Typeface.create(
                Typeface.DEFAULT,
                Typeface.BOLD
            )

        paint.textSize =
            18f

        canvas.drawText(
            "Food Safety Score: ${
                score?.let {
                    String.format(
                        Locale.US,
                        "%.1f / 10",
                        it
                    )
                } ?: "Not enough data"
            }",
            40f,
            140f,
            paint
        )

        paint.typeface =
            Typeface.DEFAULT

        paint.textSize =
            13f

        var y = 180f

        if (records.isEmpty()) {

            canvas.drawText(
                "No inspection records available.",
                40f,
                y,
                paint
            )

        } else {

            records
                .sortedByDescending {
                    it.id
                }
                .forEach { record ->

                    if (y > 780f) {

                        document.finishPage(
                            page
                        )

                        pageNumber++

                        pageInfo =
                            PdfDocument.PageInfo.Builder(
                                595,
                                842,
                                pageNumber
                            ).create()

                        page =
                            document.startPage(
                                pageInfo
                            )

                        canvas =
                            page.canvas

                        y = 55f
                    }

                    paint.typeface =
                        Typeface.create(
                            Typeface.DEFAULT,
                            Typeface.BOLD
                        )

                    canvas.drawText(
                        "${record.type} - ${record.status}",
                        40f,
                        y,
                        paint
                    )

                    paint.typeface =
                        Typeface.DEFAULT

                    y += 20f

                    canvas.drawText(
                        "${record.date} ${record.time}",
                        40f,
                        y,
                        paint
                    )

                    y += 18f

                    canvas.drawText(
                        "Item: ${record.subject}",
                        40f,
                        y,
                        paint
                    )

                    y += 18f

                    if (record.value.isNotEmpty()) {

                        canvas.drawText(
                            "Value: ${record.value}",
                            40f,
                            y,
                            paint
                        )

                        y += 18f
                    }

                    if (record.staff.isNotEmpty()) {

                        canvas.drawText(
                            "Staff: ${record.staff}",
                            40f,
                            y,
                            paint
                        )

                        y += 18f
                    }

                    if (record.notes.isNotEmpty()) {

                        canvas.drawText(
                            "Notes: ${record.notes}",
                            40f,
                            y,
                            paint
                        )

                        y += 18f
                    }

                    y += 15f

                    paint.color =
                        android.graphics.Color.LTGRAY

                    canvas.drawLine(
                        40f,
                        y,
                        555f,
                        y,
                        paint
                    )

                    paint.color =
                        android.graphics.Color.BLACK

                    y += 20f
                }
        }

        document.finishPage(
            page
        )

        val fileName =
            "Food_Safety_Report_${
                SimpleDateFormat(
                    "yyyyMMdd_HHmm",
                    Locale.getDefault()
                ).format(Date())
            }.pdf"

        val values =
            android.content.ContentValues()
                .apply {

                    put(
                        MediaStore.Downloads.DISPLAY_NAME,
                        fileName
                    )

                    put(
                        MediaStore.Downloads.MIME_TYPE,
                        "application/pdf"
                    )

                    put(
                        MediaStore.Downloads.RELATIVE_PATH,
                        "Download"
                    )
                }

        val resolver =
            context.contentResolver

        val uri =
            resolver.insert(
                MediaStore.Downloads
                    .EXTERNAL_CONTENT_URI,
                values
            )

        if (uri != null) {

            resolver
                .openOutputStream(uri)
                ?.use {
                    document.writeTo(it)
                }

            document.close()

            uri

        } else {

            document.close()

            null
        }

    } catch (_: Exception) {

        null
    }
}

fun openPdf(
    context: Context,
    uri: Uri
) {

    try {

        val intent =
            Intent(
                Intent.ACTION_VIEW
            ).apply {

                setDataAndType(
                    uri,
                    "application/pdf"
                )

                addFlags(
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }

        context.startActivity(
            intent
        )

    } catch (_: Exception) {

        sharePdf(
            context,
            uri
        )
    }
}

fun sharePdf(
    context: Context,
    uri: Uri
) {

    val intent =
        Intent(
            Intent.ACTION_SEND
        ).apply {

            type =
                "application/pdf"

            putExtra(
                Intent.EXTRA_STREAM,
                uri
            )

            addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }

    context.startActivity(
        Intent.createChooser(
            intent,
            "Share Food Safety Report"
        )
    )
}

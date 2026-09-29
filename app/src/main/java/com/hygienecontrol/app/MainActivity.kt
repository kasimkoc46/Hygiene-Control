package com.hygienecontrol.app

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.round

private val DarkBackground = Color(0xFF111315)
private val CardBackground = Color(0xFF1B1F20)
private val Lime = Color(0xFFB7F34A)
private val White = Color.White
private val SecondaryText = Color(0xFFB8BEC0)
private val Red = Color(0xFFFF5C5C)
private val Green = Color(0xFF65D68A)
private val Orange = Color(0xFFFFB74D)

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

    fun exportPdf(
        context: Context,
        records: List<AuditRecord>
    ): Uri? {

        return try {

            val pdf = PdfDocument()

            val pageWidth = 595
            val pageHeight = 842
            val margin = 40

            val titlePaint = Paint().apply {
                color = android.graphics.Color.BLACK
                textSize = 22f
                typeface = Typeface.DEFAULT_BOLD
            }

            val headerPaint = Paint().apply {
                color = android.graphics.Color.BLACK
                textSize = 12f
                typeface = Typeface.DEFAULT_BOLD
            }

            val textPaint = Paint().apply {
                color = android.graphics.Color.DKGRAY
                textSize = 10f
            }

            val smallPaint = Paint().apply {
                color = android.graphics.Color.DKGRAY
                textSize = 8f
            }

            var pageNumber = 1

            var page = pdf.startPage(
                PdfDocument.PageInfo.Builder(
                    pageWidth,
                    pageHeight,
                    pageNumber
                ).create()
            )

            var canvas = page.canvas
            var y = 50

            canvas.drawText(
                "FOOD SAFETY INSPECTION REPORT",
                margin.toFloat(),
                y.toFloat(),
                titlePaint
            )

            y += 28

            canvas.drawText(
                "Generated: ${
                    SimpleDateFormat(
                        "dd/MM/yyyy HH:mm",
                        Locale.getDefault()
                    ).format(Date())
                }",
                margin.toFloat(),
                y.toFloat(),
                smallPaint
            )

            y += 30

            val prefs =
                context.getSharedPreferences(
                    "restaurant",
                    Context.MODE_PRIVATE
                )

            val restaurantName =
                prefs.getString("name", "") ?: ""

            val address =
                prefs.getString("address", "") ?: ""

            val phone =
                prefs.getString("phone", "") ?: ""

            val email =
                prefs.getString("email", "") ?: ""

            val manager =
                prefs.getString("manager", "") ?: ""

            canvas.drawText(
                "Restaurant Information",
                margin.toFloat(),
                y.toFloat(),
                headerPaint
            )

            y += 20

            canvas.drawText(
                "Restaurant: $restaurantName",
                margin.toFloat(),
                y.toFloat(),
                textPaint
            )

            y += 15

            canvas.drawText(
                "Address: $address",
                margin.toFloat(),
                y.toFloat(),
                textPaint
            )

            y += 15

            canvas.drawText(
                "Phone: $phone",
                margin.toFloat(),
                y.toFloat(),
                textPaint
            )

            y += 15

            canvas.drawText(
                "Email: $email",
                margin.toFloat(),
                y.toFloat(),
                textPaint
            )

            y += 15

            canvas.drawText(
                "Manager / Owner: $manager",
                margin.toFloat(),
                y.toFloat(),
                textPaint
            )

            y += 30

            val score = calculateScore(records)

            canvas.drawText(
                "Food Safety Score",
                margin.toFloat(),
                y.toFloat(),
                headerPaint
            )

            y += 22

            canvas.drawText(
                if (score == null) {
                    "Score: Not enough data"
                } else {
                    "Score: ${
                        String.format(
                            Locale.US,
                            "%.1f",
                            score
                        )
                    } / 10"
                },
                margin.toFloat(),
                y.toFloat(),
                textPaint
            )

            y += 30

            canvas.drawText(
                "Inspection Records",
                margin.toFloat(),
                y.toFloat(),
                headerPaint
            )

            y += 20

            if (records.isEmpty()) {

                canvas.drawText(
                    "No records available.",
                    margin.toFloat(),
                    y.toFloat(),
                    textPaint
                )

            } else {

                records.forEach { record ->

                    if (y > pageHeight - 80) {

                        canvas.drawText(
                            "Page $pageNumber",
                            margin.toFloat(),
                            (pageHeight - 20).toFloat(),
                            smallPaint
                        )

                        pdf.finishPage(page)

                        pageNumber++

                        page = pdf.startPage(
                            PdfDocument.PageInfo.Builder(
                                pageWidth,
                                pageHeight,
                                pageNumber
                            ).create()
                        )

                        canvas = page.canvas
                        y = 50
                    }

                    canvas.drawText(
                        "${record.date} ${record.time} | ${record.type}",
                        margin.toFloat(),
                        y.toFloat(),
                        headerPaint
                    )

                    y += 14

                    canvas.drawText(
                        "Subject: ${record.subject}",
                        margin.toFloat(),
                        y.toFloat(),
                        textPaint
                    )

                    y += 14

                    if (record.value.isNotBlank()) {

                        canvas.drawText(
                            "Value: ${record.value}",
                            margin.toFloat(),
                            y.toFloat(),
                            textPaint
                        )

                        y += 14
                    }

                    canvas.drawText(
                        "Status: ${record.status}",
                        margin.toFloat(),
                        y.toFloat(),
                        textPaint
                    )

                    y += 14

                    canvas.drawText(
                        "Staff: ${record.staff}",
                        margin.toFloat(),
                        y.toFloat(),
                        textPaint
                    )

                    y += 14

                    if (record.notes.isNotBlank()) {

                        canvas.drawText(
                            "Notes: ${record.notes}",
                            margin.toFloat(),
                            y.toFloat(),
                            textPaint
                        )

                        y += 14
                    }

                    y += 10

                    canvas.drawLine(
                        margin.toFloat(),
                        y.toFloat(),
                        (pageWidth - margin).toFloat(),
                        y.toFloat(),
                        smallPaint
                    )

                    y += 16
                }
            }

            canvas.drawText(
                "Page $pageNumber",
                margin.toFloat(),
                (pageHeight - 20).toFloat(),
                smallPaint
            )

            pdf.finishPage(page)

            val fileName =
                "Food_Safety_Report_${
                    SimpleDateFormat(
                        "yyyyMMdd_HHmm",
                        Locale.getDefault()
                    ).format(Date())
                }.pdf"

            val values =
                ContentValues().apply {

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
                        Environment.DIRECTORY_DOWNLOADS
                    )
                }

            val resolver =
                context.contentResolver

            val uri =
                resolver.insert(
                    MediaStore.Downloads.EXTERNAL_CONTENT_URI,
                    values
                )

            if (uri != null) {

                resolver
                    .openOutputStream(uri)
                    ?.use { output ->
                        pdf.writeTo(output)
                    }

                pdf.close()

                uri

            } else {

                pdf.close()
                null
            }

        } catch (e: Exception) {

            e.printStackTrace()
            null
        }
    }

    fun openPdf(
        context: Context,
        uri: Uri
    ) {

        val intent =
            Intent(Intent.ACTION_VIEW).apply {

                setDataAndType(
                    uri,
                    "application/pdf"
                )

                addFlags(
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }

        try {

            context.startActivity(intent)

        } catch (e: Exception) {

            Toast.makeText(
                context,
                "No PDF viewer is installed.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    fun sharePdf(
        context: Context,
        uri: Uri
    ) {

        val intent =
            Intent(Intent.ACTION_SEND).apply {

                type = "application/pdf"

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
                "Share PDF Report"
            )
        )
    }

    fun printPdf(
        context: Context,
        uri: Uri
    ) {

        val intent =
            Intent(Intent.ACTION_SEND).apply {

                type = "application/pdf"

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
                "Print / Export PDF"
            )
        )
    }

    @Composable
    fun HygieneControlApp() {

        val context = LocalContext.current

        var currentScreen by remember {
            mutableStateOf(
                AppScreen.HOME
            )
        }

        var records by remember {
            mutableStateOf(
                loadRecords(context)
            )
        }

        BackHandler {

            if (
                currentScreen ==
                AppScreen.HOME
            ) {

                finish()

            } else {

                currentScreen =
                    AppScreen.HOME
            }
        }

        MaterialTheme {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        DarkBackground
                    )
            ) {

                when (currentScreen) {

                    AppScreen.HOME -> {

                        HomeScreen(
                            records = records,
                            onNavigate = {
                                currentScreen = it
                            },
                            onExportPdf = {

                                val uri =
                                    exportPdf(
                                        context,
                                        records
                                    )

                                if (uri != null) {

                                    openPdf(
                                        context,
                                        uri
                                    )
                                }
                            }
                        )
                    }

                    AppScreen.HYGIENE -> {

                        AuditFormScreen(
                            title = "Hygiene Check",
                            type = "Hygiene",
                            onBack = {
                                currentScreen =
                                    AppScreen.HOME
                            },
                            onSave = {

                                records =
                                    loadRecords(
                                        context
                                    )
                            }
                        )
                    }

                    AppScreen.TEMPERATURE -> {

                        AuditFormScreen(
                            title = "Temperature Check",
                            type = "Temperature",
                            onBack = {
                                currentScreen =
                                    AppScreen.HOME
                            },
                            onSave = {

                                records =
                                    loadRecords(
                                        context
                                    )
                            }
                        )
                    }

                    AppScreen.CLEANING -> {

                        AuditFormScreen(
                            title = "Cleaning Check",
                            type = "Cleaning",
                            onBack = {
                                currentScreen =
                                    AppScreen.HOME
                            },
                            onSave = {

                                records =
                                    loadRecords(
                                        context
                                    )
                            }
                        )
                    }

                    AppScreen.STAFF -> {

                        AuditFormScreen(
                            title = "Staff Check",
                            type = "Staff",
                            onBack = {
                                currentScreen =
                                    AppScreen.HOME
                            },
                            onSave = {

                                records =
                                    loadRecords(
                                        context
                                    )
                            }
                        )
                    }

                    AppScreen.CORRECTIVE -> {

                        CorrectiveActionScreen(
                            onBack = {
                                currentScreen =
                                    AppScreen.HOME
                            },
                            onSave = {

                                records =
                                    loadRecords(
                                        context
                                    )
                            }
                        )
                    }

                    AppScreen.RECORDS -> {

                        RecordsScreen(
                            records = records,
                            onBack = {
                                currentScreen =
                                    AppScreen.HOME
                            }
                        )
                    }

                    AppScreen.REPORTS -> {

                        ReportsScreen(
                            records = records,
                            onBack = {
                                currentScreen =
                                    AppScreen.HOME
                            }
                        )
                    }

                    AppScreen.SETTINGS -> {

                        SettingsScreen(
                            onBack = {
                                currentScreen =
                                    AppScreen.HOME
                            }
                        )
                    }
                }
            }
        }
    }
}

/* HOME */

@Composable
fun HomeScreen(
    records: List<AuditRecord>,
    onNavigate: (AppScreen) -> Unit,
    onExportPdf: () -> Unit
) {

    val score =
        calculateScore(records)

    val today =
        SimpleDateFormat(
            "dd/MM/yyyy",
            Locale.getDefault()
        ).format(Date())

    val todayRecords =
        records.filter {
            it.date == today
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState()
            )
            .padding(20.dp)
    ) {

        Text(
            text = "Hygiene Control",
            color = White,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Restaurant Food Safety",
            color = SecondaryText,
            fontSize = 14.sp
        )

        Spacer(
            Modifier.height(20.dp)
        )

        ScoreCard(score)

        Spacer(
            Modifier.height(20.dp)
        )

        Text(
            text = "Today's Overview",
            color = White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            Modifier.height(10.dp)
        )

        OverviewCard(
            title = "Completed",
            value =
                todayRecords.count {
                    it.status == "PASS" ||
                            it.status == "CLOSED"
                }.toString(),
            color = Green
        )

        OverviewCard(
            title = "Pending",
            value = "0",
            color = Orange
        )

        OverviewCard(
            title = "Issues",
            value =
                todayRecords.count {
                    it.status == "FAIL" ||
                            it.status == "OPEN"
                }.toString(),
            color = Red
        )

        Spacer(
            Modifier.height(20.dp)
        )

        MenuButton(
            "Hygiene Checks"
        ) {
            onNavigate(
                AppScreen.HYGIENE
            )
        }

        MenuButton(
            "Temperature Checks"
        ) {
            onNavigate(
                AppScreen.TEMPERATURE
            )
        }

        MenuButton(
            "Cleaning Checks"
        ) {
            onNavigate(
                AppScreen.CLEANING
            )
        }

        MenuButton(
            "Corrective Actions"
        ) {
            onNavigate(
                AppScreen.CORRECTIVE
            )
        }

        MenuButton(
            "Staff Checks"
        ) {
            onNavigate(
                AppScreen.STAFF
            )
        }

        MenuButton(
            "Inspection Records"
        ) {
            onNavigate(
                AppScreen.RECORDS
            )
        }

        MenuButton(
            "Reports"
        ) {
            onNavigate(
                AppScreen.REPORTS
            )
        }

        MenuButton(
            "Restaurant Settings"
        ) {
            onNavigate(
                AppScreen.SETTINGS
            )
        }

        Spacer(
            Modifier.height(20.dp)
        )

        Button(
            onClick = onExportPdf,
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Lime,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(28.dp)
        ) {

            Text(
                text = "Generate PDF Report",
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(
            Modifier.height(30.dp)
        )
    }
}

@Composable
fun ScoreCard(
    score: Double?
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
        shape = RoundedCornerShape(22.dp)
    ) {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            Text(
                text = "Food Safety Score",
                color = White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(
                Modifier.height(10.dp)
            )

            Text(
                text =
                    if (score == null)
                        "— / 10"
                    else
                        "${
                            String.format(
                                Locale.US,
                                "%.1f",
                                score
                            )
                        } / 10",
                color = Lime,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text =
                    if (score == null)
                        "Not enough data"
                    else
                        "Calculated from recorded checks",
                color = SecondaryText,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
fun OverviewCard(
    title: String,
    value: String,
    color: Color
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement =
                Arrangement.SpaceBetween,
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Text(
                text = title,
                color = White
            )

            Text(
                text = value,
                color = color,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun MenuButton(
    title: String,
    onClick: () -> Unit
) {

    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .height(52.dp),
        colors =
            ButtonDefaults.outlinedButtonColors(
                contentColor = White
            )
    ) {

        Text(
            text = title,
            fontWeight = FontWeight.Bold
        )
    }
}

/* AUDIT FORM */

@Composable
fun AuditFormScreen(
    title: String,
    type: String,
    onBack: () -> Unit,
    onSave: () -> Unit
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

    var subject by remember {
        mutableStateOf("")
    }

    var value by remember {
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

    ScreenContainer(
        title = title,
        onBack = onBack
    ) {

        DateField(
            value = date,
            onClick = {

                val calendar =
                    Calendar.getInstance()

                DatePickerDialog(
                    context,
                    { _, year, month, day ->

                        val selected =
                            Calendar.getInstance()

                        selected.set(
                            year,
                            month,
                            day
                        )

                        date =
                            SimpleDateFormat(
                                "dd/MM/yyyy",
                                Locale.getDefault()
                            ).format(
                                selected.time
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

        TimeField(
            value = time,
            onClick = {

                val calendar =
                    Calendar.getInstance()

                TimePickerDialog(
                    context,
                    { _, hour, minute ->

                        time =
                            String.format(
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
        )

        WhiteTextField(
            value = subject,
            onValueChange = {
                subject = it
            },
            label =
                if (type == "Temperature")
                    "Equipment / Location"
                else
                    "Check Area / Item"
        )

        if (type == "Temperature") {

            WhiteTextField(
                value = value,
                onValueChange = {
                    value = it
                },
                label = "Temperature °C"
            )
        }

        Text(
            text = "Status",
            color = SecondaryText
        )

        Row(
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            RadioButton(
                selected =
                    status == "PASS",
                onClick = {
                    status = "PASS"
                }
            )

            Text(
                text = "PASS",
                color = White
            )

            Spacer(
                Modifier.width(20.dp)
            )

            RadioButton(
                selected =
                    status == "FAIL",
                onClick = {
                    status = "FAIL"
                }
            )

            Text(
                text = "FAIL",
                color = White
            )
        }

        WhiteTextField(
            value = staff,
            onValueChange = {
                staff = it
            },
            label = "Staff Member"
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

        Button(
            onClick = {

                if (subject.isBlank()) {

                    Toast.makeText(
                        context,
                        "Please enter the check item.",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@Button
                }

                saveRecord(
                    context,
                    AuditRecord(
                        id =
                            System.currentTimeMillis(),
                        type = type,
                        date = date,
                        time = time,
                        subject = subject,
                        value = value,
                        status = status,
                        staff = staff,
                        notes = notes
                    )
                )

                Toast.makeText(
                    context,
                    "Record saved",
                    Toast.LENGTH_SHORT
                ).show()

                onSave()
                onBack()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Lime,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(28.dp)
        ) {

            Text(
                text = "Save Record",
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(
            Modifier.height(30.dp)
        )
    }
}

/* CORRECTIVE ACTION */

@Composable
fun CorrectiveActionScreen(
    onBack: () -> Unit,
    onSave: () -> Unit
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

    ScreenContainer(
        title = "Corrective Action",
        onBack = onBack
    ) {

        DateField(
            value = date,
            onClick = {

                val calendar =
                    Calendar.getInstance()

                DatePickerDialog(
                    context,
                    { _, year, month, day ->

                        val selected =
                            Calendar.getInstance()

                        selected.set(
                            year,
                            month,
                            day
                        )

                        date =
                            SimpleDateFormat(
                                "dd/MM/yyyy",
                                Locale.getDefault()
                            ).format(
                                selected.time
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

        TimeField(
            value = time,
            onClick = {

                val calendar =
                    Calendar.getInstance()

                TimePickerDialog(
                    context,
                    { _, hour, minute ->

                        time =
                            String.format(
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
        )

        WhiteTextField(
            value = issue,
            onValueChange = {
                issue = it
            },
            label = "Issue"
        )

        WhiteTextField(
            value = action,
            onValueChange = {
                action = it
            },
            label = "Corrective Action",
            singleLine = false,
            minLines = 4
        )

        WhiteTextField(
            value = staff,
            onValueChange = {
                staff = it
            },
            label = "Responsible Staff"
        )

        Text(
            text = "Status",
            color = SecondaryText
        )

        Row(
            verticalAlignment =
                Alignment.CenterVertically
        ) {

            RadioButton(
                selected =
                    status == "OPEN",
                onClick = {
                    status = "OPEN"
                }
            )

            Text(
                text = "OPEN",
                color = White
            )

            Spacer(
                Modifier.width(20.dp)
            )

            RadioButton(
                selected =
                    status == "CLOSED",
                onClick = {
                    status = "CLOSED"
                }
            )

            Text(
                text = "CLOSED",
                color = White
            )
        }

        Button(
            onClick = {

                if (issue.isBlank()) {

                    Toast.makeText(
                        context,
                        "Please enter the issue.",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@Button
                }

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

                Toast.makeText(
                    context,
                    "Corrective action saved",
                    Toast.LENGTH_SHORT
                ).show()

                onSave()
                onBack()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Lime,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(28.dp)
        ) {

            Text(
                text = "Save Corrective Action",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/* RECORDS */

@Composable
fun RecordsScreen(
    records: List<AuditRecord>,
    onBack: () -> Unit
) {

    ScreenContainer(
        title = "Inspection Records",
        onBack = onBack
    ) {

        if (records.isEmpty()) {

            Text(
                text = "No records available.",
                color = SecondaryText
            )

        } else {

            records
                .sortedByDescending {
                    it.id
                }
                .forEach { record ->

                    RecordCard(
                        record = record
                    )
                }
        }

        Spacer(
            Modifier.height(30.dp)
        )
    }
}

/* REPORTS */

@Composable
fun ReportsScreen(
    records: List<AuditRecord>,
    onBack: () -> Unit
) {

    val context =
        LocalContext.current

    var lastPdfUri by remember {
        mutableStateOf<Uri?>(null)
    }

    ScreenContainer(
        title = "Reports",
        onBack = onBack
    ) {

        val score =
            calculateScore(records)

        Text(
            text = "Food Safety Score",
            color = White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text =
                if (score == null)
                    "— / 10"
                else
                    "${
                        String.format(
                            Locale.US,
                            "%.1f",
                            score
                        )
                    } / 10",
            color = Lime,
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(
            Modifier.height(20.dp)
        )

        Button(
            onClick = {

                val uri =
                    (context as MainActivity)
                        .exportPdf(
                            context,
                            records
                        )

                if (uri != null) {

                    lastPdfUri = uri

                    Toast.makeText(
                        context,
                        "PDF saved to Downloads",
                        Toast.LENGTH_SHORT
                    ).show()
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

            Text(
                text = "Generate PDF"
            )
        }

        lastPdfUri?.let { uri ->

            Button(
                onClick = {

                    (context as MainActivity)
                        .openPdf(
                            context,
                            uri
                        )
                },
                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    text = "Preview PDF"
                )
            }

            Button(
                onClick = {

                    (context as MainActivity)
                        .sharePdf(
                            context,
                            uri
                        )
                },
                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    text = "Share PDF"
                )
            }

            Button(
                onClick = {

                    (context as MainActivity)
                        .printPdf(
                            context,
                            uri
                        )
                },
                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    text = "Print / Export"
                )
            }
        }

        Spacer(
            Modifier.height(20.dp)
        )

        Text(
            text =
                "The report contains restaurant information, food safety score and recorded checks.",
            color = SecondaryText
        )
    }
}

/* SETTINGS */

@Composable
fun SettingsScreen(
    onBack: () -> Unit
) {

    val context =
        LocalContext.current

    val prefs =
        context.getSharedPreferences(
            "restaurant",
            Context.MODE_PRIVATE
        )

    var name by remember {
        mutableStateOf(
            prefs.getString(
                "name",
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

    ScreenContainer(
        title = "Restaurant Settings",
        onBack = onBack
    ) {

        WhiteTextField(
            value = name,
            onValueChange = {
                name = it
            },
            label = "Restaurant Name"
        )

        WhiteTextField(
            value = address,
            onValueChange = {
                address = it
            },
            label = "Address",
            singleLine = false,
            minLines = 2
        )

        WhiteTextField(
            value = phone,
            onValueChange = {
                phone = it
            },
            label = "Phone"
        )

        WhiteTextField(
            value = email,
            onValueChange = {
                email = it
            },
            label = "Email"
        )

        WhiteTextField(
            value = manager,
            onValueChange = {
                manager = it
            },
            label = "Manager / Owner"
        )

        Button(
            onClick = {

                prefs.edit()
                    .putString(
                        "name",
                        name
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

                Toast.makeText(
                    context,
                    "Restaurant information saved",
                    Toast.LENGTH_SHORT
                ).show()

                onBack()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(55.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Lime,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(28.dp)
        ) {

            Text(
                text = "Save Restaurant Information",
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(
            Modifier.height(30.dp)
        )
    }
}

/* CONTAINER */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScreenContainer(
    title: String,
    onBack: () -> Unit,
    content: @Composable () -> Unit
) {

    Scaffold(
        containerColor = DarkBackground,
        topBar = {

            TopAppBar(
                title = {

                    Text(
                        text = title,
                        color = White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {

                    Button(
                        onClick = onBack,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = Lime
                        )
                    ) {

                        Text(
                            text = "Back"
                        )
                    }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor =
                            DarkBackground
                    )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(
                    rememberScrollState()
                )
                .padding(
                    horizontal = 20.dp,
                    vertical = 10.dp
                )
        ) {

            content()
        }
    }
}

/* FIELDS */

@Composable
fun DateField(
    value: String,
    onClick: () -> Unit
) {

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        colors =
            ButtonDefaults.outlinedButtonColors(
                containerColor =
                    CardBackground,
                contentColor = White
            )
    ) {

        Text(
            text =
                if (value.isBlank())
                    "Select Record Date"
                else
                    "Record Date: $value"
        )
    }
}

@Composable
fun TimeField(
    value: String,
    onClick: () -> Unit
) {

    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        colors =
            ButtonDefaults.outlinedButtonColors(
                containerColor =
                    CardBackground,
                contentColor = White
            )
    ) {

        Text(
            text =
                if (value.isBlank())
                    "Select Time"
                else
                    "Time: $value"
        )
    }
}

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

            Text(
                text = label
            )
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
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
        unfocusedBorderColor =
            Color(0xFF6F7778),
        disabledBorderColor =
            Color(0xFF6F7778),
        cursorColor = Lime
    )

/* RECORD CARD */

@Composable
fun RecordCard(
    record: AuditRecord
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardBackground
        ),
        shape = RoundedCornerShape(16.dp)
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = record.type,
                color = Lime,
                fontWeight = FontWeight.Bold
            )

            Text(
                text =
                    "${record.date} ${record.time}",
                color = SecondaryText,
                fontSize = 12.sp
            )

            Spacer(
                Modifier.height(8.dp)
            )

            Text(
                text = record.subject,
                color = White,
                fontWeight = FontWeight.Bold
            )

            if (record.value.isNotBlank()) {

                Text(
                    text = record.value,
                    color = White
                )
            }

            Text(
                text =
                    "Status: ${record.status}",
                color =
                    if (
                        record.status == "PASS" ||
                        record.status == "CLOSED"
                    )
                        Green
                    else
                        Red
            )

            if (record.staff.isNotBlank()) {

                Text(
                    text =
                        "Staff: ${record.staff}",
                    color = SecondaryText
                )
            }

            if (record.notes.isNotBlank()) {

                Text(
                    text = record.notes,
                    color = SecondaryText
                )
            }
        }
    }
}

/* SCORE */

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

            weight += categoryWeight
        }
    }

    if (weight == 0.0) {
        return null
    }

    val completeness =
        if (records.size >= 10)
            10.0
        else
            records.size.toDouble()

    total += completeness * 0.10
    weight += 0.10

    return round(
        (total / weight) * 10.0
    ) / 10.0
}

/* STORAGE */

fun saveRecord(
    context: Context,
    record: AuditRecord
) {

    val prefs =
        context.getSharedPreferences(
            "records",
            Context.MODE_PRIVATE
        )

    val old =
        prefs.getString(
            "data",
            ""
        ) ?: ""

    val newLine =
        listOf(
            record.id,
            record.type,
            record.date,
            record.time,
            record.subject,
            record.value,
            record.status,
            record.staff,
            record.notes
        )
            .joinToString("|")
            .replace(
                "\n",
                " "
            )

    val result =
        if (old.isBlank())
            newLine
        else
            "$old\n$newLine"

    prefs.edit()
        .putString(
            "data",
            result
        )
        .apply()
}

fun loadRecords(
    context: Context
): List<AuditRecord> {

    val prefs =
        context.getSharedPreferences(
            "records",
            Context.MODE_PRIVATE
        )

    val data =
        prefs.getString(
            "data",
            ""
        ) ?: ""

    if (data.isBlank()) {
        return emptyList()
    }

    return data
        .lines()
        .mapNotNull { line ->

            val parts =
                line.split(
                    "|",
                    limit = 9
                )

            if (parts.size < 9) {
                null
            } else {

                try {

                    AuditRecord(
                        id =
                            parts[0].toLong(),
                        type = parts[1],
                        date = parts[2],
                        time = parts[3],
                        subject = parts[4],
                        value = parts[5],
                        status = parts[6],
                        staff = parts[7],
                        notes = parts[8]
                    )

                } catch (
                    e: Exception
                ) {
                    null
                }
            }
        }
}

package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ToggleOff
import androidx.compose.material.icons.filled.ToggleOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun SettingsScreen(
    onTriggerReport: () -> Unit,
    modifier: Modifier = Modifier
) {
    var smartPlannerActive by remember { mutableStateOf(true) }
    var soundEffectsActive by remember { mutableStateOf(true) }
    var focusAlarmsActive by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DeepBlack)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Core diagnostic card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Graphite),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, ContentGrey),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = "Core Config",
                        tint = ElectricBlue,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "SYSTEM OS CONFIG PORTS",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = MainText,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Core engine telemetry and background planner configurations.",
                            fontSize = 11.sp,
                            color = SecondaryText,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // Section header
        item {
            SectionTitle("HUD TRIGGER SYSTEM TELEMETRY")
        }

        // Switch 1
        item {
            SettingsToggleRow(
                title = "AUTO PLANNING ENGINE",
                subtitle = "Synthesize daily main mission priorities dynamically based on decay metrics",
                checked = smartPlannerActive,
                onCheckedChange = { smartPlannerActive = it }
            )
        }

        // Switch 2
        item {
            SettingsToggleRow(
                title = "INTELLIGENT FOCUS SOUND FX",
                subtitle = "Emit tactical alarm signals upon starting and completing focus nodes",
                checked = soundEffectsActive,
                onCheckedChange = { soundEffectsActive = it }
            )
        }

        // Switch 3
        item {
            SettingsToggleRow(
                title = "OVERDUE THREAT INTELLIGENCE",
                subtitle = "Issue periodic UI alert markers for topics lacking review",
                checked = focusAlarmsActive,
                onCheckedChange = { focusAlarmsActive = it }
            )
        }

        // Section header 2
        item {
            SectionTitle("DEVELOPER TEST COMMANDS")
        }

        // Button action reporting
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Graphite),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, ContentGrey),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "MANUALLY RECOMPILE WEEKLY DIAGNOSTICS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MainText,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Synthesizes standard weekly reports utilizing historical study log metrics immediately.",
                        fontSize = 9.sp,
                        color = SecondaryText,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 13.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = onTriggerReport,
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue, contentColor = DeepBlack),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth().testTag("compile_weekly_report_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("FORCE COMPILE WEEKLY REPORT", fontSize = 11.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Info block
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Graphite)
                    .border(BorderStroke(1.dp, ContentGrey), RoundedCornerShape(8.dp))
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = androidx.compose.ui.Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = ElectricBlue,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "SHONEN STUDY OS CORE: V1.4.0",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MainText,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "A futuristic student study operating system centering high retention recall. Configured under Jetpack Compose + Local offline SQLite Room.",
                            fontSize = 10.sp,
                            color = SecondaryText,
                            fontFamily = FontFamily.Monospace,
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsToggleRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Graphite),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, ContentGrey),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MainText,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = SecondaryText,
                    lineHeight = 13.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            IconButton(
                onClick = { onCheckedChange(!checked) }
            ) {
                Icon(
                    imageVector = if (checked) Icons.Default.ToggleOn else Icons.Default.ToggleOff,
                    contentDescription = null,
                    tint = if (checked) ElectricBlue else SecondaryText,
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }
}

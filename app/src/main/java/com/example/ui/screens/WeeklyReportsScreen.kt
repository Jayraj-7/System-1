package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Autorenew
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.WeeklyReport
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WeeklyReportsScreen(
    reports: List<WeeklyReport>,
    onGenerateNewReport: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DeepBlack)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Diagnostic triggers
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Graphite),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, ContentGrey),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "SYSTEM METRIC COMPILER",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = MainText,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Synthesizes comprehensive 7-day analytical diagnostic reports.",
                            fontSize = 10.sp,
                            color = SecondaryText,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Button(
                        onClick = onGenerateNewReport,
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue, contentColor = DeepBlack),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.testTag("generate_report_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Autorenew,
                            contentDescription = "Trigger Compiling",
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "COMPACT",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        item {
            SectionTitle("HUD INTEL: HISTORICAL REPORT LOGS")
        }

        if (reports.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Analytics,
                            contentDescription = null,
                            tint = SecondaryText,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "NO ANALYTICAL RECORDS GENERATED",
                            color = MainText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Study core nodes, then compact reports above.",
                            color = SecondaryText,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        } else {
            items(reports) { report ->
                WeeklyReportCard(report = report)
            }
        }
    }
}

@Composable
fun WeeklyReportCard(report: WeeklyReport) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val startStr = dateFormat.format(Date(report.startDateTimestamp))
    val endStr = dateFormat.format(Date(report.endDateTimestamp))

    Card(
        colors = CardDefaults.cardColors(containerColor = Graphite),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, ContentGrey),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Range Log title
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$startStr - $endStr".uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = ElectricBlue,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 0.5.sp
                )
                
                Box(
                    modifier = Modifier
                        .background(ContentGrey, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "CORE_ID: #${report.id}",
                        fontSize = 8.sp,
                        color = SecondaryText,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Stat columns split row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Focus hours
                Column(horizontalAlignment = Alignment.Start) {
                    Text("STUDY TIME", fontSize = 8.sp, color = SecondaryText, fontFamily = FontFamily.Monospace)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("${report.totalHours} HRS", fontSize = 14.sp, fontWeight = FontWeight.Black, color = MainText, fontFamily = FontFamily.Monospace)
                }

                // Consistency index
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("CONSISTENCY", fontSize = 8.sp, color = SecondaryText, fontFamily = FontFamily.Monospace)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("${report.consistencyScore}%", fontSize = 14.sp, fontWeight = FontWeight.Black, color = ElectricBlue, fontFamily = FontFamily.Monospace)
                }

                // Focus quality scale
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("FOCUS INDEX", fontSize = 8.sp, color = SecondaryText, fontFamily = FontFamily.Monospace)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("${report.focusScore}%", fontSize = 14.sp, fontWeight = FontWeight.Black, color = NeonPurple, fontFamily = FontFamily.Monospace)
                }

                // Retention index
                Column(horizontalAlignment = Alignment.End) {
                    Text("RETENTION", fontSize = 8.sp, color = SecondaryText, fontFamily = FontFamily.Monospace)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("${report.retentionEstimate}%", fontSize = 14.sp, fontWeight = FontWeight.Black, color = EmeraldGreen, fontFamily = FontFamily.Monospace)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Sub-systems detailed analytics status rows
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Most improved sector module
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(ContentGrey)
                        .padding(8.dp)
                ) {
                    Text("IMPROVED COGNITIVE SEGMENT", fontSize = 7.sp, color = SecondaryText, fontFamily = FontFamily.Monospace)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(report.mostImprovedSubject, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EmeraldGreen, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }

                // Weak subject critical segments
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(ContentGrey)
                        .padding(8.dp)
                ) {
                    Text("CRITICAL THREAT SEGMENT", fontSize = 7.sp, color = SecondaryText, fontFamily = FontFamily.Monospace)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(report.weakestSubject, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = CrimsonRed, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Operational description logic Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ContentGrey, RoundedCornerShape(6.dp))
                    .padding(10.dp)
            ) {
                Column {
                    Text(
                        text = "HUD INTEL DIAGNOSTIC SUMMARY:",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElectricBlue,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = report.analyticalSummary,
                        fontSize = 11.sp,
                        color = MainText,
                        lineHeight = 15.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

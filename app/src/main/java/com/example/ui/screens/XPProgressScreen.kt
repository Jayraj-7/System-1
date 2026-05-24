package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StudySession
import com.example.data.model.SystemState
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun XPProgressScreen(
    state: SystemState,
    sessions: List<StudySession>,
    modifier: Modifier = Modifier
) {
    val globalLevel = 1 + (state.totalXP / 1000)
    val currentLevelXp = state.totalXP % 1000
    val rankTier = when {
        globalLevel <= 5 -> "INITIATE CADRE"
        globalLevel <= 15 -> "SENTINEL SCOUT"
        globalLevel <= 30 -> "TITAN COMMANDER"
        else -> "ARCHON OVERSEER"
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DeepBlack)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Global progress level dashboard card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Graphite),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, ContentGrey),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = "Progression HUD",
                                tint = NeonPurple,
                                modifier = Modifier.size(32.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = rankTier,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MainText,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = "GLOBAL METRIC SECURITY DEPLOYMENT",
                                    fontSize = 8.sp,
                                    color = SecondaryText,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Text(
                            text = "LV.$globalLevel",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Black,
                            color = NeonPurple,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "LEVEL RATIO: $currentLevelXp / 1000 XP PTS",
                            fontSize = 11.sp,
                            color = SecondaryText,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    LinearProgressIndicator(
                        progress = { currentLevelXp.toFloat() / 1000f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = NeonPurple,
                        trackColor = ContentGrey
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "TOTAL CUMULATIVE STUDY XP RECORDED: ${state.totalXP} PTS",
                        fontSize = 9.sp,
                        color = ElectricBlue,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // Section header
        item {
            SectionTitle("HUD TIME SERIES: SESSION LEDGER")
        }

        // List sessions
        if (sessions.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.ListAlt,
                            contentDescription = null,
                            tint = SecondaryText,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "NO COMPLETED NODES CHARGED YET",
                            color = MainText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Complete session items in focus mode to generate listings.",
                            color = SecondaryText,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        } else {
            items(sessions) { session ->
                SessionLedgerItem(session = session)
            }
        }
    }
}

@Composable
fun SessionLedgerItem(session: StudySession) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy  •  HH:mm", Locale.getDefault())
    val dateStr = dateFormat.format(Date(session.dateTimestamp))

    val gradeColor = when(session.feedbackDifficulty) {
        "EASY" -> EmeraldGreen
        "MEDIUM" -> NeonPurple
        else -> CrimsonRed
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Graphite),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, ContentGrey),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("session_ledger_item_${session.id}")
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // Header tags
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(gradeColor.copy(alpha = 0.08f))
                            .border(BorderStroke(1.dp, gradeColor.copy(alpha = 0.2f)), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = session.feedbackDifficulty,
                            color = gradeColor,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = session.subjectName.uppercase(),
                        color = SecondaryText,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = topicNameWithoutSuffix(session.topicName),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MainText
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = dateStr,
                    fontSize = 9.sp,
                    color = DarkText,
                    fontFamily = FontFamily.Monospace
                )
            }

            // XP and Duration display
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "+${session.xpEarned} XP",
                    color = NeonPurple,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${session.durationMinutes} MINS",
                    color = SecondaryText,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

private fun topicNameWithoutSuffix(topic: String): String {
    return topic
}

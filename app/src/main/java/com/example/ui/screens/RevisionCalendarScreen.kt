package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.PlayArrow
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
import com.example.data.model.Topic
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun RevisionCalendarScreen(
    topics: List<Topic>,
    subjectMap: Map<Long, String>,
    onStartFocus: (Topic, String) -> Unit,
    modifier: Modifier = Modifier
) {
    val now = System.currentTimeMillis()

    // Categorize topics: Overdue, Due Today, Upcoming
    val overdueTopics = mutableListOf<Triple<Topic, String, Long>>()
    val dueTodayTopics = mutableListOf<Triple<Topic, String, Long>>()
    val upcomingTopics = mutableListOf<Triple<Topic, String, Long>>()

    topics.forEach { topic ->
        val subName = subjectMap[topic.subjectId] ?: "Subject"
        if (topic.lastReviewed == 0L) {
            // New unrevised topic belongs to due today / queue
            dueTodayTopics.add(Triple(topic, subName, 0L))
        } else {
            val dueTimeMs = topic.lastReviewed + (topic.currentIntervalDays * 24L * 3600L * 1000L)
            val overdueMs = now - dueTimeMs
            if (overdueMs > 24L * 3600L * 1000L) {
                overdueTopics.add(Triple(topic, subName, overdueMs))
            } else if (overdueMs > -24L * 3600L * 1000L) {
                dueTodayTopics.add(Triple(topic, subName, dueTimeMs))
            } else {
                upcomingTopics.add(Triple(topic, subName, dueTimeMs))
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DeepBlack)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Calendar Status Overview Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = Graphite),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, ContentGrey),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = "Chronology HUD",
                        tint = ElectricBlue,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "CHRONOLOGY SYSTEM RADAR",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = MainText,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Critical overdues: ${overdueTopics.size}  |  Due schedule: ${dueTodayTopics.size}  |  Secured: ${upcomingTopics.size}",
                            fontSize = 11.sp,
                            color = SecondaryText,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // 1. OVERDUE REVISIONS SECTOR
        item {
            SectionTitle("HUD CRITICAL CALENDAR: OVERDUE NODES")
        }

        if (overdueTopics.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Graphite)
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "NO CRITICAL SPACED REVISIONS OUTSTANDING",
                        color = EmeraldGreen,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        } else {
            items(overdueTopics) { (topic, subName, overdueTimeMs) ->
                val overdueDays = (overdueTimeMs / (24 * 3600 * 1000L)).coerceAtLeast(1)
                CalendarTopicItem(
                    topic = topic,
                    subjectName = subName,
                    timeLabel = "CRITICAL COGNITIVE LAPSE: OVERDUE BY $overdueDays DAYS",
                    labelColor = CrimsonRed,
                    onStartFocus = { onStartFocus(topic, subName) }
                )
            }
        }

        // 2. DUE TODAY SECTOR
        item {
            SectionTitle("HUD QUEUE ACTIVE: TRAINING REPLAYS DUE")
        }

        if (dueTodayTopics.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Graphite)
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "TRAINING PIPELINE COMPLETELY NOMINAL",
                        color = SecondaryText,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp
                    )
                }
            }
        } else {
            items(dueTodayTopics) { (topic, subName, _) ->
                CalendarTopicItem(
                    topic = topic,
                    subjectName = subName,
                    timeLabel = "PENDING COMPILATION REPLAY TODAY",
                    labelColor = NeonPurple,
                    onStartFocus = { onStartFocus(topic, subName) }
                )
            }
        }

        // 3. SECURED FUTURES (Upcoming)
        item {
            SectionTitle("HUD FUTURE HORIZON: SECURED NODES")
        }

        if (upcomingTopics.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Graphite)
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "NO SECURED SPACED RUNS IN BUFFER",
                        color = SecondaryText,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp
                    )
                }
            }
        } else {
            // Sort by upcoming time ascending
            val sortedUpcoming = upcomingTopics.sortedBy { it.third }
            items(sortedUpcoming) { (topic, subName, dueTimeMs) ->
                val remainingMs = dueTimeMs - now
                val remainingDays = (remainingMs / (24 * 3600 * 1000L)).coerceAtLeast(1)
                CalendarTopicItem(
                    topic = topic,
                    subjectName = subName,
                    timeLabel = "SECURED NODE: REVIEW DUE IN $remainingDays DAYS",
                    labelColor = EmeraldGreen,
                    onStartFocus = { onStartFocus(topic, subName) }
                )
            }
        }
    }
}

@Composable
fun CalendarTopicItem(
    topic: Topic,
    subjectName: String,
    timeLabel: String,
    labelColor: Color,
    onStartFocus: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Graphite),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, ContentGrey),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("calendar_topic_${topic.id}")
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(labelColor.copy(alpha = 0.08f))
                            .border(BorderStroke(1.dp, labelColor.copy(alpha = 0.2f)), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = timeLabel.uppercase(),
                            color = labelColor,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = subjectName.uppercase(),
                        color = SecondaryText,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                
                Text(
                    text = topic.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MainText
                )

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Interval: ${topic.currentIntervalDays}d  |  Strength: ${topic.memoryStrength}%  |  Status: ${topic.status}",
                        color = SecondaryText,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Quick focus button
            IconButton(
                onClick = onStartFocus,
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(ContentGrey)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Engage instant review",
                    tint = ElectricBlue,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

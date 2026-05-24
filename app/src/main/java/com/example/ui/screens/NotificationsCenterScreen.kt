package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsNone
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
import com.example.data.model.NotificationLog
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun NotificationsCenterScreen(
    notifications: List<NotificationLog>,
    onMarkAllRead: () -> Unit,
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
        // Notification action bar card
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
                            text = "HUD INTEL ALARM SYSTEM",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            color = MainText,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Warnings regarding cognitive retention loops.",
                            fontSize = 11.sp,
                            color = SecondaryText,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    if (notifications.any { !it.isRead }) {
                        Button(
                            onClick = onMarkAllRead,
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue, contentColor = DeepBlack),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.testTag("mark_read_btn")
                        ) {
                            Text("CLEAR ALARMS", fontSize = 9.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item {
            SectionTitle("HUD INTEL CHRONOLOGY")
        }

        // List notifications
        if (notifications.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.NotificationsNone,
                            contentDescription = null,
                            tint = SecondaryText,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "CHRONO BUFFER IS ALL CLEAR",
                            color = MainText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Future decay alerts or progress checkpoints log here.",
                            color = SecondaryText,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        } else {
            items(notifications) { notification ->
                NotificationAlertItem(notification = notification)
            }
        }
    }
}

@Composable
fun NotificationAlertItem(notification: NotificationLog) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy  •  HH:mm", Locale.getDefault())
    val formattedDateStr = dateFormat.format(Date(notification.timestamp))

    val borderTint = if (notification.isRead) ContentGrey else ElectricBlue.copy(alpha = 0.25f)
    val circleColor = if (notification.isRead) SecondaryText else ElectricBlue

    Card(
        colors = CardDefaults.cardColors(containerColor = Graphite),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, borderTint),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("notification_alert_item_${notification.id}")
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .size(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(circleColor)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notification.title.uppercase(),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = if (notification.isRead) MainText else ElectricBlue,
                    fontFamily = FontFamily.Monospace
                )
                
                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = notification.message,
                    fontSize = 11.sp,
                    color = MainText,
                    lineHeight = 15.sp,
                    fontFamily = FontFamily.Monospace
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = formattedDateStr,
                    fontSize = 9.sp,
                    color = DarkText,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

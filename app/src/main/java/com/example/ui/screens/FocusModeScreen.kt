package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.FocusSessionState

@Composable
fun FocusModeScreen(
    focusState: FocusSessionState,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onTerminate: () -> Unit,
    onCompleteFeedback: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (!focusState.isActive) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DeepBlack),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "NO FOCUS PROTOCOL ENGAGED",
                color = SecondaryText,
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp
            )
        }
        return
    }

    val totalSecs = focusState.totalDurationMinutes * 60
    val secsRemaining = focusState.secondsRemaining
    val ratio = if (totalSecs > 0) secsRemaining.toFloat() / totalSecs.toFloat() else 1f

    val isBoss = focusState.topicName.contains("Boss", ignoreCase = true) || focusState.topicName.contains("Calamity", ignoreCase = true)
    val neonThemeColor = if (isBoss) CrimsonRed else ElectricBlue
    val borderBrush = Brush.linearGradient(listOf(neonThemeColor, neonThemeColor.copy(alpha = 0.3f)))

    // Clock formatting MM:SS
    val minutesStr = String.format("%02d", secsRemaining / 60)
    val secondsStr = String.format("%02d", secsRemaining % 60)

    val ambientTracks = listOf("None", "Retro White Noise", "Cyber Rain", "Zen Flute", "Space Ambient")
    var expandedTrackMenu by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlack)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxSize()
        ) {
            // High HUD Status
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(if (focusState.isPaused) NeonPurple else neonThemeColor))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (focusState.isPaused) "FOCUS LOCK: PAUSED" else "FOCUS LOCK: ACTIVE",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (focusState.isPaused) NeonPurple else neonThemeColor,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = focusState.subjectName.uppercase(),
                    color = SecondaryText,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }

            // Central Ring Core
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "TARGET SECTOR NODE",
                    fontSize = 10.sp,
                    color = SecondaryText,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = focusState.topicName,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = MainText,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Radial circle
                Box(
                    modifier = Modifier
                        .size(220.dp)
                        .clip(CircleShape)
                        .background(Graphite)
                        .border(BorderStroke(2.dp, borderBrush), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    // Inside clock glow
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$minutesStr:$secondsStr",
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Black,
                            color = neonThemeColor,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "TIME REMAINING",
                            fontSize = 9.sp,
                            color = SecondaryText,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        // Progress bar internally
                        Box(modifier = Modifier.width(100.dp)) {
                            LinearProgressIndicator(
                                progress = { ratio },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = neonThemeColor,
                                trackColor = Graphite
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Ambient Control Row
                Card(
                    colors = CardDefaults.cardColors(containerColor = Graphite),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, ContentGrey),
                    modifier = Modifier.width(220.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedTrackMenu = true }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.MusicNote,
                                contentDescription = null,
                                tint = ElectricBlue,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = focusState.ambientSoundName.uppercase(),
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = MainText,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Change track",
                            tint = SecondaryText
                        )
                    }
                }
            }

            // Lower Action Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Terminate Early
                OutlinedButton(
                    onClick = onTerminate,
                    border = BorderStroke(1.dp, CrimsonRed.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CrimsonRed),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Cancel,
                        contentDescription = "Abort Session"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ABORT",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Pause / Play
                Button(
                    onClick = { if (focusState.isPaused) onResume() else onPause() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (focusState.isPaused) ElectricBlue else NeonPurple,
                        contentColor = DeepBlack
                    ),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.weight(1.5f)
                ) {
                    Icon(
                        imageVector = if (focusState.isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                        contentDescription = if (focusState.isPaused) "Resume" else "Pause"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (focusState.isPaused) "RESUME CORE" else "PAUSE PROCESS",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

    // Interactive Floating Soundtrack picker dialog
    if (expandedTrackMenu) {
        AlertDialog(
            onDismissRequest = { expandedTrackMenu = false },
            containerColor = Graphite,
            shape = RoundedCornerShape(12.dp),
            title = {
                Text(
                    text = "SELECT STUDY AMBIENCE",
                    color = ElectricBlue,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            },
            text = {
                Column {
                    ambientTracks.forEach { track ->
                        val selected = focusState.ambientSoundName == track
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    expandedTrackMenu = false
                                }
                                .padding(vertical = 12.dp)
                        ) {
                            RadioButton(
                                selected = selected,
                                onClick = { expandedTrackMenu = false },
                                colors = RadioButtonDefaults.colors(selectedColor = ElectricBlue)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = track,
                                color = if (selected) ElectricBlue else MainText,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }

    // Spaced repetition cognitive feedback prompt dialog
    if (focusState.isSessionFinishedPendingFeedback) {
        AlertDialog(
            onDismissRequest = { }, // Lock user during evaluation
            containerColor = Graphite,
            shape = RoundedCornerShape(12.dp),
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = "Cognitive Evaluation",
                        tint = ElectricBlue,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "SESSION DIAGNOSTIC SUMMARY",
                        color = MainText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        textAlign = TextAlign.Center
                    )
                }
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "How evaluated was your active retention recall interval of standard definitions during the study?",
                        color = SecondaryText,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))

                    // Evaluation buttons
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // EASY
                        Button(
                            onClick = { onCompleteFeedback("EASY") },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen, contentColor = DeepBlack),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.fillMaxWidth().testTag("feedback_easy")
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                textTextEasy()
                            }
                        }

                        // MEDIUM
                        Button(
                            onClick = { onCompleteFeedback("MEDIUM") },
                            colors = ButtonDefaults.buttonColors(containerColor = NeonPurple, contentColor = DeepBlack),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.fillMaxWidth().testTag("feedback_medium")
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                textTextMedium()
                            }
                        }

                        // HARD
                        Button(
                            onClick = { onCompleteFeedback("HARD") },
                            colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed, contentColor = Color.White),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.fillMaxWidth().testTag("feedback_hard")
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                textTextHard()
                            }
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }
}

@Composable
private fun textTextEasy() {
    Text("EASY", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
    Text("High retention. Extend future interval.", fontSize = 9.sp, fontWeight = FontWeight.Bold)
}

@Composable
private fun textTextMedium() {
    Text("MEDIUM", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
    Text("Standard interval progression.", fontSize = 9.sp, fontWeight = FontWeight.Bold)
}

@Composable
private fun textTextHard() {
    Text("HARD / BREAKDOWN RESET ⚔️", fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
    Text("Struggled, reset memory intervals.", fontSize = 9.sp, fontWeight = FontWeight.Bold)
}

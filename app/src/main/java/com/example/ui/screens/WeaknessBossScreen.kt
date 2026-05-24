package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
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
import com.example.data.model.WeaknessBoss
import com.example.ui.theme.*

@Composable
fun WeaknessBossScreen(
    bosses: List<WeaknessBoss>,
    onFightBoss: (Long, String, Long, String) -> Unit,
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
        // Warning Banner
        item {
            val alertActive = bosses.isNotEmpty()
            Card(
                colors = CardDefaults.cardColors(containerColor = Graphite),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, if (alertActive) CrimsonRed.copy(alpha = 0.25f) else ContentGrey),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (alertActive) Icons.Default.Warning else Icons.Default.Security,
                        contentDescription = "Threat Diagnostic",
                        tint = if (alertActive) CrimsonRed else EmeraldGreen,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = if (alertActive) "MEM_BLOCKS ACTIVE: CORRECTION REQUIRED" else "COGNITIVE SYSTEMS COMPLETION",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = if (alertActive) CrimsonRed else MainText,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (alertActive) {
                                "Analytical algorithms identified memory lapses. Launch engage protocols to purge the weaknesses."
                            } else {
                                "All system subject topics exceed nominal memory containment scores. Great work."
                            },
                            fontSize = 10.sp,
                            color = SecondaryText,
                            lineHeight = 14.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // Section Title
        item {
            SectionTitle("HUD INTEL RADAR: COGNITIVE WEAKNESSES")
        }

        // List of Bosses
        if (bosses.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = EmeraldGreen,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "NO HOSTILE BLOCKS RECORDED",
                            color = MainText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Achieving low scores or skipping revisions registers bosses.",
                            color = SecondaryText,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        } else {
            items(bosses) { boss ->
                BossCardItem(
                    boss = boss,
                    onEngage = { onFightBoss(boss.topicId, boss.topicName, boss.subjectId, boss.subjectName) }
                )
            }
        }
    }
}

@Composable
fun BossCardItem(
    boss: WeaknessBoss,
    onEngage: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Graphite),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, CrimsonRed.copy(alpha = 0.35f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("boss_card_item_${boss.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(CrimsonRed.copy(alpha = 0.08f), RoundedCornerShape(4.dp))
                        .border(BorderStroke(1.dp, CrimsonRed.copy(alpha = 0.2f)), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = boss.subjectName.uppercase(),
                        color = CrimsonRed,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 0.5.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = CrimsonRed
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "INTENSITY TYPE: ${boss.intensityMeter}%",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = CrimsonRed,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Boss identification
            Text(
                text = boss.topicName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = MainText,
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Progress labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "DECONSTRUCTION LEVEL",
                    fontSize = 8.sp,
                    color = SecondaryText,
                    fontFamily = FontFamily.Monospace,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "${boss.defeatProgress}% RESOLVED",
                    fontSize = 9.sp,
                    color = ElectricBlue,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // HP Progress Bar
            LinearProgressIndicator(
                progress = { boss.defeatProgress.toFloat() / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = ElectricBlue,
                trackColor = CrimsonRed.copy(alpha = 0.15f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ElectricBolt,
                        contentDescription = "XP",
                        tint = NeonPurple,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "+200 XP DEFEAT CHARGE",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonPurple,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Button(
                    onClick = onEngage,
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed, contentColor = Color.White),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("engage_boss_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "ENGAGE CORP",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

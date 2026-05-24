package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Subject
import com.example.data.model.SystemState
import com.example.data.repository.Mission
import com.example.data.repository.PlannerMissions
import com.example.ui.theme.*

@Composable
fun CommandCenterScreen(
    state: SystemState,
    subjects: List<Subject>,
    missions: PlannerMissions,
    onNavigate: (String) -> Unit,
    onStartFocus: (Mission) -> Unit,
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
        // 1. Futuristic Status bar
        item {
            HudSystemStatus(state = state)
        }

        // 2. Active Subject Levels Grid
        if (subjects.isNotEmpty()) {
            item {
                SubjectLevelsMiniGrid(subjects = subjects, onNavigate = onNavigate)
            }
        }

        // 3. TODAY'S MAIN MISSION (The Visual Flagship)
        item {
            SectionTitle("HUD MAIN MISSION")
            
            if (missions.primary != null) {
                MainMissionCard(
                    mission = missions.primary,
                    onStartFocus = { onStartFocus(missions.primary) }
                )
            } else {
                NoMissionsCard(onNavigate = onNavigate)
            }
        }

        // 4. SECONDARY OBJECTIVES
        if (missions.secondary.isNotEmpty()) {
            item {
                SectionTitle("HUD SECONDARY SECTOR")
            }
            items(missions.secondary) { secMission ->
                SecondaryMissionItem(
                    mission = secMission,
                    onStartFocus = { onStartFocus(secMission) }
                )
            }
        }

        // 5. SECTOR DIRECT ACCESS (Quick links)
        item {
            SectionTitle("HUD ACCESS PORTS")
            QuickAccessModuleGrid(onNavigate = onNavigate, isBossActive = subjects.any { it.masteryPercent < 40 })
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(4.dp, 12.dp)
                .background(ElectricBlue)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = MainText,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp,
            fontFamily = FontFamily.Monospace
        )
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(ContentGrey)
        )
    }
}

@Composable
fun HudSystemStatus(state: SystemState) {
    val level = 1 + (state.totalXP / 1000)
    val xpInLevel = state.totalXP % 1000
    val nextLevelProgress = xpInLevel.toFloat() / 1000f

    Card(
        colors = CardDefaults.cardColors(containerColor = Graphite),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.15f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // HUD top row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(ElectricBlue)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "SYS_STATUS: ACTIVE",
                        style = MaterialTheme.typography.bodySmall,
                        color = MainText,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
                Text(
                    text = "LEVEL $level",
                    style = MaterialTheme.typography.bodySmall,
                    color = NeonPurple,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Level ratio progress bar
            LinearProgressIndicator(
                progress = { nextLevelProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .clip(RoundedCornerShape(1.5.dp)),
                color = NeonPurple,
                trackColor = ContentGrey
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Main stats indicators
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Streak
                Column {
                    Text(
                        text = "STREAK",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = SecondaryText,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "${state.dailyStreak} DAYS",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = NeonPurple,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Divider line
                Box(modifier = Modifier.width(1.dp).height(20.dp).background(ContentGrey))

                // XP
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "TOTAL XP",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = SecondaryText,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "${state.totalXP} PTS",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = MainText,
                        fontFamily = FontFamily.Monospace
                    )
                }

                // Divider line
                Box(modifier = Modifier.width(1.dp).height(20.dp).background(ContentGrey))

                // Duration Today
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "STUDY TIME",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = SecondaryText,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "${state.studyTimeTodayMinutes} MINS",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = ElectricBlue,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

@Composable
fun SubjectLevelsMiniGrid(subjects: List<Subject>, onNavigate: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        subjects.take(4).forEach { subject ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Graphite)
                    .border(BorderStroke(1.dp, ContentGrey), RoundedCornerShape(8.dp))
                    .clickable { onNavigate("subjects") }
                    .padding(8.dp)
            ) {
                Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = subject.name.uppercase(),
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = MainText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "L.${subject.level}",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            color = NeonPurple,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    // Mini progress ratio bar
                    val levelProgress = (subject.xpPoints % 400).toFloat() / 400f
                    LinearProgressIndicator(
                        progress = { levelProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.dp)
                            .clip(RoundedCornerShape(1.5.dp)),
                        color = NeonPurple,
                        trackColor = ContentGrey
                    )
                }
            }
        }
    }
}

@Composable
fun MainMissionCard(
    mission: Mission,
    onStartFocus: () -> Unit
) {
    val accentColor = if (mission.isBoss) CrimsonRed else ElectricBlue
    Card(
        colors = CardDefaults.cardColors(containerColor = Graphite),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.5.dp, accentColor.copy(alpha = 0.40f)),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("main_mission_card")
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Subject tag & estimation
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(accentColor.copy(alpha = 0.08f), RoundedCornerShape(6.dp))
                        .border(BorderStroke(1.dp, accentColor.copy(alpha = 0.25f)), RoundedCornerShape(6.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = mission.subjectName.uppercase(),
                        color = accentColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Timeline,
                        contentDescription = null,
                        modifier = Modifier.size(13.dp),
                        tint = SecondaryText
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${mission.estimatedDurationMinutes} MIN SESSION",
                        fontSize = 10.sp,
                        color = SecondaryText,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Topic name
            Text(
                text = mission.topicName,
                fontSize = 22.sp,
                fontWeight = FontWeight.Black,
                color = MainText,
                lineHeight = 28.sp,
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Recommendation Reason string
            Text(
                text = mission.reason,
                fontSize = 12.sp,
                color = if (mission.isBoss) CrimsonRed else SecondaryText,
                fontFamily = FontFamily.Monospace,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Visual divider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(ContentGrey)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Action section row
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
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "+${mission.xpReward} XP REWARD",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = NeonPurple,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Button(
                    onClick = onStartFocus,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = accentColor,
                        contentColor = DeepBlack
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                    modifier = Modifier.testTag("start_focus_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "START FOCUS",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}

@Composable
fun SecondaryMissionItem(
    mission: Mission,
    onStartFocus: () -> Unit
) {
    val borderAccent = if (mission.isBoss) CrimsonRed.copy(alpha = 0.3f) else ContentGrey
    Card(
        colors = CardDefaults.cardColors(containerColor = Graphite),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, borderAccent),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onStartFocus() }
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = mission.subjectName.uppercase(),
                        color = if (mission.isBoss) CrimsonRed else ElectricBlue,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "•  ${mission.estimatedDurationMinutes}M EST",
                        color = SecondaryText,
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = mission.topicName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MainText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "PRIORITY: ${mission.reason}",
                    fontSize = 10.sp,
                    color = SecondaryText,
                    fontFamily = FontFamily.Monospace,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            // Minimalist deployment card action
            IconButton(
                onClick = onStartFocus,
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(ContentGrey)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Engage Secondary Focus",
                    tint = if (mission.isBoss) CrimsonRed else ElectricBlue,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun NoMissionsCard(onNavigate: (String) -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Graphite),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, ContentGrey),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = EmeraldGreen,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "ACTIVE REVISION CYCLES COMPLETE",
                color = MainText,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Use the subject tree below to initialize study topics.",
                color = SecondaryText,
                fontSize = 10.sp,
                fontFamily = FontFamily.Monospace,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { onNavigate("subjects") },
                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue, contentColor = DeepBlack),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = "VISIT COGNITIVE TREES",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
fun QuickAccessModuleGrid(onNavigate: (String) -> Unit, isBossActive: Boolean) {
    val items = listOf(
        Triple("COGNITIVE CORES", Icons.Default.AccountTree, "subjects"),
        Triple("BOSS SEQUENCES", Icons.Default.Security, "bosses"),
        Triple("DIAGNOSTICS", Icons.Default.Analytics, "reports"),
        Triple("CALENDAR MESH", Icons.Default.DateRange, "calendar"),
        Triple("CHRONO LEDGER", Icons.Default.ShowChart, "progress"),
        Triple("OS CONFIG", Icons.Default.Settings, "settings")
    )

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items.chunked(2).forEach { pair ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                pair.forEach { (label, icon, route) ->
                    val isBossIndicator = route == "bosses" && isBossActive
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Graphite)
                            .border(
                                BorderStroke(
                                    1.dp,
                                    if (isBossIndicator) CrimsonRed.copy(alpha = 0.5f) else ContentGrey
                                ),
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { onNavigate(route) }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                tint = if (isBossIndicator) CrimsonRed else ElectricBlue,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isBossIndicator) CrimsonRed else MainText,
                                fontFamily = FontFamily.Monospace,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

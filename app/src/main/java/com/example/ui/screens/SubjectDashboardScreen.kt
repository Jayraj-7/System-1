package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Subject
import com.example.ui.theme.*

@Composable
fun SubjectDashboardScreen(
    subjects: List<Subject>,
    onSelectSubject: (Long, String) -> Unit,
    onAddSubject: (String, Double, String) -> Unit,
    onDeleteSubject: (Subject) -> Unit,
    modifier: Modifier = Modifier
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var newSubjectName by remember { mutableStateOf("") }
    var selectedDifficultyScale by remember { mutableStateOf(1.2) }
    var selectedIcon by remember { mutableStateOf("menu_book") }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(DeepBlack),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = ElectricBlue,
                contentColor = DeepBlack,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("add_subject_fab")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Initialize Subject Node"
                )
            }
        },
        containerColor = DeepBlack
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "COGNITIVE CORES",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = MainText,
                            fontFamily = FontFamily.Monospace,
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            text = "Activate training modules to structure knowledge nodes",
                            fontSize = 11.sp,
                            color = SecondaryText,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Graphite)
                            .border(BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.2f)), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "CORES: ${subjects.size}",
                            fontSize = 10.sp,
                            color = ElectricBlue,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            if (subjects.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "NO COGNITIVE CORES REGISTERED.\nINITIALIZE VIA FLOATING BUTTON.",
                            color = SecondaryText,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            lineHeight = 16.sp
                        )
                    }
                }
            } else {
                items(subjects) { subject ->
                    SubjectCard(
                        subject = subject,
                        onClick = { onSelectSubject(subject.id, subject.name) },
                        onDelete = { onDeleteSubject(subject) }
                    )
                }
            }
        }
    }

    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            containerColor = Graphite,
            shape = RoundedCornerShape(12.dp),
            title = {
                Text(
                    text = "INITIALIZE COGNITIVE CORE",
                    color = ElectricBlue,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = newSubjectName,
                        onValueChange = { newSubjectName = it },
                        label = { Text("Core Identifier", fontFamily = FontFamily.Monospace) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = DarkText,
                            focusedLabelColor = ElectricBlue,
                            unfocusedLabelColor = SecondaryText,
                            focusedTextColor = MainText,
                            unfocusedTextColor = MainText
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("new_subject_input")
                    )

                    Column {
                        Text(
                            text = "XP COEFFICIENT MULTIPLIER: ${selectedDifficultyScale}X",
                            fontSize = 10.sp,
                            color = SecondaryText,
                            fontFamily = FontFamily.Monospace
                        )
                        Slider(
                            value = selectedDifficultyScale.toFloat(),
                            onValueChange = { selectedDifficultyScale = Math.round(it * 10.0).toDouble() / 10.0 },
                            valueRange = 1.0f..2.0f,
                            colors = SliderDefaults.colors(
                                thumbColor = ElectricBlue,
                                activeTrackColor = ElectricBlue,
                                inactiveTrackColor = ContentGrey
                            )
                        )
                    }

                    Column {
                        Text(
                            text = "SELECT HUD ICONOLOGY",
                            fontSize = 10.sp,
                            color = SecondaryText,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            val iconsList = listOf(
                                "menu_book" to Icons.Default.Book,
                                "science" to Icons.Default.Science,
                                "calculate" to Icons.Default.Calculate,
                                "terminal" to Icons.Default.Terminal
                            )

                            iconsList.forEach { (name, vector) ->
                                val selected = selectedIcon == name
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (selected) ElectricBlue else ContentGrey)
                                        .clickable { selectedIcon = name },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = vector,
                                        contentDescription = null,
                                        tint = if (selected) DeepBlack else MainText,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newSubjectName.isNotBlank()) {
                            onAddSubject(newSubjectName, selectedDifficultyScale, selectedIcon)
                            newSubjectName = ""
                            showCreateDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue, contentColor = DeepBlack),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.testTag("confirm_add_subject_btn")
                ) {
                    Text("DEPLOY", fontFamily = FontFamily.Monospace)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateDialog = false }) {
                    Text("ABORT", color = SecondaryText, fontFamily = FontFamily.Monospace)
                }
            }
        )
    }
}

@Composable
fun SubjectCard(
    subject: Subject,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val iconVector = when(subject.iconName) {
        "science" -> Icons.Default.Science
        "calculate" -> Icons.Default.Calculate
        "terminal" -> Icons.Default.Terminal
        else -> Icons.Default.Book
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = Graphite),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, ContentGrey),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Main Top Title Area
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(ContentGrey),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = iconVector,
                            contentDescription = null,
                            tint = ElectricBlue,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = subject.name,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = MainText
                        )
                        Text(
                            text = "XP COEFFICIENT: ${subject.difficultyScale}X",
                            fontSize = 8.sp,
                            color = NeonPurple,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Delete node button
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteForever,
                        contentDescription = "Deconstruct Subject Node",
                        tint = CrimsonRed.copy(alpha = 0.50f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Level & XP Bar metrics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LEVEL EXP: ${subject.xpPoints % 400} / 400 PTS",
                    fontSize = 10.sp,
                    color = SecondaryText,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "LV.${subject.level}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = NeonPurple,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Mini progress indicator
            LinearProgressIndicator(
                progress = { (subject.xpPoints % 400).toFloat() / 400f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp)),
                color = ElectricBlue,
                trackColor = ContentGrey
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Mastery bar layout
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccountTree,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = SecondaryText
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "MASTERY INDEX: ${subject.masteryPercent}%",
                        fontSize = 10.sp,
                        color = SecondaryText,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Text(
                    text = "OPEN CORE NODES →",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = ElectricBlue,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

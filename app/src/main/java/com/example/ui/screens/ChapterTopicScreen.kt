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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Chapter
import com.example.data.model.Topic
import com.example.ui.theme.*

@Composable
fun ChapterTopicScreen(
    subjectId: Long,
    subjectName: String,
    chapters: List<Chapter>,
    topics: List<Topic>,
    onAddChapter: (Long, String, Int) -> Unit,
    onAddTopic: (Long, Long, String, Int) -> Unit,
    onDeleteTopic: (Topic) -> Unit,
    onStartFocus: (Topic, String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showChapterDialog by remember { mutableStateOf(false) }
    var showTopicDialog by remember { mutableStateOf(false) }

    // Dialog state holders
    var newChapterName by remember { mutableStateOf("") }
    var newTopicName by remember { mutableStateOf("") }
    var newTopicDifficulty by remember { mutableStateOf(3) }
    var selectedChapterIdForNewTopic by remember { mutableStateOf<Long?>(null) }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(DeepBlack),
        containerColor = DeepBlack
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Graphite)
                        .border(BorderStroke(1.dp, ContentGrey), RoundedCornerShape(6.dp))
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Return to Core Dashboard",
                        tint = ElectricBlue,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = subjectName.uppercase(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = MainText,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "INTELLIGENT SKILL BRANCHES",
                        fontSize = 9.sp,
                        color = SecondaryText,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    )
                }

                // Add Chapter FAB/Button
                IconButton(
                    onClick = { showChapterDialog = true },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(ElectricBlue)
                ) {
                    Icon(
                        imageVector = Icons.Default.CreateNewFolder,
                        contentDescription = "Deploy Chapter Core",
                        tint = DeepBlack,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(ContentGrey)
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (chapters.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "NO ACTIVE CHAPTER SECTORS INITIALIZED",
                            color = SecondaryText,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { showChapterDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue, contentColor = DeepBlack),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text("DEPLOY FIRST SECTOR", fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(chapters) { chapter ->
                        val chapterTopics = topics.filter { it.chapterId == chapter.id }
                        
                        ChapterNodeCard(
                            chapter = chapter,
                            topics = chapterTopics,
                            onAddTopicClick = {
                                selectedChapterIdForNewTopic = chapter.id
                                showTopicDialog = true
                            },
                            onDeleteTopic = onDeleteTopic,
                            onStartFocus = { topic -> onStartFocus(topic, subjectName) }
                        )
                    }
                }
            }
        }
    }

    // Dialog 1: Add Chapter
    if (showChapterDialog) {
        AlertDialog(
            onDismissRequest = { showChapterDialog = false },
            containerColor = Graphite,
            shape = RoundedCornerShape(12.dp),
            title = {
                Text(
                    text = "DEPLOY CHAPTER SECTOR CORES",
                    color = ElectricBlue,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            },
            text = {
                OutlinedTextField(
                    value = newChapterName,
                    onValueChange = { newChapterName = it },
                    label = { Text("Chapter identification name", fontFamily = FontFamily.Monospace) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricBlue,
                        unfocusedBorderColor = DarkText,
                        focusedLabelColor = ElectricBlue,
                        unfocusedLabelColor = SecondaryText,
                        focusedTextColor = MainText,
                        unfocusedTextColor = MainText
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("new_chapter_input")
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newChapterName.isNotBlank()) {
                            onAddChapter(subjectId, newChapterName, chapters.size + 1)
                            newChapterName = ""
                            showChapterDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue, contentColor = DeepBlack),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.testTag("confirm_add_chapter_btn")
                ) {
                    Text("COMPILE MODULE", fontFamily = FontFamily.Monospace)
                }
            },
            dismissButton = {
                TextButton(onClick = { showChapterDialog = false }) {
                    Text("ABORT", color = SecondaryText, fontFamily = FontFamily.Monospace)
                }
            }
        )
    }

    // Dialog 2: Add Topic
    if (showTopicDialog) {
        AlertDialog(
            onDismissRequest = { showTopicDialog = false },
            containerColor = Graphite,
            shape = RoundedCornerShape(12.dp),
            title = {
                Text(
                    text = "DEPLOY COGNITIVE LEARNING NODE",
                    color = ElectricBlue,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = newTopicName,
                        onValueChange = { newTopicName = it },
                        label = { Text("Learning node identity", fontFamily = FontFamily.Monospace) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = DarkText,
                            focusedLabelColor = ElectricBlue,
                            unfocusedLabelColor = SecondaryText,
                            focusedTextColor = MainText,
                            unfocusedTextColor = MainText
                        ),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("new_topic_input")
                    )

                    Column {
                        Text(
                            text = "TRAINING THREAT INDEX LEVEL: $newTopicDifficulty",
                            fontSize = 10.sp,
                            color = SecondaryText,
                            fontFamily = FontFamily.Monospace
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            (1..5).forEach { rate ->
                                val active = rate <= newTopicDifficulty
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = if (active) NeonPurple else DarkText,
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clickable { newTopicDifficulty = rate }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val chapterId = selectedChapterIdForNewTopic
                        if (newTopicName.isNotBlank() && chapterId != null) {
                            onAddTopic(chapterId, subjectId, newTopicName, newTopicDifficulty)
                            newTopicName = ""
                            newTopicDifficulty = 3
                            showTopicDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue, contentColor = DeepBlack),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.testTag("confirm_add_topic_btn")
                ) {
                    Text("DEPLOY NODE", fontFamily = FontFamily.Monospace)
                }
            },
            dismissButton = {
                TextButton(onClick = { showTopicDialog = false }) {
                    Text("ABORT", color = SecondaryText, fontFamily = FontFamily.Monospace)
                }
            }
        )
    }
}

@Composable
fun ChapterNodeCard(
    chapter: Chapter,
    topics: List<Topic>,
    onAddTopicClick: () -> Unit,
    onDeleteTopic: (Topic) -> Unit,
    onStartFocus: (Topic) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Graphite),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, ContentGrey),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Chapter Title Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FolderOpen,
                        contentDescription = null,
                        tint = NeonPurple,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = chapter.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = MainText,
                        maxLines = 1
                    )
                }

                // Add topic to this chapter button
                IconButton(
                    onClick = onAddTopicClick,
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(ContentGrey)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Inject Target Node",
                        tint = ElectricBlue,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (topics.isEmpty()) {
                Text(
                    text = "No training nodes compiled in sector yet.",
                    color = SecondaryText,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(vertical = 12.dp)
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    topics.forEach { topic ->
                        TopicNodeItem(
                            topic = topic,
                            onStartFocus = { onStartFocus(topic) },
                            onDelete = { onDeleteTopic(topic) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TopicNodeItem(
    topic: Topic,
    onStartFocus: () -> Unit,
    onDelete: () -> Unit
) {
    val statusColor = when (topic.status) {
        "MASTERED" -> EmeraldGreen
        "LEARNING" -> NeonPurple
        else -> SecondaryText
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(ContentGrey)
            .border(BorderStroke(1.dp, ContentGrey), RoundedCornerShape(8.dp))
            .padding(10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Status Tag
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(statusColor.copy(alpha = 0.08f))
                            .border(BorderStroke(1.dp, statusColor.copy(alpha = 0.2f)), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = topic.status,
                            color = statusColor,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    // Difficulty stars summary
                    Row {
                        (1..5).forEach { idx ->
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (idx <= topic.difficulty) NeonPurple else DarkText,
                                modifier = Modifier.size(10.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = topic.name,
                    color = MainText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Memory Decay Metrics bar
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "TERM MEMORY CELL: ",
                        fontSize = 8.sp,
                        color = SecondaryText,
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "${topic.memoryStrength}%",
                        fontSize = 9.sp,
                        color = if (topic.memoryStrength < 40) CrimsonRed else statusColor,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(modifier = Modifier.width(60.dp)) {
                        LinearProgressIndicator(
                            progress = { topic.memoryStrength.toFloat() / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(3.dp)
                                .clip(RoundedCornerShape(1.5.dp)),
                            color = if (topic.memoryStrength < 40) CrimsonRed else ElectricBlue,
                            trackColor = Graphite
                        )
                    }
                }
            }

            // Controls on right
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Delete
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Remove Node Segment",
                        tint = CrimsonRed.copy(alpha = 0.40f),
                        modifier = Modifier.size(14.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(4.dp))

                // Engage Focus
                IconButton(
                    onClick = onStartFocus,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(ElectricBlue)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Deploy Focused Node Replay",
                        tint = DeepBlack,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.Topic
import com.example.ui.theme.*
import com.example.ui.viewmodel.StudyViewModel

sealed class ScreenRoute(val route: String, val label: String, val icon: ImageVector) {
    object Home : ScreenRoute("home", "Mission", Icons.Default.Dashboard)
    object Subjects : ScreenRoute("subjects", "Subjects", Icons.Default.Book)
    object Bosses : ScreenRoute("bosses", "Bosses", Icons.Default.Security)
    object Reports : ScreenRoute("reports", "Metrics", Icons.Default.Analytics)
    object Calendar : ScreenRoute("calendar", "Calendar", Icons.Default.CalendarMonth)
    object Progress : ScreenRoute("progress", "Ledger", Icons.Default.ShowChart)
    object Notifications : ScreenRoute("notifications", "Comms", Icons.Default.Notifications)
    object Settings : ScreenRoute("settings", "Config", Icons.Default.Settings)
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MainScreenContainer(
    viewModel: StudyViewModel,
    modifier: Modifier = Modifier
) {
    var currentRoute by remember { mutableStateOf<String>("home") }
    
    // Sub-route state for detailed subject visualizer
    var activeChapterSubjectId by remember { mutableStateOf<Long?>(null) }
    var activeChapterSubjectName by remember { mutableStateOf("") }

    // Collect Viewmodel States safely
    val state by viewModel.systemState.collectAsStateWithLifecycle()
    val subjects by viewModel.allSubjects.collectAsStateWithLifecycle()
    val chapters by viewModel.allChapters.collectAsStateWithLifecycle()
    val topics by viewModel.allTopics.collectAsStateWithLifecycle()
    val bosses by viewModel.activeBosses.collectAsStateWithLifecycle()
    val sessions by viewModel.recentSessions.collectAsStateWithLifecycle()
    val reports by viewModel.weeklyReports.collectAsStateWithLifecycle()
    val notifications by viewModel.notificationLogs.collectAsStateWithLifecycle()
    val dailyMissions by viewModel.dailyMissions.collectAsStateWithLifecycle()
    val focusState by viewModel.focusModeState.collectAsStateWithLifecycle()

    val subjectMap = remember(subjects) { subjects.associate { it.id to it.name } }

    // Dynamic responsive sizing checks (guidelines compliant)
    val config = LocalConfiguration.current
    val isWideScreen = config.screenWidthDp >= 600

    // Force route context to "focus" when immersive session ticks, overriding selections
    val resolvedRoute = if (focusState.isActive) "focus" else currentRoute

    val isMenuVisible = !focusState.isActive

    val navigationItems = listOf(
        ScreenRoute.Home,
        ScreenRoute.Subjects,
        ScreenRoute.Bosses,
        ScreenRoute.Reports,
        ScreenRoute.Calendar,
        ScreenRoute.Progress,
        ScreenRoute.Notifications,
        ScreenRoute.Settings
    )

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(HudBlack),
        containerColor = HudBlack,
        bottomBar = {
            if (isMenuVisible && !isWideScreen) {
                NavigationBar(
                    containerColor = HudDarkGrey,
                    tonalElevation = 8.dp,
                    modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
                ) {
                    navigationItems.forEach { item ->
                        val selected = resolvedRoute == item.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                activeChapterSubjectId = null
                                currentRoute = item.route
                            },
                            icon = {
                                BadgedBox(
                                    badge = {
                                        if (item == ScreenRoute.Notifications && notifications.any { !it.isRead }) {
                                            Badge(containerColor = NeonRed)
                                        } else if (item == ScreenRoute.Bosses && bosses.isNotEmpty()) {
                                            Badge(containerColor = NeonRed) { Text("${bosses.size}") }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.label,
                                        tint = if (selected) NeonCyan else TextMuted
                                    )
                                }
                            },
                            label = {
                                Text(
                                    text = item.label,
                                    fontSize = 9.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (selected) NeonCyan else TextMuted,
                                    maxLines = 1
                                )
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Color(0xFF1B2F36)
                            ),
                            modifier = Modifier.testTag("nav_item_${item.route}")
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Adaptive Side Menu Rail for Expanded Tablets/ChromeOS
            if (isMenuVisible && isWideScreen) {
                NavigationRail(
                    containerColor = HudDarkGrey,
                    modifier = Modifier
                        .fillMaxHeight()
                        .windowInsetsPadding(WindowInsets.systemBars),
                    header = {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "OS HUD SYSTEM",
                            tint = NeonCyan,
                            modifier = Modifier
                                .padding(vertical = 16.dp)
                                .size(32.dp)
                        )
                    }
                ) {
                    Spacer(modifier = Modifier.weight(1f))
                    navigationItems.forEach { item ->
                        val selected = resolvedRoute == item.route
                        NavigationRailItem(
                            selected = selected,
                            onClick = {
                                activeChapterSubjectId = null
                                currentRoute = item.route
                            },
                            icon = {
                                BadgedBox(
                                    badge = {
                                        if (item == ScreenRoute.Notifications && notifications.any { !it.isRead }) {
                                            Badge(containerColor = NeonRed)
                                        } else if (item == ScreenRoute.Bosses && bosses.isNotEmpty()) {
                                            Badge(containerColor = NeonRed) { Text("${bosses.size}") }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = item.icon,
                                        contentDescription = item.label,
                                        tint = if (selected) NeonCyan else TextMuted
                                    )
                                }
                            },
                            label = {
                                Text(
                                    text = item.label,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (selected) NeonCyan else TextMuted
                                )
                            },
                            colors = NavigationRailItemDefaults.colors(
                                indicatorColor = Color(0xFF1B2F36)
                            ),
                            modifier = Modifier.testTag("rail_item_${item.route}")
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            // Screen Area with Transition Slides
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(HudBlack)
            ) {
                AnimatedContent(
                    targetState = resolvedRoute,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(150)) togetherWith fadeOut(animationSpec = tween(150))
                    },
                    modifier = Modifier.fillMaxSize(),
                    label = "OS Screen Animation"
                ) { targetRoute ->
                    // Main Route switching handling
                    when (targetRoute) {
                        "focus" -> {
                            FocusModeScreen(
                                focusState = focusState,
                                onPause = { viewModel.pauseFocusSession() },
                                onResume = { viewModel.resumeFocusSession() },
                                onTerminate = { viewModel.terminateFocusSessionEarly() },
                                onCompleteFeedback = { difficulty ->
                                    viewModel.submitFocusSessionFeedbackAndComplete(difficulty)
                                }
                            )
                        }

                        "home" -> {
                            CommandCenterScreen(
                                state = state,
                                subjects = subjects,
                                missions = dailyMissions,
                                onNavigate = { route -> currentRoute = route },
                                onStartFocus = { mission ->
                                    viewModel.startFocusSession(
                                        topicId = mission.topicId,
                                        topicName = mission.topicName,
                                        subjectId = mission.subjectId,
                                        subjectName = mission.subjectName,
                                        durationMinutes = mission.estimatedDurationMinutes
                                    )
                                }
                            )
                        }

                        "subjects" -> {
                            // Sub-navigation handling for chapter-specific sub-screens
                            val currentSubjectId = activeChapterSubjectId
                            if (currentSubjectId != null) {
                                val subjectChapters = chapters.filter { it.subjectId == currentSubjectId }
                                val subjectTopics = topics.filter { it.subjectId == currentSubjectId }

                                ChapterTopicScreen(
                                    subjectId = currentSubjectId,
                                    subjectName = activeChapterSubjectName,
                                    chapters = subjectChapters,
                                    topics = subjectTopics,
                                    onAddChapter = { subId, name, order ->
                                        viewModel.addNewChapter(subId, name, order)
                                    },
                                    onAddTopic = { chapId, subId, name, diff ->
                                        viewModel.addNewTopic(chapId, subId, name, diff)
                                    },
                                    onDeleteTopic = { topic -> viewModel.deleteTopic(topic) },
                                    onStartFocus = { topic, sName ->
                                        // Auto approximate duration base
                                        val min = when(topic.difficulty) { 5 -> 50 4 -> 40 3 -> 30 else -> 25 }
                                        viewModel.startFocusSession(
                                            topicId = topic.id,
                                            topicName = topic.name,
                                            subjectId = topic.subjectId,
                                            subjectName = sName,
                                            durationMinutes = min
                                        )
                                    },
                                    onBack = { activeChapterSubjectId = null }
                                )
                            } else {
                                SubjectDashboardScreen(
                                    subjects = subjects,
                                    onSelectSubject = { id, name ->
                                        activeChapterSubjectId = id
                                        activeChapterSubjectName = name
                                    },
                                    onAddSubject = { name, scale, icon ->
                                        viewModel.addNewSubject(name, scale, icon)
                                    },
                                    onDeleteSubject = { subject -> viewModel.deleteSubject(subject) }
                                )
                            }
                        }

                        "bosses" -> {
                            WeaknessBossScreen(
                                bosses = bosses,
                                onFightBoss = { topicId, topicName, subjectId, subjectName ->
                                    viewModel.startFocusSession(
                                        topicId = topicId,
                                        topicName = topicName,
                                        subjectId = subjectId,
                                        subjectName = subjectName,
                                        durationMinutes = 25
                                    )
                                }
                            )
                        }

                        "reports" -> {
                            WeeklyReportsScreen(
                                reports = reports,
                                onGenerateNewReport = { viewModel.triggerWeeklyReportGeneration() }
                            )
                        }

                        "calendar" -> {
                            RevisionCalendarScreen(
                                topics = topics,
                                subjectMap = subjectMap,
                                onStartFocus = { topic, subName ->
                                    val duration = when(topic.difficulty) { 5 -> 50 4 -> 40 3 -> 30 else -> 25 }
                                    viewModel.startFocusSession(
                                        topicId = topic.id,
                                        topicName = topic.name,
                                        subjectId = topic.subjectId,
                                        subjectName = subName,
                                        durationMinutes = duration
                                    )
                                }
                            )
                        }

                        "progress" -> {
                            XPProgressScreen(
                                state = state,
                                sessions = sessions
                            )
                        }

                        "settings" -> {
                            SettingsScreen(
                                onTriggerReport = { viewModel.triggerWeeklyReportGeneration() }
                            )
                        }

                        "notifications" -> {
                            NotificationsCenterScreen(
                                notifications = notifications,
                                onMarkAllRead = {
                                    viewModel.markAllNotificationsRead()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

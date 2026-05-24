package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.model.*
import com.example.data.repository.Mission
import com.example.data.repository.PlannerMissions
import com.example.data.repository.StudyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class StudyViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: StudyRepository
    private var timerJob: Job? = null

    // --- Database Source Flow ---
    val allSubjects: StateFlow<List<Subject>>
    val allChapters: StateFlow<List<Chapter>>
    val allTopics: StateFlow<List<Topic>>
    val activeBosses: StateFlow<List<WeaknessBoss>>
    val recentSessions: StateFlow<List<StudySession>>
    val weeklyReports: StateFlow<List<WeeklyReport>>
    val systemState: StateFlow<SystemState>
    val notificationLogs: StateFlow<List<NotificationLog>>

    // --- Dynamic Planner State ---
    private val _dailyMissions = MutableStateFlow<PlannerMissions>(PlannerMissions(null, emptyList()))
    val dailyMissions: StateFlow<PlannerMissions> = _dailyMissions.asStateFlow()

    // --- Focus Mode Active States ---
    private val _focusModeState = MutableStateFlow(FocusSessionState())
    val focusModeState: StateFlow<FocusSessionState> = _focusModeState.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = StudyRepository(database.studyDao())

        allSubjects = repository.allSubjects
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        allChapters = repository.allChapters
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        allTopics = repository.allTopics
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        activeBosses = repository.activeBosses
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        recentSessions = repository.recentSessions
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        weeklyReports = repository.weeklyReports
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        systemState = repository.systemState
            .map { it ?: SystemState(id = 1, dailyStreak = 3, totalXP = 1250, studyTimeTodayMinutes = 0) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SystemState(id = 1))

        notificationLogs = repository.notificationLogs
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        // Periodically refresh missions or refresh when topics update
        viewModelScope.launch {
            allTopics.collect {
                refreshPlannerMissions()
            }
        }
    }

    // --- Planner Methods ---
    fun refreshPlannerMissions() {
        viewModelScope.launch(Dispatchers.IO) {
            val missions = repository.generateDailyPlannerMissions()
            _dailyMissions.value = missions
        }
    }

    // --- Focus Mode Core Logic ---
    fun startFocusSession(
        topicId: Long,
        topicName: String,
        subjectId: Long,
        subjectName: String,
        durationMinutes: Int,
        ambientSound: String = "None"
    ) {
        timerJob?.cancel()
        _focusModeState.value = FocusSessionState(
            isActive = true,
            topicId = topicId,
            topicName = topicName,
            subjectId = subjectId,
            subjectName = subjectName,
            totalDurationMinutes = durationMinutes,
            secondsRemaining = durationMinutes * 60,
            isPaused = false,
            ambientSoundName = ambientSound
        )

        // Begin tick
        startTimerJob()
    }

    private fun startTimerJob() {
        timerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                val currentState = _focusModeState.value
                if (currentState.isActive && !currentState.isPaused) {
                    val remaining = currentState.secondsRemaining - 1
                    if (remaining <= 0) {
                        // Automatically complete / yield to feedback stage
                        _focusModeState.value = currentState.copy(
                            secondsRemaining = 0,
                            isSessionFinishedPendingFeedback = true
                        )
                        break
                    } else {
                        _focusModeState.value = currentState.copy(secondsRemaining = remaining)
                    }
                }
            }
        }
    }

    fun pauseFocusSession() {
        _focusModeState.value = _focusModeState.value.copy(isPaused = true)
    }

    fun resumeFocusSession() {
        _focusModeState.value = _focusModeState.value.copy(isPaused = false)
    }

    fun terminateFocusSessionEarly() {
        timerJob?.cancel()
        _focusModeState.value = FocusSessionState()
    }

    fun submitFocusSessionFeedbackAndComplete(feedbackDifficulty: String) {
        timerJob?.cancel()
        val stateValue = _focusModeState.value
        val minutesStudied = stateValue.totalDurationMinutes - (stateValue.secondsRemaining / 60)
        val validMinutes = if (minutesStudied <= 0) 1 else minutesStudied

        viewModelScope.launch(Dispatchers.IO) {
            repository.recordStudySession(
                topicId = stateValue.topicId,
                durationMinutes = validMinutes,
                feedbackGrade = feedbackDifficulty
            )
            // Reset focus mode state
            _focusModeState.value = FocusSessionState()
            // Force re-planner run
            refreshPlannerMissions()
        }
    }

    // --- Creation Methods ---
    fun addNewSubject(name: String, scale: Double = 1.0, icon: String = "menu_book") {
        viewModelScope.launch(Dispatchers.IO) {
            val subId = repository.insertSubject(
                Subject(
                    name = name,
                    level = 1,
                    xpPoints = 0,
                    masteryPercent = 0,
                    difficultyScale = scale,
                    iconName = icon
                )
            )
            // Default introductory chapter
            val capId = repository.insertChapter(
                Chapter(
                    subjectId = subId,
                    name = "Fundamentals of $name",
                    orderNumber = 1
                )
            )
            // Default sample topic
            repository.insertTopic(
                Topic(
                    chapterId = capId,
                    subjectId = subId,
                    name = "Introduction to $name",
                    difficulty = 2,
                    status = "NEW",
                    lastReviewed = 0L,
                    currentIntervalDays = 1,
                    memoryStrength = 100
                )
            )
        }
    }

    fun addNewChapter(subjectId: Long, name: String, orderNumber: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertChapter(
                Chapter(
                    subjectId = subjectId,
                    name = name,
                    orderNumber = orderNumber
                )
            )
        }
    }

    fun addNewTopic(chapterId: Long, subjectId: Long, name: String, difficulty: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertTopic(
                Topic(
                    chapterId = chapterId,
                    subjectId = subjectId,
                    name = name,
                    difficulty = difficulty,
                    status = "NEW",
                    lastReviewed = 0,
                    currentIntervalDays = 1,
                    memoryStrength = 100
                )
            )
        }
    }

    fun deleteTopic(topic: Topic) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTopic(topic)
        }
    }

    fun deleteSubject(subject: Subject) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteSubject(subject)
        }
    }

    fun triggerWeeklyReportGeneration() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.generateWeeklyReport()
        }
    }

    fun markAllNotificationsRead() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.markAllNotificationsRead()
        }
    }
}

// State container for Active Focus UI
data class FocusSessionState(
    val isActive: Boolean = false,
    val topicId: Long = 0L,
    val topicName: String = "",
    val subjectId: Long = 0L,
    val subjectName: String = "",
    val totalDurationMinutes: Int = 25,
    val secondsRemaining: Int = 1500,
    val isPaused: Boolean = false,
    val isSessionFinishedPendingFeedback: Boolean = false,
    val ambientSoundName: String = "None"
)

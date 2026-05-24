package com.example.data.repository

import android.util.Log
import com.example.data.dao.StudyDao
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.*

class StudyRepository(private val dao: StudyDao) {

    val allSubjects: Flow<List<Subject>> = dao.getAllSubjects()
    val allChapters: Flow<List<Chapter>> = dao.getAllChapters()
    val allTopics: Flow<List<Topic>> = dao.getAllTopics()
    val activeBosses: Flow<List<WeaknessBoss>> = dao.getActiveBosses()
    val recentSessions: Flow<List<StudySession>> = dao.getRecentSessions(30)
    val weeklyReports: Flow<List<WeeklyReport>> = dao.getAllReports()
    val systemState: Flow<SystemState?> = dao.getSystemState()
    val notificationLogs: Flow<List<NotificationLog>> = dao.getNotifications()

    suspend fun getSubjectChaptersFlow(subjectId: Long): Flow<List<Chapter>> {
        return dao.getChaptersForSubject(subjectId)
    }

    suspend fun getSubjectTopicsFlow(subjectId: Long): Flow<List<Topic>> {
        return dao.getTopicsForSubject(subjectId)
    }

    // --- Core Spaced Repetition & Progress Engine ---
    suspend fun recordStudySession(
        topicId: Long,
        durationMinutes: Int,
        feedbackGrade: String // "EASY", "MEDIUM", "HARD"
    ) {
        val topic = dao.getTopicById(topicId) ?: return
        val subject = dao.getSubjectById(topic.subjectId) ?: return
        val now = System.currentTimeMillis()

        // 1. Spaced Repetition interval & memory strength recalculation
        val prevInterval = topic.currentIntervalDays
        val prevStrength = topic.memoryStrength

        val (newInterval, newStrength, newStatus) = when (feedbackGrade) {
            "EASY" -> {
                Triple(
                    (prevInterval * 2.2 + 1).toInt().coerceIn(1, 180),
                    (prevStrength + 25).coerceAtMost(100),
                    "MASTERED"
                )
            }
            "MEDIUM" -> {
                Triple(
                    (prevInterval * 1.4 + 1).toInt().coerceIn(1, 180),
                    (prevStrength + 10).coerceAtMost(100),
                    "LEARNING"
                )
            }
            else -> { // "HARD" feedback - shrink interval and reset memory
                Triple(
                    1,
                    (prevStrength - 30).coerceAtLeast(10),
                    "LEARNING"
                )
            }
        }

        // Add to recall confidence history (keep last 5)
        val scoreInt = when(feedbackGrade) { "EASY" -> 5 "MEDIUM" -> 3 else -> 1 }
        val newHistory = if (topic.recallConfidenceHistory.isEmpty()) {
            "$scoreInt"
        } else {
            val list = topic.recallConfidenceHistory.split(",").toMutableList()
            if (list.size >= 5) list.removeAt(0)
            list.add("$scoreInt")
            list.joinToString(",")
        }

        // Update topic in DB
        val updatedTopic = topic.copy(
            lastReviewed = now,
            currentIntervalDays = newInterval,
            memoryStrength = newStrength,
            status = newStatus,
            recallConfidenceHistory = newHistory
        )
        dao.updateTopic(updatedTopic)

        // 2. XP progression calculation
        val baseXP = 50 + (durationMinutes * 2.5).toInt()
        val difficultyMultiplier = 1.0 + (topic.difficulty * 0.15)
        val gradeMultiplier = when(feedbackGrade) { "HARD" -> 1.2 "MEDIUM" -> 1.0 else -> 0.9 } // Reward tackling hard items or high consistency
        val finalXP = (baseXP * difficultyMultiplier * gradeMultiplier).toInt()

        // 3. Update Subject Level & XP
        val currentSubjectXP = subject.xpPoints + finalXP
        val newSubjectLevel = 1 + (currentSubjectXP / 400) // Subject levels up faster
        
        // Recalculate Mastery Percent in subject
        val allSubTopics = dao.getAllTopicsList().filter { it.subjectId == subject.id }
        val masteredCount = allSubTopics.count { it.status == "MASTERED" || (it.id == topicId && newStatus == "MASTERED") }
        val computedMastery = if (allSubTopics.isNotEmpty()) {
            (masteredCount * 100) / allSubTopics.size
        } else 0

        dao.updateSubject(
            subject.copy(
                xpPoints = currentSubjectXP,
                level = newSubjectLevel,
                masteryPercent = computedMastery
            )
        )

        // 4. Update Global System State (Streak, Total XP, Study Time today)
        val state = dao.getSystemStateOnce() ?: SystemState(id = 1)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayStr = dateFormat.format(Date(now))
        val yesterdayStr = dateFormat.format(Date(now - 24 * 3600 * 1000L))

        val newStreak = when (state.lastStudyDateString) {
            todayStr -> state.dailyStreak // Already studied today
            yesterdayStr -> state.dailyStreak + 1 // Consecutive day
            "" -> 1 // First time studying
            else -> 1 // Broken streak reset
        }

        val accumStudyTime = if (state.lastStudyDateString == todayStr) {
            state.studyTimeTodayMinutes + durationMinutes
        } else {
            durationMinutes
        }

        dao.insertSystemState(
            state.copy(
                dailyStreak = newStreak,
                totalXP = state.totalXP + finalXP,
                studyTimeTodayMinutes = accumStudyTime,
                lastStudyDateString = todayStr
            )
        )

        // 5. Weakness Boss Trigger & Defeat Progression
        val existingBoss = dao.getBossByTopicId(topicId)
        if (feedbackGrade == "HARD") {
            if (existingBoss == null) {
                // Summon a new Boss!
                val bossTitle = when(topic.difficulty) {
                    5 -> "Calamity: ${topic.name}"
                    4 -> "Titan: ${topic.name}"
                    else -> "Boss: ${topic.name}"
                }
                dao.insertBoss(
                    WeaknessBoss(
                        topicId = topicId,
                        topicName = bossTitle,
                        subjectId = topic.subjectId,
                        subjectName = subject.name,
                        intensityMeter = 70 + (topic.difficulty * 5),
                        defeatProgress = 10,
                        isActive = true
                    )
                )
                dao.insertNotification(
                    NotificationLog(
                        title = "Boss Enemy Summoned! ⚔️",
                        message = "Topic '${topic.name}' in ${subject.name} flagged as critical weakness. Defeat this boss by achieving higher confidence sessions!",
                        timestamp = now
                    )
                )
            } else {
                // Boss already exists, increase its intensity
                val expandedIntensity = (existingBoss.intensityMeter + 15).coerceAtMost(100)
                dao.updateBoss(
                    existingBoss.copy(
                        intensityMeter = expandedIntensity,
                        defeatProgress = (existingBoss.defeatProgress - 15).coerceAtLeast(0)
                    )
                )
            }
        } else if (existingBoss != null) {
            // Success review reduces Boss level / defeats boss
            val progressEarned = if (feedbackGrade == "EASY") 50 else 25
            val currentProgress = existingBoss.defeatProgress + progressEarned
            if (currentProgress >= 100) {
                // Boss Defeated!
                dao.deleteBoss(existingBoss)
                
                // Grant massive bonus XP for defeating a boss
                val bonusXP = 200
                val globalState = dao.getSystemStateOnce() ?: SystemState()
                dao.insertSystemState(globalState.copy(totalXP = globalState.totalXP + bonusXP))
                
                dao.insertNotification(
                    NotificationLog(
                        title = "Boss Defeated! 🏆",
                        message = "Incredible discipline! You conquered '${existingBoss.topicName}' and earned +$bonusXP Bonus XP. Weakness eradicated.",
                        timestamp = now
                    )
                )
            } else {
                // Inflict damage to boss
                val droppedIntensity = (existingBoss.intensityMeter - progressEarned).coerceAtLeast(10)
                dao.updateBoss(
                    existingBoss.copy(
                        defeatProgress = currentProgress,
                        intensityMeter = droppedIntensity
                    )
                )
            }
        }

        // 6. Record Study Session Log
        dao.insertSession(
            StudySession(
                topicId = topicId,
                topicName = topic.name,
                subjectId = topic.subjectId,
                subjectName = subject.name,
                dateTimestamp = now,
                durationMinutes = durationMinutes,
                xpEarned = finalXP,
                feedbackDifficulty = feedbackGrade
            )
        )
    }

    // --- Smart Scheduler Engine ---
    suspend fun generateDailyPlannerMissions(): PlannerMissions {
        val now = System.currentTimeMillis()
        val allTopics = dao.getAllTopicsList()
        val allSubjects = dao.getAllSubjectsList()
        if (allTopics.isEmpty() || allSubjects.isEmpty()) {
            return PlannerMissions(primary = null, secondary = emptyList())
        }

        val subjectMap = allSubjects.associateBy { it.id }

        // Compile weights per topic
        val scoredTopics = allTopics.map { topic ->
            var score = 0.0
            val sub = subjectMap[topic.subjectId]
            val subDifficultyScale = sub?.difficultyScale ?: 1.0

            // 1. Is it actively a Weakness Boss? (HUGE weight)
            val isBoss = dao.getBossByTopicId(topic.id) != null
            if (isBoss) {
                score += 1500.0
            }

            // 2. Is it marked high difficulty?
            score += topic.difficulty * 40.0

            // 3. Spaced Repetition decay and Overdue status
            if (topic.lastReviewed == 0L) {
                // Never reviewed, medium starting weight to initiate learning
                score += 400.0
            } else {
                val timeElapsedMs = now - topic.lastReviewed
                val daysElapsed = timeElapsedMs.toDouble() / (24.0 * 3600.0 * 1000.0)
                val overdueFraction = daysElapsed / topic.currentIntervalDays.toDouble()

                // If overdueFraction > 1.0, topic is due.
                if (overdueFraction >= 1.0) {
                    // Overdue math
                    score += 600.0 * overdueFraction
                }

                // Low memory strength score impact
                val strengthDeficit = 100 - topic.memoryStrength
                score += strengthDeficit * 5.0
            }

            // 4. Learning state weight
            when (topic.status) {
                "LEARNING" -> score += 300.0
                "NEW" -> score += 150.0
                "MASTERED" -> score -= 300.0 // heavily reduce priority of standard mastered topics unless due
            }

            // 5. Apply subject difficulty scaling
            score *= subDifficultyScale

            ScoredTopic(topic = topic, score = score, isBoss = isBoss)
        }

        // Sort by score descending
        val sorted = scoredTopics.sortedByDescending { it.score }

        val primaryNode = sorted.firstOrNull()
        val primaryMission = primaryNode?.let { node ->
            val topic = node.topic
            val sub = subjectMap[topic.subjectId]
            val subName = sub?.name ?: "Unknown"
            
            val reason = when {
                node.isBoss -> "⚔️ BOSS ENEMY: Active critical memory block in $subName."
                topic.lastReviewed == 0L -> "📖 NEW OBJECTIVE: Untapped knowledge area."
                else -> {
                    val daysElapsed = (now - topic.lastReviewed) / (24 * 3600 * 1000L)
                    "🔁 SPACED REVISION: Overdue by $daysElapsed days (Memory: ${topic.memoryStrength}%)."
                }
            }

            val estimatedMinutes = when (topic.difficulty) {
                5 -> 50
                4 -> 40
                3 -> 30
                else -> 25
            }

            Mission(
                topicId = topic.id,
                topicName = topic.name,
                subjectId = topic.subjectId,
                subjectName = subName,
                xpReward = (100 * (1.0 + topic.difficulty * 0.15)).toInt(),
                estimatedDurationMinutes = estimatedMinutes,
                reason = reason,
                isBoss = node.isBoss,
                difficulty = topic.difficulty
            )
        }

        // Secondary missions (items 2 to 5)
        val secondaryMissions = sorted.drop(1).take(3).map { node ->
            val topic = node.topic
            val sub = subjectMap[topic.subjectId]
            val subName = sub?.name ?: "Unknown"

            val reason = when {
                node.isBoss -> "Critical Boss"
                topic.lastReviewed == 0L -> "New chapter task"
                else -> "Due revision (${topic.memoryStrength}% retention)"
            }

            Mission(
                topicId = topic.id,
                topicName = topic.name,
                subjectId = topic.subjectId,
                subjectName = subName,
                xpReward = (60 * (1.0 + topic.difficulty * 0.1)).toInt(),
                estimatedDurationMinutes = 25,
                reason = reason,
                isBoss = node.isBoss,
                difficulty = topic.difficulty
            )
        }

        return PlannerMissions(primary = primaryMission, secondary = secondaryMissions)
    }

    // --- Generate Weekly Report ---
    suspend fun generateWeeklyReport() {
        val now = System.currentTimeMillis()
        val sessions = dao.getRecentSessions(100).firstOrNull() ?: emptyList()
        
        // Filter session of last 7 days
        val lastSevenDaysSess = sessions.filter { now - it.dateTimestamp < 7 * 24 * 3600 * 1000L }
        
        val totalMinutes = lastSevenDaysSess.sumOf { it.durationMinutes }
        val totalHours = totalMinutes.toDouble() / 60.0

        // Determine most improved subject and weakest subject
        val topics = dao.getAllTopicsList()
        val subjects = dao.getAllSubjectsList()
        val subjectMap = subjects.associateBy { it.id }

        val weakestSubject = subjects.minByOrNull { it.masteryPercent }?.name ?: "None"
        val mostImprovedSubject = subjects.maxByOrNull { it.xpPoints }?.name ?: "None"

        // Calculate consistency details
        val totalXP = lastSevenDaysSess.sumOf { it.xpEarned }
        val consistencyScore = (lastSevenDaysSess.size * 20).coerceAtMost(100)
        val focusScore = if (lastSevenDaysSess.isNotEmpty()) {
            (lastSevenDaysSess.count { it.feedbackDifficulty == "EASY" } * 100) / lastSevenDaysSess.size
        } else 0

        val retentionAvg = if (topics.isNotEmpty()) {
            topics.sumOf { it.memoryStrength } / topics.size
        } else 80

        val promptSummary = "System evaluated ${lastSevenDaysSess.size} study nodes in the past week. Study consistency score reached $consistencyScore%. Primary weakness identified in $weakestSubject due to low confidence ratings. Progression indicates high speed in $mostImprovedSubject."

        val newReport = WeeklyReport(
            startDateTimestamp = now - 7 * 24 * 3600 * 1000L,
            endDateTimestamp = now,
            totalHours = Math.round(totalHours * 10) / 10.0,
            mostImprovedSubject = mostImprovedSubject,
            weakestSubject = weakestSubject,
            consistencyScore = consistencyScore,
            missedRevisionsCount = if (consistencyScore < 60) 4 else 1,
            focusScore = focusScore.coerceAtLeast(60),
            retentionEstimate = retentionAvg,
            analyticalSummary = promptSummary
        )

        dao.insertReport(newReport)
        
        dao.insertNotification(
            NotificationLog(
                title = "Weekly System Report Synthesized 📊",
                message = "The 7-day study intelligence diagnostic is complete. View analysis on the Reports Panel.",
                timestamp = now
            )
        )
    }

    // Direct insertions for setup/user entry
    suspend fun insertSubject(subject: Subject): Long = dao.insertSubject(subject)
    suspend fun insertChapter(chapter: Chapter): Long = dao.insertChapter(chapter)
    suspend fun insertTopic(topic: Topic): Long = dao.insertTopic(topic)
    suspend fun updateTopic(topic: Topic) = dao.updateTopic(topic)
    suspend fun deleteTopic(topic: Topic) = dao.deleteTopic(topic)
    suspend fun deleteSubject(subject: Subject) = dao.deleteSubject(subject)
    suspend fun markAllNotificationsRead() = dao.markAllNotificationsAsRead()
}

data class PlannerMissions(
    val primary: Mission?,
    val secondary: List<Mission>
)

data class Mission(
    val topicId: Long,
    val topicName: String,
    val subjectId: Long,
    val subjectName: String,
    val xpReward: Int,
    val estimatedDurationMinutes: Int,
    val reason: String,
    val isBoss: Boolean,
    val difficulty: Int
)

private data class ScoredTopic(
    val topic: Topic,
    val score: Double,
    val isBoss: Boolean
)

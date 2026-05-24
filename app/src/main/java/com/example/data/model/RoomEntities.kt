package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subjects")
data class Subject(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val level: Int = 1,
    val xpPoints: Int = 0,
    val masteryPercent: Int = 0,
    val difficultyScale: Double = 1.0,
    val iconName: String = "menu_book"
)

@Entity(tableName = "chapters")
data class Chapter(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subjectId: Long,
    val name: String,
    val orderNumber: Int = 1,
    val completionPercent: Int = 0
)

@Entity(tableName = "topics")
data class Topic(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val chapterId: Long,
    val subjectId: Long,
    val name: String,
    val difficulty: Int, // 1 to 5
    val status: String, // "NEW", "LEARNING", "MASTERED"
    val lastReviewed: Long, // timestamp
    val currentIntervalDays: Int = 1,
    val recallConfidenceHistory: String = "", // comma-separated scores, e.g. "3,4"
    val memoryStrength: Int = 100 // 0 to 100
)

@Entity(tableName = "study_sessions")
data class StudySession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val topicId: Long,
    val topicName: String,
    val subjectId: Long,
    val subjectName: String,
    val dateTimestamp: Long,
    val durationMinutes: Int,
    val xpEarned: Int,
    val feedbackDifficulty: String // "EASY", "MEDIUM", "HARD"
)

@Entity(tableName = "weakness_bosses")
data class WeaknessBoss(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val topicId: Long,
    val topicName: String,
    val subjectId: Long,
    val subjectName: String,
    val intensityMeter: Int, // 0 to 100
    val defeatProgress: Int, // 0 to 100
    val isActive: Boolean = true
)

@Entity(tableName = "weekly_reports")
data class WeeklyReport(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val startDateTimestamp: Long,
    val endDateTimestamp: Long,
    val totalHours: Double,
    val mostImprovedSubject: String,
    val weakestSubject: String,
    val consistencyScore: Int,
    val missedRevisionsCount: Int,
    val focusScore: Int,
    val retentionEstimate: Int,
    val analyticalSummary: String
)

@Entity(tableName = "system_state")
data class SystemState(
    @PrimaryKey val id: Int = 1,
    val dailyStreak: Int = 0,
    val totalXP: Int = 0,
    val studyTimeTodayMinutes: Int = 0,
    val lastStudyDateString: String = "" // "YYYY-MM-DD"
)

@Entity(tableName = "notification_logs")
data class NotificationLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val message: String,
    val timestamp: Long,
    val isRead: Boolean = false
)

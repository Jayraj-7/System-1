package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.StudyDao
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Subject::class,
        Chapter::class,
        Topic::class,
        StudySession::class,
        WeaknessBoss::class,
        WeeklyReport::class,
        SystemState::class,
        NotificationLog::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun studyDao(): StudyDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "shonen_study_os_db"
                )
                .addCallback(DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database.studyDao())
                }
            }
        }

        private suspend fun populateDatabase(dao: StudyDao) {
            // 1. Initialize System State
            dao.insertSystemState(
                SystemState(
                    id = 1,
                    dailyStreak = 4, // starting streak
                    totalXP = 1250,  // initial level progress
                    studyTimeTodayMinutes = 0,
                    lastStudyDateString = "2026-05-23" // Yesterday so streak matches
                )
            )

            // 2. Insert Starter Subjects
            val physicsId = dao.insertSubject(
                Subject(name = "Physics", level = 3, xpPoints = 450, masteryPercent = 45, iconName = "science", difficultyScale = 1.3)
            )
            val mathId = dao.insertSubject(
                Subject(name = "Mathematics", level = 5, xpPoints = 900, masteryPercent = 65, iconName = "calculate", difficultyScale = 1.5)
            )
            val csId = dao.insertSubject(
                Subject(name = "Computer Science", level = 2, xpPoints = 180, masteryPercent = 20, iconName = "terminal", difficultyScale = 1.1)
            )
            val chemId = dao.insertSubject(
                Subject(name = "Chemistry", level = 1, xpPoints = 0, masteryPercent = 5, iconName = "biotech", difficultyScale = 1.2)
            )

            // 3. Insert Chapters and Topics
            // Physics
            val pCap1 = dao.insertChapter(Chapter(subjectId = physicsId, name = "Mechanics", orderNumber = 1, completionPercent = 60))
            dao.insertTopic(
                Topic(
                    chapterId = pCap1,
                    subjectId = physicsId,
                    name = "Kinematics Revision",
                    difficulty = 4,
                    status = "LEARNING",
                    lastReviewed = System.currentTimeMillis() - (3 * 24 * 3600 * 1000L), // 3 days ago, overdue
                    currentIntervalDays = 2,
                    recallConfidenceHistory = "3,2",
                    memoryStrength = 38
                )
            )
            dao.insertTopic(
                Topic(
                    chapterId = pCap1,
                    subjectId = physicsId,
                    name = "Newton's Laws of Motion",
                    difficulty = 3,
                    status = "MASTERED",
                    lastReviewed = System.currentTimeMillis() - (1 * 24 * 3600 * 1000L), // 1 day ago
                    currentIntervalDays = 7,
                    recallConfidenceHistory = "5,4,5",
                    memoryStrength = 88
                )
            )
            dao.insertTopic(
                Topic(
                    chapterId = pCap1,
                    subjectId = physicsId,
                    name = "Work, Energy & Power",
                    difficulty = 2,
                    status = "NEW",
                    lastReviewed = 0L,
                    currentIntervalDays = 1,
                    recallConfidenceHistory = "",
                    memoryStrength = 100
                )
            )

            // Mathematics
            val mCap1 = dao.insertChapter(Chapter(subjectId = mathId, name = "Calculus", orderNumber = 1, completionPercent = 75))
            val diffEqTopicId = dao.insertTopic(
                Topic(
                    chapterId = mCap1,
                    subjectId = mathId,
                    name = "Differential Equations",
                    difficulty = 5,
                    status = "LEARNING",
                    lastReviewed = System.currentTimeMillis() - (6 * 24 * 3600 * 1000L), // 6 days ago, heavily overdue
                    currentIntervalDays = 3,
                    recallConfidenceHistory = "2,1,2",
                    memoryStrength = 15 // Very poor retention
                )
            )
            // Add differential equation as weakness boss "The Derivative Devil"
            dao.insertBoss(
                WeaknessBoss(
                    topicId = diffEqTopicId,
                    topicName = "Differential Equations (Calculus II)",
                    subjectId = mathId,
                    subjectName = "Mathematics",
                    intensityMeter = 88, // high boss level
                    defeatProgress = 15,
                    isActive = true
                )
            )

            dao.insertTopic(
                Topic(
                    chapterId = mCap1,
                    subjectId = mathId,
                    name = "Integration Techniques",
                    difficulty = 4,
                    status = "MASTERED",
                    lastReviewed = System.currentTimeMillis() - (12 * 24 * 3600 * 1000L), // 12 days ago, overdue but interval is 15
                    currentIntervalDays = 15,
                    recallConfidenceHistory = "4,5,5",
                    memoryStrength = 70
                )
            )

            // Cs
            val csCap1 = dao.insertChapter(Chapter(subjectId = csId, name = "Data Structures", orderNumber = 1, completionPercent = 10))
            dao.insertTopic(
                Topic(
                    chapterId = csCap1,
                    subjectId = csId,
                    name = "Red-Black Trees",
                    difficulty = 5,
                    status = "NEW",
                    lastReviewed = 0,
                    currentIntervalDays = 1,
                    recallConfidenceHistory = "",
                    memoryStrength = 100
                )
            )
            dao.insertTopic(
                Topic(
                    chapterId = csCap1,
                    subjectId = csId,
                    name = "Binary Search Trees",
                    difficulty = 3,
                    status = "LEARNING",
                    lastReviewed = System.currentTimeMillis() - (1 * 24 * 3600 * 1000L),
                    currentIntervalDays = 2,
                    recallConfidenceHistory = "3",
                    memoryStrength = 65
                )
            )

            // 4. Populate weekly reports
            dao.insertReport(
                WeeklyReport(
                    startDateTimestamp = System.currentTimeMillis() - (7 * 24 * 3600 * 1000L),
                    endDateTimestamp = System.currentTimeMillis(),
                    totalHours = 14.5,
                    mostImprovedSubject = "Mathematics",
                    weakestSubject = "Physics",
                    consistencyScore = 85,
                    missedRevisionsCount = 2,
                    focusScore = 78,
                    retentionEstimate = 64,
                    analyticalSummary = "Great progress in integration techniques. However, multi-day omission of Physics kinematic modules has decayed retention levels. Focus more on Mechanistic review algorithms."
                )
            )

            // 5. Populate notification logs
            dao.insertNotification(
                NotificationLog(
                    title = "System Update Ready",
                    message = "Auto Daily Planner Engine refreshed. Today's high priority is Kinematics revision. Let's conquer the task with complete discipline.",
                    timestamp = System.currentTimeMillis() - 3600000L,
                    isRead = false
                )
            )
            dao.insertNotification(
                NotificationLog(
                    title = "Boss Enemy Unleashed",
                    message = "Algebra / Calculus weakness registered. 'Differential Equations' has risen to Boss tier. Bring extra focus to defeat this memory block.",
                    timestamp = System.currentTimeMillis() - 7200000L,
                    isRead = false
                )
            )
        }
    }
}

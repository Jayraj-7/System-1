package com.example.data.dao

import androidx.room.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

@Dao
interface StudyDao {

    // --- Subjects ---
    @Query("SELECT * FROM subjects ORDER BY name ASC")
    fun getAllSubjects(): Flow<List<Subject>>

    @Query("SELECT * FROM subjects ORDER BY name ASC")
    suspend fun getAllSubjectsList(): List<Subject>

    @Query("SELECT * FROM subjects WHERE id = :id")
    suspend fun getSubjectById(id: Long): Subject?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: Subject): Long

    @Update
    suspend fun updateSubject(subject: Subject)

    @Delete
    suspend fun deleteSubject(subject: Subject)


    // --- Chapters ---
    @Query("SELECT * FROM chapters")
    fun getAllChapters(): Flow<List<Chapter>>

    @Query("SELECT * FROM chapters WHERE subjectId = :subjectId ORDER BY orderNumber ASC")
    fun getChaptersForSubject(subjectId: Long): Flow<List<Chapter>>

    @Query("SELECT * FROM chapters WHERE subjectId = :subjectId ORDER BY orderNumber ASC")
    suspend fun getChaptersForSubjectList(subjectId: Long): List<Chapter>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChapter(chapter: Chapter): Long

    @Update
    suspend fun updateChapter(chapter: Chapter)


    // --- Topics ---
    @Query("SELECT * FROM topics")
    fun getAllTopics(): Flow<List<Topic>>

    @Query("SELECT * FROM topics")
    suspend fun getAllTopicsList(): List<Topic>

    @Query("SELECT * FROM topics WHERE id = :id")
    suspend fun getTopicById(id: Long): Topic?

    @Query("SELECT * FROM topics WHERE subjectId = :subjectId")
    fun getTopicsForSubject(subjectId: Long): Flow<List<Topic>>

    @Query("SELECT * FROM topics WHERE chapterId = :chapterId")
    fun getTopicsForChapter(chapterId: Long): Flow<List<Topic>>

    @Query("SELECT * FROM topics WHERE chapterId = :chapterId")
    suspend fun getTopicsForChapterList(chapterId: Long): List<Topic>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopic(topic: Topic): Long

    @Update
    suspend fun updateTopic(topic: Topic)

    @Delete
    suspend fun deleteTopic(topic: Topic)


    // --- StudySessions ---
    @Query("SELECT * FROM study_sessions ORDER BY dateTimestamp DESC")
    fun getAllSessions(): Flow<List<StudySession>>

    @Query("SELECT * FROM study_sessions ORDER BY dateTimestamp DESC LIMIT :limit")
    fun getRecentSessions(limit: Int): Flow<List<StudySession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: StudySession): Long


    // --- WeaknessBosses ---
    @Query("SELECT * FROM weakness_bosses WHERE isActive = 1")
    fun getActiveBosses(): Flow<List<WeaknessBoss>>

    @Query("SELECT * FROM weakness_bosses WHERE topicId = :topicId LIMIT 1")
    suspend fun getBossByTopicId(topicId: Long): WeaknessBoss?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBoss(boss: WeaknessBoss): Long

    @Update
    suspend fun updateBoss(boss: WeaknessBoss)

    @Delete
    suspend fun deleteBoss(boss: WeaknessBoss)


    // --- WeeklyReports ---
    @Query("SELECT * FROM weekly_reports ORDER BY endDateTimestamp DESC")
    fun getAllReports(): Flow<List<WeeklyReport>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReport(report: WeeklyReport): Long


    // --- SystemState ---
    @Query("SELECT * FROM system_state WHERE id = 1")
    fun getSystemState(): Flow<SystemState?>

    @Query("SELECT * FROM system_state WHERE id = 1")
    suspend fun getSystemStateOnce(): SystemState?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSystemState(state: SystemState)


    // --- NotificationLog ---
    @Query("SELECT * FROM notification_logs ORDER BY timestamp DESC")
    fun getNotifications(): Flow<List<NotificationLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationLog): Long

    @Query("UPDATE notification_logs SET isRead = 1 WHERE isRead = 0")
    suspend fun markAllNotificationsAsRead()
}

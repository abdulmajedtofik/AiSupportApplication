package com.example.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "study_tasks")
data class StudyTaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val subject: String,
    val deadline: String,
    val isCompleted: Boolean = false,
    val priority: String = "Normal",
    val durationMinutes: Int = 45
)

@Entity(tableName = "study_notes")
data class StudyNoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val subject: String,
    val summaryEn: String,
    val summaryAm: String,
    val keyPointsEn: String,
    val keyPointsAm: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "flashcards")
data class FlashcardEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subject: String,
    val frontEn: String,
    val frontAm: String,
    val backEn: String,
    val backAm: String,
    val isMastered: Boolean = false,
    val reviewCount: Int = 0
)

@Entity(tableName = "quiz_records")
data class QuizRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subject: String,
    val score: Int,
    val total: Int,
    val difficulty: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Dao
interface StudyTaskDao {
    @Query("SELECT * FROM study_tasks ORDER BY isCompleted ASC, id DESC")
    fun getAllTasks(): Flow<List<StudyTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: StudyTaskEntity): Long

    @Update
    suspend fun updateTask(task: StudyTaskEntity)

    @Query("DELETE FROM study_tasks WHERE id = :taskId")
    suspend fun deleteTask(taskId: Long)
}

@Dao
interface StudyNoteDao {
    @Query("SELECT * FROM study_notes ORDER BY timestamp DESC")
    fun getAllNotes(): Flow<List<StudyNoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: StudyNoteEntity): Long

    @Query("DELETE FROM study_notes WHERE id = :noteId")
    suspend fun deleteNote(noteId: Long)
}

@Dao
interface FlashcardDao {
    @Query("SELECT * FROM flashcards ORDER BY isMastered ASC, id ASC")
    fun getAllFlashcards(): Flow<List<FlashcardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlashcard(card: FlashcardEntity): Long

    @Update
    suspend fun updateFlashcard(card: FlashcardEntity)

    @Query("DELETE FROM flashcards WHERE id = :cardId")
    suspend fun deleteFlashcard(cardId: Long)
}

@Dao
interface QuizRecordDao {
    @Query("SELECT * FROM quiz_records ORDER BY timestamp DESC")
    fun getAllRecords(): Flow<List<QuizRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: QuizRecordEntity): Long
}

@Database(
    entities = [
        StudyTaskEntity::class,
        StudyNoteEntity::class,
        FlashcardEntity::class,
        QuizRecordEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun studyTaskDao(): StudyTaskDao
    abstract fun studyNoteDao(): StudyNoteDao
    abstract fun flashcardDao(): FlashcardDao
    abstract fun quizRecordDao(): QuizRecordDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "smart_study_ai_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}

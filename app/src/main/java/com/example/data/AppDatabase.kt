package com.example.data

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedWrappedDao {
    @Query("SELECT * FROM saved_wrapped ORDER BY timestamp DESC")
    fun getAllWraps(): Flow<List<SavedWrapped>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWrap(wrap: SavedWrapped)

    @Query("DELETE FROM saved_wrapped WHERE id = :id")
    suspend fun deleteWrapById(id: Int)

    @Query("DELETE FROM saved_wrapped")
    suspend fun clearAll()
}

@Database(entities = [SavedWrapped::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun savedWrappedDao(): SavedWrappedDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "vibe_wrapped_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class WrappedRepository(private val dao: SavedWrappedDao) {
    val allWraps: Flow<List<SavedWrapped>> = dao.getAllWraps()

    suspend fun insert(wrap: SavedWrapped) {
        dao.insertWrap(wrap)
    }

    suspend fun deleteById(id: Int) {
        dao.deleteWrapById(id)
    }

    suspend fun clearAll() {
        dao.clearAll()
    }
}

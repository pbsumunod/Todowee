package com.doobidapp.todowee.models

import android.content.Context
import androidx.room.*

@Entity
data class ToDo(
    @PrimaryKey(autoGenerate = true) val uid: Int?,
    @ColumnInfo(name = "task_name") var taskName: String,
    @ColumnInfo(name = "done") var isDone: Boolean) {
    constructor():this(null, "", false)
}

@Dao
interface ToDoDao {

    @Query("SELECT * FROM ToDo ORDER BY done")
    fun getAll(): List<ToDo>

    @Query("SELECT * FROM ToDo WHERE uid = :id LIMIT 1")
    fun get(id: Int): ToDo

    @Query("SELECT COUNT(*) FROM ToDo WHERE done = 0")
    fun getTasksCount(): Int

    @Update
    fun updateAll(vararg toDo: ToDo)

    @Insert
    fun insertAll(vararg toDo: ToDo)

    @Delete
    fun deleteTodo(toDo: ToDo)
}

@Database(entities = arrayOf(ToDo::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun toDoDao(): ToDoDao

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        AppDatabase::class.java, "todowee.db").allowMainThreadQueries()
                        .build()
                }
            }
            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }
    }
}
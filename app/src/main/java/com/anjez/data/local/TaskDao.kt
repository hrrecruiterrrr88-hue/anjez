package com.anjez.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface TaskDao {
    
    // Tasks CRUD
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)
    
    @Update
    suspend fun updateTask(task: Task)
    
    @Delete
    suspend fun deleteTask(task: Task)
    
    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks()
    
    @Query("SELECT * FROM tasks ORDER BY position ASC")
    fun getAllTasks(): Flow<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: String): Task?
    
    @Query("SELECT * FROM tasks WHERE status = :status ORDER BY position ASC")
    fun getTasksByStatus(status: TaskStatus): Flow<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE priority = :priority ORDER BY position ASC")
    fun getTasksByPriority(priority: Priority): Flow<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE dueDate BETWEEN :startDate AND :endDate ORDER BY position ASC")
    fun getTasksByDateRange(startDate: Date, endDate: Date): Flow<List<Task>>
    
    @Query("SELECT * FROM tasks WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ORDER BY position ASC")
    fun searchTasks(query: String): Flow<List<Task>>
    
    @Query("SELECT COUNT(*) FROM tasks WHERE status = 'COMPLETED'")
    suspend fun getCompletedCount(): Int
    
    @Query("SELECT COUNT(*) FROM tasks")
    suspend fun getTotalCount(): Int
    
    @Query("UPDATE tasks SET position = :position WHERE id = :taskId")
    suspend fun updateTaskPosition(taskId: String, position: Int)
    
    // Subtasks
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubTask(subTask: SubTask)
    
    @Update
    suspend fun updateSubTask(subTask: SubTask)
    
    @Delete
    suspend fun deleteSubTask(subTask: SubTask)
    
    @Query("SELECT * FROM subtasks WHERE taskId = :taskId ORDER BY position ASC")
    fun getSubTasksByTaskId(taskId: String): Flow<List<SubTask>>
    
    @Query("DELETE FROM subtasks WHERE taskId = :taskId")
    suspend fun deleteSubTasksByTaskId(taskId: String)
    
    @Query("SELECT COUNT(*) FROM subtasks WHERE taskId = :taskId AND isCompleted = 1")
    suspend fun getCompletedSubTasksCount(taskId: String): Int
    
    @Query("SELECT COUNT(*) FROM subtasks WHERE taskId = :taskId")
    suspend fun getTotalSubTasksCount(taskId: String): Int
}

package com.anjez.data.repository

import com.anjez.data.local.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject

class TaskRepository @Inject constructor(
    private val taskDao: TaskDao
) {
    
    // Tasks
    suspend fun insertTask(task: Task) = taskDao.insertTask(task)
    suspend fun updateTask(task: Task) = taskDao.updateTask(task)
    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)
    
    fun getAllTasks(): Flow<List<Task>> = taskDao.getAllTasks()
    fun getTasksByStatus(status: TaskStatus): Flow<List<Task>> = taskDao.getTasksByStatus(status)
    fun getTasksByPriority(priority: Priority): Flow<List<Task>> = taskDao.getTasksByPriority(priority)
    fun searchTasks(query: String): Flow<List<Task>> = taskDao.searchTasks(query)
    
    fun getStatistics(): Flow<TaskStatistics> {
        return taskDao.getAllTasks().map { tasks ->
            val total = tasks.size
            val completed = tasks.count { it.status == TaskStatus.COMPLETED }
            val overdue = tasks.count { it.isOverdue }
            val inProgress = tasks.count { it.status == TaskStatus.IN_PROGRESS }
            
            TaskStatistics(
                total = total,
                completed = completed,
                overdue = overdue,
                inProgress = inProgress
            )
        }
    }
    
    suspend fun updateTaskPositions(tasks: List<Task>) {
        tasks.forEachIndexed { index, task ->
            taskDao.updateTaskPosition(task.id, index)
        }
    }
    
    // Subtasks
    suspend fun insertSubTask(subTask: SubTask) = taskDao.insertSubTask(subTask)
    suspend fun updateSubTask(subTask: SubTask) = taskDao.updateSubTask(subTask)
    suspend fun deleteSubTask(subTask: SubTask) = taskDao.deleteSubTask(subTask)
    
    fun getSubTasksByTaskId(taskId: String): Flow<List<SubTask>> = 
        taskDao.getSubTasksByTaskId(taskId)
    
    suspend fun getSubTaskProgress(taskId: String): Pair<Int, Int> {
        val total = taskDao.getTotalSubTasksCount(taskId)
        val completed = taskDao.getCompletedSubTasksCount(taskId)
        return Pair(completed, total)
    }
    
    // Export/Import
    suspend fun getAllTasksForExport(): List<Task> {
        return taskDao.getAllTasks().map { it }.value ?: emptyList()
    }
    
    suspend fun insertAllTasks(tasks: List<Task>) {
        tasks.forEach { taskDao.insertTask(it) }
    }
}

data class TaskStatistics(
    val total: Int = 0,
    val completed: Int = 0,
    val overdue: Int = 0,
    val inProgress: Int = 0
) {
    val progressPercentage: Int
        get() = if (total == 0) 0 else (completed * 100 / total)
}

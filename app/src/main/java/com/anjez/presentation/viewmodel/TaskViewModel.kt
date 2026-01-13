package com.anjez.presentation.viewmodel

import androidx.lifecycle.*
import com.anjez.data.local.*
import com.anjez.data.repository.TaskRepository
import com.anjez.data.repository.TaskStatistics
import kotlinx.coroutines.launch
import java.util.*

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {
    
    private val _selectedFilter = MutableLiveData<TaskFilter>(TaskFilter.ALL)
    val selectedFilter: LiveData<TaskFilter> = _selectedFilter
    
    private val _searchQuery = MutableLiveData("")
    val searchQuery: LiveData<String> = _searchQuery
    
    val filteredTasks: LiveData<List<Task>> = Transformations.switchMap(
        Transformations.combine(_selectedFilter, _searchQuery)
    ) { (filter, query) ->
        when (filter) {
            TaskFilter.ALL -> {
                if (query.isNullOrBlank()) {
                    repository.getAllTasks()
                } else {
                    repository.searchTasks(query)
                }
            }
            TaskFilter.COMPLETED -> repository.getTasksByStatus(TaskStatus.COMPLETED)
            TaskFilter.IN_PROGRESS -> repository.getTasksByStatus(TaskStatus.IN_PROGRESS)
            TaskFilter.HIGH_PRIORITY -> repository.getTasksByPriority(Priority.HIGH)
            TaskFilter.OVERDUE -> {
                repository.getAllTasks().map { tasks ->
                    tasks.filter { it.isOverdue }
                }
            }
        }
    }
    
    val statistics: LiveData<TaskStatistics> = repository.getStatistics().asLiveData()
    
    fun getSubTasks(taskId: String): LiveData<List<SubTask>> {
        return repository.getSubTasksByTaskId(taskId).asLiveData()
    }
    
    // Operations
    fun addTask(title: String, description: String, priority: Priority, dueDate: Date?) {
        viewModelScope.launch {
            val task = Task(
                title = title,
                description = description,
                priority = priority,
                dueDate = dueDate
            )
            repository.insertTask(task)
        }
    }
    
    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task)
        }
    }
    
    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }
    
    fun toggleTaskStatus(task: Task) {
        viewModelScope.launch {
            val newStatus = when (task.status) {
                TaskStatus.NEW -> TaskStatus.IN_PROGRESS
                TaskStatus.IN_PROGRESS -> TaskStatus.COMPLETED
                TaskStatus.COMPLETED -> TaskStatus.NEW
            }
            task.status = newStatus
            repository.updateTask(task)
        }
    }
    
    fun updateTaskOrder(tasks: List<Task>) {
        viewModelScope.launch {
            repository.updateTaskPositions(tasks)
        }
    }
    
    // Subtasks
    fun addSubTask(taskId: String, title: String) {
        viewModelScope.launch {
            val subTask = SubTask(
                taskId = taskId,
                title = title
            )
            repository.insertSubTask(subTask)
        }
    }
    
    fun toggleSubTask(subTask: SubTask) {
        viewModelScope.launch {
            subTask.isCompleted = !subTask.isCompleted
            repository.updateSubTask(subTask)
        }
    }
    
    fun deleteSubTask(subTask: SubTask) {
        viewModelScope.launch {
            repository.deleteSubTask(subTask)
        }
    }
    
    // Filter & Search
    fun setFilter(filter: TaskFilter) {
        _selectedFilter.value = filter
    }
    
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }
}

enum class TaskFilter {
    ALL, COMPLETED, IN_PROGRESS, HIGH_PRIORITY, OVERDUE;
    
    fun getArabicName(): String = when (this) {
        ALL -> "الكل"
        COMPLETED -> "المنجزة"
        IN_PROGRESS -> "قيد التنفيذ"
        HIGH_PRIORITY -> "عالية الأولوية"
        OVERDUE -> "المتأخرة"
    }
}

class TaskViewModelFactory(private val repository: TaskRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

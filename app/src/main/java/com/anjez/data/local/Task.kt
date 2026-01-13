package com.anjez.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import kotlinx.parcelize.Parcelize
import java.util.Date
import java.util.UUID

@Entity(tableName = "tasks")
@Parcelize
data class Task(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    
    var title: String = "",
    var description: String = "",
    var priority: Priority = Priority.MEDIUM,
    
    @TypeConverters(DateConverter::class)
    var dueDate: Date? = null,
    
    var status: TaskStatus = TaskStatus.NEW,
    var position: Int = 0,
    
    @TypeConverters(DateConverter::class)
    val createdAt: Date = Date()
) : java.io.Serializable {
    val isOverdue: Boolean
        get() = dueDate?.before(Date()) == true && status != TaskStatus.COMPLETED
}

enum class Priority {
    HIGH, MEDIUM, LOW;
    
    companion object {
        fun fromString(value: String): Priority {
            return when (value.lowercase()) {
                "high" -> HIGH
                "low" -> LOW
                else -> MEDIUM
            }
        }
    }
    
    fun getArabicName(): String = when (this) {
        HIGH -> "عالية"
        MEDIUM -> "متوسطة"
        LOW -> "منخفضة"
    }
}

enum class TaskStatus {
    NEW, IN_PROGRESS, COMPLETED;
    
    companion object {
        fun fromString(value: String): TaskStatus {
            return when (value.lowercase()) {
                "in_progress" -> IN_PROGRESS
                "completed" -> COMPLETED
                else -> NEW
            }
        }
    }
    
    fun getArabicName(): String = when (this) {
        NEW -> "جديدة"
        IN_PROGRESS -> "قيد التنفيذ"
        COMPLETED -> "منجزة"
    }
}

class DateConverter {
    @androidx.room.TypeConverter
    fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }

    @androidx.room.TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time
}

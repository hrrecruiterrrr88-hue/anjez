package com.anjez.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Entity(
    tableName = "subtasks",
    foreignKeys = [
        ForeignKey(
            entity = Task::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
@Parcelize
data class SubTask(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    
    var taskId: String = "",
    var title: String = "",
    var isCompleted: Boolean = false,
    var position: Int = 0
) : java.io.Serializable

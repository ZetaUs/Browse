package com.zztx.browse.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "history")
data class History(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val url: String,
    val title: String,
    val favicon: String?,
    val visitCount: Int = 1,
    val lastVisitedAt: Long = Date().time,
    val syncId: String? = null,
    val syncStatus: Int = 0
)
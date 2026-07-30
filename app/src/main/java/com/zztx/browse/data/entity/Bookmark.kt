package com.zztx.browse.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "bookmarks")
data class Bookmark(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val url: String,
    val title: String,
    val favicon: String?,
    val folder: String = "default",
    val createdAt: Long = Date().time,
    val updatedAt: Long = Date().time,
    val syncId: String? = null,
    val syncStatus: Int = 0
)
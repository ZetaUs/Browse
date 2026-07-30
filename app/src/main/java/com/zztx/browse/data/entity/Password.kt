package com.zztx.browse.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "passwords")
data class Password(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val url: String,
    val username: String,
    val encryptedPassword: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val syncId: String? = null,
    val syncStatus: Int = 0
)
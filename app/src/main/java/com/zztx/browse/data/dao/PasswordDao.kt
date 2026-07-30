package com.zztx.browse.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.zztx.browse.data.entity.Password
import kotlinx.coroutines.flow.Flow

@Dao
interface PasswordDao {
    @Query("SELECT * FROM passwords ORDER BY updatedAt DESC")
    fun getAllPasswords(): Flow<List<Password>>

    @Query("SELECT * FROM passwords WHERE syncStatus != 2 ORDER BY updatedAt DESC")
    fun getUnsynchronizedPasswords(): Flow<List<Password>>

    @Query("SELECT * FROM passwords WHERE url = :url")
    suspend fun getPasswordsByUrl(url: String): List<Password>

    @Insert
    suspend fun insertPassword(password: Password)

    @Update
    suspend fun updatePassword(password: Password)

    @Delete
    suspend fun deletePassword(password: Password)

    @Query("DELETE FROM passwords WHERE id = :id")
    suspend fun deletePasswordById(id: Long)
}
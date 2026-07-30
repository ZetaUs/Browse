package com.zztx.browse.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.zztx.browse.data.entity.Bookmark
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM bookmarks ORDER BY updatedAt DESC")
    fun getAllBookmarks(): Flow<List<Bookmark>>

    @Query("SELECT * FROM bookmarks WHERE folder = :folder ORDER BY updatedAt DESC")
    fun getBookmarksByFolder(folder: String): Flow<List<Bookmark>>

    @Query("SELECT * FROM bookmarks WHERE syncStatus != 2 ORDER BY updatedAt DESC")
    fun getUnsynchronizedBookmarks(): Flow<List<Bookmark>>

    @Query("SELECT DISTINCT folder FROM bookmarks")
    fun getAllFolders(): Flow<List<String>>

    @Query("SELECT * FROM bookmarks WHERE url = :url LIMIT 1")
    suspend fun getBookmarkByUrl(url: String): Bookmark?

    @Insert
    suspend fun insertBookmark(bookmark: Bookmark)

    @Update
    suspend fun updateBookmark(bookmark: Bookmark)

    @Delete
    suspend fun deleteBookmark(bookmark: Bookmark)

    @Query("DELETE FROM bookmarks WHERE id = :id")
    suspend fun deleteBookmarkById(id: Long)
}
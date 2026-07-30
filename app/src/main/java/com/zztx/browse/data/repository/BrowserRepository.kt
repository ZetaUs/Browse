package com.zztx.browse.data.repository

import com.zztx.browse.data.dao.BookmarkDao
import com.zztx.browse.data.dao.HistoryDao
import com.zztx.browse.data.dao.PasswordDao
import com.zztx.browse.data.entity.Bookmark
import com.zztx.browse.data.entity.History
import com.zztx.browse.data.entity.Password
import com.zztx.browse.data.network.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.UUID

class BrowserRepository(
    private val bookmarkDao: BookmarkDao,
    private val historyDao: HistoryDao,
    private val passwordDao: PasswordDao,
    private val authRepository: AuthRepository
) {
    fun getAllBookmarks(): Flow<List<Bookmark>> = bookmarkDao.getAllBookmarks()
    fun getBookmarksByFolder(folder: String): Flow<List<Bookmark>> = bookmarkDao.getBookmarksByFolder(folder)
    fun getAllFolders(): Flow<List<String>> = bookmarkDao.getAllFolders()
    fun getAllHistory(): Flow<List<History>> = historyDao.getAllHistory()
    fun getAllPasswords(): Flow<List<Password>> = passwordDao.getAllPasswords()

    suspend fun addBookmark(url: String, title: String, favicon: String?) {
        val existing = bookmarkDao.getBookmarkByUrl(url)
        if (existing == null) {
            bookmarkDao.insertBookmark(
                Bookmark(
                    url = url,
                    title = title,
                    favicon = favicon,
                    syncId = UUID.randomUUID().toString(),
                    syncStatus = 0
                )
            )
        }
    }

    suspend fun removeBookmark(id: Long) {
        bookmarkDao.deleteBookmarkById(id)
    }

    suspend fun addHistory(url: String, title: String, favicon: String?) {
        val existing = historyDao.getHistoryByUrl(url)
        if (existing != null) {
            historyDao.updateHistory(
                existing.copy(
                    title = title,
                    favicon = favicon,
                    visitCount = existing.visitCount + 1,
                    lastVisitedAt = System.currentTimeMillis()
                )
            )
        } else {
            historyDao.insertHistory(
                History(
                    url = url,
                    title = title,
                    favicon = favicon,
                    syncId = UUID.randomUUID().toString(),
                    syncStatus = 0
                )
            )
        }
    }

    suspend fun clearHistory() {
        historyDao.clearAllHistory()
    }

    suspend fun savePassword(url: String, username: String, encryptedPassword: String) {
        val existingList = passwordDao.getPasswordsByUrl(url)
        val existing = existingList.find { it.username == username }
        
        if (existing != null) {
            passwordDao.updatePassword(
                existing.copy(
                    encryptedPassword = encryptedPassword,
                    updatedAt = System.currentTimeMillis(),
                    syncStatus = 0
                )
            )
        } else {
            passwordDao.insertPassword(
                Password(
                    url = url,
                    username = username,
                    encryptedPassword = encryptedPassword,
                    syncId = UUID.randomUUID().toString(),
                    syncStatus = 0
                )
            )
        }
    }

    suspend fun deletePassword(id: Long) {
        passwordDao.deletePasswordById(id)
    }

    suspend fun syncBookmarks(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val token = authRepository.getToken() ?: return@withContext Result.failure(Exception("Not logged in"))
                val unsynced = bookmarkDao.getUnsynchronizedBookmarks().first()
                
                for (bookmark in unsynced) {
                    when (bookmark.syncStatus) {
                        0 -> {
                            val response = ApiClient.apiService.createBookmark(token, bookmark)
                            bookmarkDao.updateBookmark(bookmark.copy(syncId = response.syncId, syncStatus = 2))
                        }
                        1 -> {
                            if (bookmark.syncId != null) {
                                ApiClient.apiService.updateBookmark(token, bookmark.syncId, bookmark)
                                bookmarkDao.updateBookmark(bookmark.copy(syncStatus = 2))
                            }
                        }
                    }
                }
                
                val remoteBookmarks = ApiClient.apiService.getBookmarks(token)
                for (remote in remoteBookmarks) {
                    val local = bookmarkDao.getBookmarkByUrl(remote.url)
                    if (local == null) {
                        bookmarkDao.insertBookmark(remote.copy(syncStatus = 2))
                    } else if (remote.updatedAt > local.updatedAt) {
                        bookmarkDao.updateBookmark(remote.copy(syncStatus = 2))
                    }
                }
                
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun syncHistory(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val token = authRepository.getToken() ?: return@withContext Result.failure(Exception("Not logged in"))
                val unsynced = historyDao.getUnsynchronizedHistory().first()
                
                if (unsynced.isNotEmpty()) {
                    ApiClient.apiService.syncHistory(token, unsynced)
                    for (item in unsynced) {
                        historyDao.updateHistory(item.copy(syncStatus = 2))
                    }
                }
                
                val remoteHistory = ApiClient.apiService.getHistory(token)
                for (remote in remoteHistory) {
                    val local = historyDao.getHistoryByUrl(remote.url)
                    if (local == null) {
                        historyDao.insertHistory(remote.copy(syncStatus = 2))
                    } else if (remote.lastVisitedAt > local.lastVisitedAt) {
                        historyDao.updateHistory(remote.copy(syncStatus = 2))
                    }
                }
                
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun syncPasswords(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val token = authRepository.getToken() ?: return@withContext Result.failure(Exception("Not logged in"))
                val unsynced = passwordDao.getUnsynchronizedPasswords().first()
                
                for (password in unsynced) {
                    when (password.syncStatus) {
                        0 -> {
                            val response = ApiClient.apiService.createPassword(token, password)
                            passwordDao.updatePassword(password.copy(syncId = response.syncId, syncStatus = 2))
                        }
                        1 -> {
                            if (password.syncId != null) {
                                ApiClient.apiService.updatePassword(token, password.syncId, password)
                                passwordDao.updatePassword(password.copy(syncStatus = 2))
                            }
                        }
                    }
                }
                
                val remotePasswords = ApiClient.apiService.getPasswords(token)
                for (remote in remotePasswords) {
                    val localList = passwordDao.getPasswordsByUrl(remote.url)
                    val local = localList.find { it.username == remote.username }
                    if (local == null) {
                        passwordDao.insertPassword(remote.copy(syncStatus = 2))
                    } else if (remote.updatedAt > local.updatedAt) {
                        passwordDao.updatePassword(remote.copy(syncStatus = 2))
                    }
                }
                
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
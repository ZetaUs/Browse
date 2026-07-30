package com.zztx.browse.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.zztx.browse.data.dao.BookmarkDao
import com.zztx.browse.data.dao.HistoryDao
import com.zztx.browse.data.dao.PasswordDao
import com.zztx.browse.data.entity.Bookmark
import com.zztx.browse.data.entity.History
import com.zztx.browse.data.entity.Password

@Database(
    entities = [Bookmark::class, History::class, Password::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookmarkDao(): BookmarkDao
    abstract fun historyDao(): HistoryDao
    abstract fun passwordDao(): PasswordDao
}
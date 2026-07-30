package com.zztx.browse.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zztx.browse.data.entity.Bookmark
import com.zztx.browse.data.entity.History
import com.zztx.browse.data.entity.Password
import com.zztx.browse.data.repository.BrowserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BrowserViewModel(private val repository: BrowserRepository) : ViewModel() {
    private val _currentTab = MutableStateFlow(Tab())
    private val _tabs = MutableStateFlow<List<Tab>>(listOf(Tab()))
    private val _syncStatus = MutableStateFlow(SyncStatus.IDLE)
    private val _syncError = MutableStateFlow<String?>(null)

    val bookmarks = repository.getAllBookmarks()
    val history = repository.getAllHistory()
    val passwords = repository.getAllPasswords()

    val browserState: StateFlow<BrowserUiState> = combine(
        _currentTab,
        _tabs,
        _syncStatus,
        _syncError
    ) { currentTab, tabs, syncStatus, syncError ->
        BrowserUiState(
            currentTab = currentTab,
            tabs = tabs,
            syncStatus = syncStatus,
            syncError = syncError
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BrowserUiState())

    fun loadUrl(url: String) {
        val currentIndex = _tabs.value.indexOf(_currentTab.value)
        val updatedTab = _currentTab.value.copy(url = url, isLoading = true)
        _tabs.value = _tabs.value.toMutableList().apply { set(currentIndex, updatedTab) }
        _currentTab.value = updatedTab
    }

    fun addNewTab() {
        val newTab = Tab(id = System.currentTimeMillis())
        _tabs.value = _tabs.value + newTab
        _currentTab.value = newTab
    }

    fun closeTab(tabId: Long) {
        if (_tabs.value.size > 1) {
            val newTabs = _tabs.value.filter { it.id != tabId }
            _tabs.value = newTabs
            if (_currentTab.value.id == tabId) {
                _currentTab.value = newTabs.first()
            }
        }
    }

    fun selectTab(tab: Tab) {
        _currentTab.value = tab
    }

    fun addBookmark(url: String, title: String, favicon: String?) {
        viewModelScope.launch {
            repository.addBookmark(url, title, favicon)
        }
    }

    fun removeBookmark(id: Long) {
        viewModelScope.launch {
            repository.removeBookmark(id)
        }
    }

    fun addHistory(url: String, title: String, favicon: String?) {
        viewModelScope.launch {
            repository.addHistory(url, title, favicon)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    fun savePassword(url: String, username: String, encryptedPassword: String) {
        viewModelScope.launch {
            repository.savePassword(url, username, encryptedPassword)
        }
    }

    fun deletePassword(id: Long) {
        viewModelScope.launch {
            repository.deletePassword(id)
        }
    }

    fun syncAll() {
        viewModelScope.launch {
            _syncStatus.value = SyncStatus.SYNCING
            _syncError.value = null

            val bookmarkResult = repository.syncBookmarks()
            if (bookmarkResult.isFailure) {
                _syncError.value = bookmarkResult.exceptionOrNull()?.message
                _syncStatus.value = SyncStatus.ERROR
                return@launch
            }

            val historyResult = repository.syncHistory()
            if (historyResult.isFailure) {
                _syncError.value = historyResult.exceptionOrNull()?.message
                _syncStatus.value = SyncStatus.ERROR
                return@launch
            }

            val passwordResult = repository.syncPasswords()
            if (passwordResult.isFailure) {
                _syncError.value = passwordResult.exceptionOrNull()?.message
                _syncStatus.value = SyncStatus.ERROR
                return@launch
            }

            _syncStatus.value = SyncStatus.SYNCED
        }
    }

    fun clearSyncError() {
        _syncError.value = null
    }
}

data class Tab(
    val id: Long = 0,
    val url: String = "about:blank",
    val title: String = "新标签页",
    val favicon: String? = null,
    val isLoading: Boolean = false
)

enum class SyncStatus {
    IDLE, SYNCING, SYNCED, ERROR
}

data class BrowserUiState(
    val currentTab: Tab = Tab(),
    val tabs: List<Tab> = listOf(),
    val syncStatus: SyncStatus = SyncStatus.IDLE,
    val syncError: String? = null
)
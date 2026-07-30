package com.zztx.browse.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zztx.browse.data.entity.User
import com.zztx.browse.viewmodel.SyncStatus

@Composable
fun MenuSheet(
    user: User?,
    syncStatus: SyncStatus,
    onBookmarksClick: () -> Unit,
    onHistoryClick: () -> Unit,
    onPasswordsClick: () -> Unit,
    onSyncClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                androidx.compose.material3.Icon(
                    Icons.Default.AccountCircle,
                    contentDescription = "用户头像",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onPrimary
                )
                Text(
                    text = user?.name ?: "未登录",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Text(
                    text = user?.email ?: "点击登录",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        ) {
            MenuItem(
                icon = Icons.Default.Bookmarks,
                text = "收藏夹",
                onClick = onBookmarksClick
            )
            MenuItem(
                icon = Icons.Default.History,
                text = "历史记录",
                onClick = onHistoryClick
            )
            MenuItem(
                icon = Icons.Default.Lock,
                text = "密码管理",
                onClick = onPasswordsClick
            )
            MenuItem(
                icon = Icons.Default.Sync,
                text = if (syncStatus == SyncStatus.SYNCING) "同步中..." else "立即同步",
                onClick = onSyncClick
            )
            MenuItem(
                icon = Icons.Default.Settings,
                text = "设置",
                onClick = onSettingsClick
            )
            MenuItem(
                icon = Icons.AutoMirrored.Default.Logout,
                text = if (user != null) "退出登录" else "登录",
                onClick = onLogoutClick
            )
        }
    }
}

@Composable
fun MenuItem(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable { onClick() }
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.material3.Icon(
                icon,
                contentDescription = text,
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = text,
                fontSize = 16.sp,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
        androidx.compose.material3.Icon(
            Icons.AutoMirrored.Default.ArrowRight,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
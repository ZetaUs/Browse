package com.zztx.browse.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.zztx.browse.data.entity.Password
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PasswordsScreen(
    passwords: List<Password>,
    onBack: () -> Unit,
    onDeletePassword: (Long) -> Unit,
    decryptPassword: (String) -> String
) {
    Column(modifier = Modifier.fillMaxSize()) {
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = "密码管理",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "返回")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp)
        ) {
            items(passwords.size) { index ->
                val password = passwords[index]
                PasswordItem(
                    password = password,
                    decryptPassword = decryptPassword,
                    onDelete = { onDeletePassword(password.id) }
                )
            }
        }
    }
}

@Composable
fun PasswordItem(
    password: Password,
    decryptPassword: (String) -> String,
    onDelete: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    var isCopied by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Text(
                text = password.url,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "用户名: ${password.username}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (isVisible) "密码: ${decryptPassword(password.encryptedPassword)}" else "密码: ********",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = {
                            isVisible = !isVisible
                        },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            if (isVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (isVisible) "隐藏密码" else "显示密码",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    CopyButton(
                        password = decryptPassword(password.encryptedPassword),
                        isCopied = isCopied,
                        onCopiedChange = { isCopied = it }
                    )
                }
            }
        }
    }
}

@Composable
fun CopyButton(
    password: String,
    isCopied: Boolean,
    onCopiedChange: (Boolean) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(isCopied) {
        if (isCopied) {
            delay(2000)
            onCopiedChange(false)
        }
    }

    IconButton(
        onClick = {
            onCopiedChange(true)
            val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val clipData = android.content.ClipData.newPlainText("password", password)
            clipboard.setPrimaryClip(clipData)
        },
        modifier = Modifier.size(24.dp)
    ) {
        Icon(
            Icons.Default.ContentCopy,
            contentDescription = if (isCopied) "已复制" else "复制密码",
            modifier = Modifier.size(20.dp)
        )
    }
}
package ru.kyamshanov.missionChat.welcomeScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.kyamshanov.missionChat.components.glassmorphism

@Composable
fun WelcomeSlidebar(
    modifier: Modifier = Modifier
) {
    val textColor = MaterialTheme.colorScheme.onSurface
    val secondaryTextColor = MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = modifier.width(280.dp).fillMaxHeight()
            .glassmorphism(shape = RoundedCornerShape(24.dp))
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier.size(44.dp).clip(CircleShape)
                        .background(textColor.copy(alpha = 0.1f))
                ) {
                    Icon(
                        Icons.Default.Person,
                        null,
                        Modifier.align(Alignment.Center),
                        tint = textColor
                    )
                }
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        "User Name",
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        color = textColor
                    )
                    Text("Online", fontSize = 12.sp, color = secondaryTextColor)
                }
            }
            Spacer(Modifier.height(32.dp))
            SidebarItem(
                Icons.AutoMirrored.Filled.Chat,
                "Chats",
                selected = true
            )
            SidebarItem(Icons.Default.History, "History")
            SidebarItem(Icons.Default.Settings, "Settings")
        }
    }
}


@Composable
fun SidebarItem(icon: ImageVector, text: String, selected: Boolean = false) {
    val textColor = MaterialTheme.colorScheme.onSurface
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) textColor.copy(alpha = 0.1f) else Color.Transparent)
            .clickable { }
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            null,
            modifier = Modifier.size(20.dp),
            tint = if (selected) textColor else textColor.copy(alpha = 0.7f)
        )
        Spacer(Modifier.width(12.dp))
        Text(
            text,
            fontSize = 14.sp,
            fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
            color = if (selected) textColor else textColor.copy(alpha = 0.7f)
        )
    }
}

package com.yuno

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.LibraryBooks
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * A lightweight account hub built from the account data Yuno actually has.
 * It deliberately doesn't invent email addresses, creator analytics, or editable
 * Google profile fields that the signed-in YouTube session doesn't expose.
 */
@Composable
fun ProfileScreen(
    contentPadding: PaddingValues,
    onSignIn: () -> Unit,
    onOpenLibrary: () -> Unit,
    onOpenAccountSettings: () -> Unit,
    onSignOut: () -> Unit,
) {
    val c = MaterialTheme.colorScheme
    val profile = Account.profile
    var confirmSignOut by remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxSize()
            .background(c.background)
            .verticalScroll(rememberScrollState())
            .padding(contentPadding)
            .padding(top = topBarHeight() + 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        LargePageTitle("Profile", fontSize = LargePageTitleCompactFontSize, barAligned = false)

        if (profile == null) {
            val shape = RoundedCornerShape(30.dp)
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = PageGutter)
                    .clip(shape)
                    .profileSurface(shape, c.surfaceContainerLow)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Icon(Icons.Rounded.Person, contentDescription = null, tint = c.primary, modifier = Modifier.size(42.dp))
                Text("Sign in to Yuno", fontSize = 21.sp, fontWeight = FontWeight.ExtraBold, color = c.onSurface)
                Text(
                    "Connect your YouTube account to see your synced history, subscriptions, playlists and saved videos.",
                    fontSize = 14.sp,
                    color = c.onSurfaceVariant,
                )
                ProfilePrimaryAction("Sign in with Google", onSignIn)
            }
        } else {
            val shape = RoundedCornerShape(30.dp)
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = PageGutter)
                    .clip(shape)
                    .profileSurface(shape, c.surfaceContainerLow)
                    .padding(22.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                ChannelAvatar(profile.avatar, profile.name, size = 84)
                Text(
                    profile.name,
                    color = c.onSurface,
                    fontSize = 23.sp,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 2,
                )
                profile.handle?.takeIf { it.isNotBlank() }?.let {
                    Text(it, color = c.onSurfaceVariant, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(7.dp),
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(c.primaryContainer.copy(alpha = 0.72f))
                        .padding(horizontal = 12.dp, vertical = 7.dp),
                ) {
                    BoxDot(c.primary)
                    Text("YouTube account connected", color = c.onPrimaryContainer, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Column(
                Modifier.padding(horizontal = PageGutter),
                verticalArrangement = Arrangement.spacedBy(9.dp),
            ) {
                Text("Your Yuno", color = c.onSurface, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold)
                ProfileActionRow(
                    icon = Icons.Rounded.History,
                    title = "Watch history",
                    subtitle = "Review and remove videos you've watched",
                    onClick = onOpenLibrary,
                )
                ProfileActionRow(
                    icon = Icons.Rounded.LibraryBooks,
                    title = "Library and playlists",
                    subtitle = "Watch later, liked videos and playlists",
                    onClick = onOpenLibrary,
                )
                ProfileActionRow(
                    icon = Icons.Rounded.Settings,
                    title = "Account settings",
                    subtitle = "Manage the account connected to Yuno",
                    onClick = onOpenAccountSettings,
                )
            }

            Column(Modifier.padding(horizontal = PageGutter)) {
                ProfileActionRow(
                    icon = Icons.Rounded.Logout,
                    title = "Sign out",
                    subtitle = "Disconnect this YouTube session from Yuno",
                    destructive = true,
                    onClick = { confirmSignOut = true },
                )
            }
        }
    }

    if (confirmSignOut) {
        AlertDialog(
            onDismissRequest = { confirmSignOut = false },
            title = { Text("Sign out of Yuno?") },
            text = { Text("Your local watch history stays on this device. Synced YouTube data will no longer be available until you sign in again.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmSignOut = false
                    onSignOut()
                }) { Text("Sign out") }
            },
            dismissButton = {
                TextButton(onClick = { confirmSignOut = false }) { Text("Cancel") }
            },
        )
    }
}

@Composable
private fun ProfileActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    destructive: Boolean = false,
    onClick: () -> Unit,
) {
    val c = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(22.dp)
    Row(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .pressable(onClick = onClick)
            .profileSurface(shape, c.surfaceContainerLow)
            .padding(horizontal = 16.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = if (destructive) c.error else c.primary,
            modifier = Modifier.size(22.dp),
        )
        Spacer(Modifier.width(13.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = if (destructive) c.error else c.onSurface, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(2.dp))
            Text(subtitle, color = c.onSurfaceVariant, fontSize = 12.sp)
        }
    }
}

@Composable
private fun ProfilePrimaryAction(label: String, onClick: () -> Unit) {
    val c = MaterialTheme.colorScheme
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(c.primary)
            .pressable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.Center,
    ) {
        Text(label, color = c.onPrimary, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
private fun BoxDot(color: Color) {
    androidx.compose.foundation.layout.Box(
        Modifier
            .size(7.dp)
            .clip(CircleShape)
            .background(color),
    )
}

@Composable
private fun Modifier.profileSurface(shape: CornerBasedShape, fallback: Color): Modifier =
    if (LocalLiquidGlassEnabled.current && isGlassSupported()) {
        liquidGlass(shape, surfaceTint = Color.White.copy(alpha = 0.10f)).liquidGlassRim(shape)
    } else {
        background(fallback, shape)
    }

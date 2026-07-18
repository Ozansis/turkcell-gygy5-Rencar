package com.turkcell.rencar_pair.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private val GreenPositive = Color(0xFF16A34A)
private val RedNegative   = Color(0xFFDC2626)

private data class ProfileMenuEntry(
    val label: String,
    val icon: ImageVector,
    val intent: ProfileContract.Intent
)

private val profileMenuEntries = listOf(
    ProfileMenuEntry("Ödeme yöntemleri", Icons.Default.CreditCard, ProfileContract.Intent.PaymentMethodsClicked),
    ProfileMenuEntry("Ayarlar", Icons.Default.Settings, ProfileContract.Intent.SettingsClicked),
    ProfileMenuEntry("Yardım & destek", Icons.AutoMirrored.Filled.HelpOutline, ProfileContract.Intent.HelpClicked),
    ProfileMenuEntry("Davet et · ₺50 kazan", Icons.AutoMirrored.Filled.Login, ProfileContract.Intent.InviteClicked)
)

@Composable
fun ProfileScreen(
    state: ProfileContract.State,
    onIntent: (ProfileContract.Intent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp)
    ) {
        ProfileHeader(
            userName    = state.userName,
            phoneNumber = state.phoneNumber,
            onEditClick = { onIntent(ProfileContract.Intent.EditProfileClicked) }
        )

        state.errorMessage?.let { message ->
            Spacer(Modifier.height(8.dp))
            Text(
                text  = message,
                style = MaterialTheme.typography.bodySmall,
                color = RedNegative
            )
        }

        Spacer(Modifier.height(20.dp))

        state.license?.let { license ->
            LicenseCard(license = license)
            Spacer(Modifier.height(20.dp))
        }

        MenuCard(onIntent = onIntent)

        Spacer(Modifier.height(16.dp))

        SignOutCard(onClick = { onIntent(ProfileContract.Intent.SignOutClicked) })
    }
}

@Composable
private fun ProfileHeader(
    userName: String,
    phoneNumber: String,
    onEditClick: () -> Unit
) {
    Row(
        modifier          = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = userName,
                style      = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color      = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text  = phoneNumber,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
                .clickable(onClick = onEditClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = Icons.Default.Edit,
                contentDescription = "Profili düzenle",
                tint               = MaterialTheme.colorScheme.onSurface,
                modifier           = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun LicenseCard(license: LicenseVerification) {
    val accentColor = if (license.isVerified) GreenPositive else MaterialTheme.colorScheme.onSurfaceVariant
    val titleText = if (license.isVerified) "Ehliyet doğrulandı" else "Ehliyet doğrulanmadı"

    Surface(
        modifier        = Modifier.fillMaxWidth(),
        shape           = RoundedCornerShape(16.dp),
        color           = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = Icons.Default.VerifiedUser,
                    contentDescription = null,
                    tint               = accentColor,
                    modifier           = Modifier.size(20.dp)
                )
            }

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = titleText,
                    style      = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color      = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text  = "Sürücü belgesi",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(accentColor.copy(alpha = 0.12f))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text       = license.statusLabel,
                    style      = MaterialTheme.typography.labelSmall,
                    color      = accentColor,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun MenuCard(onIntent: (ProfileContract.Intent) -> Unit) {
    Surface(
        modifier        = Modifier.fillMaxWidth(),
        shape           = RoundedCornerShape(16.dp),
        color           = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp
    ) {
        Column {
            profileMenuEntries.forEachIndexed { index, entry ->
                MenuRow(
                    label   = entry.label,
                    icon    = entry.icon,
                    onClick = { onIntent(entry.intent) }
                )
                if (index != profileMenuEntries.lastIndex) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        color    = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                    )
                }
            }
        }
    }
}

@Composable
private fun MenuRow(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector        = icon,
            contentDescription = null,
            tint               = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier           = Modifier.size(20.dp)
        )

        Spacer(Modifier.width(14.dp))

        Text(
            text     = label,
            style    = MaterialTheme.typography.bodyMedium,
            color    = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )

        Icon(
            imageVector        = Icons.Default.ChevronRight,
            contentDescription = null,
            tint               = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier           = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun SignOutCard(onClick: () -> Unit) {
    Surface(
        modifier        = Modifier.fillMaxWidth(),
        shape           = RoundedCornerShape(16.dp),
        color           = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Icon(
                imageVector        = Icons.AutoMirrored.Filled.ExitToApp,
                contentDescription = null,
                tint               = RedNegative,
                modifier           = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text       = "Çıkış yap",
                style      = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color      = RedNegative
            )
        }
    }
}

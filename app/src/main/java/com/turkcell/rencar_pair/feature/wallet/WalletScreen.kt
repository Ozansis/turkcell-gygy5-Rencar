package com.turkcell.rencar_pair.feature.wallet

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.turkcell.rencar_pair.ui.theme.Blue400
import com.turkcell.rencar_pair.ui.theme.Blue500

private val GreenPositive = Color(0xFF16A34A)
private val RedNegative   = Color(0xFFDC2626)
private val VisaBlue      = Color(0xFF1A56DB)
private val McRed         = Color(0xFFDC2626)

@Composable
fun WalletScreen(
    state: WalletContract.State,
    onIntent: (WalletContract.Intent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text     = "Cüzdan",
            style    = MaterialTheme.typography.headlineLarge,
            color    = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 24.dp)
        )

        BalanceCard(
            formattedBalance = state.formattedBalance,
            onAddBalance     = { onIntent(WalletContract.Intent.AddBalance) },
            modifier         = Modifier.padding(horizontal = 20.dp)
        )

        Spacer(Modifier.height(28.dp))

        SectionHeader(
            title       = "Kayıtlı kartlar",
            actionLabel = "+ Ekle",
            onAction    = { onIntent(WalletContract.Intent.AddCard) }
        )

        Spacer(Modifier.height(12.dp))

        state.savedCards.forEach { card ->
            SavedCardRow(
                card    = card,
                onClick = { onIntent(WalletContract.Intent.CardSelected(card.id)) }
            )
            Spacer(Modifier.height(8.dp))
        }

        Spacer(Modifier.height(20.dp))

        SectionHeader(
            title       = "Son işlemler",
            actionLabel = null,
            onAction    = {}
        )

        Spacer(Modifier.height(12.dp))

        state.transactions.forEach { tx ->
            TransactionRow(transaction = tx)
            Spacer(Modifier.height(12.dp))
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun BalanceCard(
    formattedBalance: String,
    onAddBalance: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.horizontalGradient(colors = listOf(Blue500, Blue400)))
            .padding(24.dp)
    ) {
        Column {
            Text(
                text  = "Rencar bakiyesi",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text       = formattedBalance,
                style      = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color      = Color.White
            )

            Spacer(Modifier.height(20.dp))

            OutlinedButton(
                onClick = onAddBalance,
                shape   = RoundedCornerShape(12.dp),
                colors  = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                border  = BorderStroke(1.dp, Color.White.copy(alpha = 0.6f))
            ) {
                Icon(
                    imageVector        = Icons.Default.Add,
                    contentDescription = null,
                    modifier           = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text  = "Bakiye Yükle",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    actionLabel: String?,
    onAction: () -> Unit
) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text(
            text       = title,
            style      = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color      = MaterialTheme.colorScheme.onBackground
        )
        if (actionLabel != null) {
            Text(
                text     = actionLabel,
                style    = MaterialTheme.typography.bodyMedium,
                color    = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable(onClick = onAction)
            )
        }
    }
}

@Composable
private fun SavedCardRow(
    card: SavedCard,
    onClick: () -> Unit
) {
    Surface(
        modifier        = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick),
        shape           = RoundedCornerShape(14.dp),
        color           = MaterialTheme.colorScheme.surface,
        shadowElevation = 1.dp
    ) {
        Row(
            modifier          = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CardTypeIcon(type = card.type)

            Spacer(Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text       = "•••• ${card.lastFour}",
                    style      = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color      = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text  = "Son kullanma ${card.expiryDate}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (card.isDefault) {
                DefaultBadge()
            }
        }
    }
}

@Composable
private fun CardTypeIcon(type: CardType) {
    val (bgColor, label) = when (type) {
        CardType.VISA -> VisaBlue to "VISA"
        CardType.MC   -> McRed   to "MC"
    }

    Box(
        modifier         = Modifier
            .size(42.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text       = label,
            color      = Color.White,
            style      = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun DefaultBadge() {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(GreenPositive.copy(alpha = 0.12f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text       = "Varsayılan",
            style      = MaterialTheme.typography.labelSmall,
            color      = GreenPositive,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun TransactionRow(transaction: WalletTransaction) {
    Row(
        modifier          = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TransactionIcon(isCredit = transaction.isCredit)

        Spacer(Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = transaction.title,
                style      = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color      = MaterialTheme.colorScheme.onSurface
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text  = transaction.dateLabel,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Text(
            text       = formatTransactionAmount(transaction),
            style      = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color      = if (transaction.isCredit) GreenPositive else RedNegative
        )
    }
}

@Composable
private fun TransactionIcon(isCredit: Boolean) {
    val bgColor   = if (isCredit) GreenPositive.copy(alpha = 0.1f)
                    else MaterialTheme.colorScheme.surfaceVariant
    val iconColor = if (isCredit) GreenPositive
                    else MaterialTheme.colorScheme.onSurfaceVariant
    val icon      = if (isCredit) Icons.Default.Add else Icons.Default.DirectionsCar

    Box(
        modifier         = Modifier
            .size(42.dp)
            .clip(CircleShape)
            .background(bgColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector        = icon,
            contentDescription = null,
            tint               = iconColor,
            modifier           = Modifier.size(20.dp)
        )
    }
}

private fun formatTransactionAmount(tx: WalletTransaction): String {
    val sign    = if (tx.isCredit) "+" else "-"
    val cents   = Math.round(tx.amount * 100)
    val intPart = cents / 100
    val decPart = cents % 100
    return "$sign₺$intPart,${decPart.toString().padStart(2, '0')}"
}

package com.turkcell.rencar_pair.feature.onboarding

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun OnboardingScreen(
    state: OnboardingContract.State,
    onIntent: (OnboardingContract.Intent) -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val page = state.pages.getOrNull(state.currentPage) ?: return

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.weight(1.5f))

            OnboardingIcon(page = page, isDark = isDark)

            Spacer(Modifier.height(32.dp))

            Text(
                text      = page.title,
                style     = MaterialTheme.typography.headlineLarge,
                color     = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text      = page.subtitle,
                style     = MaterialTheme.typography.bodyMedium,
                color     = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.weight(2f))

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment     = Alignment.CenterVertically
            ) {
                state.pages.indices.forEach { index ->
                    PageIndicatorDot(isActive = index == state.currentPage)
                }
            }

            Spacer(Modifier.height(28.dp))

            Button(
                onClick   = { onIntent(OnboardingContract.Intent.PrimaryAction) },
                modifier  = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape     = RoundedCornerShape(16.dp),
                colors    = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text(
                    text  = "Hemen Başla",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White
                )
            }

            Spacer(Modifier.height(16.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier          = Modifier.height(IntrinsicSize.Min)
            ) {
                Text(
                    text  = "Zaten hesabım var · ",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                TextButton(
                    onClick         = { onIntent(OnboardingContract.Intent.GoToLogin) },
                    contentPadding  = PaddingValues(0.dp)
                ) {
                    Text(
                        text  = "Giriş yap",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun OnboardingIcon(page: OnboardingPageData, isDark: Boolean) {
    val primary = MaterialTheme.colorScheme.primary
    Box(contentAlignment = Alignment.Center) {
        if (isDark) {
            Canvas(modifier = Modifier.size(220.dp)) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(primary.copy(alpha = 0.35f), Color.Transparent),
                        radius = size.width / 2
                    )
                )
            }
        }
        Box(
            contentAlignment = Alignment.Center,
            modifier         = Modifier
                .size(80.dp)
                .background(color = primary, shape = RoundedCornerShape(22.dp))
        ) {
            Icon(
                imageVector     = page.icon,
                contentDescription = null,
                tint            = Color.White,
                modifier        = Modifier.size(40.dp)
            )
        }
    }
}

@Composable
private fun PageIndicatorDot(isActive: Boolean) {
    val width by animateDpAsState(
        targetValue = if (isActive) 24.dp else 8.dp,
        label       = "dot_width"
    )
    val color by animateColorAsState(
        targetValue = if (isActive) MaterialTheme.colorScheme.primary
                      else MaterialTheme.colorScheme.outline,
        label       = "dot_color"
    )
    Box(
        modifier = Modifier
            .height(8.dp)
            .width(width)
            .background(color = color, shape = CircleShape)
    )
}

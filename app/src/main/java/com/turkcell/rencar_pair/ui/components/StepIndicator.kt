package com.turkcell.rencar_pair.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private val VerificationSteps = listOf("Ehliyet", "Selfie", "Onay")

@Composable
fun StepIndicator(
    currentStep: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier          = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        VerificationSteps.forEachIndexed { index, label ->
            val stepNumber = index + 1
            StepIndicatorItem(
                number   = stepNumber,
                label    = label,
                isActive = stepNumber == currentStep
            )

            if (stepNumber != VerificationSteps.size) {
                StepConnector()
            }
        }
    }
}

@Composable
private fun StepIndicatorItem(
    number: Int,
    label: String,
    isActive: Boolean
) {
    val circleColor = if (isActive) MaterialTheme.colorScheme.primary
                       else MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (isActive) Color.White
                        else MaterialTheme.colorScheme.onSurfaceVariant
    val labelColor = if (isActive) MaterialTheme.colorScheme.onBackground
                      else MaterialTheme.colorScheme.onSurfaceVariant

    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            contentAlignment = Alignment.Center,
            modifier         = Modifier
                .size(24.dp)
                .background(color = circleColor, shape = CircleShape)
        ) {
            Text(
                text       = number.toString(),
                style      = MaterialTheme.typography.labelMedium,
                color      = contentColor,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(Modifier.width(8.dp))

        Text(
            text       = label,
            style      = MaterialTheme.typography.labelMedium,
            color      = labelColor,
            fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
private fun RowScope.StepConnector() {
    Box(
        modifier = Modifier
            .weight(1f)
            .height(1.dp)
            .padding(horizontal = 8.dp)
            .background(MaterialTheme.colorScheme.outline)
    )
}

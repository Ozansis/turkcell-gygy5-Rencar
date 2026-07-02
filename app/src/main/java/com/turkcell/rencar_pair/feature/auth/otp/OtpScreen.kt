package com.turkcell.rencar_pair.feature.auth.otp

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun OtpScreen(
    state: OtpContract.State,
    onIntent: (OtpContract.Intent) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .systemBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            BackButton(onClick = { onIntent(OtpContract.Intent.ChangeNumber) })

            Spacer(Modifier.height(24.dp))

            PhoneIcon()

            Spacer(Modifier.height(24.dp))

            Text(
                text  = "Telefonunu doğrula",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text  = "+90 ${formatPhoneNumber(state.phoneNumber)} numarasına gönderdiğimiz 6 haneli kodu gir.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(32.dp))

            OtpCodeInput(
                code           = state.code,
                onCodeChanged  = { onIntent(OtpContract.Intent.CodeChanged(it)) }
            )

            Spacer(Modifier.height(20.dp))

            ResendRow(
                canResend         = state.canResend,
                remainingSeconds  = state.remainingSeconds,
                onResendClick     = { onIntent(OtpContract.Intent.ResendCode) }
            )

            Spacer(Modifier.weight(1f))

            Button(
                onClick   = { onIntent(OtpContract.Intent.Verify) },
                enabled   = state.isCodeComplete,
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
                    text  = "Doğrula ve Devam Et",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White
                )
            }

            Spacer(Modifier.height(20.dp))

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier          = Modifier.height(IntrinsicSize.Min)
                ) {
                    Text(
                        text  = "Numara yanlış mı? ",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    TextButton(
                        onClick        = { onIntent(OtpContract.Intent.ChangeNumber) },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text  = "Değiştir",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun BackButton(onClick: () -> Unit) {
    IconButton(
        onClick  = onClick,
        modifier = Modifier
            .size(40.dp)
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = CircleShape
            ),
        colors = IconButtonDefaults.iconButtonColors(
            contentColor = MaterialTheme.colorScheme.onBackground
        )
    ) {
        Icon(
            imageVector        = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Geri",
            modifier           = Modifier.size(20.dp)
        )
    }
}

@Composable
private fun PhoneIcon() {
    Box(
        contentAlignment = Alignment.Center,
        modifier         = Modifier
            .size(64.dp)
            .background(
                color = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(18.dp)
            )
    ) {
        Icon(
            imageVector        = Icons.Filled.Smartphone,
            contentDescription = null,
            tint               = Color.White,
            modifier           = Modifier.size(30.dp)
        )
    }
}

@Composable
private fun OtpCodeInput(
    code: String,
    onCodeChanged: (String) -> Unit
) {
    Box {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (index in 0 until OtpContract.CODE_LENGTH) {
                OtpDigitBox(
                    digit      = code.getOrNull(index)?.toString().orEmpty(),
                    isFocused  = index == code.length,
                    modifier   = Modifier.weight(1f)
                )
            }
        }

        BasicTextField(
            value           = code,
            onValueChange   = onCodeChanged,
            modifier        = Modifier
                .matchParentSize()
                .alpha(0f),
            singleLine      = true,
            cursorBrush     = SolidColor(Color.Transparent),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword)
        )
    }
}

@Composable
private fun OtpDigitBox(
    digit: String,
    isFocused: Boolean,
    modifier: Modifier = Modifier
) {
    val borderColor = if (isFocused) MaterialTheme.colorScheme.primary
                       else MaterialTheme.colorScheme.outline

    Box(
        contentAlignment = Alignment.Center,
        modifier         = modifier
            .height(52.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(12.dp)
            )
            .border(
                width = if (isFocused) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        Text(
            text       = digit,
            style      = MaterialTheme.typography.headlineSmall,
            color      = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold,
            textAlign  = TextAlign.Center
        )
    }
}

@Composable
private fun ResendRow(
    canResend: Boolean,
    remainingSeconds: Int,
    onResendClick: () -> Unit
) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        if (canResend) {
            TextButton(
                onClick        = onResendClick,
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text  = "Kodu tekrar gönder",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        } else {
            Text(
                text  = "Kodu tekrar gönder   ${formatTimer(remainingSeconds)}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun formatTimer(totalSeconds: Int): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "$minutes:${seconds.toString().padStart(2, '0')}"
}

private fun formatPhoneNumber(digits: String): String {
    val builder = StringBuilder()
    digits.forEachIndexed { index, char ->
        when (index) {
            3, 6, 8 -> builder.append(' ')
        }
        builder.append(char)
    }
    return builder.toString()
}

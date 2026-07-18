package com.turkcell.rencar_pair.feature.auth.login

import androidx.compose.foundation.background
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun LoginScreen(
    state: LoginContract.State,
    onIntent: (LoginContract.Intent) -> Unit
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

            BackButton(onClick = { onIntent(LoginContract.Intent.NavigateBack) })

            Spacer(Modifier.height(24.dp))

            Text(
                text  = "Tekrar hoş geldin",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text  = "Telefon numaranı gir, SMS ile doğrulama kodu gönderelim.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(32.dp))

            Text(
                text  = "Telefon numarası",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(Modifier.height(8.dp))

            PhoneNumberField(
                phoneNumber = state.phoneNumber,
                onValueChange = { onIntent(LoginContract.Intent.PhoneNumberChanged(it)) }
            )

            Spacer(Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    imageVector        = Icons.Outlined.Info,
                    contentDescription = null,
                    tint               = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier           = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text  = "6 haneli kodu bu numaraya göndereceğiz. SMS ücreti operatörünüze bağlıdır.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick   = { onIntent(LoginContract.Intent.SendCode) },
                enabled   = state.isPhoneNumberValid,
                modifier  = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape     = RoundedCornerShape(16.dp),
                colors    = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Icon(
                    imageVector        = Icons.AutoMirrored.Filled.Send,
                    contentDescription = null,
                    tint               = Color.White,
                    modifier           = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text  = "Kod Gönder",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White
                )
            }

            Spacer(Modifier.height(20.dp))

            Row(
                modifier               = Modifier.fillMaxWidth(),
                horizontalArrangement  = Arrangement.Center,
                verticalAlignment      = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier          = Modifier.height(IntrinsicSize.Min)
                ) {
                    Text(
                        text  = "Hesabın yok mu? ",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    TextButton(
                        onClick        = { onIntent(LoginContract.Intent.GoToRegister) },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text  = "Kayıt ol",
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
private fun PhoneNumberField(
    phoneNumber: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value                = phoneNumber,
        onValueChange        = { onValueChange(it) },
        visualTransformation = PhoneNumberVisualTransformation,
        modifier        = Modifier.fillMaxWidth(),
        placeholder     = {
            Text(
                text  = "532 000 00 00",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        leadingIcon = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier          = Modifier.padding(start = 12.dp)
            ) {
                Text(
                    text       = "TR +90",
                    style      = MaterialTheme.typography.bodyLarge,
                    color      = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.width(10.dp))
                Box(
                    modifier = Modifier
                        .height(20.dp)
                        .width(1.dp)
                        .background(MaterialTheme.colorScheme.outline)
                )
            }
        },
        singleLine      = true,
        textStyle       = MaterialTheme.typography.bodyLarge,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        shape           = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor   = MaterialTheme.colorScheme.surfaceVariant,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedBorderColor      = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor    = MaterialTheme.colorScheme.outline,
            focusedTextColor        = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor      = MaterialTheme.colorScheme.onSurface
        )
    )
}

private val PHONE_SPACE_THRESHOLDS = listOf(3, 6, 8)
private val PHONE_SPACE_POSITIONS = listOf(3, 7, 10)

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

/**
 * Ham (boşluksuz) rakamları görüntüde "532 123 45 67" biçiminde gösterir.
 * value/onValueChange ham metinle çalışmaya devam eder; imleç konumu OffsetMapping
 * ile ham<->görüntü arasında eşlenir (String'i her tuşta yeniden formatlayıp value'ya
 * vermek, Compose'un diff tabanlı imleç takibini bozuyordu — bkz. PROGRESS.md 2026-07-15).
 */
private object PhoneNumberVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val digits = text.text
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                val clamped = offset.coerceIn(0, digits.length)
                return clamped + PHONE_SPACE_THRESHOLDS.count { it < clamped }
            }

            override fun transformedToOriginal(offset: Int): Int {
                val spacesBefore = PHONE_SPACE_POSITIONS.count { it < offset }
                return (offset - spacesBefore).coerceIn(0, digits.length)
            }
        }
        return TransformedText(AnnotatedString(formatPhoneNumber(digits)), offsetMapping)
    }
}

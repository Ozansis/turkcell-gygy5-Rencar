package com.turkcell.rencar_pair.feature.auth.register

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun RegisterScreen(
    state: RegisterContract.State,
    onIntent: (RegisterContract.Intent) -> Unit
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
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            BackButton(onClick = { onIntent(RegisterContract.Intent.NavigateBack) })

            Spacer(Modifier.height(24.dp))

            Text(
                text  = "Hesap oluştur",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text  = "Aracını kiralamaya başlamak için bilgilerini gir.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(32.dp))

            FieldLabel("Ad Soyad")
            RegisterTextField(
                value           = state.fullName,
                onValueChange   = { onIntent(RegisterContract.Intent.FullNameChanged(it)) },
                placeholder     = "Ahmet Yılmaz",
                keyboardType    = KeyboardType.Text
            )

            Spacer(Modifier.height(16.dp))

            FieldLabel("E-posta")
            RegisterTextField(
                value           = state.email,
                onValueChange   = { onIntent(RegisterContract.Intent.EmailChanged(it)) },
                placeholder     = "ornek@eposta.com",
                keyboardType    = KeyboardType.Email
            )

            Spacer(Modifier.height(16.dp))

            FieldLabel("Telefon numarası")
            PhoneNumberField(
                phoneNumber   = state.phoneNumber,
                onValueChange = { onIntent(RegisterContract.Intent.PhoneNumberChanged(it)) }
            )

            Spacer(Modifier.height(16.dp))

            FieldLabel("Şifre")
            RegisterTextField(
                value           = state.password,
                onValueChange   = { onIntent(RegisterContract.Intent.PasswordChanged(it)) },
                placeholder     = "En az 6 karakter",
                keyboardType    = KeyboardType.Password,
                isPassword      = true
            )

            Spacer(Modifier.height(16.dp))

            FieldLabel("Şifre tekrar")
            RegisterTextField(
                value           = state.confirmPassword,
                onValueChange   = { onIntent(RegisterContract.Intent.ConfirmPasswordChanged(it)) },
                placeholder     = "Şifreni tekrar gir",
                keyboardType    = KeyboardType.Password,
                isPassword      = true,
                isError         = state.confirmPassword.isNotEmpty() && !state.doPasswordsMatch
            )

            Spacer(Modifier.height(16.dp))

            FieldLabel("Referans kodu (opsiyonel)")
            RegisterTextField(
                value           = state.referralCode,
                onValueChange   = { onIntent(RegisterContract.Intent.ReferralCodeChanged(it)) },
                placeholder     = "REN-XXXXXX",
                keyboardType    = KeyboardType.Text
            )

            Spacer(Modifier.height(32.dp))

            Button(
                onClick   = { onIntent(RegisterContract.Intent.Register) },
                enabled   = state.isFormValid && !state.isSubmitting,
                modifier  = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape     = RoundedCornerShape(16.dp),
                colors    = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                if (state.isSubmitting) {
                    CircularProgressIndicator(
                        modifier    = Modifier.size(20.dp),
                        color       = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text  = "Kayıt Ol",
                        style = MaterialTheme.typography.labelLarge,
                        color = Color.White
                    )
                }
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
                        text  = "Zaten hesabın var mı? ",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    TextButton(
                        onClick        = { onIntent(RegisterContract.Intent.GoToLogin) },
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text  = "Giriş yap",
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
private fun FieldLabel(text: String) {
    Text(
        text  = text,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onSurface
    )
    Spacer(Modifier.height(8.dp))
}

@Composable
private fun RegisterTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    keyboardType: KeyboardType,
    isPassword: Boolean = false,
    isError: Boolean = false
) {
    OutlinedTextField(
        value           = value,
        onValueChange   = onValueChange,
        modifier        = Modifier.fillMaxWidth(),
        placeholder     = {
            Text(
                text  = placeholder,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        singleLine             = true,
        isError                = isError,
        textStyle              = MaterialTheme.typography.bodyLarge,
        visualTransformation    = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions         = KeyboardOptions(keyboardType = keyboardType),
        shape                   = RoundedCornerShape(16.dp),
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

@Composable
private fun PhoneNumberField(
    phoneNumber: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value           = formatPhoneNumber(phoneNumber),
        onValueChange   = { onValueChange(it) },
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

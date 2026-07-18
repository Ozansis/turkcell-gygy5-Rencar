package com.turkcell.rencar_pair.feature.invite

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun InviteRoute(
    referralCode: String,
    onNavigateBack: () -> Unit = {},
    viewModel: InviteViewModel = hiltViewModel<InviteViewModel, InviteViewModel.Factory>(
        creationCallback = { factory -> factory.create(referralCode) }
    )
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is InviteContract.Effect.ShareReferralCode -> {
                    val sendIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "RenCar'a katıl, referans kodumla kaydolduğunda ikimiz de kazanıyoruz: ${effect.referralCode}"
                        )
                    }
                    context.startActivity(Intent.createChooser(sendIntent, null))
                }
                InviteContract.Effect.NavigateBack -> onNavigateBack()
            }
        }
    }

    InviteScreen(state = state, onIntent = viewModel::onIntent)
}

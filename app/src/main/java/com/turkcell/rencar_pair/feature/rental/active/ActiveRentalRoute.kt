package com.turkcell.rencar_pair.feature.rental.active

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun ActiveRentalRoute(
    rentalId: String,
    onNavigateToHome: () -> Unit = {},
    viewModel: ActiveRentalViewModel = hiltViewModel<ActiveRentalViewModel, ActiveRentalViewModel.Factory>(
        creationCallback = { factory -> factory.create(rentalId) }
    )
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                ActiveRentalContract.Effect.NavigateToHome -> onNavigateToHome()
                is ActiveRentalContract.Effect.ShowInfo    -> Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
                is ActiveRentalContract.Effect.ShowError   -> Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    ActiveRentalScreen(state = state, onIntent = viewModel::onIntent)
}

package com.turkcell.rencar_pair.feature.history.detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun HistoryDetailRoute(
    rentalId: String,
    onNavigateBack: () -> Unit = {},
    viewModel: HistoryDetailViewModel = hiltViewModel<HistoryDetailViewModel, HistoryDetailViewModel.Factory>(
        creationCallback = { factory -> factory.create(rentalId) }
    )
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                HistoryDetailContract.Effect.NavigateBack -> onNavigateBack()
            }
        }
    }

    HistoryDetailScreen(state = state, onIntent = viewModel::onIntent)
}

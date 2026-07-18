package com.turkcell.rencar_pair.feature.history

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@Composable
fun HistoryRoute(
    onNavigateToDetail: (String) -> Unit = {},
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is HistoryContract.Effect.NavigateToDetail -> onNavigateToDetail(effect.rentalId)
            }
        }
    }

    HistoryScreen(
        state    = state,
        onIntent = viewModel::onIntent
    )
}

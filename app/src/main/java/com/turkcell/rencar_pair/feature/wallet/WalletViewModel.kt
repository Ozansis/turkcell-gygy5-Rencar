package com.turkcell.rencar_pair.feature.wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WalletViewModel : ViewModel() {

    private val _state = MutableStateFlow(WalletContract.State())
    val state: StateFlow<WalletContract.State> = _state.asStateFlow()

    private val _effect = Channel<WalletContract.Effect>(Channel.BUFFERED)
    val effect: Flow<WalletContract.Effect> = _effect.receiveAsFlow()

    init {
        loadWalletData()
    }

    fun onIntent(intent: WalletContract.Intent) {
        when (intent) {
            WalletContract.Intent.AddBalance             -> sendEffect(WalletContract.Effect.ShowAddBalanceSheet)
            WalletContract.Intent.AddCard                -> sendEffect(WalletContract.Effect.ShowAddCardSheet)
            is WalletContract.Intent.CardSelected        -> handleCardSelected(intent.cardId)
        }
    }

    private fun loadWalletData() {
        _state.update {
            it.copy(
                balance      = WalletMockSource.balance,
                savedCards   = WalletMockSource.savedCards,
                transactions = WalletMockSource.transactions
            )
        }
    }

    private fun handleCardSelected(cardId: String) {
        _state.update { current ->
            current.copy(
                savedCards = current.savedCards.map { card ->
                    card.copy(isDefault = card.id == cardId)
                }
            )
        }
    }

    private fun sendEffect(effect: WalletContract.Effect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}

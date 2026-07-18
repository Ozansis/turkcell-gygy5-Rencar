package com.turkcell.rencar_pair.feature.wallet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.rencar_pair.data.network.dto.CardResponseDto
import com.turkcell.rencar_pair.data.network.dto.WalletTransactionDto
import com.turkcell.rencar_pair.data.repository.AuthResult
import com.turkcell.rencar_pair.data.repository.CardsRepository
import com.turkcell.rencar_pair.data.repository.WalletRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private val DATE_LABEL_FORMATTER =
    DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").withZone(ZoneId.systemDefault())

@HiltViewModel
class WalletViewModel @Inject constructor(
    private val walletRepository: WalletRepository,
    private val cardsRepository: CardsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(WalletContract.State())
    val state: StateFlow<WalletContract.State> = _state.asStateFlow()

    private val _effect = Channel<WalletContract.Effect>(Channel.BUFFERED)
    val effect: Flow<WalletContract.Effect> = _effect.receiveAsFlow()

    init {
        loadWalletAndCards()
    }

    fun onIntent(intent: WalletContract.Intent) {
        when (intent) {
            WalletContract.Intent.AddBalance             -> sendEffect(WalletContract.Effect.ShowAddBalanceSheet)
            WalletContract.Intent.AddCard                -> sendEffect(WalletContract.Effect.ShowAddCardSheet)
            is WalletContract.Intent.CardSelected        -> handleCardSelected(intent.cardId)
            is WalletContract.Intent.CardDeleteRequested -> handleCardDeleteRequested(intent.cardId)
            is WalletContract.Intent.TopupConfirmed      -> handleTopupConfirmed(intent.amount)
            is WalletContract.Intent.AddCardConfirmed    ->
                handleAddCardConfirmed(intent.brand, intent.last4, intent.expMonth, intent.expYear)
        }
    }

    private fun loadWalletAndCards() {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            val (walletResult, cardsResult) = coroutineScope {
                val walletDeferred = async { walletRepository.getWallet() }
                val cardsDeferred  = async { cardsRepository.listCards() }
                walletDeferred.await() to cardsDeferred.await()
            }
            _state.update { current ->
                var next = current.copy(isLoading = false)
                next = when (walletResult) {
                    is AuthResult.Success -> next.copy(
                        balance      = walletResult.data.balance,
                        transactions = walletResult.data.transactions.map { it.toWalletTransaction() }
                    )
                    is AuthResult.Error -> next.copy(errorMessage = walletResult.message)
                }
                next = when (cardsResult) {
                    is AuthResult.Success -> next.copy(savedCards = cardsResult.data.map { it.toSavedCard() })
                    is AuthResult.Error -> next.copy(errorMessage = next.errorMessage ?: cardsResult.message)
                }
                next
            }
        }
    }

    private fun handleTopupConfirmed(amount: Double) {
        if (_state.value.isTopupSubmitting) return
        _state.update { it.copy(isTopupSubmitting = true, errorMessage = null) }
        viewModelScope.launch {
            when (val result = walletRepository.topup(amount)) {
                is AuthResult.Success -> _state.update {
                    it.copy(
                        isTopupSubmitting = false,
                        balance           = result.data.balance,
                        transactions      = result.data.transactions.map { dto -> dto.toWalletTransaction() }
                    )
                }
                is AuthResult.Error -> _state.update {
                    it.copy(isTopupSubmitting = false, errorMessage = result.message)
                }
            }
        }
    }

    private fun handleAddCardConfirmed(brand: String, last4: String, expMonth: Int, expYear: Int) {
        if (_state.value.isAddCardSubmitting) return
        _state.update { it.copy(isAddCardSubmitting = true, errorMessage = null) }
        viewModelScope.launch {
            when (val result = cardsRepository.addCard(brand, last4, expMonth, expYear)) {
                is AuthResult.Success -> _state.update { current ->
                    current.copy(
                        isAddCardSubmitting = false,
                        savedCards          = current.savedCards + result.data.toSavedCard()
                    )
                }
                is AuthResult.Error -> _state.update {
                    it.copy(isAddCardSubmitting = false, errorMessage = result.message)
                }
            }
        }
    }

    private fun handleCardSelected(cardId: String) {
        viewModelScope.launch {
            when (val result = cardsRepository.setDefaultCard(cardId)) {
                is AuthResult.Success -> _state.update { current ->
                    current.copy(
                        savedCards = current.savedCards.map { card ->
                            card.copy(isDefault = card.id == result.data.id)
                        },
                        errorMessage = null
                    )
                }
                is AuthResult.Error -> _state.update { it.copy(errorMessage = result.message) }
            }
        }
    }

    private fun handleCardDeleteRequested(cardId: String) {
        viewModelScope.launch {
            when (val result = cardsRepository.deleteCard(cardId)) {
                is AuthResult.Success -> reloadCards()
                is AuthResult.Error   -> _state.update { it.copy(errorMessage = result.message) }
            }
        }
    }

    private suspend fun reloadCards() {
        when (val result = cardsRepository.listCards()) {
            is AuthResult.Success -> _state.update {
                it.copy(savedCards = result.data.map { dto -> dto.toSavedCard() }, errorMessage = null)
            }
            is AuthResult.Error -> _state.update { it.copy(errorMessage = result.message) }
        }
    }

    private fun WalletTransactionDto.toWalletTransaction(): WalletTransaction = WalletTransaction(
        id        = id,
        title     = description,
        dateLabel = formatDateLabel(createdAt),
        amount    = kotlin.math.abs(amount),
        isCredit  = amount >= 0
    )

    private fun CardResponseDto.toSavedCard(): SavedCard = SavedCard(
        id         = id,
        type       = when (brand) {
            "VISA"       -> CardType.VISA
            "MASTERCARD" -> CardType.MC
            else         -> CardType.OTHER
        },
        lastFour   = last4,
        expiryDate = "${expMonth.toString().padStart(2, '0')}/${(expYear % 100).toString().padStart(2, '0')}",
        isDefault  = isDefault
    )

    private fun formatDateLabel(iso: String): String = runCatching {
        DATE_LABEL_FORMATTER.format(Instant.parse(iso))
    }.getOrDefault(iso)

    private fun sendEffect(effect: WalletContract.Effect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}

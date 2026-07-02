package com.turkcell.rencar_pair.feature.wallet

object WalletContract {

    data class State(
        val balance: Double = 0.0,
        val savedCards: List<SavedCard> = emptyList(),
        val transactions: List<WalletTransaction> = emptyList(),
        val isLoading: Boolean = false
    ) {
        val formattedBalance: String get() {
            val cents    = Math.round(balance * 100)
            val intPart  = cents / 100
            val decPart  = cents % 100
            return "₺$intPart,${decPart.toString().padStart(2, '0')}"
        }
    }

    sealed interface Intent {
        data object AddBalance                          : Intent
        data object AddCard                            : Intent
        data class  CardSelected(val cardId: String)   : Intent
    }

    sealed interface Effect {
        data object ShowAddBalanceSheet : Effect
        data object ShowAddCardSheet    : Effect
    }
}

enum class CardType { VISA, MC }

data class SavedCard(
    val id: String,
    val type: CardType,
    val lastFour: String,
    val expiryDate: String,
    val isDefault: Boolean
)

data class WalletTransaction(
    val id: String,
    val title: String,
    val dateLabel: String,
    val amount: Double,
    val isCredit: Boolean
)

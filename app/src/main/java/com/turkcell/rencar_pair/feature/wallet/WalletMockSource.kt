package com.turkcell.rencar_pair.feature.wallet

object WalletMockSource {

    val balance = 340.0

    val savedCards = listOf(
        SavedCard(
            id         = "card_1",
            type       = CardType.VISA,
            lastFour   = "4291",
            expiryDate = "08/27",
            isDefault  = true
        ),
        SavedCard(
            id         = "card_2",
            type       = CardType.MC,
            lastFour   = "7740",
            expiryDate = "11/26",
            isDefault  = false
        )
    )

    val transactions = listOf(
        WalletTransaction(
            id        = "tx_1",
            title     = "Renault Clio kiralama",
            dateLabel = "Bugün, 14:22",
            amount    = 110.50,
            isCredit  = false
        ),
        WalletTransaction(
            id        = "tx_2",
            title     = "Bakiye yükleme",
            dateLabel = "Dün 09:10",
            amount    = 200.0,
            isCredit  = true
        )
    )
}

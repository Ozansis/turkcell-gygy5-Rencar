package com.turkcell.rencar_pair.feature.help

data class FaqEntry(
    val question: String,
    val answer: String
)

private val defaultFaqEntries = listOf(
    FaqEntry(
        question = "Aracı nasıl kiralarım?",
        answer   = "Haritadan uygun bir aracı seçip araç detayındaki \"Kilidi Aç\" butonuna " +
            "dokunarak kiralamanızı başlatabilirsiniz."
    ),
    FaqEntry(
        question = "Kiralama ücreti nasıl hesaplanır?",
        answer   = "Ücret, seçtiğiniz plana (dakikalık/saatlik/günlük) göre kullanım süresi ve " +
            "mesafeye göre hesaplanır; dökümü Kiralamalarım altındaki yolculuk detayında görebilirsiniz."
    ),
    FaqEntry(
        question = "Ehliyetimi nasıl onaylatırım?",
        answer   = "Profil ekranından ehliyetinizin ön/arka yüzünü ve bir selfie yükleyerek " +
            "doğrulama başvurusu yapabilirsiniz; onay süreci genellikle birkaç dakika sürer."
    ),
    FaqEntry(
        question = "Aracı nereye bırakmalıyım?",
        answer   = "Aracı, uygulamanın gösterdiği kiralama bölgesi içinde herhangi bir güvenli " +
            "park alanına bırakabilirsiniz."
    ),
    FaqEntry(
        question = "Kiralama sırasında bir sorunla karşılaşırsam ne yapmalıyım?",
        answer   = "Aşağıdaki destek hattından veya e-posta adresinden bize ulaşabilirsiniz."
    )
)

object HelpContract {

    data class State(
        val faqEntries: List<FaqEntry> = defaultFaqEntries,
        val supportEmail: String = "destek@rencar.com",
        val supportPhone: String = "+90 850 000 00 00"
    )

    sealed interface Intent {
        data object NavigateBack : Intent
    }

    sealed interface Effect {
        data object NavigateBack : Effect
    }
}

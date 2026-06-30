package com.turkcell.rencar_pair.feature.onboarding

import androidx.compose.ui.graphics.vector.ImageVector

object OnboardingContract {

    data class State(
        val pages: List<OnboardingPageData> = emptyList(),
        val currentPage: Int = 0
    ) {
        val isLastPage: Boolean get() = currentPage == pages.lastIndex
    }

    sealed interface Intent {
        data object PrimaryAction : Intent
        data object GoToLogin : Intent
    }

    sealed interface Effect {
        data object NavigateToHome : Effect
        data object NavigateToLogin : Effect
    }
}

data class OnboardingPageData(
    val icon: ImageVector,
    val title: String,
    val subtitle: String
)

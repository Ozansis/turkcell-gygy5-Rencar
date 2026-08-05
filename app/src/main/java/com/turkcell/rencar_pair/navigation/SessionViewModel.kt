package com.turkcell.rencar_pair.navigation

import androidx.lifecycle.ViewModel
import com.turkcell.rencar_pair.data.local.CurrentUserSession
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharedFlow

@HiltViewModel
class SessionViewModel @Inject constructor(
    currentUserSession: CurrentUserSession
) : ViewModel() {

    val sessionExpired: SharedFlow<Unit> = currentUserSession.sessionExpired
}

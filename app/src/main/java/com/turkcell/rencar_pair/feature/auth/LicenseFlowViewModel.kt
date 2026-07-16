package com.turkcell.rencar_pair.feature.auth

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.rencar_pair.data.repository.AuthResult
import com.turkcell.rencar_pair.data.repository.LicenseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class LicenseFlowViewModel @Inject constructor(
    private val licenseRepository: LicenseRepository
) : ViewModel() {

    data class State(
        val frontUri: Uri? = null,
        val backUri: Uri? = null,
        val selfieUri: Uri? = null,
        val isUploading: Boolean = false,
        val isUploaded: Boolean = false,
        val uploadError: String? = null
    ) {
        val isReadyToUpload: Boolean
            get() = frontUri != null && backUri != null && selfieUri != null
    }

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    fun setFrontUri(uri: Uri) {
        _state.update { it.copy(frontUri = uri) }
    }

    fun setBackUri(uri: Uri) {
        _state.update { it.copy(backUri = uri) }
    }

    fun setSelfieUri(uri: Uri) {
        _state.update { it.copy(selfieUri = uri) }
    }

    fun uploadIfReady() {
        val current = _state.value
        if (!current.isReadyToUpload || current.isUploading) return

        _state.update { it.copy(isUploading = true, uploadError = null) }
        viewModelScope.launch {
            when (
                val result = licenseRepository.upload(
                    current.frontUri!!,
                    current.backUri!!,
                    current.selfieUri!!
                )
            ) {
                is AuthResult.Success -> {
                    _state.update { it.copy(isUploading = false, isUploaded = true) }
                }
                is AuthResult.Error -> {
                    _state.update { it.copy(isUploading = false, uploadError = result.message) }
                }
            }
        }
    }
}

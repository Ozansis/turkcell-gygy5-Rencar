package com.turkcell.rencar_pair.ui.components

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.rencar_pair.data.repository.AuthResult
import com.turkcell.rencar_pair.data.repository.IyzicoRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = IyzicoPaymentWebViewViewModel.Factory::class)
class IyzicoPaymentWebViewViewModel @AssistedInject constructor(
    @Assisted("rentalId") private val rentalId: String,
    @Assisted private val price: Double,
    @Assisted("description") private val description: String?,
    private val iyzicoRepository: IyzicoRepository
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(
            @Assisted("rentalId") rentalId: String,
            price: Double,
            @Assisted("description") description: String?
        ): IyzicoPaymentWebViewViewModel
    }

    private var checkoutToken: String? = null
    private var hasResolved = false

    private val _state = MutableStateFlow(IyzicoPaymentWebViewContract.State())
    val state: StateFlow<IyzicoPaymentWebViewContract.State> = _state.asStateFlow()

    private val _effect = Channel<IyzicoPaymentWebViewContract.Effect>(Channel.BUFFERED)
    val effect: Flow<IyzicoPaymentWebViewContract.Effect> = _effect.receiveAsFlow()

    init {
        initializeCheckoutForm()
    }

    fun onIntent(intent: IyzicoPaymentWebViewContract.Intent) {
        when (intent) {
            is IyzicoPaymentWebViewContract.Intent.CallbackUrlReached -> handleCallbackUrlReached()
            IyzicoPaymentWebViewContract.Intent.Dismissed             -> handleDismissed()
        }
    }

    private fun initializeCheckoutForm() {
        _state.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            when (val result = iyzicoRepository.initializeCheckoutForm(rentalId, price, description)) {
                is AuthResult.Success -> {
                    val data = result.data
                    if (data.paymentPageUrl.isNullOrBlank()) {
                        _state.update { it.copy(isLoading = false, errorMessage = "Ödeme sayfası alınamadı.") }
                        resolve(IyzicoPaymentWebViewContract.Effect.ShowPaymentFailed("Ödeme sayfası alınamadı."))
                    } else {
                        checkoutToken = data.token
                        _state.update { it.copy(isLoading = false, paymentPageUrl = data.paymentPageUrl) }
                    }
                }
                is AuthResult.Error -> {
                    _state.update { it.copy(isLoading = false, errorMessage = result.message) }
                    resolve(IyzicoPaymentWebViewContract.Effect.ShowPaymentFailed(result.message))
                }
            }
        }
    }

    private fun handleCallbackUrlReached() {
        val token = checkoutToken ?: return
        _state.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            when (val result = iyzicoRepository.getCheckoutFormResult(token)) {
                is AuthResult.Success -> {
                    val payment = result.data
                    if (payment.paymentStatus == "SUCCESS") {
                        resolve(IyzicoPaymentWebViewContract.Effect.ShowPaymentSucceeded(payment.paymentId.orEmpty()))
                    } else {
                        resolve(IyzicoPaymentWebViewContract.Effect.ShowPaymentFailed(payment.paymentStatus ?: "Ödeme başarısız."))
                    }
                }
                is AuthResult.Error -> resolve(IyzicoPaymentWebViewContract.Effect.ShowPaymentFailed(result.message))
            }
        }
    }

    private fun handleDismissed() {
        resolve(IyzicoPaymentWebViewContract.Effect.ShowPaymentCancelled)
    }

    private fun resolve(effect: IyzicoPaymentWebViewContract.Effect) {
        if (hasResolved) return
        hasResolved = true
        sendEffect(effect)
    }

    private fun sendEffect(effect: IyzicoPaymentWebViewContract.Effect) {
        viewModelScope.launch { _effect.send(effect) }
    }
}

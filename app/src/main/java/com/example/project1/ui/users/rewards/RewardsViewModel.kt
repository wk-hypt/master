package com.example.project1.ui.users.rewards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project1.data.model.VoucherEntity
import com.example.project1.data.repository.OfferRepository
import com.example.project1.data.repository.UserRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class RewardsViewModel(
    private val offerRepository: OfferRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _studentId = MutableStateFlow("")

    fun setCurrentStudent(id: String) {
        _studentId.value = id
    }

    val currentPoints: StateFlow<Int> = _studentId
        .flatMapLatest { id ->
            if (id.isBlank()) flowOf(null) else userRepository.getUserStream(id)
        }
        .map { it?.totalPoints ?: 0 }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = 0
        )

    val available: StateFlow<List<VoucherEntity>> =
        offerRepository.getAvailableVouchersStream()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    val wallet: StateFlow<List<VoucherEntity>> = _studentId
        .flatMapLatest { id ->
            if (id.isBlank()) flowOf(emptyList())
            else offerRepository.getMyWalletVouchersStream(id)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun clearMessage() {
        _message.value = null
    }

    fun redeem(voucher: VoucherEntity) = viewModelScope.launch {
        val id = _studentId.value
        if (id.isBlank() || voucher.id == null) return@launch

        val user = userRepository.getUserById(id) ?: return@launch
        if (user.totalPoints < voucher.pointsCost) {
            _message.value = "Not enough points"
            return@launch
        }

        try {
            offerRepository.redeemVoucher(voucher.id, id)
            userRepository.updateUser(user.copy(totalPoints = user.totalPoints - voucher.pointsCost))
            _message.value = "Redeemed: ${voucher.title}"
        } catch (e: Exception) {
            _message.value = e.message ?: "Redeem failed"
        }
    }
}

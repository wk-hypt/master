package com.example.project1.ui.users.rewards

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.project1.data.model.VoucherEntity
import com.example.project1.data.model.VoucherRules
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

// viewmodel managing rewards, vouchers, and user point transactions
@OptIn(ExperimentalCoroutinesApi::class)
class RewardsViewModel(
    private val offerRepository: OfferRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _studentId = MutableStateFlow("")

    // set current active student id for fetching points and wallet
    fun setCurrentStudent(id: String) {
        _studentId.value = id
    }

    // stream tracking current student total points reactively
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

    // stream tracking all available vouchers in the reward store
    val available: StateFlow<List<VoucherEntity>> =
        offerRepository.getAvailableVouchersStream()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

    // stream tracking vouchers currently owned in the user wallet
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

    private val _isRedeeming = MutableStateFlow(false)
    val isRedeeming: StateFlow<Boolean> = _isRedeeming.asStateFlow()

    // clear feedback message state
    fun clearMessage() {
        _message.value = null
    }

    // handle voucher redemption logic with points check and limit rules
    fun redeem(voucher: VoucherEntity) = viewModelScope.launch {
        if (!_isRedeeming.compareAndSet(expect = false, update = true)) return@launch
        try {
            val id = _studentId.value
            if (id.isBlank() || voucher.id == null) return@launch

            val user = userRepository.getUserById(id) ?: return@launch
            if (user.totalPoints < voucher.pointsCost) {
                _message.value = "Not enough points"
                return@launch
            }

            val heldOfType = wallet.value.count { it.title == voucher.title }
            if (heldOfType >= VoucherRules.MAX_HELD_PER_TYPE) {
                _message.value = "You can hold up to ${VoucherRules.MAX_HELD_PER_TYPE} copies of this voucher at once."
                return@launch
            }

            offerRepository.redeemVoucher(voucher.id, id)
            userRepository.updateUser(user.copy(totalPoints = user.totalPoints - voucher.pointsCost))
            _message.value = "Redeemed: ${voucher.title}"
        } catch (e: Exception) {
            _message.value = e.message ?: "Redeem failed"
        } finally {
            _isRedeeming.value = false
        }
    }
}
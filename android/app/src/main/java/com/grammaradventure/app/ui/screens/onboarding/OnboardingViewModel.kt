package com.grammaradventure.app.ui.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.grammaradventure.app.data.local.datastore.UserPreferencesManager
import com.grammaradventure.app.data.repository.ProgressRepository
import com.grammaradventure.app.domain.model.BoardOption
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class OnboardingUiState(
    val isLoading: Boolean = true,
    val onboardingCompleted: Boolean = false,
    val existingName: String = "",
    val existingClass: Int = 9,
    val existingBoard: BoardOption = BoardOption.CONSOLIDATED
)

class OnboardingViewModel(
    private val preferencesManager: UserPreferencesManager,
    private val progressRepository: ProgressRepository
) : ViewModel() {

    val uiState: StateFlow<OnboardingUiState> = combine(
        preferencesManager.preferences,
        progressRepository.observeProfile()
    ) { prefs, profile ->
        val board = profile?.selectedBoard?.let { raw ->
            BoardOption.entries.find { it.name == raw }
        } ?: BoardOption.CONSOLIDATED

        OnboardingUiState(
            isLoading = false,
            onboardingCompleted = prefs.onboardingCompleted && profile != null,
            existingName = profile?.name.orEmpty(),
            existingClass = profile?.selectedClass ?: 9,
            existingBoard = board
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = OnboardingUiState()
    )

    fun completeProfileSetup(
        name: String,
        selectedClass: Int,
        board: BoardOption,
        onSaved: () -> Unit
    ) {
        viewModelScope.launch {
            progressRepository.saveProfileSetup(
                name = name,
                selectedClass = selectedClass,
                board = board
            )
            preferencesManager.setOnboardingCompleted(true)
            onSaved()
        }
    }

    class Factory(
        private val preferencesManager: UserPreferencesManager,
        private val progressRepository: ProgressRepository
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return OnboardingViewModel(preferencesManager, progressRepository) as T
        }
    }
}

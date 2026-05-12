package com.rakshakavach.ui.incidentlog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rakshakavach.data.local.IncidentEntity
import com.rakshakavach.domain.repository.IncidentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class IncidentSubmitState {
    object Idle : IncidentSubmitState()
    object Submitting : IncidentSubmitState()
    object Success : IncidentSubmitState()
    data class Error(val message: String) : IncidentSubmitState()
}

@HiltViewModel
class IncidentViewModel @Inject constructor(
    private val incidentRepository: IncidentRepository
) : ViewModel() {

    val incidents: StateFlow<List<IncidentEntity>> = incidentRepository.getAllIncidents()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _submitState = MutableStateFlow<IncidentSubmitState>(IncidentSubmitState.Idle)
    val submitState: StateFlow<IncidentSubmitState> = _submitState.asStateFlow()

    fun submitIncident(entity: IncidentEntity) {
        _submitState.value = IncidentSubmitState.Submitting
        viewModelScope.launch {
            try {
                incidentRepository.insertIncident(entity)
                _submitState.value = IncidentSubmitState.Success
            } catch (e: Exception) {
                _submitState.value = IncidentSubmitState.Error(e.message ?: "Failed to submit")
            }
        }
    }

    fun resetSubmitState() {
        _submitState.value = IncidentSubmitState.Idle
    }
}

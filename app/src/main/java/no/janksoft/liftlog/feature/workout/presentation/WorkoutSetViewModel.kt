package no.janksoft.liftlog.feature.workout.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.janksoft.liftlog.core.util.ApiState
import no.janksoft.liftlog.feature.workout.data.dto.LogSetRequest
import no.janksoft.liftlog.feature.workout.data.model.WorkoutSet
import no.janksoft.liftlog.feature.workout.repository.WorkoutSetRepository

class WorkoutSetViewModel : ViewModel() {

    private val TAG = "WorkoutSetViewModel"
    private val workoutSetRepository = WorkoutSetRepository()

    private val _logWorkoutSetState = MutableStateFlow<ApiState<WorkoutSet>>(ApiState.Idle)
    val logSetState: StateFlow<ApiState<WorkoutSet>> = _logWorkoutSetState.asStateFlow()

    fun resetLogWorkoutSetState() {
        _logWorkoutSetState.value = ApiState.Idle
    }

    fun logWorkoutSet(request: LogSetRequest) {
        viewModelScope.launch {
            _logWorkoutSetState.value = ApiState.Loading

            val result = workoutSetRepository.logSet(request)

            if (result is ApiState.Error) {
                logError(result, "Error logging set")
            }

            _logWorkoutSetState.value = result
        }
    }

    fun logError(error: ApiState.Error, errorHeader: String) {
        Log.e(TAG, "${errorHeader}: ${error.message}")
        error.errorResponse?.let {
            Log.e(TAG, "Backend error - Code: ${it.code}, Status: ${it.status}")
        }
    }
}
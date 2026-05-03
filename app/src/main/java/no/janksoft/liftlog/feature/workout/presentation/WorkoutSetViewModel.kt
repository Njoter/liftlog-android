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
import no.janksoft.liftlog.feature.workout.data.model.WorkoutSetResponse
import no.janksoft.liftlog.feature.workout.repository.WorkoutSetRepository

class WorkoutSetViewModel : ViewModel() {

    private val TAG = "WorkoutSetViewModel"
    private val workoutSetRepository = WorkoutSetRepository()

    private val _logWorkoutSetState = MutableStateFlow<ApiState<WorkoutSet>>(ApiState.Idle)
    val logWorkoutSetState: StateFlow<ApiState<WorkoutSet>> = _logWorkoutSetState.asStateFlow()

    private val _setsThisWeekState = MutableStateFlow<ApiState<WorkoutSetResponse>>(ApiState.Idle)
    val setsThisWeekState: StateFlow<ApiState<WorkoutSetResponse>> = _setsThisWeekState.asStateFlow()

    private val _setsThisMonthState = MutableStateFlow<ApiState<WorkoutSetResponse>>(ApiState.Idle)
    val setsThisMonthState: StateFlow<ApiState<WorkoutSetResponse>> = _setsThisMonthState.asStateFlow()

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

    fun fetchWorkoutSetsByExerciseThisWeek(exerciseId: Long) {
        viewModelScope.launch {
            _setsThisWeekState.value = ApiState.Loading

            val result = workoutSetRepository.fetchWorkoutSetByExerciseThisWeek(exerciseId)

            if (result is ApiState.Error) {
                logError(result, "Error fetching workout sets for this week")
            }

            _setsThisWeekState.value = result
        }
    }

    fun fetchWorkoutSetsByExerciseThisMonth(exerciseId: Long) {
        viewModelScope.launch {
            _setsThisMonthState.value = ApiState.Loading

            val result = workoutSetRepository.fetchWorkoutSetByExerciseThisMonth(exerciseId)

            if (result is ApiState.Error) {
                logError(result, "Error fetching workout sets for this month")
            }

            _setsThisMonthState.value = result
        }
    }

    fun logError(error: ApiState.Error, errorHeader: String) {
        Log.e(TAG, "${errorHeader}: ${error.message}")
        error.errorResponse?.let {
            Log.e(TAG, "Backend error - Code: ${it.code}, Status: ${it.status}")
        }
    }
}
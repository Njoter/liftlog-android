package no.janksoft.liftlog.feature.exercise.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.janksoft.liftlog.core.util.ApiState
import no.janksoft.liftlog.feature.exercise.data.model.Exercise
import no.janksoft.liftlog.feature.exercise.data.model.ExerciseSummary
import no.janksoft.liftlog.feature.exercise.repository.ExerciseRepository

class ExerciseListViewModel : ViewModel() {

    private val TAG = "ExerciseListViewModel"

    // Search field in exercise list
    private val _searchTerm = MutableStateFlow("")
    val searchTerm: StateFlow<String> = _searchTerm.asStateFlow()

    // The list of all exercises
    private val _exerciseSummaries = MutableStateFlow<ApiState<List<ExerciseSummary>>>(ApiState.Loading)
    val exerciseSummaries: StateFlow<ApiState<List<ExerciseSummary>>> = _exerciseSummaries.asStateFlow()

    // When selecting exercise to open details screen
    private val _selectedExercise = MutableStateFlow<ApiState<Exercise>>(ApiState.Idle)
    val selectedExercise: StateFlow<ApiState<Exercise>> = _selectedExercise.asStateFlow()

    // Delete exercise state
    private val _deleteState = MutableStateFlow<ApiState<Unit>?>(ApiState.Idle)
    val deleteState: StateFlow<ApiState<Unit>?> = _deleteState.asStateFlow()

    private val exerciseRepository = ExerciseRepository()

    fun updateSearchTerm(newText: String) {
        _searchTerm.value = newText
    }

    fun clearSearchTerm() {
        _searchTerm.value = ""
    }

    fun clearDeleteState() {
        _deleteState.value = null
    }

    fun fetchAllExercises(userId: Long) {
        viewModelScope.launch {
            _exerciseSummaries.value = ApiState.Loading

            val result = exerciseRepository.fetchAllExercises(userId)

            if (result is ApiState.Error) {
                logError(result, "Error fetching exercises")
            }

            _exerciseSummaries.value = result
        }
    }

    fun fetchExerciseById(id: Long) {
        viewModelScope.launch {
            _selectedExercise.value = ApiState.Loading

            val result = exerciseRepository.fetchExerciseById(id)

            if (result is ApiState.Error) {
                logError(result, "Error fetching exercise")
            }

            _selectedExercise.value = result
        }
    }

    fun deleteExercise(id: Long) {
        viewModelScope.launch {
            _deleteState.value = ApiState.Loading

            val result = exerciseRepository.deleteExercise(id)

            if (result is ApiState.Error) {
                logError(result, "Error deleting exercise")
            }

            _deleteState.value = result
        }
    }

    fun logError(error: ApiState.Error, errorHeader: String) {
        Log.e(TAG, "${errorHeader}: ${error.message}")
        error.errorResponse?.let {
            Log.e(TAG, "Backend error - Code: ${it.code}, Status: ${it.status}")
        }
    }

    fun refreshExercises(userId: Long) {
        fetchAllExercises(userId)
    }
}
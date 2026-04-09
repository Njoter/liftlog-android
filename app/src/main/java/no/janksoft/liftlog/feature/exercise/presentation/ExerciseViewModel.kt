package no.janksoft.liftlog.feature.exercise.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.janksoft.liftlog.core.util.ApiState
import no.janksoft.liftlog.feature.exercise.data.dto.CreateExerciseRequest
import no.janksoft.liftlog.feature.exercise.data.dto.UpdateExerciseRequest
import no.janksoft.liftlog.feature.exercise.data.model.Exercise
import no.janksoft.liftlog.feature.exercise.data.model.ExerciseSummary
import no.janksoft.liftlog.feature.exercise.repository.ExerciseRepository

class ExerciseViewModel : ViewModel() {

    private val TAG = "ExerciseViewModel"

    private val _searchTerm = MutableStateFlow("")
    val searchTerm: StateFlow<String> = _searchTerm.asStateFlow()

    private val _exerciseSummaries = MutableStateFlow<ApiState<List<ExerciseSummary>>>(ApiState.Loading)
    val exerciseSummaries: StateFlow<ApiState<List<ExerciseSummary>>> = _exerciseSummaries.asStateFlow()

    private val _selectedExerciseId = MutableStateFlow<Long?>(null)
    val selectedExerciseId: StateFlow<Long?> = _selectedExerciseId.asStateFlow()

    private val _selectedExercise = MutableStateFlow<ApiState<Exercise>>(ApiState.Loading)
    val selectedExercise: StateFlow<ApiState<Exercise>> = _selectedExercise.asStateFlow()

    private val _deleteState = MutableStateFlow<ApiState<Unit>?>(ApiState.Loading)
    val deleteState: StateFlow<ApiState<Unit>?> = _deleteState.asStateFlow()

    private val _createState = MutableStateFlow<ApiState<Exercise>>(ApiState.Loading)
    val createState: StateFlow<ApiState<Exercise>> = _createState.asStateFlow()

    private val _updateState = MutableStateFlow<ApiState<Exercise>>(ApiState.Loading)
    val updateState: StateFlow<ApiState<Exercise>> = _updateState.asStateFlow()

    private val exerciseRepository = ExerciseRepository()

    fun updateSearchTerm(newText: String) {
        _searchTerm.value = newText
    }

    fun clearText() {
        _searchTerm.value = ""
    }

    fun selectExercise(id: Long) {
        _selectedExerciseId.value = id
    }

    fun clearSelectedExercise() {
        _selectedExerciseId.value = null
    }

    fun clearDeleteState() {
        _deleteState.value = null
    }

    fun fetchAllExercises() {
        viewModelScope.launch {
            _exerciseSummaries.value = ApiState.Loading

            val result = exerciseRepository.fetchAllExercises()

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

    fun createExercise(request: CreateExerciseRequest) {
        viewModelScope.launch {
            _createState.value = ApiState.Loading

            val result = exerciseRepository.createExercise(request)

            if (result is ApiState.Error) {
                logError(result, "Error creating exercise")
            }

            _createState.value = result
        }
    }

    fun updateExercise(request: UpdateExerciseRequest) {
        viewModelScope.launch {
            _updateState.value = ApiState.Loading

            val result = exerciseRepository.updateExercise(request)

            if (result is ApiState.Error) {
                logError(result, "Error updating exercise")
            }

            _updateState.value = result
        }
    }

    fun logError(error: ApiState.Error, errorHeader: String) {
        Log.e(TAG, "${errorHeader}: ${error.message}")
        error.errorResponse?.let {
            Log.e(TAG, "Backend error - Code: ${it.code}, Status: ${it.status}")
        }
    }

    fun refreshExercises() {
        fetchAllExercises()
    }
}
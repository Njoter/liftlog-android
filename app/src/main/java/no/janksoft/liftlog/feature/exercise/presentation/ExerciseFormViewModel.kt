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
import no.janksoft.liftlog.feature.exercise.repository.ExerciseRepository
import kotlin.text.isEmpty
import kotlin.text.toInt

class ExerciseFormViewModel : ViewModel() {

    private val TAG = "ExerciseFormViewModel"

    // Form values for creating or updating exercise
    private val _id = MutableStateFlow<Long?>(null)
    val id: StateFlow<Long?> = _id.asStateFlow()
    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name.asStateFlow()
    private val _weightKg = MutableStateFlow("25")
    val weightKg: StateFlow<String> = _weightKg.asStateFlow()
    private val _reps = MutableStateFlow("10")
    val reps: StateFlow<String> = _reps.asStateFlow()
    private val _sets = MutableStateFlow("3")
    val sets: StateFlow<String> = _sets.asStateFlow()

    // Form validation
    private val _nameError = MutableStateFlow(false)
    val nameError: StateFlow<Boolean> = _nameError.asStateFlow()
    private val _weightError = MutableStateFlow(false)
    val weightError: StateFlow<Boolean> = _weightError.asStateFlow()
    private val _repsError = MutableStateFlow(false)
    val repsError: StateFlow<Boolean> = _repsError.asStateFlow()
    private val _setsError = MutableStateFlow(false)
    val setsError: StateFlow<Boolean> = _setsError.asStateFlow()
    private val _isFormValid = MutableStateFlow(false)
    val isFormValid: StateFlow<Boolean> = _isFormValid.asStateFlow()

    // Create exercise state
    private val _createState = MutableStateFlow<ApiState<Exercise>>(ApiState.Idle)
    val createState: StateFlow<ApiState<Exercise>> = _createState.asStateFlow()

    // Update exercise state
    private val _updateState = MutableStateFlow<ApiState<Exercise>>(ApiState.Idle)
    val updateState: StateFlow<ApiState<Exercise>> = _updateState.asStateFlow()

    // When selecting exercise to update
    private val _selectedExercise = MutableStateFlow<ApiState<Exercise>>(ApiState.Idle)
    val selectedExercise: StateFlow<ApiState<Exercise>> = _selectedExercise.asStateFlow()

    private val exerciseRepository = ExerciseRepository()

    fun updateName(newText: String) {
        _name.value = newText
        _nameError.value = newText.isEmpty()
        validateForm()
    }

    fun updateWeightKg(newText: String) {
        _weightKg.value = newText
        _weightError.value = newText.isEmpty() || newText.toDouble() !in 0.0..999.0
        validateForm()
    }

    fun updateReps(newText: String) {
        _reps.value = newText
        _repsError.value = newText.isEmpty() || newText.toInt() !in 1..999
        validateForm()
    }

    fun updateSets(newText: String) {
        _sets.value = newText
        _setsError.value = newText.isEmpty() || newText.toInt() !in 1..999
        validateForm()
    }

    fun validateForm() {
        _isFormValid.value = !_nameError.value &&
                !_weightError.value &&
                !_repsError.value &&
                !_setsError.value
    }

    fun fillFormWithSelectedExercise(exercise: Exercise) {
        _id.value = exercise.id
        _name.value = exercise.name
        _weightKg.value = exercise.weightKg.toString()
        _reps.value = exercise.reps.toString()
        _sets.value = exercise.sets.toString()
    }

    fun fetchExerciseById(id: Long) {
        viewModelScope.launch {
            _selectedExercise.value = ApiState.Loading

            val result = exerciseRepository.fetchExerciseById(id)

            if (result is ApiState.Error) {
                logError(result, "Error fetching exercise")
            }
            if (result is ApiState.Success) {
                fillFormWithSelectedExercise(result.data)
            }

            _selectedExercise.value = result
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
}
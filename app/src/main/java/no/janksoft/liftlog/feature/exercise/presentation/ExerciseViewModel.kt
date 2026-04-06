package no.janksoft.liftlog.feature.exercise.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import no.janksoft.liftlog.core.util.ApiState
import no.janksoft.liftlog.feature.exercise.data.model.ExerciseSummary
import no.janksoft.liftlog.feature.exercise.repository.ExerciseRepository

class ExerciseViewModel : ViewModel() {

    private val TAG = "ExerciseViewModel"

    // State for the search field
    private var _searchTerm = MutableStateFlow("")
    val searchTerm: StateFlow<String> = _searchTerm.asStateFlow()

    private val _exerciseSummaries = MutableStateFlow<ApiState<List<ExerciseSummary>>>(ApiState.Loading)
    val exerciseSummaries: StateFlow<ApiState<List<ExerciseSummary>>> = _exerciseSummaries.asStateFlow()

    private val exerciseRepository = ExerciseRepository()

    fun updateSearchTerm(newText: String) {
        _searchTerm.value = newText
    }

    fun clearText() {
        _searchTerm.value = ""
    }

    fun fetchAllExercises() {
        viewModelScope.launch {
            _exerciseSummaries.value = ApiState.Loading

            val result = exerciseRepository.fetchAllExercises()

            if (result is ApiState.Error) {
                Log.e(TAG, "Error fetching exercises: ${result.message}")
                result.errorResponse?.let {
                    Log.e(TAG, "Backend error - Code: ${it.code}, Status: ${it.status}")
                }
            }

            // Update StateFlow with result
            _exerciseSummaries.value = result
        }
    }

    fun refreshExercises() {
        fetchAllExercises()
    }
}
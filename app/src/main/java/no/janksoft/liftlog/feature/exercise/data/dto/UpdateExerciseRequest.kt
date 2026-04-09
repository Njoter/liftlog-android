package no.janksoft.liftlog.feature.exercise.data.dto

data class UpdateExerciseRequest(
    val id: Long,
    val name: String,
    val weightKg: Double,
    val reps: Int,
    val sets: Int
)

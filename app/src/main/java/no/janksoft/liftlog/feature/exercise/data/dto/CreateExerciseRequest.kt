package no.janksoft.liftlog.feature.exercise.data.dto

data class CreateExerciseRequest(
    val userId: Long,
    val name: String,
    val weightKg: Double,
    val reps: Int,
    val sets: Int
)

package no.janksoft.liftlog.feature.exercise.data.model

data class Exercise(
    val id: Long,
    val name: String,
    val weightKg: Double,
    val reps: Int,
    val sets: Int
)

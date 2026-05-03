package no.janksoft.liftlog.feature.workout.data.model

data class WorkoutSet(
    val id: Long,
    val createdAt: String,
    val weightKg: Double,
    val reps: Int
)

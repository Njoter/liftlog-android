package no.janksoft.liftlog.feature.workout.data.dto

data class LogSetRequest(
    val userId: Long,
    val exerciseId: Long,
    val weightKg: Double,
    val reps: Int
)

package no.janksoft.liftlog.feature.workout.data.model

data class WorkoutSetResponse(
    val exerciseId: Long,
    val exerciseName: String,
    val workoutSets: List<WorkoutSet>,
    val totalReps: Int,
    val totalSets: Int,
    val lastRecorded: String
)

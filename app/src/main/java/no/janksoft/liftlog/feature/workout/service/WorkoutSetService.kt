package no.janksoft.liftlog.feature.workout.service

import no.janksoft.liftlog.feature.workout.data.dto.LogSetRequest
import no.janksoft.liftlog.feature.workout.data.model.WorkoutSet
import no.janksoft.liftlog.feature.workout.data.model.WorkoutSetResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface WorkoutSetService {

    @POST("api/v1/workout")
    suspend fun logSet(@Body request: LogSetRequest): Response<WorkoutSet>

    @GET("api/v1/workout/by-exercise/{exerciseId}/week")
    suspend fun fetchWorkoutSetsByExerciseThisWeek(@Path("exerciseId") exerciseId: Long)
    : Response<WorkoutSetResponse>

    @GET("api/v1/workout/by-exercise/{exerciseId}/month")
    suspend fun fetchWorkoutSetsByExerciseThisMonth(@Path("exerciseId") exerciseId: Long)
    : Response<WorkoutSetResponse>
}
package no.janksoft.liftlog.feature.workout.service

import no.janksoft.liftlog.feature.workout.data.dto.LogSetRequest
import no.janksoft.liftlog.feature.workout.data.model.WorkoutSet
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface WorkoutSetService {

    @POST("api/v1/workout")
    suspend fun logSet(@Body request: LogSetRequest): Response<WorkoutSet>
}
package no.janksoft.liftlog.feature.exercise.service

import no.janksoft.liftlog.feature.exercise.data.dto.CreateExerciseRequest
import no.janksoft.liftlog.feature.exercise.data.dto.UpdateExerciseRequest
import no.janksoft.liftlog.feature.exercise.data.model.Exercise
import no.janksoft.liftlog.feature.exercise.data.model.ExerciseSummary
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ExerciseService {
    @GET("api/v1/exercises")
    suspend fun fetchAllExercises(): Response<List<ExerciseSummary>>

    @GET("api/v1/exercises/{id}")
    suspend fun fetchExercise(@Path("id") id: Long): Response<Exercise>

    @POST("api/v1/exercises")
    suspend fun createExercise(@Body request: CreateExerciseRequest): Response<Exercise>

    @PUT("api/v1/exercises")
    suspend fun updateExercise(@Body request: UpdateExerciseRequest): Response<Exercise>

    @DELETE("api/v1/exercises/{id}")
    suspend fun deleteExercise(@Path("id") id: Long): Response<Unit>
}
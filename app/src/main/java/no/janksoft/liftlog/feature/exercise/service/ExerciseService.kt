package no.janksoft.liftlog.feature.exercise.service

import no.janksoft.liftlog.feature.exercise.data.model.Exercise
import no.janksoft.liftlog.feature.exercise.data.model.ExerciseSummary
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ExerciseService {
    @GET("api/v1/exercises")
    suspend fun fetchAllExercises(): Response<List<ExerciseSummary>>

    @GET("api/v1/exercises/{id}")
    suspend fun fetchExercise(@Path("id") id: Long): Response<Exercise>
}
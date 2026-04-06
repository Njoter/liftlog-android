package no.janksoft.liftlog.feature.exercise.service

import no.janksoft.liftlog.feature.exercise.data.model.ExerciseSummary
import retrofit2.Response
import retrofit2.http.GET

interface ExerciseService {
    @GET("api/v1/exercises")
    suspend fun fetchAllExercises(): Response<List<ExerciseSummary>>
}
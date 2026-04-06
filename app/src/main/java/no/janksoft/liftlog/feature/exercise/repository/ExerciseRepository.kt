package no.janksoft.liftlog.feature.exercise.repository

import com.google.gson.Gson
import no.janksoft.liftlog.core.network.ErrorResponse
import no.janksoft.liftlog.core.network.RetrofitClient
import no.janksoft.liftlog.core.util.ApiState
import no.janksoft.liftlog.feature.exercise.data.model.ExerciseSummary
import okio.IOException
import retrofit2.HttpException

class ExerciseRepository {

    private val exerciseService = RetrofitClient.exerciseService
    private val gson = Gson()

    suspend fun fetchAllExercises(): ApiState<List<ExerciseSummary>> {
        return try {
            val response = exerciseService.fetchAllExercises()
            
            if (response.isSuccessful) {
                val exercises = response.body()
                if (exercises != null) {
                    ApiState.Success(exercises)
                } else {
                    ApiState.Error(
                        message = "Received empty response from server",
                        statusCode = response.code()
                    )
                }
            } else {
                val errorResponse = parseErrorResponse(response.errorBody()?.string())
                ApiState.Error(
                    message = errorResponse?.message ?: "Server error: ${response.message()}",
                    errorResponse = errorResponse,
                    statusCode = response.code()
                )
            }
        } catch (e: IOException) {
            ApiState.Error(
                message = "Network error: ${e.message}",
                errorResponse = null,
                statusCode = null
            )
        } catch (e: HttpException) {
            ApiState.Error(
                message = "HTTP error: ${e.message}",
                errorResponse = null,
                statusCode = e.code()
            )
        } catch (e: Exception) {
            ApiState.Error(
                message = "Unexpected error: ${e.message}",
                errorResponse = null,
                statusCode = null
            )
        }
    }

    private fun parseErrorResponse(errorBody: String?): ErrorResponse? {
        return try {
            if (errorBody.isNullOrBlank()) {
                null
            } else {
                gson.fromJson(errorBody, ErrorResponse::class.java)
            }
        } catch (e: Exception) {
            null
        }
    }
}
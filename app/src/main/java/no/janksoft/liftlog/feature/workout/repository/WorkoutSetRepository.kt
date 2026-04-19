package no.janksoft.liftlog.feature.workout.repository

import com.google.gson.Gson
import no.janksoft.liftlog.core.network.ErrorResponse
import no.janksoft.liftlog.core.network.RetrofitClient
import no.janksoft.liftlog.core.util.ApiState
import no.janksoft.liftlog.feature.workout.data.dto.LogSetRequest
import no.janksoft.liftlog.feature.workout.data.model.WorkoutSet
import okio.IOException
import retrofit2.HttpException

class WorkoutSetRepository {

    private val workoutSetService = RetrofitClient.workoutSetService
    private val gson = Gson()

    suspend fun logSet(request: LogSetRequest): ApiState<WorkoutSet> {
        return try {
            val response = workoutSetService.logSet(request)

            if (response.isSuccessful) {
                val workoutSet = response.body()
                if (workoutSet != null) {
                    ApiState.Success(workoutSet)
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
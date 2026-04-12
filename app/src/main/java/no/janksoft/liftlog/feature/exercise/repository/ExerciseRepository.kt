package no.janksoft.liftlog.feature.exercise.repository

import com.google.gson.Gson
import no.janksoft.liftlog.core.network.ErrorResponse
import no.janksoft.liftlog.core.network.RetrofitClient
import no.janksoft.liftlog.core.util.ApiState
import no.janksoft.liftlog.feature.exercise.data.dto.CreateExerciseRequest
import no.janksoft.liftlog.feature.exercise.data.dto.UpdateExerciseRequest
import no.janksoft.liftlog.feature.exercise.data.model.Exercise
import no.janksoft.liftlog.feature.exercise.data.model.ExerciseSummary
import okio.IOException
import retrofit2.HttpException

class ExerciseRepository {

    private val exerciseService = RetrofitClient.exerciseService
    private val gson = Gson()

    suspend fun fetchAllExercises(userId: Long): ApiState<List<ExerciseSummary>> {
        return try {
            val response = exerciseService.fetchAllExercises(userId)
            
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

    suspend fun fetchExerciseById(id: Long?): ApiState<Exercise> {
        return try {
            if (id == null) {
                return ApiState.Error(
                    message = "No exercise selected",
                    statusCode = 400
                )
            }

            val response = exerciseService.fetchExercise(id)

            if (response.isSuccessful) {
                val exercise = response.body()
                if (exercise != null) {
                    ApiState.Success(exercise)
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

    suspend fun deleteExercise(id: Long?): ApiState<Unit> {
        return try {
            if (id == null) {
                return ApiState.Error(
                    message = "No exercise selected",
                    statusCode = 400
                )
            }

            val response = exerciseService.deleteExercise(id)

            if (response.isSuccessful) {
                ApiState.Success(Unit)
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

    suspend fun createExercise(request: CreateExerciseRequest): ApiState<Exercise> {
        return try {
            val response = exerciseService.createExercise(request)

            if (response.isSuccessful) {
                val exercise = response.body()
                if (exercise != null) {
                    ApiState.Success(exercise)
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

    suspend fun updateExercise(request: UpdateExerciseRequest): ApiState<Exercise> {
        return try {
            val response = exerciseService.updateExercise(request)

            if (response.isSuccessful) {
                val exercise = response.body()
                if (exercise != null) {
                    ApiState.Success(exercise)
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
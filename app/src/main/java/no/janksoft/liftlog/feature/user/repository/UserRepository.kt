package no.janksoft.liftlog.feature.user.repository

import com.google.gson.Gson
import no.janksoft.liftlog.core.network.ErrorResponse
import no.janksoft.liftlog.core.network.RetrofitClient
import no.janksoft.liftlog.core.util.ApiState
import no.janksoft.liftlog.feature.user.data.dto.LoginRequest
import no.janksoft.liftlog.feature.user.data.model.User
import okio.IOException
import retrofit2.HttpException

class UserRepository {

    private val userService = RetrofitClient.userService
    private val gson = Gson()

    suspend fun login(request: LoginRequest): ApiState<User> {
        return try {
            val response = userService.login(request)

            if (response.isSuccessful) {
                val user = response.body()
                if (user != null) {
                    ApiState.Success(user)
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
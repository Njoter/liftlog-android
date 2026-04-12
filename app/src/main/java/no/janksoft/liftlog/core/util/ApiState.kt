package no.janksoft.liftlog.core.util

import no.janksoft.liftlog.core.network.ErrorResponse

sealed class ApiState<out T> {
    object Loading : ApiState<Nothing>()
    object Idle : ApiState<Nothing>()
    data class Success<T>(val data: T) : ApiState<T>()

    data class Error(
        val message: String,
        val errorResponse: ErrorResponse? = null,
        val statusCode: Int? = null
    ) : ApiState<Nothing>()
}
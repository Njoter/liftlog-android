package no.janksoft.liftlog.feature.user.service

import no.janksoft.liftlog.feature.user.data.dto.LoginRequest
import no.janksoft.liftlog.feature.user.data.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserService {

    @POST("api/v1/users/login")
    suspend fun login(@Body request: LoginRequest): Response<User>
}
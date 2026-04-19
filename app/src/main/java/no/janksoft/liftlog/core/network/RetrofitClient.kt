package no.janksoft.liftlog.core.network

import no.janksoft.liftlog.BuildConfig
import no.janksoft.liftlog.feature.exercise.service.ExerciseService
import no.janksoft.liftlog.feature.user.service.UserService
import no.janksoft.liftlog.feature.workout.service.WorkoutSetService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {

    private const val BASE_URL = BuildConfig.BASE_URL

    // Logging interceptor to see network calls in Logcat
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // OkHttp client with timeouts and logging
    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val exerciseService: ExerciseService by lazy {
        retrofit.create(ExerciseService::class.java)
    }

    val userService: UserService by lazy {
        retrofit.create(UserService::class.java)
    }

    val workoutSetService: WorkoutSetService by lazy {
        retrofit.create(WorkoutSetService::class.java)
    }
}
package no.janksoft.liftlog

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import no.janksoft.liftlog.feature.exercise.presentation.CreateExerciseScreen
import no.janksoft.liftlog.feature.exercise.presentation.ExerciseDetailsScreen
import no.janksoft.liftlog.feature.exercise.presentation.ExerciseListScreen
import no.janksoft.liftlog.feature.exercise.presentation.UpdateExerciseScreen
import no.janksoft.liftlog.feature.user.presentation.LoginScreen

@Composable
fun LiftLogApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        // Login screen
        composable("login") {
            LoginScreen(
                onValidated = { userId -> navController.navigate("exercise_list/$userId") }
            )
        }

        // List screen
        composable(
            "exercise_list/{userId}",
            arguments = listOf(
                navArgument("userId") {
                    type = NavType.LongType
                    nullable = false
                }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getLong("userId")
                ?: error("userId is required for exercise_list screen")
            ExerciseListScreen(
                userId = userId,
                onNavigateToDetail = { exerciseId ->
                    navController.navigate("exercise_detail/$exerciseId")
                },
                onNavigateToCreate = {
                    navController.navigate("create_exercise/$userId")
                }
            )
        }

        // Detail screen
        composable(
            "exercise_detail/{exerciseId}",
            arguments = listOf(
                navArgument("exerciseId") {
                    type = NavType.LongType
                    nullable = false
                }
            )
        ) { backStackEntry ->
            val exerciseId = backStackEntry.arguments?.getLong("exerciseId")
                ?: error("Exercise ID is required to view exercise details")
            ExerciseDetailsScreen(
                exerciseId = exerciseId,
                navController = navController
            )
        }

        // Update screen
        composable(
            "update_exercise/{exerciseId}",
            arguments = listOf(
                navArgument("exerciseId") {
                    type = NavType.LongType
                    nullable = false
                }
            )
        ) { backStackEntry ->
            val exerciseId = backStackEntry.arguments?.getLong("exerciseId")
                ?: error("Exercise ID is required to update exercise")
            UpdateExerciseScreen(
                exerciseId = exerciseId,
                navController = navController
            )

        }

        // Create screen
        composable(
            "create_exercise/{userId}",
            arguments = listOf(
                navArgument("userId") {
                    type = NavType.LongType
                    nullable = false
                }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getLong("userId")
                ?: error("User ID is required to create exercise")
            CreateExerciseScreen(
                userId = userId,
                navController = navController
            )
        }
    }
}
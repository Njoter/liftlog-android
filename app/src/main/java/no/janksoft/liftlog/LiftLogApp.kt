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

@Composable
fun LiftLogApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "exercise_list"
    ) {
        // List screen
        composable("exercise_list") {
            ExerciseListScreen(
                onNavigateToDetail = { exerciseId ->
                    navController.navigate("exercise_detail/$exerciseId")
                },
                onNavigateToCreate = {
                    navController.navigate("create_exercise")
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
            val exerciseId = backStackEntry.arguments?.getLong("exerciseId") ?: 0L
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
            val exerciseId = backStackEntry.arguments?.getLong("exerciseId") ?: 0L
            UpdateExerciseScreen(
                exerciseId = exerciseId,
                navController = navController
            )

        }

        // Create screen
        composable("create_exercise") {
            CreateExerciseScreen(
                navController
            )
        }
    }
}
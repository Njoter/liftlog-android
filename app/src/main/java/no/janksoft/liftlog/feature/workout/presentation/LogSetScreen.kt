package no.janksoft.liftlog.feature.workout.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import no.janksoft.liftlog.core.ui.ErrorDisplay
import no.janksoft.liftlog.core.ui.ErrorDisplayWithRetry
import no.janksoft.liftlog.core.ui.LiftLogLoadingIndicator
import no.janksoft.liftlog.core.ui.LiftLogTopBar
import no.janksoft.liftlog.core.util.ApiState
import no.janksoft.liftlog.feature.exercise.presentation.ExerciseListViewModel
import no.janksoft.liftlog.feature.workout.data.dto.LogSetRequest

@Composable
fun LogSetScreen(
    userId: Long,
    exerciseId: Long,
    navController: NavController,
    exerciseListViewModel: ExerciseListViewModel = viewModel(),
    workoutSetViewModel: WorkoutSetViewModel = viewModel()
) {
    val selectedExercise by exerciseListViewModel.selectedExercise.collectAsStateWithLifecycle()
    val logWorkoutSetState by workoutSetViewModel.logSetState.collectAsStateWithLifecycle()

    LaunchedEffect(exerciseId) {
        exerciseListViewModel.fetchExerciseById(exerciseId)
    }

    Scaffold(
        topBar = {
            LiftLogTopBar(
                title ="Log Set",
                onNavigateBack = { navController.navigateUp() },
                showBackButton = true,
            )
        },
    ) { paddingValues ->
        when {
            logWorkoutSetState is ApiState.Success -> {
                navController.navigateUp()
            }

            logWorkoutSetState is ApiState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    ErrorDisplay(
                        errorState = logWorkoutSetState as ApiState.Error,
                        headerMessage = "Error logging set",
                        onCancel = { workoutSetViewModel.resetLogWorkoutSetState() }
                    )
                }
            }

            selectedExercise is ApiState.Success -> {
                val exercise = (selectedExercise as ApiState.Success).data
                LogSetContent(
                    exerciseName = exercise.name,
                    weight = exercise.weightKg,
                    reps = exercise.reps,
                    onSave = { weight, reps ->
                        workoutSetViewModel.logWorkoutSet(
                            request = LogSetRequest(
                                userId = userId,
                                exerciseId = exerciseId,
                                weightKg = weight,
                                reps = reps
                            )
                        )
                    },
                    onCancel = { navController.navigateUp() },
                    paddingValues = paddingValues
                )
            }

            selectedExercise is ApiState.Error -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    ErrorDisplayWithRetry(
                        errorState = selectedExercise as ApiState.Error,
                        headerMessage = "Error fetching exercise",
                        onRetry = { exerciseListViewModel.fetchExerciseById(exerciseId) },
                        onCancel = { navController.navigateUp() }
                    )
                }
            }
            selectedExercise is ApiState.Idle -> {}
            selectedExercise is ApiState.Loading -> {
                LiftLogLoadingIndicator("Loading exercise")
            }
        }
    }
}

@Composable
fun LogSetContent(
    exerciseName: String,
    weight: Double,
    reps: Int,
    onSave: (weight: Double, reps: Int) -> Unit,
    onCancel: () -> Unit,
    paddingValues: PaddingValues
) {
    var weight by remember { mutableStateOf(weight.toString()) }
    var reps by remember { mutableStateOf(reps.toString()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            text = "Log Your Set",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = exerciseName,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Weight field
        Text(
            text = "How many kg did you lift?",
            fontSize = 14.sp,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = weight,
            onValueChange = { weight = it },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Reps field
        Text(
            text = "How many reps did you do?",
            fontSize = 14.sp,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = reps,
            onValueChange = { reps = it },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Save button
        Button(
            onClick = {
                weight.toDoubleOrNull()?.let { w ->
                    reps.toIntOrNull()?.let { r ->
                        onSave(w, r)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            //enabled = isFormValid
        ) {
            Text("Save Set")
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Cancel button
        OutlinedButton(
            onClick = onCancel,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancel")
        }
    }
}
package no.janksoft.liftlog.feature.exercise.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import no.janksoft.liftlog.core.ui.ErrorDisplayWithRetry
import no.janksoft.liftlog.core.ui.LiftLogLoadingIndicator
import no.janksoft.liftlog.core.ui.LiftLogTopBar
import no.janksoft.liftlog.core.util.ApiState
import no.janksoft.liftlog.feature.exercise.data.model.Exercise
import no.janksoft.liftlog.feature.workout.data.model.WorkoutSetResponse
import no.janksoft.liftlog.feature.workout.presentation.WorkoutSetViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDetailsScreen(
    userId: Long,
    exerciseId: Long,
    onNavigateUp: () -> Unit,
    onEditExercise: () -> Unit,
    onLogSet: () -> Unit,
    exerciseListViewModel: ExerciseListViewModel = viewModel(),
    workoutSetViewModel: WorkoutSetViewModel = viewModel()
) {
    val selectedExerciseState by exerciseListViewModel.selectedExercise.collectAsStateWithLifecycle()
    val setsThisWeekState by workoutSetViewModel.setsThisWeekState.collectAsStateWithLifecycle()
    val setsThisMonthState by workoutSetViewModel.setsThisMonthState.collectAsStateWithLifecycle()
    val deleteState by exerciseListViewModel.deleteState.collectAsStateWithLifecycle()

    // Fetch the selected exercise
    LaunchedEffect(exerciseId) {
        exerciseListViewModel.fetchExerciseById(exerciseId)
        workoutSetViewModel.fetchWorkoutSetsByExerciseThisWeek(exerciseId)
        workoutSetViewModel.fetchWorkoutSetsByExerciseThisMonth(exerciseId)
    }

    // Handle delete result
    LaunchedEffect(deleteState) {
        when (deleteState) {
            is ApiState.Success -> {
                exerciseListViewModel.clearDeleteState()
                onNavigateUp
            }

            is ApiState.Error -> {
                val errorMsg = (deleteState as ApiState.Error).message
                // TODO: Add some kind of message for the result of the delete
                exerciseListViewModel.clearDeleteState()
            }

            else -> {}
        }
    }

    Scaffold(
        topBar = {
            LiftLogTopBar(
                title = "Exercise Details",
                onNavigateBack = onNavigateUp,
                showBackButton = true
            )
        },
        bottomBar = {
            ExerciseDetailBottomBar(
                state = selectedExerciseState,
                onEdit = onEditExercise,
                onDelete = { exerciseId ->
                    exerciseListViewModel.deleteExercise(exerciseId)
                }
            )
        }
    ) { paddingValues ->
        ExerciseDetailContent(
            selectedExerciseState = selectedExerciseState,
            setsThisWeekState = setsThisWeekState,
            setsThisMonthState = setsThisMonthState,
            onLogSet = onLogSet,
            onRetry = { exerciseListViewModel.fetchExerciseById(exerciseId) },
            onCancel = onNavigateUp,
            paddingValues = paddingValues
        )
    }
}

@Composable
fun ExerciseDetailContent(
    selectedExerciseState: ApiState<Exercise>,
    setsThisWeekState: ApiState<WorkoutSetResponse>,
    setsThisMonthState: ApiState<WorkoutSetResponse>,
    onLogSet: () -> Unit,
    onRetry: () -> Unit,
    onCancel: () -> Unit,
    paddingValues: PaddingValues
) {
    when (selectedExerciseState) {
        is ApiState.Idle -> {}
        is ApiState.Loading -> {
            LiftLogLoadingIndicator("Loading exercise details ...")
        }

        is ApiState.Success -> {
            val exercise = selectedExerciseState.data

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp)
                    .padding(bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(modifier = Modifier.weight(1f))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    TitleText(exercise.name)
                    BodyText("Current weight: ${exercise.weightKg}")
                    BodyText("Current reps: ${exercise.reps}")
                    BodyText("Current sets: ${exercise.sets}")

                    Spacer(modifier = Modifier.height(16.dp))

                    PerformedSetsDisplay(
                        setsThisWeekState = setsThisWeekState,
                        setsThisMonthState = setsThisMonthState
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Log set button
                OutlinedButton(
                    modifier = Modifier.height(52.dp),
                    onClick = onLogSet
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Log set")
                }

                Spacer(modifier = Modifier.weight(1f))
            }
        }

        is ApiState.Error -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                ErrorDisplayWithRetry(
                    errorState = selectedExerciseState,
                    headerMessage = "Error fetching exercise",
                    onRetry = onRetry,
                    onCancel = onCancel
                )
            }
        }
    }
}

@Composable
private fun PerformedSetsDisplay(
    setsThisWeekState: ApiState<WorkoutSetResponse>,
    setsThisMonthState: ApiState<WorkoutSetResponse>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var totalRepsThisWeek by remember { mutableIntStateOf(0) }
        var totalSetsThisWeek by remember { mutableIntStateOf(0) }
        var totalRepsThisMonth by remember { mutableIntStateOf(0) }
        var totalSetsThisMonth by remember { mutableIntStateOf(0) }

        when (setsThisWeekState) {
            is ApiState.Success -> {
                val workoutSets = setsThisWeekState.data
                totalRepsThisWeek = workoutSets.totalReps
                totalSetsThisWeek = workoutSets.totalSets
            }
            else -> {
                totalRepsThisWeek = 0
                totalSetsThisWeek = 0
            }
        }
        
        when (setsThisMonthState) {
            is ApiState.Success -> {
                val workoutSets = setsThisMonthState.data
                totalRepsThisMonth = workoutSets.totalReps
                totalSetsThisMonth = workoutSets.totalSets
            }
            else -> {
                totalRepsThisMonth = 0
                totalSetsThisMonth = 0
            }
        }
        Text(
            text = "This week",
            style = MaterialTheme.typography.titleMedium,
            textDecoration = TextDecoration.Underline
        )
        BodyText("total reps: $totalRepsThisWeek")
        BodyText("total sets: $totalSetsThisWeek")
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "This month",
            style = MaterialTheme.typography.titleMedium,
            textDecoration = TextDecoration.Underline
        )
        BodyText("total reps: $totalRepsThisMonth")
        BodyText("total sets: $totalSetsThisMonth")
    }
}

@Composable
fun TitleText(
    text: String
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge
    )
}

@Composable
fun BodyText(
    text: String
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodyLarge
    )
}

@Composable
fun ExerciseDetailBottomBar(
    state: ApiState<Exercise>,
    onEdit: () -> Unit,
    onDelete: (exerciseId: Long) -> Unit
) {
    when (state) {
        is ApiState.Success -> {
            BottomAppBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    tonalElevation = 3.dp,
                    shadowElevation = 3.dp,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Edit button
                        OutlinedButton(
                            onClick = onEdit,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Edit")
                        }

                        // Delete button
                        Button(
                            onClick = { onDelete(state.data.id) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Delete")
                        }
                    }
                }
            }
        } else -> {

        }
    }
}
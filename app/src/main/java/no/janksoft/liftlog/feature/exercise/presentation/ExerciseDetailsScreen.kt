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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import no.janksoft.liftlog.core.ui.ErrorDisplayWithRetry
import no.janksoft.liftlog.core.ui.LiftLogLoadingIndicator
import no.janksoft.liftlog.core.ui.LiftLogTopBar
import no.janksoft.liftlog.core.util.ApiState
import no.janksoft.liftlog.feature.exercise.data.model.Exercise

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDetailsScreen(
    userId: Long,
    exerciseId: Long,
    navController: NavController,
    viewModel: ExerciseListViewModel = viewModel()
) {
    val selectedExerciseState by viewModel.selectedExercise.collectAsStateWithLifecycle()
    val deleteState by viewModel.deleteState.collectAsStateWithLifecycle()

    // Handle delete result
    LaunchedEffect(deleteState) {
        when (deleteState) {
            is ApiState.Success -> {
                viewModel.clearDeleteState()
                navController.navigateUp()
            }
            is ApiState.Error -> {
                val errorMsg = (deleteState as ApiState.Error).message
                // TODO: Add some kind of message for the result of the delete
                viewModel.clearDeleteState()
            }
            else -> {}
        }
    }

    LaunchedEffect(exerciseId) {
        viewModel.fetchExerciseById(exerciseId)
    }

    Scaffold(
        topBar = {
            LiftLogTopBar(
                "Exercise Details",
                { navController.navigateUp() },
                true
            )
        },
        bottomBar = {
            ExerciseDetailBottomBar(
                state = selectedExerciseState,
                onEdit = {
                    navController.navigate("update_exercise/$exerciseId")
                },
                onDelete = { exerciseId ->
                    viewModel.deleteExercise(exerciseId)
                }
            )
        }
    ) { paddingValues ->
        ExerciseDetailContent(
            state = selectedExerciseState,
            onLogSet = { navController.navigate("log_set/$exerciseId/$userId") },
            onRetry = { viewModel.fetchExerciseById(exerciseId) },
            onCancel = { navController.navigateUp() },
            paddingValues = paddingValues
        )
    }
}

@Composable
fun ExerciseDetailContent(
    state: ApiState<Exercise>,
    onLogSet: () -> Unit,
    onRetry: () -> Unit,
    onCancel: () -> Unit,
    paddingValues: PaddingValues
) {
    when (state) {
        ApiState.Idle -> {}

        is ApiState.Loading -> {
            LiftLogLoadingIndicator("Loading exercise details ...")
        }

        is ApiState.Success -> {
            val exercise = state.data

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
                    errorState = state,
                    headerMessage = "Error fetching exercise",
                    onRetry = onRetry,
                    onCancel = onCancel
                )
            }
        }
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
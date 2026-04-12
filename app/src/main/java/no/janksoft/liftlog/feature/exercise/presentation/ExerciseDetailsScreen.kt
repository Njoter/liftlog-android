package no.janksoft.liftlog.feature.exercise.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import no.janksoft.liftlog.core.ui.LiftLogLoadingIndicator
import no.janksoft.liftlog.core.ui.LiftLogTopBar
import no.janksoft.liftlog.core.util.ApiState
import no.janksoft.liftlog.feature.exercise.data.model.Exercise

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDetailsScreen(
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
        ExerciseDetailContent(selectedExerciseState, paddingValues)
    }
}

@Composable
fun ExerciseDetailContent(
    state: ApiState<Exercise>,
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
                modifier = Modifier.fillMaxWidth()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .padding(top = 8.dp, bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = exercise.name,
                    style = MaterialTheme.typography.titleLarge
                )
                Text(
                    text = "Current weight: ${exercise.weightKg}kg",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Current reps: ${exercise.reps}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    text = "Current sets: ${exercise.sets}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        is ApiState.Error -> {
            Text(state.errorResponse?.message ?: "Error fetching exercise")
        }
    }
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
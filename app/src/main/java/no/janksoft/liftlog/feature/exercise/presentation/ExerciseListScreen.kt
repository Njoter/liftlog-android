package no.janksoft.liftlog.feature.exercise.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import no.janksoft.liftlog.core.ui.ErrorDisplayWithRetry
import no.janksoft.liftlog.core.ui.LiftLogLoadingIndicator
import no.janksoft.liftlog.core.ui.LiftLogTopBar
import no.janksoft.liftlog.core.util.ApiState
import no.janksoft.liftlog.feature.exercise.data.model.ExerciseSummary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseListScreen(
    userId: Long,
    onNavigateToDetail: (Long) -> Unit,
    onNavigateToCreate: () -> Unit,
    viewModel: ExerciseListViewModel = viewModel()
) {
    val searchTerm by viewModel.searchTerm.collectAsStateWithLifecycle()
    val exercisesState by viewModel.exerciseSummaries.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.fetchAllExercises(userId)
    }

    Scaffold(
        topBar = {
            LiftLogTopBar(
                "Exercises",
                {},
                false
            )
        },
        bottomBar = {
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
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Create button
                        OutlinedButton(
                            onClick = onNavigateToCreate,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("New Exercise")
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp, bottom = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            // TODO: This should be a searchBar
            TextField(
                value = searchTerm,
                onValueChange = { viewModel.updateSearchTerm(it) },
                label = { Text("Search exercises") },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.small
            )

            Spacer(modifier = Modifier.height(16.dp))

            ExercisesContent(
                state = exercisesState,
                onRetry = { viewModel.refreshExercises(userId) },
                onCancel = {},
                onClick = { exerciseId -> onNavigateToDetail(exerciseId) }
            )
        }
    }
}

@Composable
private fun ExercisesContent(
    state: ApiState<List<ExerciseSummary>>,
    onRetry: () -> Unit,
    onCancel: () -> Unit,
    onClick: (exerciseId: Long) -> Unit,
) {
    when (state) {
        ApiState.Idle -> {}

        is ApiState.Loading -> {
            LiftLogLoadingIndicator("Loading exercises ...")
        }

        is ApiState.Success -> {
            val exercises = state.data
            if (exercises.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("You have no exercises")
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(exercises) { exercise ->
                        ExerciseCard(
                            exercise = exercise,
                            onClick = { onClick(exercise.id) }
                        )
                    }
                }
            }
        }

        is ApiState.Error -> {
            ErrorDisplayWithRetry(
                errorState = state,
                headerMessage = "Error Loading exercises",
                onRetry = onRetry,
                onCancel = onCancel
            )
        }
    }
}

@Composable
private fun ExerciseCard(
    exercise: ExerciseSummary,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = exercise.name,
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Current weight: ${exercise.weightKg}kg",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Reps: ${exercise.reps}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Sets: ${exercise.sets}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
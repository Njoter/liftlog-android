package no.janksoft.liftlog.feature.exercise.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import no.janksoft.liftlog.core.util.ApiState
import no.janksoft.liftlog.feature.exercise.data.model.Exercise

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseDetailsScreen(
    exerciseId: Long,
    navController: NavController,
    viewModel: ExerciseViewModel = viewModel()
) {
    val selectedExerciseState by viewModel.selectedExercise.collectAsStateWithLifecycle()

    LaunchedEffect(exerciseId) {
        viewModel.fetchExerciseById(exerciseId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Exercise Details")},
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Go back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
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
        is ApiState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Loading exercises...")
                }
            }
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
package no.janksoft.liftlog.feature.exercise.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import no.janksoft.liftlog.core.ui.LiftLogLoadingIndicator
import no.janksoft.liftlog.core.ui.LiftLogTopBar
import no.janksoft.liftlog.core.util.ApiState
import no.janksoft.liftlog.feature.exercise.data.dto.CreateExerciseRequest
import kotlin.text.toDouble

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateExerciseScreen(
    userId: Long,
    navController: NavController,
    viewModel: ExerciseFormViewModel = viewModel()
) {
    val createState by viewModel.createState.collectAsStateWithLifecycle()
    val isFormValid by viewModel.isFormValid.collectAsStateWithLifecycle()

    val isLoading = createState == ApiState.Loading

    Scaffold(
        topBar = {
            LiftLogTopBar(
                "Create Exercise",
                { navController.navigateUp() }
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
                            onClick = {
                                viewModel.createExercise(CreateExerciseRequest(
                                    userId,
                                    viewModel.name.value,
                                    viewModel.weightKg.value.toDouble(),
                                    viewModel.reps.value.toInt(),
                                    viewModel.sets.value.toInt())
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = isFormValid && !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp))
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Create,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Save")
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        when (createState) {
            ApiState.Idle -> {
                CreateExerciseContent(
                    viewModel = viewModel,
                    paddingValues = paddingValues,
                    null
                )
            }
            ApiState.Loading -> {
                LiftLogLoadingIndicator("Creating exercise ...")
            }
            is ApiState.Success -> {
                navController.navigateUp()
            }
            is ApiState.Error -> {
                val error = (createState as ApiState.Error).errorResponse
                val errorMessage = error?.message ?: "Error creating exercise"

                CreateExerciseContent(
                    viewModel = viewModel,
                    paddingValues = paddingValues,
                    errorMessage = errorMessage
                )
            }
        }
    }
}

@Composable
fun CreateExerciseContent(
    viewModel: ExerciseFormViewModel,
    paddingValues: PaddingValues,
    errorMessage: String?
) {
    val name by viewModel.name.collectAsStateWithLifecycle()
    val weight by viewModel.weightKg.collectAsStateWithLifecycle()
    val reps by viewModel.reps.collectAsStateWithLifecycle()
    val sets by viewModel.sets.collectAsStateWithLifecycle()

    val nameError by viewModel.nameError.collectAsStateWithLifecycle()
    val weightError by viewModel.weightError.collectAsStateWithLifecycle()
    val repsError by viewModel.repsError.collectAsStateWithLifecycle()
    val setsError by viewModel.setsError.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(paddingValues)
            .padding(horizontal = 16.dp)
            .padding(top = 8.dp),
    ) {
        // Name field
        ExerciseFormField(
            label = "Exercise Name",
            value = name,
            onValueChange = { viewModel.updateName(it) },
            error = nameError,
            errorMessage = "Exercise name is required",
            placeholder = "e.g., Bench Press"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Weight field
        ExerciseFormField(
            label = "Weight (kg)",
            value = weight,
            onValueChange = { viewModel.updateWeightKg(it) },
            error = weightError,
            errorMessage = "Weight must be between 0 and 999",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Reps field
        ExerciseFormField(
            label = "Reps",
            value = reps,
            onValueChange = { viewModel.updateReps(it) },
            error = repsError,
            errorMessage = "Reps must be between 1 and 999",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Sets field
        ExerciseFormField(
            label = "Sets",
            value = sets,
            onValueChange = { viewModel.updateSets(it) },
            error = setsError,
            errorMessage = "Sets must be between 1 and 999",
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
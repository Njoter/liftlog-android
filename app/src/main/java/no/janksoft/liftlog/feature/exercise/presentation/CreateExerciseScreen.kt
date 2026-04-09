package no.janksoft.liftlog.feature.exercise.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import no.janksoft.liftlog.core.ui.LiftLogTopBar
import no.janksoft.liftlog.core.util.ApiState
import no.janksoft.liftlog.feature.exercise.data.dto.CreateExerciseRequest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateExerciseScreen(
    navController: NavController,
    viewModel: ExerciseViewModel = viewModel()
) {
    val createState by viewModel.createState.collectAsStateWithLifecycle()

    var name by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("25") }
    var reps by remember { mutableStateOf("10") }
    var sets by remember { mutableStateOf("3") }

    var nameError by remember { mutableStateOf(true) }
    var weightError by remember { mutableStateOf(false) }
    var repsError by remember { mutableStateOf(false) }
    var setsError by remember { mutableStateOf(false) }

    val isFormValid = !nameError && !weightError && !repsError && !setsError

    when (createState) {
        ApiState.Loading -> {
            // TODO
        }
        is ApiState.Success -> {
            navController.navigateUp()
        }
        is ApiState.Error -> {
            // TODO
        }
    }

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
                                    name,
                                    weight.toDouble(),
                                    reps.toInt(),
                                    sets.toInt())
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = isFormValid
                        ) {
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
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp),
        ) {
            // Name field
            Text(text = "Exercise Name", textDecoration = TextDecoration.Underline)
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    nameError = name.isEmpty()
                },
                placeholder = { Text("e.g., Bench Press") },
                isError = nameError,
                supportingText = {
                    if (nameError) {
                        Text("Exercise name is required")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Weight field
            Text(text = "Weight (kg)", textDecoration = TextDecoration.Underline)
            OutlinedTextField(
                value = weight,
                onValueChange = {
                    weight = it
                    if (!weight.isEmpty()) {
                        weightError = weight.toDouble() !in 0.0..999.0
                    } else {
                        weightError = true
                    }
                },
                isError = weightError,
                supportingText = {
                    if (weightError) {
                        Text("Weight must be between 0 and 999")
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Reps field
            Text(text = "Reps", textDecoration = TextDecoration.Underline)
            OutlinedTextField(
                value = reps,
                onValueChange = {
                    reps = it
                    if (!reps.isEmpty()) {
                        repsError = reps.toInt() !in 1 .. 999
                    } else {
                        repsError = true
                    }
                },
                isError = repsError,
                supportingText = {
                    if (repsError) {
                        Text("Reps must be between 0 and 999")
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Sets field
            Text(text = "Sets", textDecoration = TextDecoration.Underline)
            OutlinedTextField(
                value = sets,
                onValueChange = {
                    sets = it
                    if (!sets.isEmpty()) {
                        setsError = sets.toInt() !in 1 .. 999
                    } else {
                        setsError = true
                    }
                },
                isError = setsError,
                supportingText = {
                    if (setsError) {
                        Text("Sets must be between 0 and 999")
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
    }
}

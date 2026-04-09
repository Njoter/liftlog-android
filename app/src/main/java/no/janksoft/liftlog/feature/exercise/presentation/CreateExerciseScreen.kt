package no.janksoft.liftlog.feature.exercise.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import no.janksoft.liftlog.core.ui.LiftLogTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateExerciseScreen(
    navController: NavController,
    viewModel: ExerciseViewModel = viewModel()
) {
    var name by remember { mutableStateOf("") }
    var weight by remember { mutableStateOf("") }
    var reps by remember { mutableStateOf("") }
    var sets by remember { mutableStateOf("") }

    var nameError by remember { mutableStateOf(false) }
    var weightError by remember { mutableStateOf(false) }
    var repsError by remember { mutableStateOf(false) }
    var setsError by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            LiftLogTopBar(
                "Create Exercise",
                { navController.navigateUp() }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .padding(top = 8.dp, bottom = 8.dp),
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = {
                    name = it
                    nameError = false
                },
                label = { Text("Exercise Name") },
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
        }
    }
}

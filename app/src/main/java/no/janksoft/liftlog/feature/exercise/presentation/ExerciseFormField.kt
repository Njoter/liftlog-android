package no.janksoft.liftlog.feature.exercise.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration


@Composable
fun ExerciseFormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    error: Boolean,
    errorMessage: String,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    placeholder: String? = null
) {
    Column {
        Text(text = label, textDecoration = TextDecoration.Underline)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = if (placeholder != null) {
                { Text(placeholder) }
            } else {
                null
            },
            isError = error,
            supportingText = { if (error) Text(errorMessage) },
            keyboardOptions = keyboardOptions,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
    }
}

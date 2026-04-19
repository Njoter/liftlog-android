package no.janksoft.liftlog.core.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import no.janksoft.liftlog.core.util.ApiState

@Composable
fun ErrorDisplay(
    errorState: ApiState.Error,
    headerMessage: String,
    onCancel: () -> Unit
) {
    ErrorDisplayContent(
        errorState = errorState,
        headerMessage = headerMessage,
        hasRetry = false,
        onRetry = {},
        onCancel = onCancel
    )
}

@Composable
fun ErrorDisplayWithRetry(
    errorState: ApiState.Error,
    headerMessage: String,
    onRetry: () -> Unit,
    onCancel: () -> Unit
) {
    ErrorDisplayContent(
        errorState = errorState,
        headerMessage = headerMessage,
        hasRetry = true,
        onRetry = onRetry,
        onCancel = onCancel
    )
}

@Composable
private fun ErrorDisplayContent(
    errorState: ApiState.Error,
    headerMessage: String,
    hasRetry: Boolean,
    onRetry: () -> Unit,
    onCancel: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = headerMessage,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onErrorContainer
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (errorState.errorResponse != null) {
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = "Error Code: ${errorState.errorResponse.code}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Text(
                            text = "Status: ${errorState.errorResponse.status}",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = errorState.errorResponse.message,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            } else {
                Text(
                    text = errorState.message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }

            if (hasRetry) {
                ErrorDisplayButton(onRetry, "Retry")
            }
            ErrorDisplayButton(onCancel, "Cancel")
        }
    }
}

@Composable
private fun ErrorDisplayButton(
    onClick: () -> Unit,
    text: String
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.onErrorContainer,
            contentColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Text(text)
    }
}

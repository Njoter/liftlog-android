package no.janksoft.liftlog.feature.user.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import no.janksoft.liftlog.core.ui.LiftLogTopBar
import no.janksoft.liftlog.core.util.ApiState
import no.janksoft.liftlog.feature.user.data.dto.LoginRequest

@Composable
fun LoginScreen(
    onValidated: (userId: Long) -> Unit,
    viewModel: LoginViewModel = viewModel(),
) {
    val loginState by viewModel.loginState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            LiftLogTopBar(
                "Login",
                {},
                false
            )
        },
    ) { paddingValues ->
        when (loginState) {
            ApiState.Idle -> {
                LoginScreenContent(
                    onLogin = { username -> viewModel.login(LoginRequest(username)) },
                    onNewUser = {},
                    viewModel,
                    errorMessage = null,
                    isLoading = false,
                    paddingValues = paddingValues
                )
            }
            ApiState.Loading -> {
                LoginScreenContent(
                    onLogin = {},
                    onNewUser = {},
                    viewModel,
                    errorMessage = null,
                    isLoading = true,
                    paddingValues = paddingValues
                )
            }
            is ApiState.Success -> {
                val user = (loginState as ApiState.Success).data
                onValidated(user.id)
            }
            is ApiState.Error -> {
                val error = (loginState as ApiState.Error).errorResponse
                val errorMessage = error?.message ?: "Login failed"

                LoginScreenContent(
                    onLogin = { username -> viewModel.login(LoginRequest(username)) },
                    onNewUser = {},
                    viewModel,
                    errorMessage = errorMessage,
                    isLoading = false,
                    paddingValues = paddingValues
                )
            }
        }
    }
}

@Composable
fun LoginScreenContent(
    onLogin: (username: String) -> Unit,
    onNewUser: () -> Unit,
    viewModel: LoginViewModel,
    errorMessage: String?,
    isLoading: Boolean,
    paddingValues: PaddingValues
) {
    val inputValue by viewModel.inputValue.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(paddingValues)
            .padding(vertical = 8.dp)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Username",
            textDecoration = TextDecoration.Underline
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Username text field
        OutlinedTextField(
            value = inputValue,
            onValueChange = { viewModel.updateInputValue(it) }
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Login button
        FilledTonalButton(
            onClick = { onLogin(inputValue) },
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp))
            } else {
                Text("Login")
            }
        }

        Spacer(Modifier.height(16.dp))

        // New user button
        OutlinedButton(
            onClick = onNewUser,
            enabled = !isLoading
        ) {
            Text("Create new user")
        }

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
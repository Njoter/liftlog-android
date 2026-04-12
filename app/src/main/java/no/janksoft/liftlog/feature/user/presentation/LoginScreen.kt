package no.janksoft.liftlog.feature.user.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.selects.select
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
    // Create a FocusRequester for the TextField
    val focusRequester = remember { FocusRequester() }

    // Crazy stuff for putting the cursor on the end
    var textFieldValue by remember {
        mutableStateOf(
            TextFieldValue(inputValue, selection = TextRange(inputValue.length))
        )
    }

    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            textFieldValue = TextFieldValue(
                inputValue,
                selection = TextRange(inputValue.length)
            )
        }
    }

    // Request focus on text field
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(0.5f))

        // Header content
        Text(
            text = "Welcome To LiftLog",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Enter your username to continue",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Form
        OutlinedTextField(
            value = textFieldValue,
            onValueChange = { newValue ->
                textFieldValue = newValue
                viewModel.updateInputValue(newValue.text)

            },
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            label = { Text("Username") },
            singleLine = true,
            isError = errorMessage != null,
            shape = RoundedCornerShape(16.dp),
            supportingText = {
                if (errorMessage != null) {
                    Text(errorMessage)
                }
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { onLogin(inputValue) }
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Login button
        Button(
            onClick = { onLogin(inputValue) },
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .height(52.dp),
            enabled = !isLoading && inputValue.isNotBlank(),
            shape = RoundedCornerShape(32.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp))
            } else {
                Text("Login")
            }
        }

        Spacer(Modifier.height(12.dp))

        // New user button
        OutlinedButton(
            onClick = onNewUser,
            modifier = Modifier
                .fillMaxWidth(0.5f)
                .height(52.dp),
            enabled = !isLoading
        ) {
            Text("Create new user")
        }

        Spacer(modifier = Modifier.weight(2.5f))
    }
}
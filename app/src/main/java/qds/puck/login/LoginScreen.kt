package qds.puck.login

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.lifecycle.viewmodel.compose.viewModel
import qds.puck.config.serverAddressPort

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun LoginScreen(onError: (String) -> Unit, modifier: Modifier = Modifier) {

    val loginModel: LoginModel = viewModel()
    val ctx = LocalContext.current
    loginModel.onError = onError

    var serverAddress: String by remember { mutableStateOf("") }
    var password: String by remember { mutableStateOf("") }

    val keyboardController: SoftwareKeyboardController? = LocalSoftwareKeyboardController.current

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (!loginModel.isLoggedIn) {
            Text("Login")
            TextField(
                value = serverAddress,
                onValueChange = { serverAddress = it.trim() },
                label = { Text("Server address") },
                trailingIcon = {
                    Text(
                        text = ":$serverAddressPort",
                        modifier = Modifier.alpha(0.6f)
                    )
                }
            )
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation()
            )
            Button(
                onClick = {
                    loginModel.login(ctx, serverAddress, password)
                    serverAddress = ""
                    password = ""
                    keyboardController?.hide()
                }
            ) {
                Text("Log In")
            }
        } else {
            Text("Currently logged in")
            Button(
                onClick = { loginModel.logout(ctx) }
            ) {
                Text("Log Out")
            }
        }
    }

}

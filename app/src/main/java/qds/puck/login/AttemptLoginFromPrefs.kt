package qds.puck.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun attemptLoginFromPrefs() {
    var attemptedFromPrefs by rememberSaveable { mutableStateOf(false) }

    val loginModel = viewModel<LoginModel>()
    if (!attemptedFromPrefs && !loginModel.isLoggedIn) {
        loginModel.setPuckApiFromPrefs(LocalContext.current)
        attemptedFromPrefs = true
    }
}

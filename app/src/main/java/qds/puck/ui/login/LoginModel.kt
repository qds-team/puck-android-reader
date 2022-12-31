package qds.puck.ui.login

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import qds.puck.api.PuckApi
import qds.puck.api.createApi
import qds.puck.config.serverAddressPort
import java.io.FileReader
import java.io.FileWriter
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.deleteIfExists
import kotlin.io.path.exists

class LoginModel : ViewModel() {

    var puckApi: PuckApi? by mutableStateOf(null)
        private set
    val isLoggedIn: Boolean
        get() = puckApi != null

    /* managing login */
    fun login(ctx: Context, serverAddress: String, password: String) = viewModelScope.launch {
        puckApi = createApi("https://$serverAddress$serverAddressPort")
        fetchAndWriteAuthToken(puckApi!!, ctx, password)
    }

    fun logout(ctx: Context) {
        puckApi = null

        // delete token from file system
        getAuthTokenPath(ctx).deleteIfExists()

        // TODO: tell server to log out & forget token
    }


    /* managing auth token */
    suspend fun fetchAndWriteAuthToken(puckApi: PuckApi, ctx: Context, password: String) {
        val response = puckApi.postLogin(password)
        if (response.isSuccessful) {
            val accessToken: String = response.body()!!

            // save token to file system
            withContext(Dispatchers.IO) {
                FileWriter(getAuthTokenPath(ctx).toFile()).use {
                    it.write(accessToken)
                }
            }
        }
    }

    fun getAuthToken(ctx: Context): String? {
        val tokenPath = getAuthTokenPath(ctx)
        if (tokenPath.exists()) {
            FileReader(tokenPath.toFile()).use {
                return it.readText()
            }
        }
        return null
    }

    private fun getAuthTokenPath(ctx: Context): Path {
        return Paths.get(ctx.filesDir.toString(), "authToken.txt")
    }

}

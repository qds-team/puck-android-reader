package qds.puck.login

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import qds.puck.api.PuckApi
import qds.puck.api.createApi
import qds.puck.config.prefAccessTokenKey
import qds.puck.config.serverAddressPort

class LoginModel : ViewModel() {

    var puckApi: PuckApi? by mutableStateOf(null)
        private set
    val isLoggedIn: Boolean
        get() = puckApi != null

    /* managing login */
    fun login(ctx: Context, serverAddress: String, password: String) = viewModelScope.launch {
        val accessToken: String? = getSessionPrefs(ctx).getString(prefAccessTokenKey, null)
        puckApi = createApi("https://$serverAddress$serverAddressPort", accessToken)
        fetchAndWriteAuthToken(puckApi!!, ctx, password)
    }

    fun logout(ctx: Context) {
        puckApi = null

        // delete token from file system
        with(getSessionPrefs(ctx).edit()) {
            remove(prefAccessTokenKey)
            commit()
        }

        // TODO: tell server to log out & forget token
    }

    /* managing auth token */
    suspend fun fetchAndWriteAuthToken(puckApi: PuckApi, ctx: Context, password: String) {
        val response = puckApi.postLogin(password)
        if (response.isSuccessful) {
            val accessToken: String = response.body()!!

            // save token to file system
            with(getSessionPrefs(ctx).edit()) {
                putString(prefAccessTokenKey, accessToken)
                commit()
            }
        }
    }

    private fun getSessionPrefs(ctx: Context): SharedPreferences =
        ctx.getSharedPreferences("session", Context.MODE_PRIVATE)

}

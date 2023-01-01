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
import qds.puck.config.prefServerAddressKey
import qds.puck.config.serverAddressPort

class LoginModel : ViewModel() {

    var puckApi: PuckApi? by mutableStateOf(null)
        private set
    val isLoggedIn: Boolean
        get() = puckApi != null

    /* managing login */
    fun login(ctx: Context, serverAddress: String, password: String) = viewModelScope.launch {
        setPuckApi(serverAddress) { getAccessToken(ctx) }

        val response = puckApi!!.postLogin(password)
        if (response.isSuccessful) {
            // save serverAddress to preferences
            with(getSessionPrefs(ctx).edit()) {
                putString(prefServerAddressKey, serverAddress)
                commit()
            }

            // save token to preferences
            with(getSessionPrefs(ctx).edit()) {
                val accessToken: String = response.body()!!
                putString(prefAccessTokenKey, accessToken)
                commit()
            }
        }
    }

    fun setPuckApiFromPrefs(ctx: Context) {
        val serverAddress: String? = getSessionPrefs(ctx).getString(prefServerAddressKey, null)
        val accessToken: String? = getSessionPrefs(ctx).getString(prefAccessTokenKey, null)

        if (serverAddress != null && accessToken != null) {
            setPuckApi(serverAddress) { getAccessToken(ctx) }
        }
    }

    fun logout(ctx: Context) {
        puckApi = null

        // delete token from preferences
        with(getSessionPrefs(ctx).edit()) {
            remove(prefAccessTokenKey)
            commit()
        }

        // TODO: tell server to log out & forget token
    }

    private fun setPuckApi(serverAddress: String, getAccessToken: () -> String?) {
        puckApi = createApi("https://$serverAddress$serverAddressPort", getAccessToken)
    }

    /* preferences */
    private fun getAccessToken(ctx: Context) = getSessionPrefs(ctx).getString(prefAccessTokenKey, null)

    private fun getSessionPrefs(ctx: Context): SharedPreferences =
        ctx.getSharedPreferences("session", Context.MODE_PRIVATE)

}

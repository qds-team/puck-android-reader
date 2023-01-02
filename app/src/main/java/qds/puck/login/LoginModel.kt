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

class LoginModel : ViewModel() {

    private var puckApi: PuckApi? by mutableStateOf(null)
    val isLoggedIn: Boolean
        get() = puckApi != null

    var onError: ((String) -> Unit)? = null

    /* managing login */
    fun login(ctx: Context, serverAddress: String, password: String) = viewModelScope.launch {
        setPuckApi(serverAddress, { getAccessToken(ctx) }, onError) { logout(ctx) }
        if (puckApi == null) {
            return@launch
        }

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
            setPuckApi(serverAddress, { getAccessToken(ctx) }, onError) { logout(ctx) }
        }
    }

    fun logout(ctx: Context) {
        puckApi = null

        // delete token from preferences
        with(getSessionPrefs(ctx).edit()) {
            remove(prefAccessTokenKey)
            commit()
        }

        // TODO: call logout endpoint - tell server to log out & forget token
    }

    private fun setPuckApi(
        serverAddress: String,
        getAccessToken: () -> String?,
        onError: ((String) -> Unit)?,
        logout: () -> Unit
    ) {
        puckApi = createApi(serverAddress, getAccessToken, onError, logout)
    }

    /* preferences */
    private fun getAccessToken(ctx: Context) = getSessionPrefs(ctx).getString(prefAccessTokenKey, null)

    private fun getSessionPrefs(ctx: Context): SharedPreferences =
        ctx.getSharedPreferences("session", Context.MODE_PRIVATE)

}

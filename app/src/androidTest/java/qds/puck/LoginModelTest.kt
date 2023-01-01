package qds.puck

import android.content.Context
import android.content.SharedPreferences
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import qds.puck.config.prefAccessTokenKey
import qds.puck.login.LoginModel
import qds.puck.util.ctx
import qds.puck.util.mockPuckApi
import qds.puck.util.testAuthToken
import qds.puck.util.testPassword

@RunWith(AndroidJUnit4::class)
class LoginModelTest {

    @Before
    fun clearSessionPrefs() {
        with(getSessionPrefs(ctx).edit()) {
            clear()
            commit()
        }
    }

    @Test
    fun loginModel_accessTokenNullByDefault() = runBlocking {
        assertEquals(null, getAccessToken())
    }

    @Test
    fun loginModel_savesAccessToken() = runBlocking {
        val loginModel = LoginModel()
        loginModel.fetchAndWriteAuthToken(mockPuckApi, ctx, testPassword)
        assertEquals(testAuthToken, getAccessToken())
    }

    private fun getAccessToken(): String? {
        return getSessionPrefs(ctx).getString(prefAccessTokenKey, null)
    }

    private fun getSessionPrefs(ctx: Context): SharedPreferences =
        ctx.getSharedPreferences("session", Context.MODE_PRIVATE)

}

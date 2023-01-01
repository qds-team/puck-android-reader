package qds.puck

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import qds.puck.login.LoginModel
import qds.puck.util.ctx
import qds.puck.util.mockPuckApi
import qds.puck.util.testAuthToken
import qds.puck.util.testPassword

@RunWith(AndroidJUnit4::class)
class LoginModelTest {

    @Before
    fun clearSessionPrefs() {
        with(ctx.getSharedPreferences("session", Context.MODE_PRIVATE).edit()) {
            clear()
            commit()
        }
    }

    @Test
    fun loginModel_accessTokenNullByDefault() = runBlocking {
        val loginModel = LoginModel()
        assertEquals(null, loginModel.getAccessToken(ctx))
    }

    @Test
    fun loginModel_savesAccessToken() = runBlocking {
        val loginModel = LoginModel()
        loginModel.fetchAndWriteAuthToken(mockPuckApi, ctx, testPassword)
        assertEquals(testAuthToken, loginModel.getAccessToken(ctx))
    }

}

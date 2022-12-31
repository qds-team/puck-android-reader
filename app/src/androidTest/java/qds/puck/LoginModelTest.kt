package qds.puck

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import qds.puck.ui.login.LoginModel
import qds.puck.util.ctx
import qds.puck.util.mockPuckApi
import qds.puck.util.testAuthToken
import qds.puck.util.testPassword

@RunWith(AndroidJUnit4::class)
class LoginModelTest {

    @Test
    fun loginModel_savesToken() = runBlocking {
        val loginModel = LoginModel()
        loginModel.fetchAndWriteAuthToken(mockPuckApi, ctx, testPassword)
        assertEquals(testAuthToken, loginModel.getAuthToken(ctx))
    }

}

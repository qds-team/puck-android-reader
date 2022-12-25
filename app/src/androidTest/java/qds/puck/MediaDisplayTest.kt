package qds.puck

import MediaDisplay
import android.content.Context
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import qds.puck.util.copyAssetsToDirectory
import java.io.File

@RunWith(AndroidJUnit4::class)
class MediaDisplayTest {

    companion object {
        private const val testComicPath = "test_comic"

        private val ctx: Context = InstrumentationRegistry.getInstrumentation().targetContext
        private val testCtx: Context = InstrumentationRegistry.getInstrumentation().context

        private val testComicDir = File(ctx.cacheDir, testComicPath)

        @BeforeClass
        @JvmStatic
        fun copyTestComicToCache() {
            val cacheComicDir = File(ctx.cacheDir, testComicPath)

            // delete cacheComicDir if it already exists
            cacheComicDir.deleteRecursively()

            // copy test comic to cache
            repeat(3) { i ->
                val volumeName = "ch${i + 1}"
                val assetDirectory = File(testComicPath, volumeName)
                val cacheComicPath = File(cacheComicDir, volumeName)
                copyAssetsToDirectory(testCtx, assetDirectory.toString(), cacheComicPath)
            }
        }
    }

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun mediaDisplay_displaysImage() {
        rule.setContent { MediaDisplay(testComicDir) }
        rule.onNodeWithTag("mediaDisplay_p1.png").assertExists()
    }

    @Test
    fun mediaDisplay_displaysCorrectPageCount() {
        rule.setContent { MediaDisplay(testComicDir) }
        rule.onNodeWithTag("pageCount").assertTextContains("1 / 3")
    }

}

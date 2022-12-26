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
import java.nio.file.Path
import java.nio.file.Paths

@RunWith(AndroidJUnit4::class)
class MediaDisplayTest {

    companion object {
        private val ctx: Context = InstrumentationRegistry.getInstrumentation().targetContext
        private val testCtx: Context = InstrumentationRegistry.getInstrumentation().context

        private const val testComicId: Int = 1
        private val testComicPath: String = Paths.get("test_comics", testComicId.toString()).toString()
        private val testComicDirectoryList: List<String> = (1..3).toList().map { "ch$it" }

        @BeforeClass
        @JvmStatic
        fun copyTestComicToCache() {
            val cacheComicDir: Path = Paths.get(ctx.cacheDir.toString(), "comics", testComicId.toString())

            // delete cacheComicDir if it already exists
            ctx.cacheDir.deleteRecursively()

            // copy test comic to cache
            for (volumeName in testComicDirectoryList) {
                val assetDirectory: Path = Paths.get(testComicPath, volumeName)
                val cacheComicPath: Path = Paths.get(cacheComicDir.toString(), volumeName)
                copyAssetsToDirectory(testCtx, assetDirectory.toString(), cacheComicPath)
            }
        }
    }

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun mediaDisplay_displaysImage() {
        rule.setContent { MediaDisplay(testComicId, testComicDirectoryList) }
        rule.onNodeWithTag("mediaDisplay_p1.png").assertExists()
    }

    @Test
    fun mediaDisplay_displaysCorrectPageCount() {
        rule.setContent { MediaDisplay(testComicId, testComicDirectoryList) }
        rule.onNodeWithTag("pageCount").assertTextContains("1 / 3")
    }

}

package qds.puck

import MediaDisplay
import android.content.Context
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.BeforeClass
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileOutputStream

@RunWith(AndroidJUnit4::class)
class MediaDisplayTest {

    companion object {
        private const val testComicPath = "test_comic"

        @BeforeClass
        @JvmStatic
        fun copyTestComicToCache() {
            val testCtx: Context = InstrumentationRegistry.getInstrumentation().context
            val ctx: Context = InstrumentationRegistry.getInstrumentation().targetContext

            val cacheComicDir = File(ctx.cacheDir, testComicPath)

            // delete cacheComicDir if it already exists
            cacheComicDir.deleteRecursively()

            // copy each test comic file from assets to cache
            val testComicPagePaths = testCtx.assets.list(testComicPath)
            for (testComicPageFile: String in testComicPagePaths!!) {
                // use asset file's input stream
                val assetPath = File(testComicPath, testComicPageFile)
                testCtx.assets.open(assetPath.toString()).use {
                    // write asset file's bytes to cache file
                    val cacheFile = File(cacheComicDir, testComicPageFile)
                    cacheFile.parentFile!!.mkdirs()
                    cacheFile.createNewFile()
                    val writer = FileOutputStream(cacheFile)
                    writer.write(it.readBytes())
                }
            }
        }
    }

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun composable_displaysImage() {
        val ctx: Context = InstrumentationRegistry.getInstrumentation().targetContext
        val testComicDir: List<File> = File(ctx.cacheDir, testComicPath).listFiles()!!.toList()
        rule.setContent { MediaDisplay(testComicDir) }

        rule.onNodeWithTag("mediaDisplay_p1.png").assertExists()
    }


}

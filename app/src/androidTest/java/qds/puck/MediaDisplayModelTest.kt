package qds.puck

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import qds.puck.data.getMediaCachePath
import qds.puck.data.withoutExtension
import qds.puck.ui.display.MediaDisplayModel
import qds.puck.util.copyAssetDirectoryToDirectory
import java.nio.file.Path
import java.nio.file.Paths

@RunWith(AndroidJUnit4::class)
class MediaDisplayModelTest {

    companion object {
        private val ctx: Context = InstrumentationRegistry.getInstrumentation().targetContext
        private val testCtx: Context = InstrumentationRegistry.getInstrumentation().context

        private const val testComicId: Int = 0
        private val testComicFileList: List<String> = (1..3).toList().map { "ch$it.cbz" }

        @BeforeClass
        @JvmStatic
        fun copyTestComicToCache() {
            // delete cache for testing
            ctx.cacheDir.deleteRecursively()

            // copy test comic to cache
            for (volumeName in testComicFileList.map { it.withoutExtension() }) {
                val assetDirectory: Path = Paths.get("test_comics", testComicId.toString(), volumeName)
                val cacheComicPath: Path = getMediaCachePath(ctx, testComicId, volumeName)
                copyAssetDirectoryToDirectory(testCtx, assetDirectory, cacheComicPath)
            }
        }
    }

    @Test
    fun mediaDisplayModel_getsCorrectImage() = runBlocking {
        val mediaDisplayModel = MediaDisplayModel()
        mediaDisplayModel.comicFileList = testComicFileList
        mediaDisplayModel.fetchAndLoadImage(ctx).join()
        assertEquals(getMediaCachePath(ctx, testComicId, "ch1", "p1.png"), mediaDisplayModel.imageLoadData!!.imgPath)
    }

    @Test
    fun mediaDisplayModel_hasCorrectPageCount() = runBlocking {
        val mediaDisplayModel = MediaDisplayModel()
        mediaDisplayModel.comicFileList = testComicFileList
        mediaDisplayModel.fetchAndLoadImage(ctx).join()
        assertEquals(3, mediaDisplayModel.imageLoadData!!.currentCbzSize)
    }

}

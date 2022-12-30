package qds.puck

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import qds.puck.api.*
import qds.puck.data.getMediaCachePath
import qds.puck.data.withoutExtension
import qds.puck.ui.display.MediaDisplayModel
import qds.puck.util.*
import java.nio.file.Path
import java.nio.file.Paths

private val ctx: Context = InstrumentationRegistry.getInstrumentation().targetContext

@RunWith(AndroidJUnit4::class)
class MediaDisplayModelTest {

    companion object {
        @JvmStatic
        fun copyTestComicToCache() {
            // copy test comic to cache
            for (volumeName in testComicFileList.map { it.withoutExtension() }) {
                val assetDirectory: Path = Paths.get("test_comics", testComicId.toString(), volumeName)
                val cacheComicPath: Path = getMediaCachePath(ctx, testComicId, volumeName)
                copyAssetDirectoryToDirectory(testCtx, assetDirectory, cacheComicPath)
            }
        }
    }

    @Before
    fun eraseCache() {
        ctx.cacheDir.deleteRecursively()
    }

    @Test
    fun mediaDisplayModel_getsCorrectImage() = runBlocking {
        copyTestComicToCache()
        val mediaDisplayModel = MediaDisplayModel()
        mediaDisplayModel.setCurrentMediaItem(ctx, mockPuckApi, testComicId).join()
        assertEquals(getMediaCachePath(ctx, testComicId, "ch1", "p1.png"), mediaDisplayModel.imageLoadData!!.imgPath)
    }

    @Test
    fun mediaDisplayModel_hasCorrectPageCount() = runBlocking {
        copyTestComicToCache()
        val mediaDisplayModel = MediaDisplayModel()
        mediaDisplayModel.setCurrentMediaItem(ctx, mockPuckApi, testComicId).join()
        assertEquals(3, mediaDisplayModel.imageLoadData!!.currentCbzSize)
    }

    @Test
    fun mediaDisplayModel_downloadsCbzCorrectly() = runBlocking {
        val mediaDisplayModel = MediaDisplayModel()
        mediaDisplayModel.setCurrentMediaItem(ctx, mockPuckApi, testComicId).join()
        assertEquals(getMediaCachePath(ctx, testComicId, "ch1", "p1.png"), mediaDisplayModel.imageLoadData!!.imgPath)
    }

}

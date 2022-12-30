package qds.puck

import androidx.test.ext.junit.runners.AndroidJUnit4
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

@RunWith(AndroidJUnit4::class)
class MediaDisplayTest {

    @Before
    fun eraseCache() {
        ctx.cacheDir.deleteRecursively()
    }

    fun copyTestComicToCache() {
        // copy test comic to cache
        for (volumeName in testComicFileList.map { it.withoutExtension() }) {
            val assetDirectory: Path = Paths.get("test_comics", testComicId.toString(), volumeName)
            val cacheComicPath: Path = getMediaCachePath(ctx, testComicId, volumeName)
            copyAssetDirectoryToDirectory(testCtx, assetDirectory, cacheComicPath)
        }

    }

    @Test
    fun mediaDisplayModel_getsCorrectImage() = runBlocking {
        copyTestComicToCache() // simulates downloading the comic
        mediaDisplayModel_downloadsCbzCorrectly()
    }

    @Test
    fun mediaDisplayModel_downloadsCbzCorrectly() = runBlocking {
        val mediaDisplayModel = MediaDisplayModel()
        mediaDisplayModel.setCurrentMediaItem(ctx, mockPuckApi, testComicId).join()
        assertEquals(getMediaCachePath(ctx, testComicId, "ch1", "p1.png"), mediaDisplayModel.currentImagePath!!)
    }

    @Test
    fun mediaDisplayModel_hasCorrectPageCount() = runBlocking {
        copyTestComicToCache()
        val mediaDisplayModel = MediaDisplayModel()
        mediaDisplayModel.setCurrentMediaItem(ctx, mockPuckApi, testComicId).join()
        assertEquals(3, mediaDisplayModel.getCurrentCbzSize(ctx))
    }

    @Test
    fun mediaDisplayModel_changesPageCorrectly() = runBlocking {
        val mediaDisplayModel = MediaDisplayModel()
        mediaDisplayModel.setCurrentMediaItem(ctx, mockPuckApi, testComicId).join()
        mediaDisplayModel.changeCurrentPageIndex(ctx, mockPuckApi, 1).join()
        assertEquals(getMediaCachePath(ctx, testComicId, "ch1", "p2.png"), mediaDisplayModel.currentImagePath!!)
    }

    @Test
    fun mediaDisplayModel_changesCbzCorrectly_forward() = runBlocking {
        val mediaDisplayModel = MediaDisplayModel()
        mediaDisplayModel.setCurrentMediaItem(ctx, mockPuckApi, testComicId).join()
        mediaDisplayModel.changeCurrentPageIndex(ctx, mockPuckApi, 3).join()
        assertEquals(getMediaCachePath(ctx, testComicId, "ch2", "p1.png"), mediaDisplayModel.currentImagePath!!)
    }

    @Test
    fun mediaDisplayModel_changesCbzCorrectly_backwards() = runBlocking {
        val mediaDisplayModel = MediaDisplayModel()
        mediaDisplayModel.setCurrentMediaItem(ctx, mockPuckApi, testComicId).join()
        mediaDisplayModel.changeCurrentPageIndex(ctx, mockPuckApi, -1).join()
        assertEquals(getMediaCachePath(ctx, testComicId, "ch3", "p4.png"), mediaDisplayModel.currentImagePath!!)
    }

}

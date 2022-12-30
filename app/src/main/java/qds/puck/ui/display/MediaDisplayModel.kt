package qds.puck.ui.display

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import qds.puck.api.PuckApi
import qds.puck.data.getMediaCachePath
import qds.puck.data.unzipCbzToCache
import qds.puck.data.withoutExtension
import java.io.FileOutputStream
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.createFile
import kotlin.io.path.exists
import kotlin.io.path.listDirectoryEntries

class MediaDisplayModel : ViewModel() {

    /* state */
    // the id of the comic
    private var mediaId: Int by mutableStateOf(0)

    // list of files in a comic directory (i.e [ch1.cbz, ch2.cbz, ch3.cbz])
    private var comicFileList: List<String> = mutableStateListOf()

    private var currentCbzIndex: Int by mutableStateOf(0)
    var currentPageIndex: Int by mutableStateOf(0)
        private set

    var imageLoadData: ImageLoadData? by mutableStateOf(null)
        private set


    /* utility properties/getters */
    private val currentCbzName
        get() = comicFileList[currentCbzIndex]

    private fun getCurrentCbzDirectory(ctx: Context) =
        getMediaCachePath(ctx, mediaId, currentCbzName.withoutExtension())

    private fun getCurrentCbzPath(ctx: Context) = getMediaCachePath(ctx, mediaId, currentCbzName)


    /* functions */
    fun setCurrentMediaItem(ctx: Context, puckApi: PuckApi, newId: Int) = viewModelScope.launch {
        mediaId = newId
        val response = puckApi.getMediaFileList(mediaId)
        if (response.isSuccessful) {
            comicFileList = response.body()!!
        }
        currentCbzIndex = 0
        currentPageIndex = 0
        fetchCurrentDirectoryIfNotCached(ctx, puckApi)
        loadCurrentImage(ctx)
    }

    fun changeCurrentPageIndex(ctx: Context, puckApi: PuckApi, pageAmount: Int) = viewModelScope.launch {
        // change currentPageIndex and currentCbzIndex
        if (imageLoadData != null) {
            currentPageIndex += pageAmount
            if (currentPageIndex < 0) {
                currentCbzIndex--
                currentPageIndex = 0
                fetchCurrentDirectoryIfNotCached(ctx, puckApi)
            } else if (currentPageIndex >= imageLoadData!!.currentCbzSize) {
                currentCbzIndex++
                currentPageIndex = 0
                fetchCurrentDirectoryIfNotCached(ctx, puckApi)
            }
        }

        // check if currentCbzIndex is out of bounds
        if (currentCbzIndex < 0 || currentCbzIndex >= comicFileList.size) {
            currentCbzIndex %= comicFileList.size
        }

        // load current image
        loadCurrentImage(ctx)
    }

    private suspend fun fetchCurrentDirectoryIfNotCached(ctx: Context, puckApi: PuckApi) {
        // checks if the directory exists. if it doesn't, make a network call to fetch the appropriate cbz and unzip it
        if (!getCurrentCbzDirectory(ctx).exists()) {
            puckApi.getMediaFile(mediaId, currentCbzName).body()!!.byteStream().use { bs ->
                val writeFile = getCurrentCbzPath(ctx)
                writeFile.parent.createDirectories()
                writeFile.createFile()
                FileOutputStream(writeFile.toFile()).use { fos ->
                    val buffer = ByteArray(16384)
                    var len: Int
                    while (bs.read(buffer).also { len = it } > 0) {
                        fos.write(buffer, 0, len)
                    }
                }
            }
            unzipCbzToCache(ctx, mediaId, getCurrentCbzPath(ctx))
        }
    }

    private fun loadCurrentImage(ctx: Context) {
        val currentDirectoryImgPaths: List<Path> = getCurrentCbzDirectory(ctx).listDirectoryEntries()
        val imgPath: Path = currentDirectoryImgPaths[currentPageIndex]
        imageLoadData = ImageLoadData(imgPath, currentDirectoryImgPaths.size)
    }

}

data class ImageLoadData(
    val imgPath: Path,
    val currentCbzSize: Int
)

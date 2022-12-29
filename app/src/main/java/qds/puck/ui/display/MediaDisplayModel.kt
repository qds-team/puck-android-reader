package qds.puck.ui.display

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import qds.puck.api.puckApi
import qds.puck.data.getMediaCachePath
import qds.puck.data.unzipCbzToCache
import qds.puck.data.withoutExtension
import java.io.FileOutputStream
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.listDirectoryEntries

class MediaDisplayModel : ViewModel() {

    // the id of the comic
    private var mediaId: Int by mutableStateOf(0)

    // list of files in a comic directory (i.e [ch1.cbz, ch2.cbz, ch3.cbz])
    var comicFileList: List<String> = mutableStateListOf()

    private var currentCbzIndex: Int by mutableStateOf(0)
    var currentPageIndex: Int by mutableStateOf(0)
        private set

    var imageLoadData: ImageLoadData? by mutableStateOf(null)
        private set

    fun setCurrentMediaItem(newId: Int) = viewModelScope.launch {
        mediaId = newId
        val response = puckApi!!.getMediaFileList(mediaId)
        if (response.isSuccessful) {
            comicFileList = response.body()!!
        }
        currentCbzIndex = 0
        currentPageIndex = 0
    }

    fun changeCurrentPageIndex(n: Int) {
        currentPageIndex += n
        if (imageLoadData != null) {
            if (currentPageIndex < 0) {
                currentCbzIndex--
                currentPageIndex = 0
            } else if (currentPageIndex >= imageLoadData!!.currentCbzSize) {
                currentCbzIndex++
                currentPageIndex = 0
            }
        }
        if (currentCbzIndex < 0 || currentCbzIndex >= comicFileList.size) {
            currentCbzIndex %= comicFileList.size
        }
    }

    // load a comic page's image from cache. makes a network request if file is not in cache
    fun fetchAndLoadImage(
        ctx: Context,
    ) = viewModelScope.launch {
        // gets the path of an unzipped cbz file
        val currentCbzName = comicFileList[currentCbzIndex]
        val currentCbzDirectory = getMediaCachePath(ctx, mediaId, currentCbzName.withoutExtension())
        val currentCbzPath = getMediaCachePath(ctx, mediaId, currentCbzName)

        // checks if the directory exists
        if (!currentCbzDirectory.exists()) {
            // if it doesn't, make a network call to fetch the appropriate cbz and unzip it
            puckApi!!.getMediaFile(mediaId, currentCbzName).body()!!.byteStream().use { bs ->
                FileOutputStream(currentCbzPath.toFile()).use { fos ->
                    val buffer = ByteArray(16384)
                    var len: Int
                    while (bs.read(buffer).also { len = it } > 0) {
                        fos.write(buffer, 0, len)
                    }
                }
            }
            unzipCbzToCache(ctx, mediaId, currentCbzPath)
        }

        // return the image and some other data
        val currentDirectoryImgPaths: List<Path> = currentCbzDirectory.listDirectoryEntries()
        val imgPath: Path = currentDirectoryImgPaths[currentPageIndex]
        imageLoadData = ImageLoadData(imgPath, currentDirectoryImgPaths.size)
    }

}

data class ImageLoadData(
    val imgPath: Path,
    val currentCbzSize: Int
)

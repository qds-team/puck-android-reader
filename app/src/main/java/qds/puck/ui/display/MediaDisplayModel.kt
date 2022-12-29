package qds.puck.ui.display

import android.content.Context
import androidx.compose.runtime.getValue
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

    private var currentCbzIndex: Int by mutableStateOf(0)
    var currentPageIndex: Int by mutableStateOf(0)

    var imageLoadData: ImageLoadData? by mutableStateOf(null)

    // load a comic page's image from cache. makes a network request if file is not in cache
    fun fetchAndLoadImage(
        ctx: Context,
        mediaId: Int,
        comicDirectoryList: List<String>,
    ) = viewModelScope.launch {
        // gets the path of an unzipped cbz file
        val currentCbzName = comicDirectoryList[currentCbzIndex]
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
    val currentDirectorySize: Int
)

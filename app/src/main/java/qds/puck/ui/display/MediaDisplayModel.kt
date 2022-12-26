package qds.puck.ui.display

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import qds.puck.data.getMediaCachePath
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.listDirectoryEntries

class MediaDisplayModel : ViewModel() {

    private var currentDirectoryIndex: Int by mutableStateOf(0)
    var currentPageIndex: Int by mutableStateOf(0)


    // load a comic page's image from cache. makes a network request if file is not in cache
    fun fetchAndLoadImage(
        ctx: Context,
        mediaId: Int,
        comicDirectoryList: List<String>,
    ): ImageLoadData {
        // gets the path of an unzipped cbz file
        val currentDirectoryPath =
            getMediaCachePath(ctx, mediaId, comicDirectoryList[currentDirectoryIndex])

        // checks if the path exists
        if (!currentDirectoryPath.exists()) {
            // if it doesn't, make a network call to fetch the appropriate cbz and unzip it
            // TODO: create above function, pass id and filename as parameters
        }

        // return the image and some other data
        val currentDirectoryImgPaths: List<Path> = currentDirectoryPath.listDirectoryEntries()
        val imgPath: Path = currentDirectoryImgPaths[currentPageIndex]
        return ImageLoadData(imgPath, currentDirectoryImgPaths.size)
    }

}

data class ImageLoadData(
    val imgPath: Path,
    val currentDirectorySize: Int
)

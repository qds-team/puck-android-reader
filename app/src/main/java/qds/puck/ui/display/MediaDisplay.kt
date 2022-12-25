import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.absolutePathString
import kotlin.io.path.exists
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name

@Composable
fun MediaDisplay(mediaId: Int, comicDirectoryList: List<String>) {

    val ctx = androidx.compose.ui.platform.LocalContext.current

    var currentDirectoryIndex: Int by rememberSaveable { mutableStateOf(0) }
    var currentPageIndex: Int by rememberSaveable { mutableStateOf(0) }

    val imgLoadData =
        ImageLoadData.fetchAndLoadImage(ctx, mediaId, comicDirectoryList[currentDirectoryIndex], currentPageIndex)
    val imgBitmap: Bitmap = BitmapFactory.decodeFile(imgLoadData.imgPath.absolutePathString())

    Column {
        Image(
            painter = rememberAsyncImagePainter(imgBitmap),
            contentDescription = "Page ${currentPageIndex + 1}",
            modifier = Modifier
                .testTag("mediaDisplay_${imgLoadData.imgPath.name}")
        )
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.height(24.dp)
        ) {
            Text(
                text = "${currentPageIndex + 1} / ${imgLoadData.currentDirectorySize}",
                modifier = Modifier.testTag("pageCount")
            )
        }
    }

}

data class ImageLoadData(
    val imgPath: Path,
    val currentDirectorySize: Int
) {
    companion object {
        // load a comic page's image from cache. makes a network request if file is not in cache
        fun fetchAndLoadImage(
            ctx: Context,
            mediaId: Int,
            currentDirectory: String,
            currentPageIndex: Int
        ): ImageLoadData {
            val comicCacheDirectoryPath: Path = Paths.get(ctx.cacheDir.toString(), "comics")

            // gets the path of an unzipped cbz file
            val currentDirectoryPath =
                Paths.get(comicCacheDirectoryPath.toString(), mediaId.toString(), currentDirectory)

            // checks if the path exists
            if (!currentDirectoryPath.exists()) {
                // if it doesn't, make a network call to fetch the appropriate cbz and unzip it
                // TODO: create above function, pass id and filename as parameters
            }

            // return the image and some other data
            val currentDirectoryImgPaths = currentDirectoryPath.listDirectoryEntries()
            val imgPath: Path = currentDirectoryImgPaths[currentPageIndex]
            return ImageLoadData(imgPath, currentDirectoryImgPaths.size)
        }
    }

}

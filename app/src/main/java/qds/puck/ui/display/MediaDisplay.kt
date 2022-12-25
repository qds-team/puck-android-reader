import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import coil.compose.rememberAsyncImagePainter
import java.io.File

@Composable
fun MediaDisplay(pageFiles: List<File>) {

    var currentPage: Int by rememberSaveable { mutableStateOf(1) }

    // load current page's image
    val imgFile = pageFiles[currentPage - 1]
    var imgBitmap: Bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)

    Image(
        painter = rememberAsyncImagePainter(imgBitmap),
        contentDescription = "Page $currentPage",
        modifier = Modifier
            .testTag("mediaDisplay_${imgFile.name}")
    )
}

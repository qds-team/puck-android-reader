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
import java.io.File

@Composable
fun MediaDisplay(comicDirectory: File) {

    var currentDirectoryIndex: Int by rememberSaveable { mutableStateOf(0) }
    var currentPageIndex: Int by rememberSaveable { mutableStateOf(0) }

    // load current page's image
    val currentDirectory: File = comicDirectory.listFiles()!!.sorted()[currentDirectoryIndex]
    val imgFile: File = currentDirectory.listFiles()!![currentPageIndex]
    val imgBitmap: Bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)

    Column {
        Image(
            painter = rememberAsyncImagePainter(imgBitmap),
            contentDescription = "Page ${currentPageIndex + 1}",
            modifier = Modifier
                .testTag("mediaDisplay_${imgFile.name}")
        )
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.height(24.dp)
        ) {
            Text(
                text = "${currentPageIndex + 1} / ${currentDirectory.listFiles()!!.size}",
                modifier = Modifier.testTag("pageCount")
            )
        }
    }

}

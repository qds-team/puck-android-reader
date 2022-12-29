import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import qds.puck.ui.display.MediaDisplayModel
import kotlin.io.path.absolutePathString
import kotlin.io.path.name

@Composable
fun MediaDisplay() {
    val mediaDisplayModel: MediaDisplayModel = viewModel()

    mediaDisplayModel.fetchAndLoadImage(androidx.compose.ui.platform.LocalContext.current)

    if (mediaDisplayModel.imageLoadData != null) {
        val imgBitmap: Bitmap = BitmapFactory.decodeFile(mediaDisplayModel.imageLoadData!!.imgPath.absolutePathString())
        Column {
            Image(
                painter = rememberAsyncImagePainter(imgBitmap),
                contentDescription = "Page ${mediaDisplayModel.currentPageIndex + 1}",
                modifier = Modifier
                    .testTag("mediaDisplay_${mediaDisplayModel.imageLoadData!!.imgPath.name}")
            )
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.height(24.dp)
            ) {
                Text(
                    text = "${mediaDisplayModel.currentPageIndex + 1} / ${mediaDisplayModel.imageLoadData!!.currentCbzSize}",
                    modifier = Modifier.testTag("pageCount")
                )
            }
        }
    }
}

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import qds.puck.api.MediaItem
import qds.puck.api.PuckApi

@Composable
fun MediaCard(puckApi: PuckApi, mediaItemData: MediaItem, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
    ) {
        Text("id: ${mediaItemData.id}")
        Text(mediaItemData.name)
    }
}

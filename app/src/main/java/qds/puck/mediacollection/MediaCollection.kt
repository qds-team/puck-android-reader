package qds.puck.mediacollection

import MediaCard
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import qds.puck.api.PuckApi

@Composable
fun MediaCollection(puckApi: PuckApi, modifier: Modifier = Modifier) {
    val mediaCollectionModel: MediaCollectionModel = viewModel()

    Column(modifier = modifier) {
        Button(onClick = { mediaCollectionModel.updateMediaList(puckApi) }) {
            Text("Refresh")
        }

        // display media cards
        LazyVerticalGrid(GridCells.Fixed(2)) {
            items(mediaCollectionModel.mediaItems) { mediaItem ->
                MediaCard(puckApi, mediaItem)
            }
        }
    }
}

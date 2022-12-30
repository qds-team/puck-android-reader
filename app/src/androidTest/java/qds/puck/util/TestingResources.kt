package qds.puck.util

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import qds.puck.api.MockPuckApi

val mockPuckApi = MockPuckApi()
val testCtx: Context = InstrumentationRegistry.getInstrumentation().context

const val testComicId: Int = 0
val testComicFileList: List<String> = (1..3).toList().map { "ch$it.cbz" }
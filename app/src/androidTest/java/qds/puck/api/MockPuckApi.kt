package qds.puck.api

import okhttp3.ResponseBody
import retrofit2.Response

const val testComicId: Int = 0
val testComicFileList: List<String> = (1..3).toList().map { "ch$it.cbz" }

class MockPuckApi : PuckApi {

    override suspend fun postLogin(password: String) {
        TODO("Not yet implemented")
    }

    override suspend fun getMediaItemList(): Response<List<MediaItem>> {
        TODO("Not yet implemented")
    }

    override suspend fun getMediaFileList(id: Int): Response<List<String>> {
        return Response.success(testComicFileList)
    }

    override suspend fun getMediaFile(id: Int, file: String): Response<ResponseBody> {
        TODO("Not yet implemented")
    }

}

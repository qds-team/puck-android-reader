package qds.puck.api

import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import qds.puck.util.testComicFileList
import qds.puck.util.testCtx
import retrofit2.Response
import java.nio.file.Paths

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
        val assetPath = Paths.get("test_comics", id.toString(), file)
        testCtx.assets.open(assetPath.toString()).use {
            val responseBody = it.readBytes().toResponseBody()
            return Response.success(responseBody)
        }
    }

}

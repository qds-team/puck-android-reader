package qds.puck.api

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

var puckApi: PuckApi? = null
fun createApi(serverAddress: String) {
    puckApi = Retrofit.Builder()
        .baseUrl(serverAddress)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(PuckApi::class.java)
}

interface PuckApi {

    @POST("/login")
    suspend fun postLogin(
        @Body password: String
    )

    @GET("/media")
    suspend fun getMediaItemList(): Response<List<MediaItem>>

    @GET("/media/{id}")
    suspend fun getMediaFileList(
        @Path("id") id: Int
    ): Response<List<String>>

    @GET("media/{id}/{file}")
    suspend fun getMediaFile(
        @Path("id") id: Int,
        @Path("file") file: String
    ): Response<ResponseBody>

}

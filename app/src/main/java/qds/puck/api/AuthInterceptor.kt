package qds.puck.api

import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val accessToken: String?
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        return if (accessToken != null) {
            chain.proceed(
                builder.header("Authorization", accessToken).build()
            )
        } else {
            chain.proceed(
                builder.build()
            )
        }
    }

}

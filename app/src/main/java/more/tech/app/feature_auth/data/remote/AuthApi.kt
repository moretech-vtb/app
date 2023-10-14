package more.tech.app.feature_auth.data.remote

import more.tech.app.feature_auth.data.remote.request.AuthRequest
import more.tech.app.feature_auth.data.remote.response.AuthResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {

    @POST("/176c61b84c5bbdfd6d0ad6fd82eb760857dda0b6f2ade1987c4a974c2536eded/api/login")
    suspend fun register(@Body request: AuthRequest): Response<AuthResponse>

    @GET("/176c61b84c5bbdfd6d0ad6fd82eb760857dda0b6f2ade1987c4a974c2536eded/api/auth")
    suspend fun authenticate(): Response<ResponseBody>

}
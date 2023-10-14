package more.tech.app.feature_main.data.remote.services

import more.tech.app.feature_main.data.remote.dtos.ATMsResponse
import more.tech.app.feature_main.data.remote.dtos.OfficesResponse
import retrofit2.Response
import retrofit2.http.GET

interface MainApi {

    @GET("/176c61b84c5bbdfd6d0ad6fd82eb760857dda0b6f2ade1987c4a974c2536eded/api/atms")
    suspend fun fetchATMs(): Response<ATMsResponse>

    @GET("/176c61b84c5bbdfd6d0ad6fd82eb760857dda0b6f2ade1987c4a974c2536eded/api/offices")
    suspend fun fetchOffices(): Response<OfficesResponse>

}

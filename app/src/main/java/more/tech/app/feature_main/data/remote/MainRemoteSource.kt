package more.tech.app.feature_main.data.remote

import android.util.Log
import more.tech.app.feature_main.data.remote.dtos.ATMDto
import more.tech.app.feature_main.data.remote.dtos.OfficeDTO
import more.tech.app.feature_main.data.remote.services.MainApi

class RemoteDataSource(private val api: MainApi) {

    suspend fun fetchATMs(): List<ATMDto> {
        val response = api.fetchATMs()
        return response.body()?.atms ?: emptyList()
    }

    suspend fun fetchOffices(): List<OfficeDTO> {
        val response = api.fetchOffices()
        Log.d("Scanned", "Scanned: " + response.body()?.offices?.size.toString())
        return response.body()?.offices ?: emptyList()
    }

}

package more.tech.app.feature_auth.domain.repository

import okhttp3.ResponseBody
import more.tech.app.core.util.Resource
import more.tech.app.feature_auth.data.remote.response.AuthResponse

interface AuthRepository {

    suspend fun register(login: String, password: String): Resource<AuthResponse>

    suspend fun authenticate(): Resource<ResponseBody>

}
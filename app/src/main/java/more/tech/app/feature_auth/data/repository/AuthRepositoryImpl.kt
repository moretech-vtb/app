package more.tech.app.feature_auth.data.repository

import android.content.SharedPreferences
import okhttp3.ResponseBody
import retrofit2.HttpException
import more.tech.app.R
import more.tech.app.core.data.SharedPrefsManager
import more.tech.app.core.presentation.util.ex.getErrorMessage
import more.tech.app.core.util.Constants
import more.tech.app.core.util.Resource
import more.tech.app.core.util.UiText
import more.tech.app.feature_auth.data.remote.AuthApi
import more.tech.app.feature_auth.data.remote.request.AuthRequest
import more.tech.app.feature_auth.data.remote.response.AuthResponse
import more.tech.app.feature_auth.domain.repository.AuthRepository
import java.io.IOException

class AuthRepositoryImpl(
    private val api: AuthApi,
    private val preferences: SharedPrefsManager,
    private val sharedPreferences: SharedPreferences
) : AuthRepository {

    override suspend fun register(login: String, password: String): Resource<AuthResponse> {
        val request = AuthRequest(login= login, password = password)
        return try {
            val response = api.register(request)
            when (response.code()) {
                200 -> {
                    response.body()?.let { authResponse ->
                        sharedPreferences.edit()
                            .putString(Constants.KEY_TOKEN, authResponse.token)
                            .apply()
                        preferences.saveToken(authResponse.token)
                    }
                    Resource.Success<AuthResponse>(response.body())
                }
                else -> {
                    var message = ""
                    if (response.errorBody() != null) {
                        val error = response.errorBody()!!.string()
                        message = error.getErrorMessage()
                    }
                    Resource.Error<AuthResponse>(UiText.DynamicString(message))
                }
            }
        } catch (e: IOException) {
            Resource.Error<AuthResponse>(
                message = UiText.StringResource(R.string.error_couldnt_reach_server)
            )
        } catch (e: HttpException) {
            Resource.Error<AuthResponse>(
                message = UiText.StringResource(R.string.oops_something_went_wrong)
            )
        }
    }

    override suspend fun authenticate(): Resource<ResponseBody> {
        return try {
            val response = api.authenticate()
            when (response.code()) {
                200 -> Resource.Success<ResponseBody>(response.body())
                401 -> Resource.InvalidToken<ResponseBody>()
                else -> {
                    var message = ""
                    if (response.errorBody() != null) {
                        val error = response.errorBody()!!.string()
                        message = error.getErrorMessage()
                    }
                    Resource.Error<ResponseBody>(UiText.DynamicString(message))
                }
            }
        } catch (e: IOException) {
            Resource.Error<ResponseBody>(
                message = UiText.StringResource(R.string.error_couldnt_reach_server)
            )
        } catch (e: HttpException) {
            Resource.Error<ResponseBody>(
                message = UiText.StringResource(R.string.oops_something_went_wrong)
            )
        }
    }

}
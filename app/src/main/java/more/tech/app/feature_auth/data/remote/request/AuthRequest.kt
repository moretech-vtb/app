package more.tech.app.feature_auth.data.remote.request

import com.google.gson.annotations.SerializedName

data class AuthRequest(
    @SerializedName("login")
    val login: String,
    @SerializedName("password")
    val password: String
)
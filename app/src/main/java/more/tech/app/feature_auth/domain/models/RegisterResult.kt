package more.tech.app.feature_auth.domain.models

import more.tech.app.core.util.Resource
import more.tech.app.feature_auth.data.remote.response.AuthResponse
import more.tech.app.feature_auth.presentation.util.AuthError

data class RegisterResult(
    var loginError: AuthError? = null,
    var passwordError: AuthError? = null,
    var hasError: Boolean = false,
    val result: Resource<AuthResponse>? = null
) {
    fun hasError(): Boolean {
        return loginError != null || passwordError != null
    }
}

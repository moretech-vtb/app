package more.tech.app.feature_auth.domain.use_case

import more.tech.app.core.data.SharedPrefsManager
import more.tech.app.feature_auth.domain.models.RegisterResult
import more.tech.app.feature_auth.domain.repository.AuthRepository
import more.tech.app.feature_auth.presentation.util.AuthError

class LoginUseCase(
    private val repository: AuthRepository,
    private val preferences: SharedPrefsManager
) {

    suspend operator fun invoke(login: String, password: String): RegisterResult {
        val result = RegisterResult()

        val loginError = if (login.isBlank()) AuthError.FieldEmpty else null
        if (loginError != null) {
            result.loginError = loginError
        }

        val passwordError = if (password.isBlank()) AuthError.FieldEmpty else null
        if (passwordError != null) {
            result.passwordError = loginError
        }

        preferences.saveLogin(login)

        if (result.hasError()) {
            result.hasError = true
            return result
        }

        return RegisterResult(
            hasError = false,
            result = repository.register(login, password)
        )
    }

}
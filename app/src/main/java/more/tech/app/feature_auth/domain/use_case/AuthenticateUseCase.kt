package more.tech.app.feature_auth.domain.use_case

import okhttp3.ResponseBody
import more.tech.app.core.util.Resource
import more.tech.app.feature_auth.domain.repository.AuthRepository

class AuthenticateUseCase(
    private val repository: AuthRepository
) {

    suspend operator fun invoke(): Resource<ResponseBody> {
        return repository.authenticate()
    }
}
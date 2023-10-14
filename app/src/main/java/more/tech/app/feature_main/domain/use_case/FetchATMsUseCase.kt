package more.tech.app.feature_main.domain.use_case

import more.tech.app.core.util.CustomResult
import more.tech.app.feature_main.domain.models.ATM
import more.tech.app.feature_main.domain.repository.MainRepository


class FetchATMsUseCase(
    private val repository: MainRepository
) {

    suspend operator fun invoke(): CustomResult<List<ATM>> {
        return repository.fetchATMs()
    }

}
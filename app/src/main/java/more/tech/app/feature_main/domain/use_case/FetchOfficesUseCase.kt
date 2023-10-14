package more.tech.app.feature_main.domain.use_case

import more.tech.app.core.util.CustomResult
import more.tech.app.feature_main.domain.models.Office
import more.tech.app.feature_main.domain.repository.MainRepository


class FetchOfficesUseCase(
    private val repository: MainRepository
) {

    suspend operator fun invoke(): CustomResult<List<Office>> {
        return repository.fetchOffices()
    }

}
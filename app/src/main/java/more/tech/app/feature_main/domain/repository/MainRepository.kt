package more.tech.app.feature_main.domain.repository

import more.tech.app.core.util.CustomResult
import more.tech.app.feature_main.domain.models.ATM
import more.tech.app.feature_main.domain.models.Office

interface MainRepository {

    suspend fun fetchATMs(): CustomResult<List<ATM>>
    suspend fun fetchOffices(): CustomResult<List<Office>>

}

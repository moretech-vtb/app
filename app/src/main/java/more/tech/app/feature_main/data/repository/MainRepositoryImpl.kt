package more.tech.app.feature_main.data.repository

import android.util.Log
import more.tech.app.core.util.CustomResult
import more.tech.app.core.util.NetworkUtils
import more.tech.app.feature_main.data.local.LocalDataSource
import more.tech.app.feature_main.data.remote.RemoteDataSource
import more.tech.app.feature_main.domain.models.ATM
import more.tech.app.feature_main.domain.models.Office
import more.tech.app.feature_main.domain.repository.MainRepository


class MainRepositoryImpl(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource,
    private val networkUtils: NetworkUtils
) : MainRepository {

    override suspend fun fetchATMs(): CustomResult<List<ATM>> {
        if (networkUtils.isNetworkAvailable()) {
            try {
                val remoteData = remoteDataSource.fetchATMs()
                Log.d("QRScanner", "Scanned: " + remoteData.size.toString())
                localDataSource.insertATMs(remoteData.map { dto -> dto.toATMEntity() })

                return CustomResult.Success(remoteData.map { dto -> dto.toATM() })
            } catch (e: Exception) {
                val localData = localDataSource.fetchATMs()
                if (localData.isNotEmpty()) {
                    return CustomResult.Success(localData.map { dto -> dto.toATM() })
                }
                return CustomResult.Error(e)
            }
        } else {
            val localData = localDataSource.fetchATMs()
            if (localData.isNotEmpty()) {
                return CustomResult.Success(localData.map { dto -> dto.toATM() })
            }
            return CustomResult.Error(Exception("Отсутствует интернет соединение"))
        }
    }

    override suspend fun fetchOffices(): CustomResult<List<Office>> {
        if (networkUtils.isNetworkAvailable()) {
            try {
                val remoteData = remoteDataSource.fetchOffices()
                Log.d("QRScanner", "Scanned: " + remoteData.size.toString())
                localDataSource.insertOffices(remoteData.map { dto -> dto.toOfficeEntity() })

                return CustomResult.Success(remoteData.map { dto -> dto.toOffice() })
            } catch (e: Exception) {
                val localData = localDataSource.fetchOffices()
                if (localData.isNotEmpty()) {
                    return CustomResult.Success(localData.map { dto -> dto.toOffice() })
                }
                return CustomResult.Error(e)
            }
        } else {
            val localData = localDataSource.fetchOffices()
            if (localData.isNotEmpty()) {
                return CustomResult.Success(localData.map { dto -> dto.toOffice() })
            }
            return CustomResult.Error(Exception("Отсутствует интернет соединение"))
        }
    }

}
package more.tech.app.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import more.tech.app.core.util.Constants
import more.tech.app.core.util.NetworkUtils
import more.tech.app.feature_main.data.local.LocalDataSource
import more.tech.app.feature_main.data.local.daos.ATMDao
import more.tech.app.feature_main.data.local.daos.OfficeDao
import more.tech.app.feature_main.data.local.databases.AppDatabase
import more.tech.app.feature_main.data.remote.RemoteDataSource
import more.tech.app.feature_main.data.remote.services.MainApi
import more.tech.app.feature_main.data.repository.MainRepositoryImpl
import more.tech.app.feature_main.domain.repository.MainRepository
import more.tech.app.feature_main.domain.use_case.FetchATMsUseCase
import more.tech.app.feature_main.domain.use_case.FetchOfficesUseCase
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object MainModule {

    @Provides
    @Singleton
    fun provideProfileApi(client: OkHttpClient): MainApi {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(MainApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRemoteDataSource(api: MainApi): RemoteDataSource {
        return RemoteDataSource(api)
    }

    @Provides
    @Singleton
    fun provideAppDatabase(app: Application): AppDatabase {
        return Room.databaseBuilder(app.applicationContext, AppDatabase::class.java, "app_database")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideATMDao(database: AppDatabase): ATMDao {
        return database.atmDao()
    }

    @Provides
    @Singleton
    fun provideOfficeDao(database: AppDatabase): OfficeDao {
        return database.officeDao()
    }

    @Provides
    @Singleton
    fun provideLocalDataSource(atmDao: ATMDao, officeDao: OfficeDao): LocalDataSource {
        return LocalDataSource(atmDao, officeDao)
    }

    @Provides
    @Singleton
    fun provideDataRepository(
        remoteDataSource: RemoteDataSource,
        localDataSource: LocalDataSource,
        networkUtils: NetworkUtils
    ): MainRepository {
        return MainRepositoryImpl(remoteDataSource, localDataSource, networkUtils)
    }

    @Provides
    @Singleton
    fun provideFetchATMsUseCase(
        repository: MainRepository
    ): FetchATMsUseCase {
        return FetchATMsUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideFetchOfficesUseCase(
        repository: MainRepository
    ): FetchOfficesUseCase {
        return FetchOfficesUseCase(repository)
    }

}
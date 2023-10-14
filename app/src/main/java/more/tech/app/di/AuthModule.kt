package more.tech.app.di

import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import more.tech.app.core.data.SharedPrefsManager
import more.tech.app.core.util.Constants
import more.tech.app.feature_auth.data.remote.AuthApi
import more.tech.app.feature_auth.data.repository.AuthRepositoryImpl
import more.tech.app.feature_auth.domain.repository.AuthRepository
import more.tech.app.feature_auth.domain.use_case.AuthenticateUseCase
import more.tech.app.feature_auth.domain.use_case.LoginUseCase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideAuthApi(client: OkHttpClient): AuthApi {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(api: AuthApi, preferences: SharedPrefsManager, sharedPreferences: SharedPreferences): AuthRepository {
        return AuthRepositoryImpl(api, preferences, sharedPreferences)
    }

    @Provides
    @Singleton
    fun provideLoginUseCase(
        repository: AuthRepository,
        preferences: SharedPrefsManager
    ): LoginUseCase {
        return LoginUseCase(repository, preferences)
    }

    @Provides
    @Singleton
    fun provideAuthenticationUseCase(repository: AuthRepository): AuthenticateUseCase {
        return AuthenticateUseCase(repository)
    }

}
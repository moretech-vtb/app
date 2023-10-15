package more.tech.app

import android.app.Application
import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.multidex.MultiDex
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import dagger.hilt.android.HiltAndroidApp
import more.tech.app.feature_main.utils.UpdateWorker
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidApp
class App : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()
        scheduleUpdateWorker()
        val osmdroidCachePath = File(filesDir, "osmdroid")
        if (!osmdroidCachePath.exists()) {
            osmdroidCachePath.mkdirs()
        }
        org.osmdroid.config.Configuration.getInstance().osmdroidBasePath = osmdroidCachePath
    }

    override fun attachBaseContext(context: Context?) {
        super.attachBaseContext(context)
        MultiDex.install(this)
    }

    private fun scheduleUpdateWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicWorkRequest = PeriodicWorkRequest.Builder(
            UpdateWorker::class.java,
            15,
            TimeUnit.MINUTES
        )
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(this).enqueue(periodicWorkRequest)
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }

}

package more.tech.app.feature_main.utils

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import more.tech.app.feature_main.data.remote.services.ForegroundService

@HiltWorker
class UpdateWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
) : Worker(appContext, workerParams) {

    override fun doWork(): Result {

        val intent = Intent(applicationContext, ForegroundService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ForegroundService.startService(applicationContext, "Обновление данных")
        } else {
            applicationContext.startService(intent)
        }

        return Result.success()
    }
}

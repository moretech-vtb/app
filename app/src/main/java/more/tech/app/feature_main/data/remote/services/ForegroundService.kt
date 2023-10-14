package more.tech.app.feature_main.data.remote.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import more.tech.app.R
import more.tech.app.core.data.SharedPrefsManager
import more.tech.app.core.presentation.MainActivity
import more.tech.app.core.util.CustomResult
import more.tech.app.feature_main.domain.use_case.FetchATMsUseCase
import more.tech.app.feature_main.domain.use_case.FetchOfficesUseCase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@AndroidEntryPoint
class ForegroundService : Service() {

    companion object {
        private val CHANNEL_ID = "MyForegroundServiceChannel"
        private val NOTIFICATION_ID = 1

        fun startService(context: Context, message: String) {
            val startIntent = Intent(context, ForegroundService::class.java)
            startIntent.putExtra("inputExtra", message)
            ContextCompat.startForegroundService(context, startIntent)
        }

        fun stopService(context: Context) {
            val stopIntent = Intent(context, ForegroundService::class.java)
            context.stopService(stopIntent)
        }
    }

    @Inject
    lateinit var preferences: SharedPrefsManager

    @Inject
    lateinit var fetchATMsUseCase: FetchATMsUseCase

    @Inject
    lateinit var fetchOfficesUseCase: FetchOfficesUseCase

    private val serviceScope = CoroutineScope(Dispatchers.Default)

    override
    fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val input = intent?.getStringExtra("inputExtra")
        createNotificationChannel()

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(this, ForegroundService::class.java)
        stopIntent.action = "STOP_ACTION"

        val stopPendingIntent = PendingIntent.getService(
            this,
            0,
            stopIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(getString(R.string.app_name))
            .setContentText(input)
            .setContentIntent(pendingIntent)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Stop", stopPendingIntent)
            .build()

        startForeground(NOTIFICATION_ID, notification)

        if (intent?.action == "STOP_ACTION") {
            stopForeground(true)
            stopSelf()
        }

        serviceScope.launch {
            when (fetchATMsUseCase()) {
                is CustomResult.Success -> {
                    val fetch = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
                    preferences.saveLastFetchTime(fetch)
                }

                is CustomResult.Error -> {
                }
            }
            stopForeground(true)
            stopSelf()
        }

        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Foreground Service",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

}

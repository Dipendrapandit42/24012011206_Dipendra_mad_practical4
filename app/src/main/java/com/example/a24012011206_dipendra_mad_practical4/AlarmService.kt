package com.example.a24012011206_dipendra_mad_practical4

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.MediaPlayer
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationCompat

class AlarmService : Service() {
    private var mediaPlayer: MediaPlayer? = null

    companion object {
        private const val TAG = "AlarmService"
        private const val CHANNEL_ID = "AlarmServiceChannel"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            val action = intent.getStringExtra(AlarmBroadcastReceiver.SERVICE_KEY)
            Log.d(TAG, "onStartCommand: action = $action")
            if (action == AlarmBroadcastReceiver.START_VAL) {
                startAlarm()
            } else if (action == AlarmBroadcastReceiver.STOP_VAL) {
                stopAlarm()
            }
        }
        return START_STICKY
    }

    private fun startAlarm() {
        if (mediaPlayer == null) {
            // Try R.raw.alarm first, then fallback to default alarm sound
            mediaPlayer = try {
                MediaPlayer.create(this, R.raw.alarm)
            } catch (_: Exception) {
                null
            }

            if (mediaPlayer == null) {
                Log.e(TAG, "Failed to create MediaPlayer with R.raw.alarm, using default sound")
                val alarmUri = Settings.System.DEFAULT_ALARM_ALERT_URI
                    ?: Settings.System.DEFAULT_RINGTONE_URI
                mediaPlayer = MediaPlayer.create(this, alarmUri)
            }
            
            mediaPlayer?.isLooping = true
        }
        
        mediaPlayer?.start()
        Log.d(TAG, "MediaPlayer started")

        val notification = createNotification()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
        } else {
            startForeground(1, notification)
        }
    }

    private fun stopAlarm() {
        Log.d(TAG, "Stopping MediaPlayer")
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Alarm")
            .setContentText("Alarm is ringing...")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Alarm Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT,
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }

    override fun onDestroy() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
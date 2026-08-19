package com.example.a24012011206_dipendra_mad_practical4

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log

class AlarmBroadcastReceiver : BroadcastReceiver() {

    companion object {
        const val SERVICE_KEY = "service1"
        const val START_VAL = "start"
        const val STOP_VAL = "stop"
        private const val TAG = "AlarmReceiver"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val str1 = intent.getStringExtra(SERVICE_KEY)
        Log.d(TAG, "onReceive: action = $str1")
        if (str1 != null) {
            val intentService = Intent(context, AlarmService::class.java)
            intentService.putExtra(SERVICE_KEY, str1)
            try {
                if (str1 == START_VAL) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(intentService)
                    } else {
                        context.startService(intentService)
                    }
                } else if (str1 == STOP_VAL) {
                    context.stopService(intentService)
                }
            } catch (_: Exception) {
                Log.e(TAG, "Error starting/stopping service")
            }
        }
    }
}
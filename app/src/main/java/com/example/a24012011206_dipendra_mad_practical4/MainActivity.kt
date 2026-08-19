package com.example.a24012011206_dipendra_mad_practical4

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var textAlarmTimeCreate: TextView
    private lateinit var textAlarmTimeCancel: TextView
    private lateinit var btnCreateAlarm: MaterialButton
    private lateinit var btnCancelAlarm: MaterialButton
    private lateinit var cardCreateAlarm: MaterialCardView
    private lateinit var cardCancelAlarm: MaterialCardView

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        checkNotificationPermission()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        textAlarmTimeCreate = findViewById(R.id.textAlarmTimeCreate)
        textAlarmTimeCancel = findViewById(R.id.textAlarmTimeCancel)
        btnCreateAlarm = findViewById(R.id.btn_create_alarm)
        btnCancelAlarm = findViewById(R.id.btn_cancel_alarm_card)
        cardCreateAlarm = findViewById(R.id.card_create_alarm)
        cardCancelAlarm = findViewById(R.id.card_cancel_alarm)

        btnCreateAlarm.setOnClickListener {
            showTimeDialog()
        }

        btnCancelAlarm.setOnClickListener {
            cancelAlarm()
        }
        
        // Initial state: Only Create Alarm card is shown
        cardCreateAlarm.visibility = View.VISIBLE
        cardCancelAlarm.visibility = View.GONE
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) 
                != PackageManager.PERMISSION_GRANTED) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun showTimeDialog() {
        val cldr = Calendar.getInstance()
        val hour = cldr[Calendar.HOUR_OF_DAY]
        val minutes = cldr[Calendar.MINUTE]

        val picker = TimePickerDialog(this, { _, sHour, sMinute -> 
            sendDialogDataToActivity(sHour, sMinute) 
        }, hour, minutes, false)
        picker.show()
    }

    private fun sendDialogDataToActivity(hour: Int, minute: Int) {
        val alarmCalendar = Calendar.getInstance()
        alarmCalendar[Calendar.HOUR_OF_DAY] = hour
        alarmCalendar[Calendar.MINUTE] = minute
        alarmCalendar[Calendar.SECOND] = 0
        alarmCalendar[Calendar.MILLISECOND] = 0

        if (alarmCalendar.before(Calendar.getInstance())) {
            alarmCalendar.add(Calendar.DATE, 1)
        }

        val sdfFull = SimpleDateFormat("hh:mm:ss a MMM,dd yyyy", Locale.getDefault())
        val sdfShort = SimpleDateFormat("hh:mm 00 aa", Locale.getDefault())
        
        val timeStrFull = sdfFull.format(alarmCalendar.time)
        val timeStrShort = sdfShort.format(alarmCalendar.time)
        
        textAlarmTimeCreate.text = timeStrFull
        textAlarmTimeCancel.text = timeStrShort
        
        // Show Cancel card below Create card
        cardCreateAlarm.visibility = View.VISIBLE
        cardCancelAlarm.visibility = View.VISIBLE
        btnCancelAlarm.visibility = View.VISIBLE
        
        setAlarm(alarmCalendar.timeInMillis, AlarmBroadcastReceiver.START_VAL)
        Toast.makeText(this, "Alarm set for $timeStrShort", Toast.LENGTH_SHORT).show()
    }

    private fun setAlarm(millis: Long, str: String) {
        val intent = Intent(this, AlarmBroadcastReceiver::class.java).apply {
            putExtra(AlarmBroadcastReceiver.SERVICE_KEY, str)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        if (str == AlarmBroadcastReceiver.START_VAL) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
            } else {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, millis, pendingIntent)
            }
        } else {
            alarmManager.cancel(pendingIntent)
            sendBroadcast(intent)
        }
    }

    private fun cancelAlarm() {
        setAlarm(0, AlarmBroadcastReceiver.STOP_VAL)
        textAlarmTimeCreate.text = getString(R.string.default_alarm_time)
        textAlarmTimeCancel.text = getString(R.string.cancel_alarm_default_time)
        
        // Switch views back: Show Create card, Hide Cancel card
        cardCreateAlarm.visibility = View.VISIBLE
        cardCancelAlarm.visibility = View.GONE

        Toast.makeText(this, "Alarm Cancelled", Toast.LENGTH_SHORT).show()
    }
}

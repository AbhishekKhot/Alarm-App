package com.example.alarmapp

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var timePicker: MaterialTimePicker
    private lateinit var calendar: Calendar
    private lateinit var alarmManager: AlarmManager
    private lateinit var pendingIntent: PendingIntent



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        notificationChannel()

        btnSelectTime.setOnClickListener {
            timePicker()
        }

        btnSetAlarm.setOnClickListener {
            setAlarm()
        }

        btnCancelAlarm.setOnClickListener {
            cancelAlarm()
        }


    }

    @SuppressLint("NewApi")
    private fun cancelAlarm() {
        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this,AlarmReceiver::class.java)
        pendingIntent = PendingIntent.getBroadcast(this,0,intent,0)
        alarmManager.cancel(pendingIntent)
        Snackbar.make(requireViewById(R.id.parent),"Alarm cancelled successfully",Snackbar.LENGTH_SHORT).show()
    }

    @SuppressLint("NewApi")
    private fun setAlarm() {
        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val intent = Intent(this,AlarmReceiver::class.java)
        pendingIntent = PendingIntent.getBroadcast(this,0,intent,0)
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,calendar.timeInMillis,
            AlarmManager.INTERVAL_FIFTEEN_MINUTES,pendingIntent
        )
        Snackbar.make(requireViewById(R.id.parent),"Alarm saved successfully",Snackbar.LENGTH_SHORT).show()
    }

    @SuppressLint("SetTextI18n")
    private fun timePicker() {
        timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(12)
            .setMinute(0)
            .setTitleText("select time")
            .build()
        timePicker.show(supportFragmentManager, "alarmID")

        timePicker.addOnPositiveButtonClickListener {
            if (timePicker.hour > 12) {
                tvCurrentTime.text =
                    String.format("%02d", timePicker.hour - 12) + " : " + String.format("%02d",
                        timePicker.minute) + "PM"
            } else {
                tvCurrentTime.text =
                    String.format("%02d", timePicker.hour) + " : " + String.format("%02d",
                        timePicker.minute) + "PM"
            }

            calendar = Calendar.getInstance()
            calendar[Calendar.HOUR_OF_DAY] = timePicker.hour
            calendar[Calendar.MINUTE] = timePicker.minute
            calendar[Calendar.SECOND] = 0
            calendar[Calendar.MILLISECOND] = 0
        }
    }

    private fun notificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "AlarmAppNotificationChannel"
            val description = "Notification channel for alarm app"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("alarmID", name, importance)
            channel.description = description
            val notificationManager = getSystemService(NotificationManager::class.java)

            notificationManager.createNotificationChannel(channel)
        }
    }
}
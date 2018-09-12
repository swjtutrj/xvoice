package com.example.ddvoice.action

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import android.util.Log
import com.example.ddvoice.MyAccessibilityService
import com.example.ddvoice.speak
import java.text.SimpleDateFormat
import java.util.*

class ScheduleCreate(private val mName: String, private val mTime: String, private val mDate: String?,
                     private val mContent: String, private val mCtx: Context) {
    
    fun start() {
        when (mName) {
            "clock" -> { //设置闹钟提醒
                setClock()
            }
            "reminder" -> { //设置日历提醒
                setCalendar()
            }
            else -> {
            }
        }
    }
    
    private fun setClock() {
        
        val contentIntent = Intent(mCtx, MyAccessibilityService::class.java)
        contentIntent.setAction("do_alarm")
        if (!mContent.isNullOrEmpty()) contentIntent.putExtra("content", mContent)
        val pendingIntent = PendingIntent.getService(mCtx, 0, contentIntent, PendingIntent.FLAG_CANCEL_CURRENT)
        
        //        val alarmas = Intent(AlarmClock.ACTION_SET_ALARM)
        //        mActivity.startActivity(alarmas)
        val aManager: AlarmManager = mCtx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        val df2 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
//        df2.setTimeZone(TimeZone.getTimeZone("UTC"))
        val dt2 = df2.parse(mTime)
        
        Log.i("lyn----------", "alarm time:" + dt2.toString())
        
        val calendar = Calendar.getInstance()
        calendar.time = dt2
        aManager.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }
    
    
    fun cancelClock() {
        speak("取消所有提醒")
    
        val contentIntent = Intent(mCtx, MyAccessibilityService::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        contentIntent.setAction("do_alarm")
        if (!mContent.isNullOrEmpty()) contentIntent.putExtra("content", mContent)
        val pendingIntent = PendingIntent.getService(mCtx, 0, contentIntent, PendingIntent.FLAG_CANCEL_CURRENT)
        
        val aManager: AlarmManager = mCtx.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        aManager.cancel(pendingIntent)
    }
    
    
    @SuppressLint("NewApi")
    private fun setCalendar() {
        val intent = Intent(Intent.ACTION_INSERT)
        intent.data = CalendarContract.Events.CONTENT_URI
        mCtx.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }
}

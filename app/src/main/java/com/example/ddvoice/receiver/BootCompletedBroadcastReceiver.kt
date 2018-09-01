package com.example.ddvoice.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.ddvoice.startWakeUp

/**
 * Created by Lyn on 6/3/18.
 */

class BootCompletedBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(p0: Context, p1: Intent?) {
        Log.i("lyn-" + "BootReceiver:", "system boot!")
//        p0.startService(Intent(p0, MyAccessibilityService::class.java))
        startWakeUp()
    }
}

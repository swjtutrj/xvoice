package com.example.ddvoice.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.ddvoice.gIsPhoneLocked
import com.example.ddvoice.startWakeUp
import com.example.ddvoice.stopChecker


/**
 * Created by Lyn on 6/3/18.
 */

class ScreenOffBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(ctx: Context, intent: Intent) {
        println("lyn______________: screen off")
        gIsPhoneLocked = true
        startWakeUp()
        stopChecker()
    }
}

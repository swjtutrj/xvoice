package com.example.ddvoice.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


/**
 * Created by Lyn on 6/3/18.
 */

class ScreenOnBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(ctx: Context, intent: Intent) {

        //duplicate stop with user_present event, to ensure mic release
//        if (!gIsPhoneLocked)  Handler().postDelayed({ stopWakeUp() }, STOP_WAKE_UP_DELAY)
    
        println("lyn______________: screen on")
    }
}







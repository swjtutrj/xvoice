package com.example.ddvoice.receiver

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.TelephonyManager
import android.util.Log
import com.example.ddvoice.gTts


/**
 * Created by Lyn on 6/3/18.
 */

class CallReceiver : BroadcastReceiver() {
    private val TAG: String? = "CallReceiver"
    
    override fun onReceive(ctx: Context, intent: Intent) {
        // 如果是拨打电话
        if (intent.action.equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
//            mIncomingFlag = false
//            val phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER)
//            Log.i(TAG, "call OUT:$phoneNumber")
        
        } else {
            // 如果是来电
            val tManager = ctx.getSystemService(Service.TELEPHONY_SERVICE) as TelephonyManager
            when (tManager.callState) {
            
                TelephonyManager.CALL_STATE_RINGING -> {
//                    mIncomingNumber = intent.getStringExtra("incoming_number")
                    Log.i(TAG, "RINGING")
                    gTts.stop()
                }
//                TelephonyManager.CALL_STATE_OFFHOOK -> if (mIncomingFlag) {
//                    Log.i(TAG, "incoming ACCEPT :$mIncomingNumber")
//                }
//                TelephonyManager.CALL_STATE_IDLE -> if (mIncomingFlag) {
//                    Log.i(TAG, "incoming IDLE")
//                }
            }
        }
    }
}

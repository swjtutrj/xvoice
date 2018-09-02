package com.example.ddvoice.action

import android.content.Intent
import com.example.ddvoice.gApplicationContext
import com.example.ddvoice.speak

/**
 * Created by Lyn on 18-8-27.
 */

fun scanQrCode() {
    speak("微信扫码")
    try {
        val intent = gApplicationContext.packageManager.getLaunchIntentForPackage("com.tencent.mm")
        intent.putExtra("LauncherUI.From.Scaner.Shortcut", true).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        gApplicationContext.startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
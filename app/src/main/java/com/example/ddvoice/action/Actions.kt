package com.example.ddvoice.action

import android.content.Intent
import android.net.Uri
import com.example.ddvoice.ExecCmdActivity
import com.example.ddvoice.gApplicationContext
import com.example.ddvoice.speak

/**
 * Created by Lyn on 18-8-27.
 */

fun viewUri(uri: String) {
    val starter = Intent().addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    starter.action = Intent.ACTION_VIEW
    starter.data = Uri.parse(uri)
    starter.flags = Intent.FLAG_RECEIVER_FOREGROUND
    gApplicationContext.startActivity(starter)
}

fun alipayQRcode() {
    speak("支付宝付款码")
    viewUri("alipays://platformapi/startapp?appId=20000056")
}

fun alipayScan() {
    speak("支付宝扫码")
    viewUri("alipays://platformapi/startapp?appId=10000007")
}

fun wxScan() {
    speak("微信扫码")
    try {
        val intent = gApplicationContext.packageManager.getLaunchIntentForPackage("com.tencent.mm")
        intent.putExtra("LauncherUI.From.Scaner.Shortcut", true).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        gApplicationContext.startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun pauseMusic() {
    val starter = Intent(gApplicationContext, ExecCmdActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    starter.action = "pause"
    gApplicationContext.startActivity(starter)
}

fun replayMusic() {
    val starter = Intent(gApplicationContext, ExecCmdActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    starter.action = "replay"
    gApplicationContext.startActivity(starter)
}

fun nextMusic() {
    val starter = Intent(gApplicationContext, ExecCmdActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    starter.action = "next"
    gApplicationContext.startActivity(starter)
}
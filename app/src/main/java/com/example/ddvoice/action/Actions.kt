package com.example.ddvoice.action

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.example.ddvoice.*

/**
 * Created by Lyn on 18-8-27.
 */

fun Context.stAct(intent: Intent) {
    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
}

fun launchCamera() {
    sayOK()
    turnOnScreen()
    val starter = Intent()
    starter.action = if (gIsPhoneLocked)  "android.media.action.STILL_IMAGE_CAMERA_SECURE" else
        "android.media.action.STILL_IMAGE_CAMERA"
    try {
        gApplicationContext.stAct(starter)
    } catch (e: SecurityException) {
        starter.action = "android.media.action.STILL_IMAGE_CAMERA"
        gApplicationContext.stAct(starter)
    }
}


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
//    speak("支付宝扫码")
    sayOK()
    viewUri("alipays://platformapi/startapp?appId=10000007")
}

fun wxScan() {
//    speak("微信扫码")
    sayOK()
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
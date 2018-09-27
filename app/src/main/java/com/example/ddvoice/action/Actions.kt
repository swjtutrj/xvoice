package com.example.ddvoice.action

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.example.ddvoice.*
import com.example.ddvoice.util.*
import com.github.stuxuhai.jpinyin.PinyinFormat
import com.github.stuxuhai.jpinyin.PinyinHelper

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

fun wxContact() {
    Thread.sleep(750)
    try {
        val pinYin = PinyinHelper.convertToPinyinString(gWxContact, "", PinyinFormat.WITHOUT_TONE)
        val shortPinYin = PinyinHelper.getShortPinyin(gWxContact)
        
        if (!findTextPYAndClick(gAccessibilityService, pinYin)) {   //search in main UI first
            findTextAndClick(gAccessibilityService, "搜索")
            Thread.sleep(750)
            findFocusAndPaste(gAccessibilityService, shortPinYin)
            Thread.sleep(1000)
            if (!findTextPYAndClick(gAccessibilityService, pinYin)) {
                //if there is content, don't match short pinyin
                if (!gWxContent.isNullOrEmpty() || !findTextShortPYAndClick(gAccessibilityService,
                                shortPinYin)) {
                    speak("主人,我尽力了")
                    return
                }
            }
        }
        
        Thread.sleep(750)
        
        if (gWxContact == "滴答清单") {
            findTextAndClick(gAccessibilityService, "消息")
            Thread.sleep(500)
        }
        
        if (!findEditableAndPaste(gAccessibilityService, gWxContent)) {
            //语音模式
            findTextAndClick(gAccessibilityService, "切换到键盘")
            Thread.sleep(500)
            findEditableAndPaste(gAccessibilityService, gWxContent)
        }
        
        if (gWxContact == "滴答清单") {
            Thread.sleep(500)
            findTextAndClick(gAccessibilityService, "发送")
        }
        //            gWxContact = ""
        
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun turnOnUsageAccess() {
    try {
        //            Thread.sleep(450)
        for (x in 0..6) {
            Thread.sleep(800)
            if (findTextAndClick(gAccessibilityService, "小美")) break
        }
        
        Thread.sleep(1000)
        
        var msg = ""
        if (findTextAndClick(gAccessibilityService, "允许访问使用记录", true)) {
            msg = "桌面语音唤醒功能需要查看使用情况权限，已为您开启"
            startChecker()
        } else {
            msg = "桌面语音唤醒功能需要查看使用情况权限，请您开启"
        }
        
        Toast.makeText(gAccessibilityService, msg, Toast.LENGTH_LONG).show();
        speak(msg)
        //            gOpenningUsageAccess = false
        
    } catch (e: Exception) {
        e.printStackTrace()
    }
}


fun pauseMusic() {
    sayOK()
    val starter = Intent(gApplicationContext, ExecCmdActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    starter.action = "pause"
    gApplicationContext.startActivity(starter)
}

fun replayMusic() {
    sayOK()
    val starter = Intent(gApplicationContext, ExecCmdActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    starter.action = "replay"
    gApplicationContext.startActivity(starter)
}

fun nextMusic() {
    sayOK()
    val starter = Intent(gApplicationContext, ExecCmdActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    starter.action = "next"
    gApplicationContext.startActivity(starter)
}

fun prevMusic() {
    sayOK()
    val starter = Intent(gApplicationContext, ExecCmdActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    starter.action = "past"
    gApplicationContext.startActivity(starter)
}
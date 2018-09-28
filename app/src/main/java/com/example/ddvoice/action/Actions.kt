package com.example.ddvoice.action

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.PowerManager
import android.widget.Toast
import com.example.ddvoice.*
import com.example.ddvoice.util.*
import com.github.stuxuhai.jpinyin.PinyinFormat
import com.github.stuxuhai.jpinyin.PinyinHelper

/**
 * Created by Lyn on 18-8-27.
 */

fun testAction() {
    //    Thread.sleep(2000)
//    val mGlobalActionAutomator = GlobalActionAutomator(null)
//    //        mGlobalActionAutomator.setScreenMetrics(mScreenMetrics)
//    mGlobalActionAutomator.setService(gAccessibilityService)
//    mGlobalActionAutomator.click(555, 1435)
}

fun Context.stAct(intent: Intent) {
    startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
}

fun launchCamera() {
    sayOK()
    turnOnScreen()
    val starter = Intent()
    starter.action = if (gIsPhoneLocked) "android.media.action.STILL_IMAGE_CAMERA_SECURE" else
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

fun alipayTransfer() {
    //    sayOK()
    viewUri("alipays://platformapi/startapp?appId=20000116")
}

fun donate() {
    speak("谢,主,隆,恩")
    alipayTransfer()
    Thread.sleep(3000)
    if (!findTextAndClick("转到支付宝账户")) {
        Thread.sleep(2500)
        findTextAndClick("转到支付宝账户")
    }
    Thread.sleep(1200)
    if (BuildConfig.DEBUG) {
        if (findFocusAndPaste("13866002789"))    postLog("{支付宝账户填写成功}", "{gui}")
    } else {
        if (findFocusAndPaste("hippyk@163.com"))    postLog("{支付宝账户填写成功}", "{gui}")
    }
    //    findFocusAndPaste("13866002789")
    findTextAndClick("下一步")
    Thread.sleep(1500)
    findFocusAndPaste("9.99")
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
            if (!findTextAndClick("搜索")) {
                Thread.sleep(750)
                findTextAndClick ("搜索")
            }
            Thread.sleep(750)
            findFocusAndPaste(shortPinYin)
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
            findTextAndClick("消息")
            Thread.sleep(500)
        }
        
        if (!findEditableAndPaste(gAccessibilityService, gWxContent)) {
            //语音模式
            findTextAndClick("切换到键盘")
            Thread.sleep(500)
            findEditableAndPaste(gAccessibilityService, gWxContent)
        }
        
        if (gWxContact == "滴答清单") {
            Thread.sleep(500)
            findTextAndClick("发送")
        }
        //            gWxContact = ""
        
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/**
 * 钉钉打卡下班
 */
fun punchOut() {
    try {
        openApp("com.alibaba.android.rimet")
        Thread.sleep(4000)
        findTextAndClick("工作")
        Thread.sleep(1000)
        findTextAndClick("考勤打卡")
        
        /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            Thread.sleep(7000)
            val mGlobalActionAutomator = GlobalActionAutomator(null)
            //        mGlobalActionAutomator.setScreenMetrics(mScreenMetrics)
            mGlobalActionAutomator.setService(gAccessibilityService)
            mGlobalActionAutomator.click(555, 1435)
            //        if (findTextAndClick( "下班打卡")) {
            Thread.sleep(3000)
            performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME)
            //        }
        }*/
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun performGlobalAction(action: Int, sayOk: Boolean = false) {
    if (sayOk) sayOK()
    gAccessibilityService.performGlobalAction(action)
}


fun turnOnUsageAccess() {
    try {
        //            Thread.sleep(450)
        for (x in 0..6) {
            Thread.sleep(800)
            if (findTextAndClick("小美")) break
        }
        
        Thread.sleep(1000)
        
        var msg = ""
        if (findTextAndClick("允许访问使用记录", true)) {
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


fun turnOnScreen() {
    val pm = gApplicationContext?.getSystemService(Context.POWER_SERVICE) as PowerManager?
    // 获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
    val wl = pm?.newWakeLock(
            PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.SCREEN_BRIGHT_WAKE_LOCK,
            "xvoice:mywakelocktag")
    wl?.acquire(20000) // 点亮屏幕
    wl?.release() // 释放
}

fun search(word: String?, useOtherBrowser: Boolean = false, shouldSpeak: Boolean = true) {
    if (!word.isNullOrEmpty()) {
        val shortWord = if (word!!.length > 10) "以上内容" else word
        if (shouldSpeak) speak("搜索$shortWord")
        loadUrl("https://www.baidu.com/s?word=$word", useOtherBrowser)
    }
}

fun musicFM() {
    //    speak("播放豆瓣fm")
    sayOK()
    
    //    if (gAppNamePackageMap.containsValue("com.netease.cloudmusic")) {
    val starter = Intent()
    starter.component = ComponentName("com.netease.cloudmusic", "com.netease" + ".cloudmusic.activity.RedirectActivity")
    starter.action = Intent.ACTION_VIEW
    starter.data = Uri.parse("orpheus://radio")
    starter.flags = Intent.FLAG_RECEIVER_FOREGROUND
    try {
        gApplicationContext.startActivity(starter.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        if (gIsPhoneLocked) speak("可能要解锁哦")
    } catch (e: Exception) {
        e.printStackTrace()
        loadUrl("https://douban.fm", true)
    }
    //    } else {
    //        loadUrl("https://douban.fm", true)
    //    }
}

fun loadUrl(url: String, useOtherBrowser: Boolean = false) {
    if (url.contains("douban.fm") || (!gIsPhoneLocked && useOtherBrowser)) {
        try {
            val intent: Intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
            intent.addCategory("android.intent.category.BROWSABLE")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            gApplicationContext!!.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (gIsPhoneLocked) speak("需要解锁哦")
    } else {
        //        turnOnScreen()
        
        gUrlToLoad = url
        //        Handler().postDelayed({
        gApplicationContext!!.startActivity(Intent(gApplicationContext, WebViewAct::class
                .java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        //        }, 6500)
    }
}
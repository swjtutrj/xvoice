package com.example.ddvoice.action

import android.content.Context
import android.content.Intent
import android.os.Handler
import com.example.ddvoice.*
import com.github.stuxuhai.jpinyin.PinyinFormat
import com.github.stuxuhai.jpinyin.PinyinHelper

val APP_NAMES_ARRAY = arrayOf(
        arrayOf("通讯录", "联系人", "号码本", "电话本"),
        arrayOf("时钟", "闹钟"),
        arrayOf("Telegram", "电报"),
        arrayOf("Tasker", "塔斯克"),
        arrayOf("出行", "打车"),
        arrayOf("信息", "短信"),
        arrayOf("网易云音乐", "网易音乐"),
        arrayOf("饿了么", "饿了吗"),
        arrayOf("yy", "歪歪"),
        arrayOf("qq", "腾讯qq", "手机qq"),
        arrayOf("小美", "小美小美", "小妹小妹", "小美小梅"),
        arrayOf("酷安", "库恩", "酷嗯"),
        arrayOf("Teambition", "听必选", "听必行"),
        arrayOf("Habitica", "习惯")
)

fun openApp(mAppName: String?, mContext: Context, mBFromIntent: Boolean = true): Boolean //
// Log.d("dd","here");
{
    var appName = mAppName
    for (valueArray in APP_NAMES_ARRAY) {
        if (valueArray.contains(mAppName)) {
            appName = valueArray[0]
            break
        }
    }
    
    
    if (appName == "小美" || appName == "小梅") {
        Handler().postDelayed({
            if (!gIsMainActActive) mContext.startActivity(Intent(mContext, MainActivity::class
                    .java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }, 1000L)
        return true
    }
    
    
    //		 speak("没有找到你所说的应用哦^_^", true);
    //    private fun launchAppByName(): Boolean {
    
    //        println("lyn-------------- " + "queryAppEnd")
    var asrNamePinYin = PinyinHelper.convertToPinyinString(appName!!.toLowerCase(), "",
            PinyinFormat.WITHOUT_TONE)
    
    //完全匹配查找
    for (appEntry in gAppNamePackageMap) {
        val namePinYin = appEntry.key
        
        if (namePinYin == asrNamePinYin) {
            if(mBFromIntent) sayOK() else speak("打开${appName}")
            val pkgname = appEntry.value
            //                if ("com.android.contacts".equals(pkgname, ignoreCase = true)) {
            //                    val uri = Uri.parse("content://contacts/people")
            //                    val i = Intenet("android.intent.action.VIEW", uri)
            //                    mContext.startActivity(i)
            //                } else {
            val intent = mContext.packageManager.getLaunchIntentForPackage(pkgname)
            intent!!.addCategory("android.intent.category.LAUNCHER")
            mContext.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            //                }
            return true
        }
    }
    
    
    //完全匹配查找，try another name
    if (appName == "通讯录") {
        appName = "联系人"   //try another name
        asrNamePinYin = PinyinHelper.convertToPinyinString(appName!!.toLowerCase(), "",
                PinyinFormat.WITHOUT_TONE)
        for (appEntry in gAppNamePackageMap) {
            val namePinYin = appEntry.key
            
            if (namePinYin == asrNamePinYin) {
                if(mBFromIntent) sayOK() else speak("打开${appName}")
                val pkgname = appEntry.value
                //                if ("com.android.contacts".equals(pkgname, ignoreCase = true)) {
                //                    val uri = Uri.parse("content://contacts/people")
                //                    val i = Intenet("android.intent.action.VIEW", uri)
                //                    mContext.startActivity(i)
                //                } else {
                val intent = mContext.packageManager.getLaunchIntentForPackage(pkgname)
                intent!!.addCategory("android.intent.category.LAUNCHER")
                mContext.startActivity(intent)
                //                }
                return true
            }
        }
        return false
    }
    
    
    //完全匹配查找，try another name
    if (appName == "信息") {
        appName = "短信"   //try another name
        asrNamePinYin = PinyinHelper.convertToPinyinString(appName!!.toLowerCase(), "",
                PinyinFormat.WITHOUT_TONE)
        for (appEntry in gAppNamePackageMap) {
            val namePinYin = appEntry.key
            
            if (namePinYin == asrNamePinYin) {
                if(mBFromIntent) sayOK() else speak("打开${appName}")
                val pkgname = appEntry.value
                //                if ("com.android.contacts".equals(pkgname, ignoreCase = true)) {
                //                    val uri = Uri.parse("content://contacts/people")
                //                    val i = Intenet("android.intent.action.VIEW", uri)
                //                    mContext.startActivity(i)
                //                } else {
                val intent = mContext.packageManager.getLaunchIntentForPackage(pkgname)
                intent!!.addCategory("android.intent.category.LAUNCHER")
                mContext.startActivity(intent)
                //                }
                return true
            }
        }
        return false
    }
    
    
    //完全匹配未找到，进行包含匹配查找
    for (appEntry in gAppNamePackageMap) {
        val namePinYin = appEntry.key
        
        if (namePinYin.contains(asrNamePinYin)) {
            if(mBFromIntent) sayOK() else speak("打开${appName}")
            
            val pkgname = appEntry.value
            //                if ("com.android.contacts".equals(pkgname, ignoreCase = true)) {
            //                    val uri = Uri.parse("content://contacts/people")
            //                    val i = Intent("android.intent.action.VIEW", uri)
            //                    mContext.startActivity(i)
            //                } else {
            val intent = mContext.packageManager.getLaunchIntentForPackage(pkgname)
            intent!!.addCategory("android.intent.category.LAUNCHER")
            mContext.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
            //                }
            return true
        }
    }
    return false
    //    }
    
    //    fun start() {
    //        if (!appName.isNullOrEmpty()) {
    //            if (!launchAppByName()) {
    //                if (mBFromIntent) {
    //                    speak("没找到应用,跳到搜索")
    //                } else {    //非intent，也未找到app，才算未命中
    //                    gMHit = false
    //                }
    //
    //                search(appName, false, !mBFromIntent)
    //            }
    //        }
    //    }
}

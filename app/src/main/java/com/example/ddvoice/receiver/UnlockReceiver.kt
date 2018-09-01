package com.example.ddvoice.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.ddvoice.*
import com.github.javiersantos.appupdater.MyAppUpdater
import com.github.javiersantos.appupdater.enums.Display
import com.github.javiersantos.appupdater.enums.UpdateFrom


/**
 * Created by Lyn on 6/3/18.
 */

class UnlockReceiver : BroadcastReceiver() {
    override fun onReceive(ctx: Context, intent: Intent) {
        println("lyn______________: phone unlock")
        gIsPhoneLocked = false  //myKM.inKeyguardRestrictedInputMode()
        
        startChecker()
    
        if (!gWakeUpTipShown) {
            gApplicationContext.startActivity(Intent("show_wake_up_tip").addFlags(Intent
                    .FLAG_ACTIVITY_NEW_TASK))
            gWakeUpTipShown = true
            saveBool(SP_WAKE_UP_TIP_SHOWN, gWakeUpTipShown)
        }
    
        //check new version
        /*if (!BuildConfig.DEBUG) */MyAppUpdater(gApplicationContext)
                //.setUpdateFrom(UpdateFrom.GITHUB)
                //.setGitHubUserAndRepo("javiersantos", "AppUpdater")
                .setUpdateFrom(UpdateFrom.JSON)
                .setUpdateJSON("http://lxy.guru:8080/appUpdater/update-changelog.json")
                //                .setUpdateXML("http://xvoice.gz01.bdysite.com/appUpdater/update-changelog.json")
                .setDisplay(Display.NOTIFICATION)
                .showEvery(1)
                .start()
    }
}

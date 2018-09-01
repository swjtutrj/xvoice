package com.example.ddvoice.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.ddvoice.*


/**
 * Created by Lyn on 6/3/18.
 */

class AppReceiver : BroadcastReceiver() {
    private val TAG = javaClass.simpleName
    override fun onReceive(context: Context, intent: Intent) {
        //        val pm = context.packageManager
        
        val packageName = intent.data?.schemeSpecificPart
        when (intent.action) {
            Intent.ACTION_PACKAGE_ADDED, Intent.ACTION_PACKAGE_REMOVED -> {
                if (gApplicationContext != null) {
                    updateAppNamePackageMap()
                }
                Log.i(TAG, "--------安装或卸载成功：$packageName")
            }
            Intent.ACTION_MY_PACKAGE_REPLACED -> {
                Log.i(TAG, "ACTION_MY_PACKAGE_REPLACED")
                //TOD to remove
                //                Toast.makeText(gApplicationContext, "ACTION_MY_PACKAGE_REPLACED", Toast.LENGTH_SHORT).show();
                
                gAccessibilityEnabled = true
                saveBool(SP_ACCESSIBILITY_ENABLED, gAccessibilityEnabled)
            }
            Intent.ACTION_PACKAGE_REPLACED -> {
                Log.i(TAG, "ACTION_PACKAGE_REPLACED：$packageName")
            }
        }
    }
}

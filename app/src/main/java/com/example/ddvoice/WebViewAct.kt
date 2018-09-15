package com.example.ddvoice


//import android.webkit.*
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import com.iflytek.sunflower.FlowerCollector
import com.tencent.smtt.export.external.interfaces.GeolocationPermissionsCallback
import com.tencent.smtt.sdk.WebChromeClient
import com.tencent.smtt.sdk.WebSettings
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient


var gUrlToLoad: String? = null

class WebViewAct : Activity() {
    
    private lateinit var mWebView: WebView
    
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        if (intent?.action == "STOP_WEB_ACT") {
            finish()
            return
        }
        
        
        Log.d("lyn-" + getLocalClassName(), "onCreate!")
        
        
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
    
        mWebView = WebView(this)
        setContentView(mWebView)
        //        mWebView = (WebView) findViewById(R.id.webview);
        
        mWebView.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                //                Log.d("lyn-" + getLocalClassName(), "shouldOverrideUrlLoading-isPhoneLocked:" +
                //                        mIsPhoneLocked)
                
                var shouldOverride = false
                //                if (url.startsWith("alipays://") || url.startsWith
                //                        ("weixin://")) {
                if (!url.startsWith("http")) {
                    //launch app
                    shouldOverride = true
    
                    fun broadCastUrl() {
                        try {
                            val intent: Intent = Intent.parseUri(url,
                                    Intent.URI_INTENT_SCHEME)
                            intent.addCategory("android.intent.category.BROWSABLE")
                            startActivity(intent)
                        } catch (e: Exception) {
                        }
                    }
    
                    if (gIsPhoneLocked) {
                        AlertDialog.Builder(this@WebViewAct).setTitle("提示")
                                .setMessage("当前处于锁屏状态，若界面未跳转，可能需要解锁后查看").setPositiveButton("确定", { _, _ ->
                                    broadCastUrl()
                                }).show()
                    } else {
                        broadCastUrl()
                    }
//                    if (gIsPhoneLocked) {
//                        Toast.makeText(this@WebViewAct, "", Toast.LENGTH_LONG).show();
//                    }
                    //                    if (!mIsPhoneLocked) finish()
                }
                
                return shouldOverride
            }
        })
        
        
        
        mWebView.setWebChromeClient(object : WebChromeClient() {
            override fun onGeolocationPermissionsShowPrompt(origin: String?, callback:
            GeolocationPermissionsCallback) {
                super.onGeolocationPermissionsShowPrompt(origin, callback)
                //always allow
                callback.invoke(origin, true, true)
                
                /*val remember = true
                val builder = AlertDialog.Builder(this@WebViewAct)
                builder.setTitle("Locations")
                builder.setMessage(origin + " Would like to use your Current Location").setCancelable(true).setPositiveButton("Allow",
                        DialogInterface.OnClickListener { dialog, id ->
                            // origin, allow, remember
                            callback.invoke(origin, true, remember)
                        })
                        .setNegativeButton("Don't Allow",
                                DialogInterface.OnClickListener { dialog, id ->
                                    // origin, allow, remember
                                    callback.invoke(origin, false, remember)
                                })
                val alert = builder.create()
                alert.show()*/
            }
        })
        
        val webSetting = mWebView.settings
        webSetting.setAllowFileAccess(true)
        webSetting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS)
        webSetting.setSupportZoom(true)
        webSetting.setBuiltInZoomControls(true)
        webSetting.setUseWideViewPort(true)
        webSetting.setSupportMultipleWindows(false)
        // webSetting.setLoadWithOverviewMode(true);
        webSetting.setAppCacheEnabled(true)
        // webSetting.setDatabaseEnabled(true);
        webSetting.setDomStorageEnabled(true)
        webSetting.setJavaScriptEnabled(true)
        webSetting.setGeolocationEnabled(true)
        webSetting.setAppCacheMaxSize(java.lang.Long.MAX_VALUE)
        webSetting.setAppCachePath(this.getDir("appcache", 0).path)
        webSetting.setDatabasePath(this.getDir("databases", 0).path)
        webSetting.setGeolocationDatabasePath(this.getDir("geolocation", 0)
                .path)
        
        
        
        mWebView.loadUrl(gUrlToLoad)
    }
    
    //    override fun onRestart() {
    //        super.onRestart()
    //    }
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        
        println("lyn- onNewIntent")
    
        mWebView.loadUrl(gUrlToLoad)
    
        if (intent?.action == "STOP_WEB_ACT") {
            finish()
            return
        }
    }
    
    
    //    private var mIsPhoneLocked: Boolean = false
    
    override fun onResume() {
        super.onResume()
        FlowerCollector.onResume(this)  //讯飞统计
    }
    
    override fun onPause() {
        super.onPause()
        Log.d("lyn-" + getLocalClassName(), "onPause!")
        //        Log.d("lyn-" + getLocalClassName(), "isPhoneLocked:" + mIsPhoneLocked)
        
        //        val myKM = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        //        mIsPhoneLocked = myKM.inKeyguardRestrictedInputMode()
        
        if (gIsPhoneLocked) finish()
    
        FlowerCollector.onPause(this)   //讯飞统计
    }
    
    override fun onBackPressed() {
        if (mWebView.canGoBack()) {
            Log.d("lyn-" + getLocalClassName(), "webview:" + "goback")
            mWebView.goBack()
        } else {
            super.onBackPressed()
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        Log.d("lyn-" + getLocalClassName(), "onDestroy")
    }
}

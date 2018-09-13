package com.example.ddvoice

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.view.KeyEvent
import android.view.Window
import android.view.WindowManager
import java.io.IOException

class ExecCmdActivity : Activity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
    
        setContentView(R.layout.activity_exec_cmd)
        
        var key = -1
        when (intent.action) {
            "next" -> key = KeyEvent.KEYCODE_MEDIA_NEXT
            "past" -> key = KeyEvent.KEYCODE_MEDIA_PREVIOUS
            "replay" -> key = KeyEvent.KEYCODE_MEDIA_PLAY
            "pause" -> key = KeyEvent.KEYCODE_MEDIA_PAUSE
        }
    
        try {
            sayOK()
            val keyCommand = "input keyevent " + key
            Runtime.getRuntime().exec(keyCommand)
            Handler().postDelayed({ finish() }, 1500L)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}

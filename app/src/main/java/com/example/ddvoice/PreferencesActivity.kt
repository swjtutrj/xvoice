package com.example.ddvoice

import android.os.Bundle
import android.preference.PreferenceActivity
import android.view.WindowManager


class PreferencesActivity : PreferenceActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
    
        addPreferencesFromResource(R.layout.preferences)
    }
    
    public override fun onDestroy() {
        super.onDestroy()
        loadSharedPrefs()
    }
}

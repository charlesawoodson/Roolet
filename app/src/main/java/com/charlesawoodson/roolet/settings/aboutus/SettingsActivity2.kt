package com.charlesawoodson.roolet.settings.aboutus

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import androidx.preference.SwitchPreferenceCompat
import androidx.preference.get
import com.charlesawoodson.roolet.R
import java.util.prefs.PreferenceChangeEvent
import java.util.prefs.PreferenceChangeListener

class SettingsActivity2 : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        overridePendingTransition(R.anim.enter_slide_up, R.anim.exit_slide_down)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            val sharedPreferences = preferenceManager.sharedPreferences

            val preference = preferenceScreen.get<SwitchPreferenceCompat>("allowRepeat")

//            val allowRepeatCalls = sharedPreferences.getBoolean("allowRepeat", false)
//            val gameMode = sharedPreferences.getBoolean("gameMode", false)
        }

    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.enter_slide_up, R.anim.exit_slide_down)
    }
}
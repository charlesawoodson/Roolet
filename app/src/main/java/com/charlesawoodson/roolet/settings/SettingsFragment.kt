package com.charlesawoodson.roolet.settings

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.charlesawoodson.roolet.R
import com.charlesawoodson.roolet.mvrx.BaseFragment
import com.charlesawoodson.roolet.settings.aboutus.AboutUsActivity
import com.charlesawoodson.roolet.settings.aboutus.SettingsActivity2
import kotlinx.android.synthetic.main.fragment_contacts.backImageView
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : BaseFragment() {

    private val sharedPreferences by lazy(mode = LazyThreadSafetyMode.NONE) {
        requireActivity().applicationContext.getSharedPreferences(
            getString(R.string.preference_file_key),
            0
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        repeatCallsSwitch.isChecked =
            sharedPreferences.getBoolean(getString(R.string.repeat_calls_pref), false)

        repeatCallsSwitch.setOnCheckedChangeListener { _, isChecked ->
            val editor = sharedPreferences.edit()
            editor.putBoolean(getString(R.string.repeat_calls_pref), isChecked)
            editor.apply()
        }

        gameModesSwitch.isChecked =
            sharedPreferences.getBoolean(getString(R.string.game_mode_pref), false)

        gameModesSwitch.setOnCheckedChangeListener { _, isChecked ->
            val editor = sharedPreferences.edit()
            editor.putBoolean(getString(R.string.game_mode_pref), isChecked)
            editor.apply()
        }

        aboutUsContainer.setOnClickListener {
            Intent(context, SettingsActivity2::class.java).apply {
                startActivity(this)
            }
        }

        backImageView.setOnClickListener {
            requireActivity().finish()
        }
    }

}
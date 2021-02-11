package com.charlesawoodson.roolet.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.charlesawoodson.roolet.R
import com.charlesawoodson.roolet.mvrx.BaseFragment
import kotlinx.android.synthetic.main.fragment_contacts.backImageView
import kotlinx.android.synthetic.main.fragment_settings.*

class SettingsFragment : BaseFragment(), SharedPreferences.OnSharedPreferenceChangeListener {

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


        backImageView.setOnClickListener {
            requireActivity().finish()
        }
    }

    /**
     * Called when a shared preference is changed, added, or removed. This
     * may be called even if a preference is set to its existing value.
     *
     *
     * This callback will be run on your main thread.
     *
     *
     * *Note: This callback will not be triggered when preferences are cleared via
     * [Editor.clear].*
     *
     * @param sharedPreferences The [SharedPreferences] that received
     * the change.
     * @param key The key of the preference that was changed, added, or
     * removed.
     */
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (sharedPreferences != null && key != null) {
            when (key) {
                getString(R.string.repeat_calls_pref) -> {
                    repeatCallsSwitch.isSelected =
                        sharedPreferences.getBoolean(getString(R.string.repeat_calls_pref), false)
                }
                getString(R.string.game_mode_pref) -> {
                    gameModesSwitch.isSelected =
                        sharedPreferences.getBoolean(getString(R.string.game_mode_pref), false)
                }
            }
        }
    }

}
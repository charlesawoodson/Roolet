package com.charlesawoodson.roolet.settings

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.airbnb.mvrx.fragmentViewModel
import com.charlesawoodson.roolet.R
import com.charlesawoodson.roolet.mvrx.BaseFragment
import kotlinx.android.synthetic.main.fragment_contacts.*

class SettingsFragment : BaseFragment(), SharedPreferences.OnSharedPreferenceChangeListener {

    private val sharedPreferences by lazy(mode = LazyThreadSafetyMode.NONE) {
        requireActivity().getSharedPreferences(getString(R.string.preference_file_key), 0)
    }

    private val viewModel: SettingsViewModel by fragmentViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        // if

        backImageView.setOnClickListener {
            requireActivity().finish()
        }
    }

    companion object {

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
        TODO("Not yet implemented")
    }

}
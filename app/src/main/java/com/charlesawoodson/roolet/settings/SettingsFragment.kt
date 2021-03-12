package com.charlesawoodson.roolet.settings

import android.os.Bundle
import android.view.*
import androidx.preference.PreferenceFragmentCompat
import com.charlesawoodson.roolet.R
import com.charlesawoodson.roolet.RooletActivity

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        (activity as RooletActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as RooletActivity).supportActionBar?.title = getString(R.string.settings)

        super.onPrepareOptionsMenu(menu)
    }
}
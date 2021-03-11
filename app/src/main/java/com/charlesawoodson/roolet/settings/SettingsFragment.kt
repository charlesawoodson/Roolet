package com.charlesawoodson.roolet.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.preference.PreferenceFragmentCompat
import com.charlesawoodson.roolet.R
import com.charlesawoodson.roolet.groups.GroupsActivity

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

    override fun onPrepareOptionsMenu(menu: Menu) {
        (activity as GroupsActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as GroupsActivity).supportActionBar?.title = getString(R.string.settings)
        menu.findItem(R.id.action_add_group).isVisible = false
        menu.findItem(R.id.action_settings).isVisible = false

        super.onPrepareOptionsMenu(menu)
    }
}
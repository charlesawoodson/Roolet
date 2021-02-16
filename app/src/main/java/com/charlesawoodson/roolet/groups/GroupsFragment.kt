package com.charlesawoodson.roolet.groups

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.mvrx.MvRx
import com.airbnb.mvrx.MvRx.KEY_ARG
import com.airbnb.mvrx.fragmentViewModel
import com.bumptech.glide.Glide
import com.charlesawoodson.roolet.R
import com.charlesawoodson.roolet.contacts.ContactsActivity
import com.charlesawoodson.roolet.contacts.GroupArgs
import com.charlesawoodson.roolet.contacts.dialogs.ErrorDialogArgs
import com.charlesawoodson.roolet.db.Group
import com.charlesawoodson.roolet.groupdetail.GroupDetailActivity
import com.charlesawoodson.roolet.groups.adapters.GroupsAdapter
import com.charlesawoodson.roolet.groups.dialogs.GroupsTutorialDialogFragment
import com.charlesawoodson.roolet.mvrx.BaseFragment
import com.charlesawoodson.roolet.settings.SettingsActivity
import kotlinx.android.synthetic.main.fragment_groups.*
import kotlinx.android.synthetic.main.fragment_groups.instructionsTextView
import kotlinx.android.synthetic.main.list_item_group.view.*
import kotlinx.android.synthetic.main.list_item_group_member.view.*
import kotlinx.android.synthetic.main.view_groups_tutorial.*
import kotlinx.android.synthetic.main.view_groups_tutorial.view.*

class GroupsFragment : BaseFragment(), GroupsAdapter.OnGroupItemClickListener {

    private val sharedPreferences by lazy(mode = LazyThreadSafetyMode.NONE) {
        requireActivity().applicationContext.getSharedPreferences(
            getString(R.string.preference_file_key),
            0
        )
    }

    private val viewModel: GroupsViewModel by fragmentViewModel()

    private val adapter by lazy(mode = LazyThreadSafetyMode.NONE) {
        GroupsAdapter(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.selectSubscribe(GroupsState::groups) { groups ->
            instructionsTextView.isGone = groups.isNotEmpty()
            adapter.updateData(groups)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_groups, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        groupsRecyclerView.layoutManager =
            LinearLayoutManager(
                context,
                RecyclerView.VERTICAL,
                false
            ) // todo: reverse layout start at top of page
        groupsRecyclerView.adapter = adapter


        addGroupImageView.setOnClickListener {
            when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.READ_CONTACTS
                ) -> {
                    Intent(context, ContactsActivity::class.java).apply {
                        putExtra(KEY_ARG, GroupArgs())
                        startActivity(this)
                    }
                }
                // todo:
                /*shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS) -> {

                }*/
                else -> {
                    requestPermissions(
                        arrayOf(Manifest.permission.READ_CONTACTS),
                        PERMISSIONS_REQUEST_READ_CONTACTS
                    )
                }
            }
        }

        settingsImageView.setOnClickListener {
            Intent(context, SettingsActivity::class.java).apply {
                startActivity(this)
            }
        }

        titleTextView.setOnClickListener {
            GroupsTutorialDialogFragment().show(childFragmentManager, null)
        }

        if (!sharedPreferences.getBoolean(getString(R.string.groups_tutorial_seen_pref), false)) {
            GroupsTutorialDialogFragment().show(childFragmentManager, null)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_READ_CONTACTS -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    Intent(context, ContactsActivity::class.java).apply {
                        putExtra(KEY_ARG, GroupArgs())
                        startActivity(this)
                    }
                } else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                }
                return
            }
        }
    }

    companion object {
        const val PERMISSIONS_REQUEST_READ_CONTACTS = 100
    }

    override fun onGroupItemClick(group: Group) {
        Intent(context, GroupDetailActivity::class.java).apply {
            putExtra(KEY_ARG, group.groupId)
            startActivity(this)
            // todo: return id instead of entire group
        }
    }

}
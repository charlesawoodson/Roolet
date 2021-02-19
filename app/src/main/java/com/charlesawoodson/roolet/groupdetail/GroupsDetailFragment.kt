package com.charlesawoodson.roolet.groupdetail

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.mvrx.MvRx.KEY_ARG
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.charlesawoodson.roolet.R
import com.charlesawoodson.roolet.contacts.ContactsActivity
import com.charlesawoodson.roolet.contacts.EditGroupArgs
import com.charlesawoodson.roolet.groupdetail.adapters.GroupDetailAdapter
import com.charlesawoodson.roolet.groupdetail.dialogs.CallPhoneDialogFragment
import com.charlesawoodson.roolet.groupdetail.dialogs.GameModeDialogFragment
import com.charlesawoodson.roolet.groupdetail.dialogs.GroupDetailTutorialDialogFragment
import com.charlesawoodson.roolet.groups.GroupsFragment
import com.charlesawoodson.roolet.mvrx.BaseFragment
import kotlinx.android.synthetic.main.fragment_group_detail.*

class GroupsDetailFragment : BaseFragment() {

    private val sharedPreferences by lazy(mode = LazyThreadSafetyMode.NONE) {
        PreferenceManager.getDefaultSharedPreferences(requireActivity())
    }

    private val viewModel: GroupsDetailViewModel by fragmentViewModel()

    private val adapter by lazy(mode = LazyThreadSafetyMode.NONE) {
        GroupDetailAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.asyncSubscribe(GroupDetailState::group) { group ->
            adapter.updateData(group.members)
            groupNameTextView.text = group.title
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_group_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        callActionButton.setOnClickListener {
            when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.CALL_PHONE
                ) -> {
                    checkCallOrGameDialog()
                }
                else -> {
                    // todo: Non deprecated version
                    requestPermissions(
                        arrayOf(Manifest.permission.CALL_PHONE),
                        PERMISSIONS_REQUEST_CALL_PHONE
                    )
                }
            }
        }

        groupsDetailRecyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        groupsDetailRecyclerView.adapter = adapter

        editGroupImageView.setOnClickListener {
            when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.READ_CONTACTS
                ) -> {
                    withState(viewModel) { state ->
                        Intent(context, ContactsActivity::class.java).apply {
                            putExtra(KEY_ARG, EditGroupArgs(state.group()))
                            startActivity(this)
                        }
                    }
                }
                else -> {
                    requestPermissions(
                        arrayOf(Manifest.permission.READ_CONTACTS),
                        PERMISSIONS_REQUEST_READ_CONTACTS
                    )
                }
            }
        }

        backImageView.setOnClickListener {
            requireActivity().finish()
        }

        if (!sharedPreferences.getBoolean(
                getString(R.string.group_detail_tutorial_seen_pref),
                false
            )
        ) {
            GroupDetailTutorialDialogFragment().show(childFragmentManager, null)
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
                withState(viewModel) { state ->
                    Intent(context, ContactsActivity::class.java).apply {
                        putExtra(KEY_ARG, EditGroupArgs(state.group()))
                        startActivity(this)
                    }
                }
                return
            }
            PERMISSIONS_REQUEST_CALL_PHONE -> {
                // todo: check if call permissions were granted, if not show dialog
                checkCallOrGameDialog()
            }
        }
    }

    private fun checkCallOrGameDialog() {
        if (sharedPreferences.getBoolean(getString(R.string.game_mode_pref), false)) {
            GameModeDialogFragment().show(childFragmentManager, null)
        } else {
            CallPhoneDialogFragment().show(childFragmentManager, null)
        }
    }

    companion object {
        const val PERMISSIONS_REQUEST_READ_CONTACTS = 100
        const val PERMISSIONS_REQUEST_CALL_PHONE = 200
    }

}
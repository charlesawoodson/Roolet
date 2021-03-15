package com.charlesawoodson.roolet.groupdetail

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.mvrx.MvRx.KEY_ARG
import com.airbnb.mvrx.args
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.charlesawoodson.roolet.R
import com.charlesawoodson.roolet.RooletActivity
import com.charlesawoodson.roolet.contacts.EditGroupArgs
import com.charlesawoodson.roolet.contacts.dialogs.ErrorDialogArgs
import com.charlesawoodson.roolet.contacts.dialogs.ErrorDialogFragment
import com.charlesawoodson.roolet.groupdetail.adapters.GroupDetailAdapter
import com.charlesawoodson.roolet.groupdetail.dialogs.CallPhoneDialogFragment
import com.charlesawoodson.roolet.groupdetail.dialogs.GameModeDialogFragment
import com.charlesawoodson.roolet.groupdetail.dialogs.GroupDetailTutorialDialogFragment
import com.charlesawoodson.roolet.mvrx.BaseFragment
import kotlinx.android.synthetic.main.fragment_group_detail.*

class GroupsDetailFragment : BaseFragment() {

    private val sharedPreferences by lazy(mode = LazyThreadSafetyMode.NONE) {
        PreferenceManager.getDefaultSharedPreferences(requireActivity())
    }

    private val arguments: GroupDetailArgs by args()

    private val viewModel: GroupsDetailViewModel by fragmentViewModel()

    private val adapter by lazy(mode = LazyThreadSafetyMode.NONE) {
        GroupDetailAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel.asyncSubscribe(GroupDetailState::group) { group ->
            (activity as RooletActivity).supportActionBar?.title = group.title
        }

        viewModel.selectSubscribe(GroupDetailState::groupMembers) { members ->
            adapter.updateData(members)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_group_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.startElapsedTimer()

        callActionButton.setOnClickListener {
            when (PackageManager.PERMISSION_GRANTED) {
                ContextCompat.checkSelfPermission(
                    requireActivity(),
                    Manifest.permission.CALL_PHONE
                ) -> {
                    if (viewModel.getNumbersLeftSize() > 0) {
                        checkCallOrGameDialog()
                    } else {
                        showErrorDialog(R.string.all_members_called)
                    }
                }
                else -> {
                    activity?.requestPermissions(
                        arrayOf(Manifest.permission.CALL_PHONE),
                        PERMISSIONS_REQUEST_CALL_PHONE
                    )
                }
            }
        }

        groupsDetailRecyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        groupsDetailRecyclerView.adapter = adapter

        if (!sharedPreferences.getBoolean(
                getString(R.string.group_detail_tutorial_seen_pref),
                false
            )
        ) {
            GroupDetailTutorialDialogFragment().show(childFragmentManager, null)
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.updateElapsedTime()
    }

    private fun showErrorDialog(messageRes: Int) {
        ErrorDialogFragment().apply {
            arguments = Bundle().apply {
                putParcelable(
                    KEY_ARG,
                    ErrorDialogArgs(R.string.oooop, messageRes)
                )
            }
        }.show(childFragmentManager, null)
    }

    private fun checkCallOrGameDialog() {
        if (sharedPreferences.getBoolean(getString(R.string.game_mode_pref), false)) {
            GameModeDialogFragment().show(childFragmentManager, null)
        } else {
            CallPhoneDialogFragment().show(childFragmentManager, null)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.group_detail_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        (activity as RooletActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as RooletActivity).supportActionBar?.title = arguments.groupName
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_edit -> {
                when (PackageManager.PERMISSION_GRANTED) {
                    ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.READ_CONTACTS
                    ) -> {
                        withState(viewModel) { state ->
                            (activity as RooletActivity).commitContactsFragment(EditGroupArgs(state.group()))
                        }
                    }
                    else -> {
                        requestPermissions(
                            arrayOf(Manifest.permission.READ_CONTACTS),
                            PERMISSIONS_REQUEST_READ_CONTACTS
                        )
                    }
                }
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_READ_CONTACTS -> {
                withState(viewModel) { state ->
                    (activity as RooletActivity).commitContactsFragment(EditGroupArgs(state.group()))
                }
                return
            }
            PERMISSIONS_REQUEST_CALL_PHONE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    if (viewModel.getNumbersLeftSize() > 0) {
                        checkCallOrGameDialog()
                    } else {
                        showErrorDialog(R.string.all_members_called)
                    }
                } else {
                    showErrorDialog(R.string.call_permissions_needed)
                }
                return
            }
        }
    }

    companion object {
        const val PERMISSIONS_REQUEST_READ_CONTACTS = 100
        const val PERMISSIONS_REQUEST_CALL_PHONE = 200
    }

}
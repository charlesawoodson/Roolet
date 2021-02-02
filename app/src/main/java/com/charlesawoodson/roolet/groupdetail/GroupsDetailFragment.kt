package com.charlesawoodson.roolet.groupdetail

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.mvrx.MvRx.KEY_ARG
import com.airbnb.mvrx.fragmentViewModel
import com.airbnb.mvrx.withState
import com.charlesawoodson.roolet.R
import com.charlesawoodson.roolet.contacts.ContactsActivity
import com.charlesawoodson.roolet.contacts.GroupArgs
import com.charlesawoodson.roolet.db.Group
import com.charlesawoodson.roolet.groupdetail.adapters.GroupDetailAdapter
import com.charlesawoodson.roolet.mvrx.BaseFragment
import kotlinx.android.synthetic.main.fragment_group_detail.*

class GroupsDetailFragment : BaseFragment() {

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
                            putExtra(KEY_ARG, GroupArgs(state.group()))
                            startActivity(this)
                        }
                    }
                }

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
                    withState(viewModel) { state ->
                        Intent(context, ContactsActivity::class.java).apply {
                            putExtra(KEY_ARG, GroupArgs(state.group()))
                            startActivity(this)
                        }
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

}
package com.charlesawoodson.roolet.groups

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.mvrx.fragmentViewModel
import com.charlesawoodson.roolet.R
import com.charlesawoodson.roolet.RooletActivity
import com.charlesawoodson.roolet.db.Group
import com.charlesawoodson.roolet.groups.adapters.GroupsAdapter
import com.charlesawoodson.roolet.groups.dialogs.GroupsTutorialDialogFragment
import com.charlesawoodson.roolet.mvrx.BaseFragment
import kotlinx.android.synthetic.main.fragment_groups.*

class GroupsFragment : BaseFragment(), GroupsAdapter.OnGroupItemClickListener {

    private val sharedPreferences by lazy(mode = LazyThreadSafetyMode.NONE) {
        PreferenceManager.getDefaultSharedPreferences(requireActivity())
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
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_groups, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        groupsRecyclerView.layoutManager =
            LinearLayoutManager(context, RecyclerView.VERTICAL, false)

        groupsRecyclerView.adapter = adapter

        if (!sharedPreferences.getBoolean(getString(R.string.groups_tutorial_seen_pref), false)) {
            GroupsTutorialDialogFragment().show(childFragmentManager, null)
        }
    }

    override fun onGroupItemClick(group: Group) {
        (activity as RooletActivity).commitGroupDetailFragment(group)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        (activity as RooletActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
        (activity as RooletActivity).supportActionBar?.title = getString(R.string.roolet)
        super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add_group -> {
                when (PackageManager.PERMISSION_GRANTED) {
                    ContextCompat.checkSelfPermission(
                        requireContext(),
                        Manifest.permission.READ_CONTACTS
                    ) -> {
                        (activity as RooletActivity).commitContactsFragment()
                    }
                    else -> {
                        requestPermissions(
                            // requireActivity(),
                            arrayOf(Manifest.permission.READ_CONTACTS),
                            PERMISSIONS_REQUEST_READ_CONTACTS
                        )
                    }
                }
                true
            }
            R.id.action_settings -> {
                (activity as RooletActivity).commitSettingsFragment()
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
                (activity as RooletActivity).commitContactsFragment()
                return
            }
        }
    }

    companion object {
        const val PERMISSIONS_REQUEST_READ_CONTACTS = 100
    }
}
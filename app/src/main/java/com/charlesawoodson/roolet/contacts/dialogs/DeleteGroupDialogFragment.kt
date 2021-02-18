package com.charlesawoodson.roolet.contacts.dialogs

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.PreferenceManager
import com.airbnb.mvrx.parentFragmentViewModel
import com.charlesawoodson.roolet.R
import com.charlesawoodson.roolet.contacts.ContactsViewModel
import com.charlesawoodson.roolet.groupdetail.GroupsDetailViewModel
import com.charlesawoodson.roolet.groups.GroupsActivity
import com.charlesawoodson.roolet.mvrx.BaseDialogFragment
import kotlinx.android.synthetic.main.fragment_delete_group_dialog.*
import kotlinx.android.synthetic.main.fragment_game_mode_dialog.*

class DeleteGroupDialogFragment : BaseDialogFragment(width = 0.8f, roundEdges = false) {

    private val viewModel: ContactsViewModel by parentFragmentViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_delete_group_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        positiveButton.setOnClickListener {
            viewModel.selectedGroupId?.also {
                viewModel.deleteGroup(it)
            }
            Intent(requireContext(), GroupsActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(this)
            }
            dismiss()
        }

        negativeButton.setOnClickListener {
            dismiss()
        }
    }

}
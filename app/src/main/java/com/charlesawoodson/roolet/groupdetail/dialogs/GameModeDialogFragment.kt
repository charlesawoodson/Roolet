package com.charlesawoodson.roolet.groupdetail.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.preference.PreferenceManager
import com.airbnb.mvrx.parentFragmentViewModel
import com.charlesawoodson.roolet.R
import com.charlesawoodson.roolet.groupdetail.GroupsDetailViewModel
import com.charlesawoodson.roolet.mvrx.BaseDialogFragment
import kotlinx.android.synthetic.main.fragment_game_mode_dialog.*

class GameModeDialogFragment : BaseDialogFragment(width = 0.8f, roundEdges = false) {

    private val sharedPreferences by lazy(mode = LazyThreadSafetyMode.NONE) {
        PreferenceManager.getDefaultSharedPreferences(requireActivity())
    }

    private val viewModel: GroupsDetailViewModel by parentFragmentViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_game_mode_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ruleDescriptionTextView.text = viewModel.getRandomRule()

        gotItButton.setOnClickListener {
            viewModel.callGroupMember(
                sharedPreferences.getBoolean(getString(R.string.repeat_calls_pref), false)
            )
            dismiss()
        }
    }

}
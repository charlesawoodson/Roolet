package com.charlesawoodson.roolet.groupdetail.dialogs

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.charlesawoodson.roolet.R
import kotlinx.android.synthetic.main.view_groups_tutorial.*

class GroupDetailTutorialDialogFragment : DialogFragment() {

    private val sharedPreferences by lazy(mode = LazyThreadSafetyMode.NONE) {
        requireActivity().getSharedPreferences(getString(R.string.preference_file_key), 0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_App_Dialog_Fullscreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.view_group_detail_tutorial, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // requireDialog().window?.setWindowAnimations(R.style.DialogAnimationSlideUp)
        setupTutorialScreen()
    }

    private fun setupTutorialScreen() {
        dialogContainer.setOnClickListener {
            dismiss()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        val editor = sharedPreferences.edit()
        editor.putBoolean(getString(R.string.group_detail_tutorial_seen_pref), true)
        editor.apply()
    }

}
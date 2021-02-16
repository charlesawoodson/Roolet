package com.charlesawoodson.roolet.groups.dialogs

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.charlesawoodson.roolet.R
import kotlinx.android.synthetic.main.list_item_group.*
import kotlinx.android.synthetic.main.list_item_group.view.*
import kotlinx.android.synthetic.main.view_groups_tutorial.*

class GroupsTutorialDialogFragment : DialogFragment() {

    private val sharedPreferences by lazy(mode = LazyThreadSafetyMode.NONE) {
        requireActivity().applicationContext.getSharedPreferences(
            getString(R.string.preference_file_key),
            0
        )
    }

    var tutorialPageCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_App_Dialog_Fullscreen)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.view_groups_tutorial, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // requireDialog().window?.setWindowAnimations(R.style.DialogAnimationSlideUp)
        setupTutorialScreen()
    }

    private fun setupTutorialScreen() {

        Glide.with(requireContext())
            .load(ContextCompat.getDrawable(requireContext(), R.drawable.roolet_icon_grey))
            .circleCrop()
            .into(groupItem.memberOneImageView)

        Glide.with(requireContext())
            .load(ContextCompat.getDrawable(requireContext(), R.drawable.roolet_icon_grey))
            .circleCrop()
            .into(groupItem.memberTwoImageView)

        Glide.with(requireContext())
            .load(ContextCompat.getDrawable(requireContext(), R.drawable.roolet_icon_grey))
            .circleCrop()
            .into(groupItem.memberThreeImageView)

        dialogContainer.setOnClickListener {
            when (tutorialPageCount) {
                0 -> {
                    groupItem.isVisible = true
                    instructionsTextView.setText(R.string.after_building_party_message)

                    tutorialPageCount++
                }
                1 -> {
                    tapHereCreatePartyTextView.isVisible = true
                    tutorialArrowImageView.isVisible = true
                    skipTextView.isGone = true
                    tutorialPageCount++
                }
                else -> {
                    dismiss()
//
                }
            }
        }

        skipTextView.setOnClickListener {
            dismiss()
        }

    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        val editor = sharedPreferences.edit()
        editor.putBoolean(getString(R.string.groups_tutorial_seen_pref), true)
        editor.apply()
    }

}
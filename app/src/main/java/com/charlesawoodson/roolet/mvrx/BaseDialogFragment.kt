package com.charlesawoodson.roolet.mvrx

import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.annotation.CallSuper
import androidx.fragment.app.DialogFragment
import com.airbnb.mvrx.MvRxView
import com.charlesawoodson.roolet.R

open class BaseDialogFragment(
    override val mvrxViewId: String = "",
    width: Float = 1.0F,
    gravity: Int = Gravity.CENTER,
    private val roundEdges: Boolean = true
) : DialogFragment(), MvRxView {

    /**
     * Override to customize the width of the dialog window.
     */
    open val dialogWidth: Int by lazy(mode = LazyThreadSafetyMode.NONE) {
        (resources.displayMetrics.widthPixels * width).toInt()
    }

    /**
     * Override to customize the height of the dialog window. By default the height will be set to wrap content but
     * if you need to show scrolling content inside a dialog you can set a fixed height and should ask the designers
     * if they were sure about that...
     */
    open val dialogHeight: Int = ViewGroup.LayoutParams.WRAP_CONTENT

    /**
     * Override to customize the gravity of the dialog. By default the dialog will be centered in the window.
     */
    open val dialogGravity: Int = gravity

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (roundEdges) {
            dialog?.window?.setBackgroundDrawableResource(R.drawable.rounded_bg)
        }
    }

    @CallSuper
    override fun onStart() {
        super.onStart()
        val params = dialog?.window?.attributes?.apply {
            width = dialogWidth
            height = dialogHeight
            gravity = dialogGravity
            // windowAnimations = R.style.DialogAnimationTheme
        }
        if (params != null) {
            dialog?.window?.attributes = params
        }
    }

    /**
     * Override this to handle any state changes from MvRxViewModels created through MvRx Fragment delegates.
     */
    override fun invalidate() {

    }
}